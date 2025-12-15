package com.smartorm.exception;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details Smart ORM 统一异常<br>
 * @CreateDate 2025/12/12
 * @LastModified 2025/12/12
 * @VersionHistory [版本历史]
 */
// TODO：后续所有 Handler / AOP / 构建器遇到错误都抛出此异常然后进行统一封装方便定位异常是此工具中的异常
// 模仿远程调用OpenFeign执行过程中出现异常后会统一进行封装返回一个DecodeException
// 有点是可以让用户准确的定位到问题发生在工具中，但是这也对全局拦截器的设计有一定的影响，就需要引入DecodeException来统一处理异常，但是我像改善这点（但没什么思路）
public class SmartOrmException extends RuntimeException {

    public SmartOrmException(String message) {
        super(message);
    }

    public SmartOrmException(String message, Throwable cause) {
        super(message, cause);
    }
}