package com.demo.common;

import com.demo.annotation.*;
import com.demo.config.ConfigurationUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static com.demo.config.ConfigurationUtils.getPropertiesByKey;
import static com.demo.utils.MyArrayUtils.useArrayUtils;

/**
 * @author 张新玲
 * @since 2020/3/14 9:17
 */
public class MyApplicationContext {
    //IOC容器， 如：String(loginController) --> object(loginController实例)
    private Map<String ,Object> iocBeanMap=new ConcurrentHashMap(32);
    //类集合，存放所有的全限制类名
    private Set<String> classSet=new HashSet<>();
    //构造函数
    public MyApplicationContext() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        //初始化数据
        this.classLoader();
    }

    /**
     * 从IOC容器中获取对象
     * @param beanName
     * @return
     */
    public Object getIocBean(String beanName){
        if (iocBeanMap!=null){
            return iocBeanMap.get(toLowercaseIndex(beanName));
        }else {
            return  null;
        }
    }


    /**
     * 类加载器
     */
    private  void classLoader() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        //加载配置文件所有配置信息
        new ConfigurationUtils(null);
        //获取扫描包路径
        String classScanPath= (String) ConfigurationUtils.properties.get("IocDemo.scan.path");
        if (StringUtils.isNotEmpty(classScanPath)){
            classScanPath=classScanPath.replace(".","/");
        } else  {
            throw new RuntimeException("请配置项目包扫描路径 IocDemo.scan.path");
        }
        //扫描项目根目中所有的class文件
        getPackageClassFile(classScanPath);
        //遍历所有的class文件
        for (String className : classSet){
            addServiceToIoc(Class.forName(className));
        }
        //获取带有myService注解类的所有的带Autowired注解的属性并对其进行实例化
        Set<String> beanKeySet = iocBeanMap.keySet();
        for (String beanName:beanKeySet){
            addAutowiredToField(iocBeanMap.get(beanName));
        }
    }

    /**
     * 控制反转
     * @param clazz
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void addServiceToIoc (Class clazz) throws IllegalAccessException,InstantiationException{
        if (clazz.getAnnotation(MyController.class)!=null){
            iocBeanMap.put(toLowercaseIndex(clazz.getSimpleName()),clazz.newInstance());
            System.out.println("控制反转访问控制层："+ toLowercaseIndex(clazz.getSimpleName()));
        }else if (clazz.getAnnotation(MyService.class)!=null){
            //将当前类交由IOC管理
            MyService myService= (MyService) clazz.getAnnotation(MyService.class);
            iocBeanMap.put(StringUtils.isEmpty(myService.value())?toLowercaseIndex(clazz.getSimpleName()):toLowercaseIndex(myService.value()),clazz.newInstance());
            System.out.println("控制反转服务层："+ toLowercaseIndex(clazz.getSimpleName()));
        }else if (clazz.getAnnotation(MyMapping.class)!=null){
            MyMapping myMapping= (MyMapping) clazz.getAnnotation(MyMapping.class);
            iocBeanMap.put(StringUtils.isEmpty(myMapping.value())?toLowercaseIndex(clazz.getSimpleName()):toLowercaseIndex(myMapping.value()),clazz.newInstance());
            System.out.println("控制反转持久层：" + toLowercaseIndex(clazz.getSimpleName()));
        }
    }
    /**
     * 依赖注入
     */
    private void addAutowiredToField(Object obj) throws IllegalAccessException, InstantiationException {
        Field[] fields=obj.getClass().getDeclaredFields();
        for (Field field : fields){
            if (field.getAnnotation(MyAutowired.class)!=null){
                field.setAccessible(true);
                MyAutowired myAutowired=field.getAnnotation(MyAutowired.class);
                Class<?> fieldClass=field.getType();
                //接口不能被实例化，需要对接口进行特殊处理获取其子类，获取所有实现类
                if (fieldClass.isInterface()){
                    //如果有指定获取子类名
                    if (StringUtils.isNotEmpty(myAutowired.value())){
                        field.set(obj,iocBeanMap.get(myAutowired.value()));
                    }else {
                        //当注入接口时，属性的名字与接口实现类名一致则直接从容器中获取
                        Object objByName=iocBeanMap.get(field.getName());
                        if (objByName != null){
                            field.set(obj,objByName);
                            //递归依赖注入
                            addAutowiredToField(field.getType());
                        } else {
                            //注入接口时，如果属性名称与接口实现类名不一致
                            List<Object> list=findSuperInterfaceByIoc(field.getType());
                            if (list!=null && list.size()>0){
                                if (list.size()>1){
                                    throw new RuntimeException(obj.getClass() + "注入接口"+ field.getType() +"失败，请在注解中确定需要注入的具体实现类");
                                }else  {
                                    field.set(obj,list.get(0));
                                    //递归依赖注入
                                    addAutowiredToField(field.getType());
                                }
                            }else {
                                throw new RuntimeException("当前类"+obj.getClass()+"不能注入接口" +field.getType().getClass()+",接口没有实现类不能被实例化");
                            }
                        }
                    }
                }else {
                    //如果不是接口时
                    String beanName=StringUtils.isEmpty(myAutowired.value())? toLowercaseIndex(field.getName()):toLowercaseIndex(myAutowired.value());
                    Object beanObj=iocBeanMap.get(beanName);
                    field.set(obj,beanObj == null ? field.getType().newInstance():beanObj);
                    System.out.println("依赖注入"+ field.getName());
                }
                //递归依赖注入
                addAutowiredToField(field.getType());
            }
            if (field.getAnnotation(Value.class)!=null){
                field.setAccessible(true);
                Value value=field.getAnnotation(Value.class);
                field.set(obj, StringUtils.isNotEmpty(value.value()) ? getPropertiesByKey(value.value()) : null );
                System.out.println("注入配置文件" + obj.getClass() + "加载配置属性" + value.value());
            }
        }
    }

    /**
     * 判断需要注入的接口所有的实现类
     * @param clazz
     * @return
     */
    private List<Object> findSuperInterfaceByIoc(Class<?> clazz) {
        Set<String> beanNameList =iocBeanMap.keySet();
        ArrayList<Object> objectArrayList =new ArrayList<>();
        for (String beanName:beanNameList){
            Object obj =iocBeanMap.get(beanName);
            Class<?>[] interfaces=obj.getClass().getInterfaces();
            if (useArrayUtils(interfaces,clazz)){
                objectArrayList.add(obj);
            }
        }
        return objectArrayList;
    }

    /**
     * 扫描项目根目录中所有的class文件存放到classSet中
     * @param packageName
     */
    private void getPackageClassFile(String packageName) {
        URL url=this.getClass().getClassLoader().getResource(packageName);
        File file= new File(url.getFile());
        //如果文件存在并且是个文件夹，则遍历文件夹下所有文件放在数组中
        if (file.exists() && file.isDirectory()){
            File[] files=file.listFiles();
            for (File fileSon:files) {
                if (fileSon.isDirectory()){
                    //递归扫描
                    getPackageClassFile(packageName+"/" +fileSon.getName());

                }else   {
                    //是文件并且是以.class结尾的
                    if (fileSon.getName().endsWith(".class")){
                        System.out.println("正在加载:" + packageName.replace("/","."));
                        classSet.add(packageName.replace("/",".")+ "." + fileSon.getName().replace(".class",""));
                    }
                }
            }
        }else {
            throw  new RuntimeException("没有找到需要扫描的目录文件");
        }
    }
    /**
     * 类名首字母转小写
     * @param beanName
     * @return
     */
    private static String toLowercaseIndex(String beanName) {
        if (StringUtils.isNotEmpty(beanName)){
            return beanName.substring(0,1).toLowerCase() + beanName.substring(1, beanName.length());

        }
        return beanName;
    }

    /**
     * 类名首字母转大写
     * @param name
     * @return
     */
    private static String toUpperCaseIndex(String name){
        if (StringUtils.isNotEmpty(name)){
            return name.substring(0,1).toUpperCase()+name.substring(1,name.length());
        }
        return  name;
    }
}
