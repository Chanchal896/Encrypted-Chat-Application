package com.example.something;

public class User {
    private String username;
    private boolean online;

    public User(String username, boolean online) {
        this.username = username;
        this.online = online;
    }

    public String getUsername() { return username; }
    public boolean isOnline() { return online; }
}