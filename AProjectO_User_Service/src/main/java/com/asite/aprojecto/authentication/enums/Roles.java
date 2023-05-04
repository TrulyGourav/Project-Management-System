package com.asite.aprojecto.authentication.enums;

import java.util.*;

public enum Roles {
    ADMIN(new HashSet<>(Arrays.asList(Permissions.MANAGE_PROJECTS, Permissions.MANAGE_USERS,Permissions.MANAGE_USERS,
            Permissions.MANAGE_ROLES,Permissions.MANAGE_TASKS,Permissions.MANAGE_SUB_TASKS,Permissions.CAN_VIEW_REPORTS,
            Permissions.CAN_CREATE_PROJECT,Permissions.CAN_VIEW_ALL_PROJECT))),
    PROJECT_MANAGER(new HashSet<>(Arrays.asList(Permissions.MANAGE_PROJECTS, Permissions.MANAGE_TASKS,
            Permissions.MANAGE_SUB_TASKS))),
    USER;

    private final Set<Permissions> permissions;
    Roles() {
        permissions = null;
    }
    Roles(Set<Permissions> permissions) {
        this.permissions = permissions;
    }
    public Set<Permissions> getPermissions() {
        return permissions;
    }
}
