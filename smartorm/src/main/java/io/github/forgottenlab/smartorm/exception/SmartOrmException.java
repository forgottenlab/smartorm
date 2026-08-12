package io.github.forgottenlab.smartorm.exception;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartORM 统一异常类型
 * @CreateDate 2025/12/12
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartOrmException extends RuntimeException {

    public SmartOrmException(String message) {
        super(message);
    }

    public SmartOrmException(String message, Throwable cause) {
        super(message, cause);
    }
}