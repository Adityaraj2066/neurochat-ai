package com.adityaraj.neurochat.controller;

import com.adityaraj.neurochat.entity.ChatMessage;
import com.adityaraj.neurochat.repository.ChatRepository;
import com.adityaraj.neurochat.service.GeminiService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
public class ChatController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private ChatRepository repo;

    // 🔥 STREAM CHAT (MAIN API)
    @GetMapping(value = "/stream", produces = "text/plain;charset=UTF-8")
    public void streamChat(@RequestParam String chatId,
                           @RequestParam String message,
                           HttpServletResponse response,
                           HttpServletRequest request) throws IOException {

        String username = (String) request.getAttribute("username");

        // ✅ fallback (guest safety)
        if (username == null) {
            username = "guest";
        }

        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        // ✅ Save user message
        repo.save(new ChatMessage("user", message, chatId, username));

        String fullResponse;
        try {
            fullResponse = geminiService.getResponse(message);
        } catch (Exception e) {
            writer.write("Error: AI service failed");
            writer.flush();
            return;
        }

        StringBuilder finalResponse = new StringBuilder();

        // 🔥 streaming effect
        for (char c : fullResponse.toCharArray()) {
            writer.write(c);
            writer.flush();
            finalResponse.append(c);

            try {
                Thread.sleep(15); // smoother typing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // ✅ CLEAN RESPONSE (fix encoding issues)
        String cleanResponse = finalResponse.toString().trim();

        // ✅ Save bot response
        repo.save(new ChatMessage("bot", cleanResponse, chatId, username));

        writer.close();
    }

    // 🔥 GET CHAT HISTORY (PER USER + CHAT)
    @GetMapping("/history/{chatId}")
    public List<ChatMessage> getChatById(@PathVariable String chatId,
                                        HttpServletRequest req) {

        String username = (String) req.getAttribute("username");

        if (username == null) {
            username = "guest";
        }

        return repo.findByUsernameAndChatIdOrderByIdAsc(username, chatId);
    }

    // 🔥 GET CHAT TITLES (SIDEBAR)
    @GetMapping("/chat-titles")
    public List<Map<String, String>> getChatTitles(HttpServletRequest req) {

        String username = (String) req.getAttribute("username");

        if (username == null) {
            username = "guest";
        }

        List<ChatMessage> all = repo.findByUsername(username);

        Map<String, String> map = new LinkedHashMap<>();

        for (ChatMessage msg : all) {
            if ("user".equals(msg.getRole())) {
                // only first message per chat
                map.putIfAbsent(msg.getChatId(), msg.getContent());
            }
        }

        List<Map<String, String>> result = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {

            String title = entry.getValue();

            // ✅ CLEAN TEXT (fix weird characters)
            title = title.replaceAll("[^\\x00-\\x7F]", "").trim();

            // ✅ DEFAULT TITLE (if empty)
            if (title.isEmpty()) {
                title = "New Chat";
            }

            // ✅ LIMIT LENGTH
            if (title.length() > 30) {
                title = title.substring(0, 30);
            }

            Map<String, String> m = new HashMap<>();
            m.put("chatId", entry.getKey());
            m.put("title", title);

            result.add(m);
        }

        return result;
    }

    // 🔥 DELETE CHAT
    @DeleteMapping("/delete/{chatId}")
    public Map<String, String> deleteChat(@PathVariable String chatId,
                                          HttpServletRequest req) {

        String username = (String) req.getAttribute("username");

        if (username == null) {
            username = "guest";
        }

        repo.deleteByChatIdAndUsername(chatId, username);

        return Map.of("message", "Chat deleted successfully");
    }
}