package com.example.LessonRest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "editors", schema = "distcomp")
public class Editor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login", unique = true, nullable = false, length = 64)
    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    private String login;

    @Column(name = "password", nullable = false, length = 128)
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @Column(name = "firstname", nullable = false, length = 64)
    @Size(min = 2, max = 64, message = "Firstname must be between 2 and 64 characters")
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 64)
    @Size(min = 2, max = 64, message = "Lastname must be between 2 and 64 characters")
    private String lastname;

    // Конструкторы

    public Editor() {
    }

    public Editor(String login, String password, String firstname, String lastname) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}