package io.github.forgottenlab.smartorm.autoconfigure;

import io.github.forgottenlab.smartorm.annotations.SmartDelete;
import io.github.forgottenlab.smartorm.annotations.SmartInsert;
import io.github.forgottenlab.smartorm.annotations.SmartPage;
import io.github.forgottenlab.smartorm.annotations.SmartSelect;
import io.github.forgottenlab.smartorm.annotations.SmartUpdate;
import io.github.forgottenlab.smartorm.aspect.SmartAnnotationAspect;
import io.github.forgottenlab.smartorm.autoconfigure.defaultscan.DefaultApplicationMapper;
import io.github.forgottenlab.smartorm.autoconfigure.defaultscan.DefaultScanApplication;
import io.github.forgottenlab.smartorm.autoconfigure.explicit.ApplicationMapper;
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
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class SmartOrmAutoConfigurationTest {

    private static final String IMPORTS_PATH =
            "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";
    private static final String NATIVE_MAPPER_BEAN_NAME = "smartNativeMapper";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SmartOrmAutoConfiguration.class));

    @Test
    void autoRegistersTheInternalMapperAndExactRuntimeGraphFromAnApplicationSessionFactory() {
        DataSource dataSource = mock(DataSource.class);

        contextRunner
                .withBean(SqlSessionFactory.class, () -> newSqlSessionFactory(dataSource))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertExactRuntimeGraph(context);
                    assertThat(context).doesNotHaveBean(SmartWrapperBuilder.class);
                    assertThat(context).doesNotHaveBean(DefaultSmartExecutor.class);
                    assertThat(context).doesNotHaveBean(DataSource.class);
                    assertThat(context).hasSingleBean(SqlSessionFactory.class);
                    assertThat(context).doesNotHaveBean(SqlSessionTemplate.class);
                    assertThat(context).doesNotHaveBean(PlatformTransactionManager.class);
                    assertThat(context).doesNotHaveBean(MapperScannerConfigurer.class);
                    assertThat(context).doesNotHaveBean(JdbcMetaProvider.class);
                    assertThat(nativeMapperFactory(context).getSqlSessionFactory())
                            .isSameAs(context.getBean(SqlSessionFactory.class));
                    verifyNoInteractions(dataSource);
                });
    }

    @Test
    void preservesMyBatisBootDefaultMapperDiscoveryWhileRegisteringTheInternalMapper() {
        DataSource dataSource = mock(DataSource.class);

        new ApplicationContextRunner()
                .withUserConfiguration(DefaultScanApplication.class)
                .withBean(DataSource.class, () -> dataSource)
                .withPropertyValues("spring.sql.init.mode=never")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(DefaultApplicationMapper.class);
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertExactRuntimeGraph(context);
                    assertThat(context).hasSingleBean(MapperScannerConfigurer.class);
                    assertThat(nativeMapperFactory(context).getSqlSessionTemplate())
                            .isSameAs(context.getBean(SqlSessionTemplate.class));
                    assertNoDatabaseConnections(dataSource);
                    verifyNoInteractions(dataSource);
                });
    }

    @Test
    void preservesAnExplicitApplicationOnlyMapperScan() {
        DataSource dataSource = mock(DataSource.class);

        contextRunner
                .withUserConfiguration(ApplicationOnlyMapperScanConfiguration.class)
                .withBean(DataSource.class, () -> dataSource)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(ApplicationMapper.class);
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertExactRuntimeGraph(context);
                    assertThat(context).hasSingleBean(MapperScannerConfigurer.class);
                    verifyNoInteractions(dataSource);
                });
    }

    @Test
    void exposesFactoryBeanObjectTypeForNonEagerTypeResolution() {
        DataSource dataSource = mock(DataSource.class);

        contextRunner
                .withBean(SqlSessionFactory.class, () -> newSqlSessionFactory(dataSource))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    BeanDefinition definition = context.getBeanFactory()
                            .getBeanDefinition(NATIVE_MAPPER_BEAN_NAME);
                    assertThat(definition.getBeanClassName())
                            .isEqualTo(MapperFactoryBean.class.getName());
                    assertThat(definition.getAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE))
                            .isEqualTo(SmartNativeMapper.class);
                    assertThat(definition.getPropertyValues().get("mapperInterface"))
                            .isEqualTo(SmartNativeMapper.class);
                    assertThat(definition.getPropertyValues().get("addToConfig"))
                            .isEqualTo(true);
                    assertThat(definition.getPropertyValues().get("sqlSessionFactory"))
                            .isInstanceOfSatisfying(
                                    RuntimeBeanReference.class,
                                    reference -> assertThat(reference.getBeanName())
                                            .isEqualTo("sqlSessionFactory")
                            );
                    assertThat(context.getBeanFactory().getBeanNamesForType(
                            SmartNativeMapper.class,
                            false,
                            false
                    )).containsExactly(NATIVE_MAPPER_BEAN_NAME);
                    verifyNoInteractions(dataSource);
                });
    }

    @Test
    void reusesAnApplicationRegisteredMapperFactoryBean() {
        DataSource dataSource = mock(DataSource.class);

        contextRunner
                .withUserConfiguration(DirectMapperFactoryBeanConfiguration.class)
                .withBean(DataSource.class, () -> dataSource)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertThat(context.getBeanNamesForType(SmartNativeMapper.class))
                            .containsExactly(NATIVE_MAPPER_BEAN_NAME);
                    assertThat(context.getBean("&" + NATIVE_MAPPER_BEAN_NAME))
                            .isInstanceOf(MapperFactoryBean.class);
                    assertThat(context).hasSingleBean(SqlSessionFactory.class);
                    assertThat(context).doesNotHaveBean(SqlSessionTemplate.class);
                    assertExactRuntimeGraph(context);
                    verifyNoInteractions(dataSource);
                });
    }

    @Test
    void legacyInternalPackageScanReusesOneNativeMapperWithoutDuplicates() {
        DataSource dataSource = mock(DataSource.class);

        contextRunner
                .withUserConfiguration(LegacyMapperScanConfiguration.class)
                .withBean(DataSource.class, () -> dataSource)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(ApplicationMapper.class);
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertThat(context.getBeanNamesForType(SmartNativeMapper.class))
                            .containsExactly(NATIVE_MAPPER_BEAN_NAME);
                    assertExactRuntimeGraph(context);
                    verifyNoInteractions(dataSource);
                });
    }

    @Test
    void usesThePrimarySessionFactoryWhenSeveralFactoriesExist() {
        DataSource primaryDataSource = mock(DataSource.class);
        DataSource secondaryDataSource = mock(DataSource.class);

        contextRunner
                .withUserConfiguration(PrimarySessionFactoryConfiguration.class)
                .withBean("primaryDataSource", DataSource.class, () -> primaryDataSource)
                .withBean("secondaryDataSource", DataSource.class, () -> secondaryDataSource)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertExactRuntimeGraph(context);
                    assertThat(nativeMapperFactory(context).getSqlSessionFactory())
                            .isSameAs(context.getBean("primarySqlSessionFactory"));
                    verifyNoInteractions(primaryDataSource, secondaryDataSource);
                });
    }

    @Test
    void backsOffCleanlyWhenSessionFactoriesAreAmbiguous() {
        DataSource firstDataSource = mock(DataSource.class);
        DataSource secondDataSource = mock(DataSource.class);

        contextRunner
                .withUserConfiguration(AmbiguousSessionFactoryConfiguration.class)
                .withBean("firstDataSource", DataSource.class, () -> firstDataSource)
                .withBean("secondDataSource", DataSource.class, () -> secondDataSource)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SmartNativeMapper.class);
                    assertNoRuntimeGraph(context);
                    verifyNoInteractions(firstDataSource, secondDataSource);
                });
    }

    @Test
    void backsOffCleanlyWhenSqlSessionTemplatesAreAmbiguous() {
        DataSource firstDataSource = mock(DataSource.class);
        DataSource secondDataSource = mock(DataSource.class);

        contextRunner
                .withUserConfiguration(AmbiguousSessionTemplateConfiguration.class)
                .withBean("firstDataSource", DataSource.class, () -> firstDataSource)
                .withBean("secondDataSource", DataSource.class, () -> secondDataSource)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SmartNativeMapper.class);
                    assertNoRuntimeGraph(context);
                    verifyNoInteractions(firstDataSource, secondDataSource);
                });
    }

    @Test
    void usesThePrimarySqlSessionTemplateWhenSeveralTemplatesExist() {
        DataSource primaryDataSource = mock(DataSource.class);
        DataSource secondaryDataSource = mock(DataSource.class);

        contextRunner
                .withUserConfiguration(PrimarySessionTemplateConfiguration.class)
                .withBean("primaryDataSource", DataSource.class, () -> primaryDataSource)
                .withBean("secondaryDataSource", DataSource.class, () -> secondaryDataSource)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertExactRuntimeGraph(context);
                    assertThat(nativeMapperFactory(context).getSqlSessionTemplate())
                            .isSameAs(context.getBean("primarySqlSessionTemplate"));
                    verifyNoInteractions(primaryDataSource, secondaryDataSource);
                });
    }

    @Test
    void backsOffCleanlyWhenMyBatisInfrastructureIsNotRegistered() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(SmartNativeMapper.class);
            assertNoRuntimeGraph(context);
            assertThat(context).doesNotHaveBean(DataSource.class);
            assertThat(context).doesNotHaveBean(SqlSessionFactory.class);
            assertThat(context).doesNotHaveBean(SqlSessionTemplate.class);
            assertThat(context).doesNotHaveBean(PlatformTransactionManager.class);
        });
    }

    @Test
    void backsOffWhenTheOnlyExistingNativeMapperIsNotAnAutowireCandidate() {
        SmartNativeMapper nativeMapper = mock(SmartNativeMapper.class);

        contextRunner
                .withBean(
                        "nonCandidateNativeMapper",
                        SmartNativeMapper.class,
                        () -> nativeMapper,
                        definition -> definition.setAutowireCandidate(false)
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBeansOfType(SmartNativeMapper.class))
                            .containsOnlyKeys("nonCandidateNativeMapper");
                    assertNoRuntimeGraph(context);
                    verifyNoInteractions(nativeMapper);
                });
    }

    @Test
    void ignoresANonCandidateSessionFactoryAndBacksOffCleanly() {
        DataSource dataSource = mock(DataSource.class);

        contextRunner
                .withBean(
                        "nonCandidateSqlSessionFactory",
                        SqlSessionFactory.class,
                        () -> newSqlSessionFactory(dataSource),
                        definition -> definition.setAutowireCandidate(false)
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SmartNativeMapper.class);
                    assertNoRuntimeGraph(context);
                    verifyNoInteractions(dataSource);
                });
    }

    @Test
    void backsOffCleanlyWhenTheCanonicalInternalMapperNameIsIncompatible() {
        Object applicationBean = new Object();
        DataSource dataSource = mock(DataSource.class);

        contextRunner
                .withBean(NATIVE_MAPPER_BEAN_NAME, Object.class, () -> applicationBean)
                .withBean(SqlSessionFactory.class, () -> newSqlSessionFactory(dataSource))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(NATIVE_MAPPER_BEAN_NAME)).isSameAs(applicationBean);
                    assertThat(context).doesNotHaveBean(SmartNativeMapper.class);
                    assertNoRuntimeGraph(context);
                    verifyNoInteractions(dataSource);
                });
    }

    @Test
    void autoConfiguresTheExactRuntimeGraphAroundAnApplicationMapper() {
        SmartNativeMapper nativeMapper = mock(SmartNativeMapper.class);

        contextRunner
                .withBean(SmartNativeMapper.class, () -> nativeMapper)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SmartNativeMapper.class);
                    assertExactRuntimeGraph(context);
                    assertThat(context).doesNotHaveBean(DataSource.class);
                    assertThat(context).doesNotHaveBean(SqlSessionFactory.class);
                    assertThat(context).doesNotHaveBean(SqlSessionTemplate.class);
                    assertThat(context).doesNotHaveBean(PlatformTransactionManager.class);
                    assertThat(context).doesNotHaveBean(MapperScannerConfigurer.class);
                    verifyNoInteractions(nativeMapper);
                });
    }

    @Test
    void backsOffCleanlyWhenARequiredCoreTypeIsMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(SmartMapper.class))
                .withConfiguration(AutoConfigurations.of(SmartOrmAutoConfiguration.class))
                .withBean(SqlSessionFactory.class, () -> newSqlSessionFactory(mock(DataSource.class)))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SmartNativeMapper.class);
                    assertNoRuntimeGraph(context);
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
    void backsOffAsAWholeWhenAReservedRuntimeBeanNameAlreadyExists() {
        Object applicationBean = new Object();

        contextRunner
                .withBean(SmartNativeMapper.class, () -> mock(SmartNativeMapper.class))
                .withBean("smartNativeExecutor", Object.class, () -> applicationBean)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean("smartNativeExecutor")).isSameAs(applicationBean);
                    assertThat(context).doesNotHaveBean(SmartNativeExecutor.class);
                    assertThat(context).doesNotHaveBean(SmartHandlerRegistry.class);
                    assertThat(context).doesNotHaveBean(SmartAnnotationAspect.class);
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
                    assertExactRuntimeGraph(context);
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

    private static void assertExactRuntimeGraph(
            org.springframework.boot.test.context.assertj.AssertableApplicationContext context
    ) {
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
    }

    private static void assertNoRuntimeGraph(
            org.springframework.boot.test.context.assertj.AssertableApplicationContext context
    ) {
        assertThat(context).doesNotHaveBean(SmartNativeExecutor.class);
        assertThat(context).doesNotHaveBean(SmartSelectHandler.class);
        assertThat(context).doesNotHaveBean(SmartPageHandler.class);
        assertThat(context).doesNotHaveBean(SmartInsertHandler.class);
        assertThat(context).doesNotHaveBean(SmartUpdateHandler.class);
        assertThat(context).doesNotHaveBean(SmartDeleteHandler.class);
        assertThat(context).doesNotHaveBean(SmartHandlerRegistry.class);
        assertThat(context).doesNotHaveBean(SmartAnnotationAspect.class);
    }

    private static void assertNoDatabaseConnections(DataSource dataSource) {
        try {
            verify(dataSource, never()).getConnection();
            verify(dataSource, never()).getConnection(anyString(), anyString());
        } catch (SQLException exception) {
            throw new AssertionError("Verifying DataSource interaction should not throw", exception);
        }
    }

    private static MapperFactoryBean<?> nativeMapperFactory(
            org.springframework.boot.test.context.assertj.AssertableApplicationContext context
    ) {
        return context.getBean(
                "&" + NATIVE_MAPPER_BEAN_NAME,
                MapperFactoryBean.class
        );
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    static class DiscoveryConfiguration {

        @Bean
        SmartNativeMapper smartNativeMapper() {
            return mock(SmartNativeMapper.class);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @MapperScan(basePackageClasses = ApplicationMapper.class, annotationClass = Mapper.class)
    static class ApplicationOnlyMapperScanConfiguration {

        @Bean
        SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
            return newSqlSessionFactory(dataSource);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class DirectMapperFactoryBeanConfiguration {

        @Bean
        SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
            return newSqlSessionFactory(dataSource);
        }

        @Bean
        MapperFactoryBean<SmartNativeMapper> smartNativeMapper(SqlSessionFactory sqlSessionFactory) {
            MapperFactoryBean<SmartNativeMapper> mapperFactoryBean =
                    new MapperFactoryBean<>(SmartNativeMapper.class);
            mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
            return mapperFactoryBean;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @MapperScan(
            basePackageClasses = {ApplicationMapper.class, SmartNativeMapper.class}
    )
    static class LegacyMapperScanConfiguration {

        @Bean
        SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
            return newSqlSessionFactory(dataSource);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class PrimarySessionFactoryConfiguration {

        @Bean
        @Primary
        SqlSessionFactory primarySqlSessionFactory(DataSource primaryDataSource) {
            return newSqlSessionFactory(primaryDataSource);
        }

        @Bean
        SqlSessionFactory secondarySqlSessionFactory(DataSource secondaryDataSource) {
            return newSqlSessionFactory(secondaryDataSource);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class AmbiguousSessionFactoryConfiguration {

        @Bean
        SqlSessionFactory firstSqlSessionFactory(DataSource firstDataSource) {
            return newSqlSessionFactory(firstDataSource);
        }

        @Bean
        SqlSessionFactory secondSqlSessionFactory(DataSource secondDataSource) {
            return newSqlSessionFactory(secondDataSource);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class AmbiguousSessionTemplateConfiguration {

        @Bean
        SqlSessionFactory firstSqlSessionFactory(DataSource firstDataSource) {
            return newSqlSessionFactory(firstDataSource);
        }

        @Bean
        SqlSessionFactory secondSqlSessionFactory(DataSource secondDataSource) {
            return newSqlSessionFactory(secondDataSource);
        }

        @Bean
        SqlSessionTemplate firstSqlSessionTemplate(SqlSessionFactory firstSqlSessionFactory) {
            return new SqlSessionTemplate(firstSqlSessionFactory);
        }

        @Bean
        SqlSessionTemplate secondSqlSessionTemplate(SqlSessionFactory secondSqlSessionFactory) {
            return new SqlSessionTemplate(secondSqlSessionFactory);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class PrimarySessionTemplateConfiguration {

        @Bean
        SqlSessionFactory primarySqlSessionFactory(DataSource primaryDataSource) {
            return newSqlSessionFactory(primaryDataSource);
        }

        @Bean
        SqlSessionFactory secondarySqlSessionFactory(DataSource secondaryDataSource) {
            return newSqlSessionFactory(secondaryDataSource);
        }

        @Bean
        @Primary
        SqlSessionTemplate primarySqlSessionTemplate(
                SqlSessionFactory primarySqlSessionFactory
        ) {
            return new SqlSessionTemplate(primarySqlSessionFactory);
        }

        @Bean
        SqlSessionTemplate secondarySqlSessionTemplate(
                SqlSessionFactory secondarySqlSessionFactory
        ) {
            return new SqlSessionTemplate(secondarySqlSessionFactory);
        }
    }

    private static SqlSessionFactory newSqlSessionFactory(DataSource dataSource) {
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();
        configuration.setEnvironment(new Environment(
                "starter-test",
                new JdbcTransactionFactory(),
                dataSource
        ));
        return new DefaultSqlSessionFactory(configuration);
    }
}
