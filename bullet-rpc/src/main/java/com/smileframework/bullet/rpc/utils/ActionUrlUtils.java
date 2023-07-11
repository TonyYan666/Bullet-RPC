package com.smileframework.bullet.rpc.utils;

import cn.hutool.core.util.StrUtil;

public class ActionUrlUtils {

    /**
     * 获得提供者方法请求路径
     *
     * @param providerPath
     * @param methodName
     * @return
     */
    public static String getActionUrl(String providerPath, String methodName) {
        if (StrUtil.isEmpty(providerPath) || StrUtil.isEmpty(methodName)) {
            return null;
        }
        return providerPath + "#" + methodName;
    }

}
