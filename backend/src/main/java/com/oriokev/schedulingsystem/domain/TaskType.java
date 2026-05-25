package com.oriokev.schedulingsystem.domain;

public enum TaskType {
    LOG_TASK("Log Task"),
    EMAIL_TASK("Email Task"),
    HTTP_REQUEST_TASK("HTTP Request Task");

    private final String displayName;

    TaskType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
