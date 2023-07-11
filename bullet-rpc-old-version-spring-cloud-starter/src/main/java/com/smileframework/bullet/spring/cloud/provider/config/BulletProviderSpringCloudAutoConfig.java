package com.smileframework.bullet.spring.cloud.provider.config;

import com.smileframework.bullet.spring.cloud.provider.registrar.ProviderSpringCloudRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ProviderSpringCloudRegistrar.class})
@Configuration
public class BulletProviderSpringCloudAutoConfig {



}
