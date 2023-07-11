package com.smileframework.bullet.rpc.spring.consumer.bean.registrar;

import com.smileframework.bullet.rpc.spring.common.config.BulletScanPackageConfig;
import com.smileframework.bullet.rpc.spring.consumer.bean.scan.ServiceConsumerBeanScannerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class BulletConsumerRegistrar implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar {

    private BeanFactory beanFactory;
    private Environment environment;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        Set<String> packages = new HashSet<>();
        if (AutoConfigurationPackages.has(this.beanFactory)) {
            packages.addAll(AutoConfigurationPackages.get(this.beanFactory));
        }
        BulletScanPackageConfig scanPackageConfig = this.beanFactory.getBean(BulletScanPackageConfig.class);
        packages.addAll(scanPackageConfig.getConsumerPackages());
        if (packages.isEmpty()) {
            log.info("Could not determine auto-configuration package, automatic bullet consumer scanning disabled.");
            return;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ServiceConsumerBeanScannerConfig.class);
        builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(ServiceConsumerBeanScannerConfig.class.getName(), builder.getBeanDefinition());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
    }
}
