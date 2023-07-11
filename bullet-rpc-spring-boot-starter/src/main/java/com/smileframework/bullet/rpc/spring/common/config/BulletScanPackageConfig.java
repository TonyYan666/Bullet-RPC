package com.smileframework.bullet.rpc.spring.common.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class BulletScanPackageConfig {

    private Set<String> consumerPackages;

    private Set<String> providerPackages;

}
