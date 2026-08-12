package io.github.forgottenlab.smartorm.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartORM Demo 启动类
 * @CreateDate 2026/04/22
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootApplication(scanBasePackages = "io.github.forgottenlab.smartorm")
public class SmartOrmDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartOrmDemoApplication.class, args);
    }
}