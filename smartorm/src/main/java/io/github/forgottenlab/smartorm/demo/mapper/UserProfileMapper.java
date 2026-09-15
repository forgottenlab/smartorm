package io.github.forgottenlab.smartorm.demo.mapper;

import io.github.forgottenlab.smartorm.annotations.SmartInsert;
import io.github.forgottenlab.smartorm.demo.entity.UserProfile;
import io.github.forgottenlab.smartorm.mapper.SmartMapper;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 用户资料 Mapper
 * @CreateDate 2025/12/16
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public interface UserProfileMapper extends SmartMapper<UserProfile> {

    @SmartInsert(
            fields = {"user_id", "email"},
            values = {"#{0}", "#{1}"}
    )
    void insertProfile(Long userId, String email);
}