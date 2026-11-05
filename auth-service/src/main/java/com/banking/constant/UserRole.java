package com.banking.constant;

import lombok.Getter;

import java.util.Set;

@Getter
public enum UserRole {

    ROLE_CUSTOMER (Set.of(UserPermissions.DEPOSIT, UserPermissions.WITHDRAW, UserPermissions.TRANSFER, UserPermissions.VIEW_OWN_ACCOUNT)),
    ROLE_ADMIN (Set.of(UserPermissions.FREEZE_ACCOUNT, UserPermissions.VIEW_ALL_USERS)),
    ROLE_SUPPORT (Set.of(UserPermissions.VIEW_ALL_TRANSACTIONS));

    private final Set<UserPermissions> permissions;

    UserRole(Set<UserPermissions> permissions) {
        this.permissions = permissions;
    }

}
