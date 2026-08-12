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
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class SmartOrmRuntimeBeanRegistrar implements BeanFactoryPostProcessor {

    private static final String NATIVE_MAPPER_BEAN_NAME = "smartNativeMapper";

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

        if (hasRuntimeGraphConflict(beanFactory)) {
            return;
        }

        // MapperScannerConfigurer is a registry post-processor. Registering one
        // known internal Mapper in this later phase cannot suppress its scan.
        Set<String> nativeMapperBeanNames = findNativeMapperBeanNames(beanFactory, registry);
        if (nativeMapperBeanNames.size() > 1) {
            return;
        }
        if (nativeMapperBeanNames.size() == 1
                && !isAutowireCandidate(
                        beanFactory,
                        nativeMapperBeanNames.iterator().next()
                )) {
            return;
        }
        if (nativeMapperBeanNames.isEmpty()) {
            if (beanFactory.containsBean(NATIVE_MAPPER_BEAN_NAME)) {
                return;
            }
            SessionBinding sessionBinding = findSessionBinding(beanFactory);
            if (sessionBinding == null) {
                return;
            }
            registerNativeMapper(registry, sessionBinding);
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
        Set<String> beanNames = new LinkedHashSet<>(Arrays.asList(
                beanFactory.getBeanNamesForType(SmartNativeMapper.class, true, false)
        ));
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition definition = registry.getBeanDefinition(beanName);
            Class<?> objectType = resolveFactoryBeanObjectType(
                    definition.getAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE)
            );
            if (objectType != null && SmartNativeMapper.class.isAssignableFrom(objectType)) {
                beanNames.add(beanName);
            }
        }
        return beanNames;
    }

    private Class<?> resolveFactoryBeanObjectType(Object attribute) {
        if (attribute instanceof Class<?> objectType) {
            return objectType;
        }
        if (attribute instanceof ResolvableType resolvableType) {
            return resolvableType.resolve();
        }
        return null;
    }

    private SessionBinding findSessionBinding(ConfigurableListableBeanFactory beanFactory) {
        CandidateChoice template = chooseCandidate(beanFactory, SqlSessionTemplate.class);
        if (template.state() == CandidateState.SELECTED) {
            return new SessionBinding("sqlSessionTemplate", template.beanName());
        }
        if (template.state() == CandidateState.AMBIGUOUS) {
            return null;
        }

        CandidateChoice factory = chooseCandidate(beanFactory, SqlSessionFactory.class);
        if (factory.state() == CandidateState.SELECTED) {
            return new SessionBinding("sqlSessionFactory", factory.beanName());
        }
        return null;
    }

    private CandidateChoice chooseCandidate(
            ConfigurableListableBeanFactory beanFactory,
            Class<?> requiredType
    ) {
        List<String> candidates = Arrays.stream(
                        beanFactory.getBeanNamesForType(requiredType, true, false)
                )
                .filter(beanName -> isAutowireCandidate(beanFactory, beanName))
                .toList();
        if (candidates.isEmpty()) {
            return CandidateChoice.missing();
        }
        if (candidates.size() == 1) {
            return CandidateChoice.selected(candidates.get(0));
        }

        List<String> primaryCandidates = candidates.stream()
                .filter(beanName -> isPrimary(beanFactory, beanName))
                .toList();
        if (primaryCandidates.size() == 1) {
            return CandidateChoice.selected(primaryCandidates.get(0));
        }
        return CandidateChoice.ambiguous();
    }

    private boolean isAutowireCandidate(
            ConfigurableListableBeanFactory beanFactory,
            String beanName
    ) {
        return !beanFactory.containsBeanDefinition(beanName)
                || beanFactory.getMergedBeanDefinition(beanName).isAutowireCandidate();
    }

    private boolean isPrimary(ConfigurableListableBeanFactory beanFactory, String beanName) {
        return beanFactory.containsBeanDefinition(beanName)
                && beanFactory.getMergedBeanDefinition(beanName).isPrimary();
    }

    private void registerNativeMapper(
            BeanDefinitionRegistry registry,
            SessionBinding sessionBinding
    ) {
        RootBeanDefinition definition = new RootBeanDefinition(MapperFactoryBean.class);
        definition.getPropertyValues().add("mapperInterface", SmartNativeMapper.class);
        definition.getPropertyValues().add("addToConfig", true);
        definition.getPropertyValues().add(
                sessionBinding.propertyName(),
                new RuntimeBeanReference(sessionBinding.beanName())
        );
        definition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, SmartNativeMapper.class);
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(NATIVE_MAPPER_BEAN_NAME, definition);
    }

    private boolean hasRuntimeGraphConflict(ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.getBeanNamesForType(
                SmartAnnotationAspect.class,
                true,
                false
        ).length > 0) {
            return true;
        }
        for (RuntimeBean runtimeBean : RUNTIME_BEANS) {
            if (beanFactory.containsBean(runtimeBean.name())) {
                return true;
            }
        }
        return false;
    }

    private record RuntimeBean(String name, Class<?> type) {
    }

    private record SessionBinding(String propertyName, String beanName) {
    }

    private enum CandidateState {
        MISSING,
        SELECTED,
        AMBIGUOUS
    }

    private record CandidateChoice(CandidateState state, String beanName) {

        private static CandidateChoice missing() {
            return new CandidateChoice(CandidateState.MISSING, null);
        }

        private static CandidateChoice selected(String beanName) {
            return new CandidateChoice(CandidateState.SELECTED, beanName);
        }

        private static CandidateChoice ambiguous() {
            return new CandidateChoice(CandidateState.AMBIGUOUS, null);
        }
    }
}
