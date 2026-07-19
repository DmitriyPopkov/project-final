package com.example.LessonRest.dto;

public class AuthenticationDetails {
    private final boolean editorLogin;

    public AuthenticationDetails(boolean editorLogin) {
        this.editorLogin = editorLogin;
    }

    public boolean isEditorLogin() {
        return editorLogin;
    }
}
