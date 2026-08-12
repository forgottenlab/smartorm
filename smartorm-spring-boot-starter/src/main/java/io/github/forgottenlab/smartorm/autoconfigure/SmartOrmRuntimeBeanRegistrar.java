package io.github.forgottenlab.smartorm.autoconfigure;

import io.github.forgottenlab.smartorm.aspect.SmartAnnotationAspect;
import io.github.forgottenlab.smartorm.executor.SmartNativeExecutor;
import io.github.forgottenlab.smartorm.handler.SmartDeleteHandler;
import io.github.forgottenlab.smartorm.handler.SmartHandlerRegistry;
import io.github.forgottenlab.smartorm.handler.SmartInsertHandler;
import io.github.forgottenlab.smartorm.handler.SmartPageHandler;
import io.github.forgottenlab.smartorm.handler.SmartSelectHandler;
import io.github.forgottenlab.smartorm.handler.SmartUpdateHandler;
import io.github.forgottenlab.smartorm.mapper.SmartNativeMapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

final class SmartOrmRuntimeBeanRegistrar implements BeanFactoryPostProcessor {

    private static final RuntimeBean[] RUNTIME_BEANS = {
            new RuntimeBean("smartNativeExecutor", SmartNativeExecutor.class),
            new RuntimeBean("smartSelectHandler", SmartSelectHandler.class),
            new RuntimeBean("smartPageHandler", SmartPageHandler.class),
            new RuntimeBean("smartInsertHandler", SmartInsertHandler.class),
            new RuntimeBean("smartUpdateHandler", SmartUpdateHandler.class),
            new RuntimeBean("smartDeleteHandler", SmartDeleteHandler.class),
            new RuntimeBean("smartHandlerRegistry", SmartHandlerRegistry.class),
            new RuntimeBean("smartAnnotationAspect", SmartAnnotationAspect.class)
    };

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        if (!(beanFactory instanceof BeanDefinitionRegistry registry)) {
            return;
        }

        // MapperScannerConfigurer runs as a registry post-processor. Observing in
        // this later phase preserves its ownership without introducing another scan.
        if (findNativeMapperBeanNames(beanFactory, registry).size() != 1
                || beanFactory.getBeanNamesForType(
                        SmartAnnotationAspect.class,
                        false,
                        false
                ).length > 0
                || hasRuntimeBeanNameConflict(registry)) {
            return;
        }

        for (RuntimeBean runtimeBean : RUNTIME_BEANS) {
            RootBeanDefinition definition = new RootBeanDefinition(runtimeBean.type());
            definition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
            registry.registerBeanDefinition(runtimeBean.name(), definition);
        }
    }

    private Set<String> findNativeMapperBeanNames(
            ConfigurableListableBeanFactory beanFactory,
            BeanDefinitionRegistry registry
    ) {
        Set<String> beanNames = new LinkedHashSet<>(Set.of(
                beanFactory.getBeanNamesForType(SmartNativeMapper.class, false, false)
        ));
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition definition = registry.getBeanDefinition(beanName);
            if (SmartNativeMapper.class.equals(
                    definition.getAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE)
            )) {
                beanNames.add(beanName);
            }
        }
        return beanNames;
    }

    private boolean hasRuntimeBeanNameConflict(BeanDefinitionRegistry registry) {
        for (RuntimeBean runtimeBean : RUNTIME_BEANS) {
            if (registry.containsBeanDefinition(runtimeBean.name())) {
                return true;
            }
        }
        return false;
    }

    private record RuntimeBean(String name, Class<?> type) {
    }
}
