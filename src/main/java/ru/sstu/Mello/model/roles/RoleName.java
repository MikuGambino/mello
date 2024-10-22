package ru.sstu.Mello.model.roles;

import lombok.Getter;

@Getter
public enum RoleName {
    ROLE_USER("user"),
    ROLE_ADMIN("admin");

    private final String roleName;

    RoleName(String roleName) {
        this.roleName = roleName;
    }
}
