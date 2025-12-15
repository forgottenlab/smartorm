package com.smartorm.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:1349801439@qq.com">王鹤然</a>
 * @Details [简述]
 * @CreateDate 2025/12/15
 * @LastModified 2025/12/15
 * @VersionHistory [版本历史]
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartQuery {
    // TODO:按理来说分页是在查询的基础上完成的，所以我想有一个基础的select直接复用然后分页的时候去添加属性
    // 但是好像注解之间不能继承，就算继承了专门设置了ElementType.ANNOTATION_TYPE, ElementType.METHOD子注解也获取不到属性
    // 因为之前有见过在SpringBoot中其实@RestController注解就等于@Controller + @ResponseBody
    // 但是就是实现的逻辑好像并不是简单的继承而是Spring 扫描时递归解析元注解（好像是“元注解（meta-annotation）机制”）
}
