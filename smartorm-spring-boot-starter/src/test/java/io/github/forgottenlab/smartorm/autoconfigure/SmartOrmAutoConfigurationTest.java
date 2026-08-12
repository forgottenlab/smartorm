package io.github.forgottenlab.smartorm.autoconfigure;

import io.github.forgottenlab.smartorm.annotations.SmartDelete;
import io.github.forgottenlab.smartorm.annotations.SmartInsert;
import io.github.forgottenlab.smartorm.annotations.SmartPage;
import io.github.forgottenlab.smartorm.annotations.SmartSelect;
import io.github.forgottenlab.smartorm.annotations.SmartUpdate;
import io.github.forgottenlab.smartorm.aspect.SmartAnnotationAspect;
import io.github.forgottenlab.smartorm.builder.SmartWrapperBuilder;
import io.github.forgottenlab.smartorm.executor.DefaultSmartExecutor;
import io.github.forgottenlab.smartorm.executor.SmartNativeExecutor;
import io.github.forgottenlab.smartorm.handler.SmartDeleteHandler;
import io.github.forgottenlab.smartorm.handler.SmartHandler;
import io.github.forgottenlab.smartorm.handler.SmartHandlerRegistry;
import io.github.forgottenlab.smartorm.handler.SmartInsertHandler;
import io.github.forgottenlab.smartorm.handler.SmartPageHandler;
import io.github.forgottenlab.smartorm.handler.SmartSelectHandler;
import io.github.forgottenlab.smartorm.handler.SmartUpdateHandler;
import io.github.forgottenlab.smartorm.mapper.SmartMapper;
import io.github.forgottenlab.smartorm.mapper.SmartNativeMapper;
import io.github.forgottenlab.smartorm.resolver.meta.JdbcMetaProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SmartOrmAutoConfigurationTest {

    private static final String IMPORTS_PATH =
            "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SmartOrmAutoConfiguration.class));

    @Test
    void autoConfiguresTheExactRuntimeGraphWithoutOwningInfrastructure() {
        SmartNativeMapper nativeMapper = mock(SmartNativeMapper.class);

        contextRunner
                .withBean(SmartNativeMapper.class, () -> nativeMapper)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertThat(context).hasSingleBean(SmartNativeExecutor.class);
                    assertThat(context).hasSingleBean(SmartSelectHandler.class);
                    assertThat(context).hasSingleBean(SmartPageHandler.class);
                    assertThat(context).hasSingleBean(SmartInsertHandler.class);
                    assertThat(context).hasSingleBean(SmartUpdateHandler.class);
                    assertThat(context).hasSingleBean(SmartDeleteHandler.class);
                    assertThat(context).hasSingleBean(SmartHandlerRegistry.class);
                    assertThat(context).hasSingleBean(SmartAnnotationAspect.class);

                    assertThat(context.getBeansOfType(SmartHandler.class)).hasSize(5);
                    SmartHandlerRegistry registry = context.getBean(SmartHandlerRegistry.class);
                    assertThat(registry.getHandler(SmartSelect.class))
                            .isSameAs(context.getBean(SmartSelectHandler.class));
                    assertThat(registry.getHandler(SmartPage.class))
                            .isSameAs(context.getBean(SmartPageHandler.class));
                    assertThat(registry.getHandler(SmartInsert.class))
                            .isSameAs(context.getBean(SmartInsertHandler.class));
                    assertThat(registry.getHandler(SmartUpdate.class))
                            .isSameAs(context.getBean(SmartUpdateHandler.class));
                    assertThat(registry.getHandler(SmartDelete.class))
                            .isSameAs(context.getBean(SmartDeleteHandler.class));

                    assertThat(context).doesNotHaveBean(SmartWrapperBuilder.class);
                    assertThat(context).doesNotHaveBean(DefaultSmartExecutor.class);
                    assertThat(context).doesNotHaveBean(DataSource.class);
                    assertThat(context).doesNotHaveBean(SqlSessionFactory.class);
                    assertThat(context).doesNotHaveBean(SqlSessionTemplate.class);
                    assertThat(context).doesNotHaveBean(PlatformTransactionManager.class);
                    assertThat(context).doesNotHaveBean(MapperScannerConfigurer.class);
                    assertThat(context).doesNotHaveBean(JdbcMetaProvider.class);
                    verifyNoInteractions(nativeMapper);
                });
    }

    @Test
    void backsOffCleanlyWhenMyBatisInfrastructureIsNotRegistered() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(SmartNativeMapper.class);
            assertThat(context).doesNotHaveBean(SmartNativeExecutor.class);
            assertThat(context).doesNotHaveBean(SmartHandlerRegistry.class);
            assertThat(context).doesNotHaveBean(SmartAnnotationAspect.class);
        });
    }

    @Test
    void supportsAnApplicationRegisteredMapperWithoutConnectingToADatabase() {
        contextRunner
                .withUserConfiguration(MapperRegistrationConfiguration.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertThat(context).hasSingleBean(SmartAnnotationAspect.class);
                    assertThat(context).doesNotHaveBean(DataSource.class);
                    verifyNoInteractions(context.getBean(SqlSessionFactory.class)
                            .getConfiguration().getEnvironment().getDataSource());
                });
    }

    @Test
    void backsOffCleanlyWhenARequiredCoreTypeIsMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(SmartMapper.class))
                .withConfiguration(AutoConfigurations.of(SmartOrmAutoConfiguration.class))
                .withBean(SmartNativeMapper.class, () -> mock(SmartNativeMapper.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SmartNativeExecutor.class);
                    assertThat(context).doesNotHaveBean(SmartAnnotationAspect.class);
                });
    }

    @Test
    void backsOffAsAWholeWhenAnApplicationAlreadyProvidesTheAspect() {
        SmartAnnotationAspect applicationAspect = new SmartAnnotationAspect(
                new SmartHandlerRegistry(List.<SmartHandler<?>>of())
        );

        contextRunner
                .withBean(SmartNativeMapper.class, () -> mock(SmartNativeMapper.class))
                .withBean(SmartAnnotationAspect.class, () -> applicationAspect)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartAnnotationAspect.class);
                    assertThat(context.getBean(SmartAnnotationAspect.class)).isSameAs(applicationAspect);
                    assertThat(context).doesNotHaveBean(SmartNativeExecutor.class);
                    assertThat(context).doesNotHaveBean(SmartHandlerRegistry.class);
                });
    }

    @Test
    void preservesAnApplicationProvidedNativeMapper() {
        SmartNativeMapper applicationMapper = mock(SmartNativeMapper.class);

        contextRunner
                .withBean(SmartNativeMapper.class, () -> applicationMapper)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertThat(context.getBean(SmartNativeMapper.class)).isSameAs(applicationMapper);
                    assertThat(context).hasSingleBean(SmartAnnotationAspect.class);
                    verifyNoInteractions(applicationMapper);
                });
    }

    @Test
    void discoversTheAutoConfigurationThroughSpringBootImports() {
        new ApplicationContextRunner()
                .withUserConfiguration(DiscoveryConfiguration.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartOrmAutoConfiguration.class);
                    assertThat(context).hasSingleBean(SmartAnnotationAspect.class);
                });
    }

    @Test
    void declaresOnlyTheCurrentAutoConfigurationInImportMetadata() throws Exception {
        ClassPathResource imports = new ClassPathResource(IMPORTS_PATH);

        assertThat(imports.exists()).isTrue();
        assertThat(imports.getContentAsString(StandardCharsets.UTF_8).lines().toList())
                .containsExactly(SmartOrmAutoConfiguration.class.getName());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    static class DiscoveryConfiguration {

        @org.springframework.context.annotation.Bean
        SmartNativeMapper smartNativeMapper() {
            return mock(SmartNativeMapper.class);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @MapperScan(
            basePackageClasses = SmartNativeMapper.class,
            annotationClass = Mapper.class
    )
    static class MapperRegistrationConfiguration {

        @org.springframework.context.annotation.Bean
        SqlSessionFactory sqlSessionFactory() {
            return newSqlSessionFactory();
        }
    }

    private static SqlSessionFactory newSqlSessionFactory() {
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();
        configuration.setEnvironment(new Environment(
                "starter-test",
                new JdbcTransactionFactory(),
                mock(DataSource.class)
        ));
        return new DefaultSqlSessionFactory(configuration);
    }
}
