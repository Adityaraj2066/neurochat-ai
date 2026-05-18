package com.adityaraj.neurochat.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_messages") // ✅ better table name
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user / bot
    private String role;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String chatId;

    // 🔥 NEW FIELD (important for multi-user chats)
    private String username;

    // ✅ Default constructor (required by JPA)
    public ChatMessage() {}

    // ✅ Parameterized constructor
    public ChatMessage(String role, String content, String chatId, String username) {
        this.role = role;
        this.content = content;
        this.chatId = chatId;
        this.username = username;
    }

    // ✅ GETTERS
    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUsername() {
        return username;
    }

    // ✅ SETTERS
    public void setId(Long id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}