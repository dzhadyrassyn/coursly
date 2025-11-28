package edu.coursly.app.controller;

public final class ApiPaths {

    private ApiPaths() {}

    public static final String API_V1 = "/api/v1";

    public static final String API_V1_AUTH = API_V1 + "/auth";
    public static final String API_V1_AUTH_LOGIN = API_V1_AUTH + "/login";
    public static final String API_V1_AUTH_REGISTER = API_V1_AUTH + "/register";
    public static final String API_V1_AUTH_REFRESH = API_V1_AUTH + "/refresh";

    public static final String API_V1_CHAT = API_V1 + "/chat";
    public static final String API_V1_CHAT_SESSIONS = API_V1_CHAT + "/sessions";
    public static final String API_V1_CHAT_MESSAGES = API_V1_CHAT + "/sessions/{id}";

    public static final String API_V1_COURSES = API_V1 + "/courses";

    public static final String API_V1_COURSE_SECTIONS = API_V1_COURSES + "/{courseId}/sections";
}
