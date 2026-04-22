package io.github.forgottenlab.smartorm.demo.dto;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 用户简要信息 DTO
 * @CreateDate 2025/12/20
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class UserSimpleDTO {

    private Long id;

    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserSimpleDTO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                '}';
    }
}