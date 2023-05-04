package com.asite.apo.api;

public class PermissionConstants {
    public static final String MANAGE_USER="hasAuthority('MANAGE_USERS')";
    public static final String MANAGE_PROJECT="hasAuthority('MANAGE_PROJECTS')";
    public static final String MANAGE_ROLES="hasAuthority('MANAGE_ROLES')";
    public static final String MANAGE_TASKS="hasAuthority('MANAGE_TASKS')";
    public static final String MANAGE_SUB_TASKS="hasAuthority('MANAGE_SUB_TASKS')";
    public static final String CAN_VIEW_REPORTS="hasAuthority('CAN_VIEW_REPORTS')";
    public static final String CAN_CREATE_PROJECT="hasAuthority('CAN_CREATE_PROJECT')";
    public static final String CAN_VIEW_ALL_PROJECT="hasAuthority('CAN_VIEW_ALL_PROJECT')";
}
