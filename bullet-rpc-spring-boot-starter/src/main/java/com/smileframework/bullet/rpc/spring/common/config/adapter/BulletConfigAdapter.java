package com.smileframework.bullet.rpc.spring.common.config.adapter;

import com.smileframework.bullet.rpc.spring.common.config.BulletRpcConfig;

/**
 * Bullet RPC 框架配置器
 */
public interface BulletConfigAdapter {

    /**
     * 配置rpc框架
     * @param config
     */
    void config(BulletRpcConfig config);

}
