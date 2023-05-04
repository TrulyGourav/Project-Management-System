package com.asite.aprojecto.authentication.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserServiceURI {
    public static final String USER_NAME_BY_USERID = "/user/api/name/";
    public static final String AUTH_API = "/user/api/auth";
    public static final String USER_API = "/user/api/user";
    public static final String ROLE_API="/user/api/role";
    public static final String PERMISSION_API="/user/api/permissions";
    public static final String PROJECT_API = "/user/api/project/";
    public static final String ADD_USERS_TO_PROJECT_API = "{project_id}/users";
    public static final String LOGIN_API="/login";
    public static final String REGISTER_API="/register";
    public static final String FORGOT_PASSWORD_OTP="/send-otp/{toEmail}";
    public static final String OTP_IS_VALID="/validate-otp/{toEmail}/{otp}";
    public static final String UPDATE_USER="/update";
    public static final String UPDATE_ME="/update/me";
    public static final String USER_ALL="/all";
    public static final String USER_ME="/me";
    public static final String ROLE_ALL="/all";
    public static final String GET_ALL_PERMISSIONS_FROM_ROLE="/permissions";
    public static final String GET_ALL_PERMISSIONS="/all";
    public static final String GET_PROFILE_PICTURE="/picture";
    public static final String POST_PROFILE_PICTURE="/picture";
    public static final String UPDATE_PASSWORD = "/update/password";
    public static final String UPDATE_ROLE = "/update";
    public static final String LOGIN_URL = "/user/api/auth/login";
    public static final String LOGOUT_URL = "/user/api/auth/logout";
    public static final String FORGOT_PASSWORD_OTP_URL="/user/api/auth/send-otp/**";
    public static final String OTP_IS_VALID_URL="/user/api/auth/validate-otp/**";
    public static final String RESET_PASSWORD_AFTER_VERIFICATION = "/reset/password";
    public static final String GET_USERS_TO_PROJECT_API = "{project_id}/users";
    public static final String DELETE_USER_TO_PROJECT_API = "{project_id}/users/{uid}";

    public static final String GET_ME_NAME = "/name";
}
