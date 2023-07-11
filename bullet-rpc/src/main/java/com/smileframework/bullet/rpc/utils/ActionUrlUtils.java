package com.smileframework.bullet.rpc.utils;

public class ActionUrlUtils {

    /**
     * 获得提供者方法请求路径
     * @param providerPath
     * @param methodName
     * @return
     */
    public static String getActionUrl(String providerPath, String methodName) {
        return providerPath + "#" + methodName;
    }

}
