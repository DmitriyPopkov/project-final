package com.example.LessonRest.dto;

public class AuthResponseTo {
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private String role;
    private String firstName;
    private String lastName;
    private Long editorId;

    public AuthResponseTo() {
    }

    public AuthResponseTo(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthResponseTo(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public AuthResponseTo(String username, String role, String firstName, String lastName) {
        this.username = username;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public AuthResponseTo(String username, String role, String firstName, String lastName, Long editorId) {
        this.username = username;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.editorId = editorId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }
}
