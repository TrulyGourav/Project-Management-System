package com.asite.apo.api;

public final class ProjectServiceURI {


    private ProjectServiceURI(){}
    public static final String TASK_API = "/project/task";
    public static final String GET_TASK_BY_USER_URI="/user";
    public static final String GET_ALL_TASKS_URI="/getAllTask";
    public static final String UPDATE_TASK_STATUS_URI="/updateStatus";
    public static final String GET_TASK_BY_PROJECT_URI="/list";
    public static final String CREATE_TASK_URI="/add";
    public static final String UPDATE_TASK_URI="/update";
    public static final String DELETE_TASK_URI="/delete";
    public static final String GET_TASK_ATTACHMENT_BY_ID="/{tId}/attachment";
    public static final String DELETE_TASK_ATTACHMENT_BY_ID="/{tId}/attachment/delete";
    public static final String GET_TASK_BY_USER_PROJECT_URI = "/view";
}