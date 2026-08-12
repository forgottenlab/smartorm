package io.github.forgottenlab.smartorm.autoconfigure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.forgottenlab.smartorm.mapper.SmartMapper;
import io.github.forgottenlab.smartorm.mapper.SmartNativeMapper;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configures the existing SmartORM runtime graph around a native mapper
 * registered by the application's MyBatis integration.
 */
@AutoConfiguration
@ConditionalOnClass({
        SmartMapper.class,
        SmartNativeMapper.class,
        BaseMapper.class,
        Aspect.class
})
public class SmartOrmAutoConfiguration {

    @Bean
    static BeanFactoryPostProcessor smartOrmRuntimeBeanRegistrar() {
        return new SmartOrmRuntimeBeanRegistrar();
    }
}
