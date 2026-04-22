package io.github.forgottenlab.smartorm.demo.mapper;

import io.github.forgottenlab.smartorm.annotations.JoinType;
import io.github.forgottenlab.smartorm.annotations.SelectResultType;
import io.github.forgottenlab.smartorm.annotations.SmartDelete;
import io.github.forgottenlab.smartorm.annotations.SmartInsert;
import io.github.forgottenlab.smartorm.annotations.SmartJoin;
import io.github.forgottenlab.smartorm.annotations.SmartPage;
import io.github.forgottenlab.smartorm.annotations.SmartSelect;
import io.github.forgottenlab.smartorm.annotations.SmartUpdate;
import io.github.forgottenlab.smartorm.demo.dto.UserProfileDTO;
import io.github.forgottenlab.smartorm.demo.dto.UserSimpleDTO;
import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.mapper.SmartMapper;
import io.github.forgottenlab.smartorm.model.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details Demo 用户数据访问层接口
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public interface UserMapper extends SmartMapper<User> {

    /* ============================================================
     *                         SmartSelect
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

    /** 查询一条用户记录（限制返回条数为1） */
    @SmartSelect(limit = 1)
    User findUserOrderByAgeLimit1();

    /** 根据用户名进行模糊匹配查询 */
    @SmartSelect(
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

    /** 查询所有用户（显式声明返回实体列表） */
    @SmartSelect(resultType = SelectResultType.ENTITY_LIST)
    List<User> findAllUsersAsEntity();

    /** 查询所有用户（返回 Map 列表） */
    @SmartSelect(resultType = SelectResultType.MAP_LIST)
    List<Map<String, Object>> findAllUsersAsMap();

    /** 查询所有用户（返回 DTO 列表） */
    @SmartSelect(
            fields = {"id", "user_name"},
            resultType = SelectResultType.DTO_LIST
    )
    List<UserSimpleDTO> findAllUsersAsDTO();

    /** 用户与资料 LEFT JOIN 查询，适用于用户列表场景 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email"
            },
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.LEFT
                    )
            }
    )
    List<Map<String, Object>> selectUserWithProfileLeft();

    /** 用户与资料 INNER JOIN 查询，适用于仅关注完整用户数据的场景 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email"
            },
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.INNER
                    )
            }
    )
    List<Map<String, Object>> selectUserWithProfileInner();

    /** 查询有邮箱的活跃用户 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email"
            },
            where = "user.status = #{0}",
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.INNER
                    )
            }
    )
    List<Map<String, Object>> selectActiveUsersWithEmail(Integer status);

    /** 查询用户与邮箱信息，并映射为 DTO */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email"
            },
            resultType = SelectResultType.DTO_LIST,
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.LEFT
                    )
            }
    )
    List<UserProfileDTO> selectUserProfileDTO();

    /** 查询用户与资料信息（原生 SQL 路径验证场景） */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email"
            },
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.LEFT
                    )
            }
    )
    List<Map<String, Object>> selectUserWithProfileNative();

    /** 根据状态与邮箱模糊条件查询用户资料 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email"
            },
            where = "user.status = #{0} AND user_profile.email LIKE #{1}",
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.INNER
                    )
            }
    )
    List<Map<String, Object>> selectByStatusAndEmailLike(
            Integer status,
            String emailLike
    );

    /** 使用显式 ON 条件进行 JOIN 查询 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email"
            },
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.LEFT,
                            on = "user.id = user_profile.user_id"
                    )
            }
    )
    List<Map<String, Object>> selectUserWithCustomOn();

    /** 使用自动推断 ON 条件进行 JOIN 查询 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email"
            },
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.INNER
                    )
            }
    )
    List<Map<String, Object>> selectUserWithAutoOn();

    /** JOIN 场景下按用户年龄排序查询 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name"
            },
            orderBy = "user.age",
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.LEFT
                    )
            }
    )
    List<Map<String, Object>> selectUserOrderByAge();

    /** JOIN 场景下限制返回条数为1 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name"
            },
            limit = 1,
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.LEFT
                    )
            }
    )
    List<Map<String, Object>> selectUserLimit1();

    /** 用户、资料与角色三表关联查询（LEFT JOIN） */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email",
                    "user_role.role_code",
                    "user_role.role_name"
            },
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.LEFT
                    ),
                    @SmartJoin(
                            table = "user_role",
                            type = JoinType.LEFT
                    )
            }
    )
    List<Map<String, Object>> selectUserWithProfileAndRole();

    /** 用户、资料与角色三表关联查询（INNER JOIN） */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_profile.email",
                    "user_role.role_code"
            },
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.INNER
                    ),
                    @SmartJoin(
                            table = "user_role",
                            type = JoinType.INNER
                    )
            }
    )
    List<Map<String, Object>> selectUserWithProfileAndRoleInner();

    /** 根据状态与角色编码查询活跃用户 */
    @SmartSelect(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user_role.role_code"
            },
            where = "user.status = #{0} AND user_role.role_code = #{1}",
            join = {
                    @SmartJoin(
                            table = "user_profile",
                            type = JoinType.LEFT
                    ),
                    @SmartJoin(
                            table = "user_role",
                            type = JoinType.INNER
                    )
            }
    )
    List<Map<String, Object>> selectActiveUserByRole(
            Integer status,
            String roleCode
    );

    /* ============================================================
     *                          SmartPage
     * ============================================================ */

    /** 根据用户状态进行分页查询（按创建时间降序，每页20条） */
    @SmartPage(
            fields = {"id", "user_name", "age", "create_time", "status"},
            where = "status = #{0}",
            orderBy = "create_time",
            desc = true,
            pageSize = 20
    )
    PageResult<User> findUsersByStatusPage(Integer status);

    /** 查询年龄在指定范围内的用户分页列表（按年龄升序，每页15条） */
    @SmartPage(
            where = "age BETWEEN #{0} AND #{1}",
            orderBy = "age",
            pageSize = 15
    )
    PageResult<User> findUsersByAgeRangePage(Integer minAge, Integer maxAge);

    /** 根据关键词和状态进行分页查询（按创建时间降序） */
    @SmartPage(
            fields = {"id", "user_name", "age", "status", "create_time"},
            where = "user_name LIKE CONCAT('%', #{0}, '%') AND status = #{1}",
            orderBy = "create_time",
            desc = true,
            page = 2,
            pageSize = 5
    )
    PageResult<User> searchUsersPage(String keyword, Integer status);

    /** 用户与资料分页关联查询 */
    @SmartPage(
            fields = {
                    "user.id",
                    "user.user_name",
                    "user.age",
                    "user.status",
                    "email"
            },
            where = "user.status = #{0}",
            orderBy = "user.create_time",
            desc = true,
            page = 2,
            pageSize = 5,
            join = {
                    @SmartJoin(
                            type = JoinType.LEFT,
                            table = "user_profile"
                    )
            }
    )
    PageResult<Map<String, Object>> findUserWithProfilePage(Integer status);

    /* ============================================================
     *                         SmartInsert
     * ============================================================ */

    /** 插入一个包含用户名、年龄和状态的完整新用户 */
    @SmartInsert(
            fields = {"user_name", "age", "status"},
            values = {"#{0}", "#{1}", "#{2}"}
    )
    int insertUser(String userName, Integer age, Integer status);

    /** 插入一个仅包含用户名和年龄的简单新用户 */
    @SmartInsert(
            fields = {"user_name", "age"},
            values = {"#{0}", "#{1}"}
    )
    int insertSimpleUser(String userName, Integer age);

    /** 插入一个用户，并显式设置默认状态为1 */
    @SmartInsert(
            fields = {"user_name", "age", "status"},
            values = {"#{0}", "#{1}", "1"}
    )
    int insertUserWithDefaultStatus(String userName, Integer age);

    /* ============================================================
     *                         SmartUpdate
     * ============================================================ */

    /** 根据用户ID更新用户名和年龄 */
    @SmartUpdate(
            fields = {"user_name", "age"},
            values = {"#{0}", "#{1}"},
            where = "id = #{2}"
    )
    int updateUserNameAndAge(String userName, Integer age, Long id);

    /** 根据用户ID更新状态 */
    @SmartUpdate(
            fields = {"status"},
            values = {"#{0}"},
            where = "id = #{1}"
    )
    int updateUserStatus(Integer status, Long id);

    /** 根据用户ID递增年龄字段 */
    @SmartUpdate(
            fields = {"age"},
            values = {"age + #{0}"},
            where = "id = #{1}"
    )
    int incrementAge(Integer increment, Long id);

    /** 批量更新年龄大于指定值的用户状态 */
    @SmartUpdate(
            fields = {"status"},
            values = {"#{0}"},
            where = "age > #{1}"
    )
    int updateStatusForOldUsers(Integer status, Integer minAge);

    /* ============================================================
     *                         SmartDelete
     * ============================================================ */

    /** 根据用户ID删除指定用户 */
    @SmartDelete(where = "id = #{0}")
    int deleteUserById(Long id);

    /** 删除状态匹配且年龄小于指定值的年轻非活跃用户 */
    @SmartDelete(where = "status = #{0} AND age < #{1}")
    int deleteInactiveYoungUsers(Integer status, Integer maxAge);

    /** 根据精确用户名删除用户 */
    @SmartDelete(where = "user_name = #{0}")
    int deleteUserByName(String userName);
}