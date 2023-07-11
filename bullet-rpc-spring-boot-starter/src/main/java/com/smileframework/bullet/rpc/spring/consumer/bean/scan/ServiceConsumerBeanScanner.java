package com.smileframework.bullet.rpc.spring.consumer.bean.scan;

import com.smileframework.bullet.rpc.spring.consumer.bean.factory.ServiceConsumerBeanFactory;
import com.smileframework.bullet.rpc.consumer.definition.annotation.ServiceConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.springframework.context.annotation.AnnotationConfigUtils.registerAnnotationConfigProcessors;

@Slf4j
public class ServiceConsumerBeanScanner extends ClassPathBeanDefinitionScanner {


    public ServiceConsumerBeanScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, ResourceLoader resourceLoader) {
        super(registry, useDefaultFilters);
        setEnvironment(environment);
        setResourceLoader(resourceLoader);
        registerAnnotationConfigProcessors(registry);
    }

    public ServiceConsumerBeanScanner(BeanDefinitionRegistry registry, Environment environment,
                                      ResourceLoader resourceLoader) {
        this(registry, false, environment, resourceLoader);
    }

    public void registerFilters() {

        addIncludeFilter(new AnnotationTypeFilter(ServiceConsumer.class));

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.isEmpty()) {
            log.warn("No Bullet service consumer was found in " + Arrays.toString(basePackages) + " package. Please check your configuration.");
            return beanDefinitionHolders;
        }
        this.processBeanDefinitions(beanDefinitionHolders);
        return beanDefinitionHolders;
    }

    /**
     * 添加 provider bean 定义
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        AbstractBeanDefinition definition;
        BeanDefinitionRegistry registry = getRegistry();
        for (BeanDefinitionHolder definitionHolder : beanDefinitionHolders) {
            definition = (AbstractBeanDefinition) definitionHolder.getBeanDefinition();
            boolean scopedProxy = false;
            if (ScopedProxyFactoryBean.class.getName().equals(definition.getBeanClassName())) {
                definition = (AbstractBeanDefinition) Optional
                        .ofNullable(((RootBeanDefinition) definition).getDecoratedDefinition())
                        .map(BeanDefinitionHolder::getBeanDefinition).orElseThrow(() -> new IllegalStateException(
                                "The target bean definition of scoped proxy bean not found. Root bean definition[" + definitionHolder + "]"));
                scopedProxy = true;
            }
            Class beanClass = null;
            try {
                beanClass = getClass().getClassLoader().loadClass(definition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                log.error("[Bullet] " + definition.getBeanClassName() + " could not be found in class loader. ");
                continue;
            }
            definition.setBeanClass(ServiceConsumerBeanFactory.class);
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClass);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            if (scopedProxy) {
                continue;
            }
            definition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
            if (!definition.isSingleton()) {
                BeanDefinitionHolder proxyHolder = ScopedProxyUtils.createScopedProxy(definitionHolder, registry, true);
                if (registry.containsBeanDefinition(proxyHolder.getBeanName())) {
                    registry.removeBeanDefinition(proxyHolder.getBeanName());
                }
                registry.registerBeanDefinition(proxyHolder.getBeanName(), proxyHolder.getBeanDefinition());
            }
        }
    }


    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface();
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            log.warn("Skipping service consumer with name '" + beanName + "' and '"
                    + beanDefinition.getBeanClassName() + "' service consumer interface " + ". Bean already defined with the same name!");
            return false;
        }
    }


}
