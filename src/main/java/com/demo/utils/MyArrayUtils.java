package com.demo.utils;

import com.sun.deploy.util.ArrayUtil;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author 张新玲
 * @since 2020/3/14 15:17
 */

public class MyArrayUtils {
    /**
     * 判断数组中是否包含元素
     * @param arr
     * @param targetValue
     * @return
     */
    public static boolean useArrayUtils(String[] arr,String targetValue){
        return ArrayUtils.contains(arr,targetValue);
    }

    /**
     * 判断数组中是否包含元素
     * @param arr
     * @param targetValue
     * @return
     */
    public static boolean useArrayUtils (Class[] arr,Class targetValue){
        return ArrayUtils.contains(arr,targetValue);
    }
}
