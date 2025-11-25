package com.smartorm._demo.mapper;

import com.smartorm._demo.entiey.User;
import com.smartorm.annotation.*;
import com.smartorm.mapper.SmartMapper;
import com.smartorm.model.PageResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 用户数据访问层接口
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
@Mapper
public interface UserMapper extends SmartMapper<User> {

    // TODO:待优化方法的调用的代码

    /**
     * 查询 -- 活跃用户列表（按年龄筛选）
     *
     * @param minAge 最小年龄
     * @param status 用户状态
     * @return 用户列表
     */
    @SmartSelect(
            fields = {"id", "user_name", "age", "status"},
            where = "age > #{0} AND status = #{1}",
            orderBy = "create_time",
            desc = true,
            limit = 10
    )
    default List<User> findActiveUsersByAge(Integer minAge, Integer status) {
        return findByAnnotation("findActiveUsersByAge", new Object[]{minAge, status});
    }

    /**
     * 查询 -- 用户列表（按用户名模糊匹配）
     *
     * @param userName 用户名（支持模糊匹配）
     * @return 用户列表
     */
    @SmartSelect(
            fields = {"id", "user_name", "age"},
            where = "user_name LIKE CONCAT('%', #{0}, '%')",
            orderBy = "age"
    )
    default List<User> findUsersByName(String userName) {
        return findByAnnotation("findUsersByName", new Object[]{userName});
    }

    /**
     * 查询 -- 用户信息（按ID）
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @SmartSelect(
            where = "id = #{0}",
            limit = 1
    )
    default User findUserById(Long id) {
        return findOneByAnnotation("findUserById", new Object[]{id});
    }

    /**
     * 分页查询 -- 用户列表（按状态）
     *
     * @param status 用户状态
     * @return 分页结果
     */
    @SmartPage(
            fields = {"id", "user_name", "age", "create_time"},
            where = "status = #{0}",
            orderBy = "create_time",
            desc = true,
            pageSize = 20
    )
    default PageResult<User> findUsersByStatusPage(Integer status) {
        return findByPageAnnotation("findUsersByStatusPage", new Object[]{status});
    }

    /**
     * 分页查询 -- 用户列表（按年龄范围）
     *
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 分页结果
     */
    @SmartPage(
            where = "age BETWEEN #{0} AND #{1}",
            orderBy = "age",
            page = 1,
            pageSize = 15
    )
    default PageResult<User> findUsersByAgeRangePage(Integer minAge, Integer maxAge) {
        return findByPageAnnotation("findUsersByAgeRangePage", new Object[]{minAge, maxAge});
    }

    /**
     * 新增 -- 用户信息
     *
     * @param userName 用户名
     * @param age 年龄
     * @param status 状态
     * @return 影响行数
     */
    @SmartInsert(
            fields = {"user_name", "age", "status"},
            values = {"#{0}", "#{1}", "#{2}"}
    )
    default int insertUser(String userName, Integer age, Integer status) {
        return insertByAnnotation("insertUser", new Object[]{userName, age, status});
    }

    /**
     * 新增 -- 简单用户信息（只包含用户名和年龄）
     *
     * @param userName 用户名
     * @param age 年龄
     * @return 影响行数
     */
    @SmartInsert(
            fields = {"user_name", "age"},
            values = {"#{0}", "#{1}"}
    )
    default int insertSimpleUser(String userName, Integer age) {
        return insertByAnnotation("insertSimpleUser", new Object[]{userName, age});
    }


    /**
     * 更新 -- 用户姓名和年龄
     *
     * @param userName 新用户名
     * @param age 新年龄
     * @param id 用户ID
     * @return 影响行数
     */
    @SmartUpdate(
            fields = {"user_name", "age"},
            values = {"#{0}", "#{1}"},
            where = "id = #{2}"
    )
    default int updateUserNameAndAge(String userName, Integer age, Long id) {
        User user = new User();
        user.setUserName(userName);
        user.setAge(age);
        return updateByAnnotation("updateUserNameAndAge", user, new Object[]{userName, age, id});
    }

    /**
     * 更新 -- 用户状态
     *
     * @param status 新状态
     * @param id 用户ID
     * @return 影响行数
     */
    @SmartUpdate(
            fields = {"status"},
            values = {"#{0}"},
            where = "id = #{1}"
    )
    default int updateUserStatus(Integer status, Long id) {
        User user = new User();
        user.setStatus(status);
        return updateByAnnotation("updateUserStatus", user, new Object[]{status, id});
    }

    /**
     * 更新 -- 递增用户年龄（特殊）
     *
     * @param increment 递增数值
     * @param id 用户ID
     * @return 影响行数
     */
    @SmartUpdate(
            fields = {"age"},
            values = {"age + #{0}"},
            where = "id = #{1}"
    )
    default int incrementAge(Integer increment, Long id) {
        User user = new User();
        return updateByAnnotation("incrementAge", user, new Object[]{increment, id});
    }

    /**
     * 删除 -- 用户（按ID）
     *
     * @param id 用户ID
     * @return 影响行数
     */
    @SmartDelete(where = "id = #{0}")
    default int deleteUserById(Long id) {
        return deleteByAnnotation("deleteUserById", new Object[]{id});
    }

    /**
     * 删除 -- 非活跃的年轻用户
     *
     * @param status 状态条件
     * @param maxAge 最大年龄
     * @return 影响行数
     */
    @SmartDelete(where = "status = #{0} AND age < #{1}")
    default int deleteInactiveYoungUsers(Integer status, Integer maxAge) {
        return deleteByAnnotation("deleteInactiveYoungUsers", new Object[]{status, maxAge});
    }

    /**
     * 删除 -- 用户（按用户名）
     *
     * @param userName 用户名
     * @return 影响行数
     */
    @SmartDelete(where = "user_name = #{0}")
    default int deleteUserByName(String userName) {
        return deleteByAnnotation("deleteUserByName", new Object[]{userName});
    }
}