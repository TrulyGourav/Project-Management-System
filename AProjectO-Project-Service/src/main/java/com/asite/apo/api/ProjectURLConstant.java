package com.asite.apo.api;

public final class ProjectURLConstant {
    ProjectURLConstant(){};
    public final static String PROJECT_API = "/project";
    public final static String GET_PROJECT_BY_USER_ID_URL = "/user";

    public static final String TASK_API = "/project/task";
    public static final String GET_TASK_BY_USER_URI="/user";
    public static final String GET_TASK_BY_PROJECT_URI="/list";


    public static final String SUB_TASK_API = "/project/subtask";
    public static final String CREATE_SUB_TASK_URI = "/add";
    public static final String UPDATE_SUB_TASK_URI = "/update";
    public static final String DELETE_SUB_TASK_URI="/delete";
    public static final String GET_SUB_TASK_LIST_BY_TASK_URI = "/task";
    public static final String GET_SUB_TASK_LIST_BY_USER_URI = "/user";
    public static final String GET_SUB_TASK_LIST_BY_PROJECT_URI = "/user";
    public static final String  GET_SUB_TASK_LIST_BY_USER_AND_TASK_URI = "/userandtask";
    public static final String GET_SUB_TASK_LIST_BY_USER_AND_PROJECT_URI = "/userandproject";
    public static final String GET_SUBTASK_ATTACHMENT_BY_ID="/{sId}/attachment";
    public static final String DELETE_SUBTASK_ATTACHMENT_BY_ID="/{sId}/attachment/delete";
    public static final String COMMENT_API = "/project/comment";
    
    public static final String GET_COMMENTS_BY_COMMENT_ID = "/getcomment";
    public static final String GET_COMMENTS_BY_TASK_ID = "/taskcomments";
    public static final String GET_COMMENTS_BY_SUB_TASK_ID = "/subtaskcomments";

    public static final String CREATE_COMMENT = "/create";
    public static final String UPDATE_COMMENT = "/update";
    public static final String DELETE_COMMENT = "/delete";
    
    
    public static final String WORKLOG_API = "project/worklog";
    public static final String GET_WORKLOG_BY_VERSION_ID = "/getbyproject";
    public static final String GET_WORKLOG_BY_TASK_ID = "/getbytask";
    public static final String GET_WORKLOG_BY_SUB_TASK_ID = "/getbysubtask";
    public static final String GET_WORKLOG_BY_USER_ID = "/getbyuser";
    public static final String GET_WORKLOG_OF_USER_BY_PROJECT = "/getbyprojectofuser";
    public static final String GET_WORKLOG_OF_USER_BY_TASK = "/getbytaskofuser";
    public static final String GET_WORKLOG_OF_USER_BY_SUB_TASK = "/getbysubtaskofuser";
    public static final String GET_WORKLOG_OF_TASK_BY_DATE = "/gettaskbydate";
    public static final String GET_WORKLOG_OF_PROJECT_BY_DATE = "/getprojectbydate";
    public static final String GET_WORKLOG_OF_SUBTASK_BY_DATE = "/getsubtaskbydate";
    public static final String GET_TOTAL_HOURS_OF_PROJECT = "/gettotalhours";
    public static final String GET_TOTAL_HOURS_OF_PROJECT_BY_TASK = "/gettotalhoursbytask";
    public static final String GET_TOTAL_HOURS_OF_PROJECT_BY_SUBTASK = "/gettotalhoursbysubtask";
    public static final String GET_TOTAL_HOURS_OF_USER_BY_DATE_ON_TASK = "/gettotalhoursbydate";
    public static final String GET_TOTAL_HOURS_OF_USER_BY_TASK = "/gettotalhoursofuserbytask";
    public static final String GET_TOTAL_HOURS_OF_USER_BY_DATE_ON_PROJECT = "/gettotalhoursbydateonproject";
    public static final String GET_TOTAL_HOURS_OF_USER_BY_DATE_ON_SUB_TASK = "/gettotalhoursbydateonsubtask";
    public static final String GET_STATUS = "/getstatus";
    public static final String GET_INTERVAL_OF_PROJECT_BY_USER = "/getintervalofprojectbyuser";
    public static final String GET_INTERVAL_OF_TASK_BY_USER = "/getintervaloftaskbyuser";
    public static final String GET_INTERVAL_OF_SUBTASK_BY_USER = "/getintervalofsubtaskbyuser";
}
