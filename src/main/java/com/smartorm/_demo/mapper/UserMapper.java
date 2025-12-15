package com.smartorm._demo.mapper;

import com.smartorm._demo.entiey.User;
import com.smartorm.mapper.SmartMapper;
import com.smartorm.annotation.*;
import com.smartorm.model.PageResult;

import java.util.List;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details UserMapper - 用户数据访问层接口
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 */
public interface UserMapper extends SmartMapper<User> {

    /* ============================================================
     *                      SmartSelect
     * ============================================================ */

    /** 查询年龄大于指定值且状态匹配的活跃用户列表（按创建时间降序，最多10条） */
    @SmartSelect(
            fields = {"id", "user_name", "age", "status"},
            where = "age > #{0} AND status = #{1}",
            orderBy = "create_time",
            desc = true,
            limit = 10
    )
    List<User> findActiveUsersByAge(Integer minAge, Integer status);

    /** 根据年龄倒序查询第一条用户信息 */
    // TODO : 错误
    @SmartSelect(
//            orderBy = "age",
//            desc = true,
            limit = 1
    )
    User findUserOrderByAgeLimit1();

    /** 根据用户名字段进行模糊匹配查询 */
    // TODO:后续待优化CONCAT('%', #{0}, '%')应该无需用户自己输入,用户只需要正常写sql语句例如 like '%xxx%'然后工具自己解析
    @SmartSelect(
//            fields = {"id", "user_name", "age"}, // 不写默认是全部字段
            where = "user_name LIKE CONCAT('%', #{0}, '%')",
            orderBy = "age"
    )
    List<User> findUsersByName(String userName);

    /** 根据ID精确查询单个用户 */
    @SmartSelect(
            where = "id = #{0}",
            limit = 1
    )
    User findUserById(Long id);

    /** 多条件组合查询：年龄、状态及用户名的模糊匹配 */
    @SmartSelect(
            fields = {"id", "user_name", "age", "status"},
            where = "age > #{0} AND status = #{1} AND user_name LIKE CONCAT('%', #{2}, '%')",
            orderBy = "create_time"
    )
    List<User> findComplexUsers(Integer minAge, Integer status, String userName);

    /** 查询状态匹配且在最近30天内有创建记录的活跃用户 */
    @SmartSelect(
            where = "status = #{0} AND create_time > DATE_SUB(NOW(), INTERVAL 30 DAY)"
    )
    List<User> findRecentActiveUsers(Integer status);

    /* ============================================================
     *                      SmartPage
     * ============================================================ */

    /** 根据用户状态进行分页查询（按创建时间降序，每页20条）*/
    @SmartPage(
            fields = {"id", "user_name", "age", "create_time"},
            where = "status = #{0}",
            orderBy = "create_time",
            desc = true,
            pageSize = 20
    )
    PageResult<User> findUsersByStatusPage(Integer status);

    /** 查询年龄在指定范围内的用户分页列表（按年龄升序，每页15条，从第1页开始） */
    @SmartPage(
            where = "age BETWEEN #{0} AND #{1}",
            orderBy = "age",
            page = 1,
            pageSize = 15
    )
    PageResult<User> findUsersByAgeRangePage(Integer minAge, Integer maxAge);

    /** 根据关键词（模糊匹配用户名）和状态进行分页查询（按创建时间降序，每页10条，从第1页开始） */
    @SmartPage(
            fields = {"id", "user_name", "age", "status", "create_time"},
            where = "user_name LIKE CONCAT('%', #{0}, '%') AND status = #{1}",
            orderBy = "create_time",
            desc = true,
            page = 1,
            pageSize = 10
    )
    PageResult<User> searchUsersPage(String keyword, Integer status);


    /* ============================================================
     *                      SmartInsert
     * ============================================================ */

    /** 插入一个包含用户名、年龄和状态的完整新用户 */
    @SmartInsert(
            fields = {"user_name", "age", "status"},
            values = {"#{0}", "#{1}", "#{2}"}
    )
    int insertUser(String userName, Integer age, Integer status);

    /** 插入一个仅包含用户名和年龄的简单新用户（状态字段依赖数据库默认值） */
    @SmartInsert(
            fields = {"user_name", "age"},
            values = {"#{0}", "#{1}"}
    )
    int insertSimpleUser(String userName, Integer age);

    /** 插入一个用户，并为其状态字段显式设置默认值 1 */
    @SmartInsert(
            fields = {"user_name", "age", "status"},
            values = {"#{0}", "#{1}", "1"}
    )
    int insertUserWithDefaultStatus(String userName, Integer age);


    /* ============================================================
     *                      SmartUpdate
     * ============================================================ */

    /** 根据用户ID，更新其用户名和年龄 */
    @SmartUpdate(
            fields = {"user_name", "age"},
            values = {"#{0}", "#{1}"},
            where = "id = #{2}"
    )
    int updateUserNameAndAge(String userName, Integer age, Long id);

    /** 根据用户ID，更新其状态 */
    @SmartUpdate(
            fields = {"status"},
            values = {"#{0}"},
            where = "id = #{1}"
    )
    int updateUserStatus(Integer status, Long id);

    /** 根据用户ID，将其年龄字段递增指定的数值（使用 SQL 表达式） */
    @SmartUpdate(
            fields = {"age"},
            values = {"age + #{0}"},
            where = "id = #{1}"
    )
    int incrementAge(Integer increment, Long id);

    /** 将年龄大于指定值的所有用户的状态批量更新为新值 */
    @SmartUpdate(
            fields = {"status"},
            values = {"#{0}"},
            where = "age > #{1}"
    )
    int updateStatusForOldUsers(Integer status, Integer minAge);


    /* ============================================================
     *                      SmartDelete
     * ============================================================ */

    /** 根据用户ID删除指定用户 */
    @SmartDelete(where = "id = #{0}")
    int deleteUserById(Long id);

    /** 删除状态匹配且年龄小于指定值的年轻非活跃用户 */
    @SmartDelete(where = "status = #{0} AND age < #{1}")
    int deleteInactiveYoungUsers(Integer status, Integer maxAge);

    /** 根据精确的用户名删除用户 */
    @SmartDelete(where = "user_name = #{0}")
    int deleteUserByName(String userName);

}
