package com.adityaraj.neurochat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adityaraj.neurochat.entity.ChatMessage;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByUsername(String username);

    List<ChatMessage> findByUsernameAndChatIdOrderByIdAsc(String username, String chatId);

    // 🔥 NEW (DELETE CHAT)
    void deleteByChatIdAndUsername(String chatId, String username);
}