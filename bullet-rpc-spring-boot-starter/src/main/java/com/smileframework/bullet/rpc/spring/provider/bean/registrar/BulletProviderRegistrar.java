package com.smileframework.bullet.rpc.spring.provider.bean.registrar;

import com.smileframework.bullet.rpc.spring.common.config.BulletScanPackageConfig;
import com.smileframework.bullet.rpc.spring.provider.bean.scan.ServiceProviderBeanScannerConfig;
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
public class BulletProviderRegistrar implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar {

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
        this.registerProviderBeanDefinitions(importingClassMetadata, registry, importBeanNameGenerator);
//        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ServiceProviderExportPostProcessor.class.getName(), ServiceProviderExportPostProcessor.class);
    }

    private void registerProviderBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        Set<String> packages = new HashSet<>();
        if (AutoConfigurationPackages.has(this.beanFactory)) {
            packages.addAll(AutoConfigurationPackages.get(this.beanFactory));
        }
        BulletScanPackageConfig scanPackageConfig = this.beanFactory.getBean(BulletScanPackageConfig.class);
        packages.addAll(scanPackageConfig.getProviderPackages());
        if(packages.isEmpty()){
            log.debug("Could not determine auto-configuration package, automatic bullet provider scanning disabled.");
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ServiceProviderBeanScannerConfig.class);
        builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(ServiceProviderBeanScannerConfig.class.getName(), builder.getBeanDefinition());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
    }
}
