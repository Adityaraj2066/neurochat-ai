package com.adityaraj.neurochat.config;

import com.adityaraj.neurochat.entity.User;
import com.adityaraj.neurochat.repository.UserRepository;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter implements Filter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserRepository userRepo;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // 🔥 DEBUG (ADDED)
        System.out.println("🔥 RateLimitFilter HIT: " + path);

        // ✅ allow HTML pages
        if (path.endsWith(".html")) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ only for chat API
        if (!path.contains("/api/chat")) {
            chain.doFilter(request, response);
            return;
        }

        String username = (String) req.getAttribute("username");

        // =========================
        // 🔹 GUEST USER
        // =========================
        if (username == null || username.equals("guest")) {

        	String ip = req.getRemoteAddr().replace(":", "_");
            String key = "guest_limit:" + ip;

            Long count = 0L;

            try {
                count = redisTemplate.opsForValue().increment(key);

                // 🔥 DEBUG (ADDED)
                System.out.println("👤 Guest Count: " + count);

                if (count == 1) {
                    redisTemplate.expire(key, 1, TimeUnit.HOURS);
                }

            } catch (Exception e) {
                chain.doFilter(request, response);
                return;
            }

            if (count > 10) {
                res.setStatus(429);
                res.setContentType("application/json");

                res.getWriter().write("""
                {
                  "error": "LIMIT",
                  "type": "GUEST",
                  "message": "Please register to continue"
                }
                """);

                res.flushBuffer();
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        // =========================
        // 🔹 LOGGED-IN USER
        // =========================
        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null) {
            chain.doFilter(request, response);
            return;
        }

        // 💎 PRO USER → UNLIMITED
        if ("PRO".equalsIgnoreCase(user.getRole())) {
            chain.doFilter(request, response);
            return;
        }

        String key = "user_limit:" + username;

        Long count = 0L;

        try {
            count = redisTemplate.opsForValue().increment(key);

            // 🔥 DEBUG (ADDED)
            System.out.println("👤 User: " + username + " Count: " + count);

            if (count == 1) {
                redisTemplate.expire(key, 1, TimeUnit.HOURS);
            }

        } catch (Exception e) {
            chain.doFilter(request, response);
            return;
        }

        if (count > 10) {
            res.setStatus(429);
            res.setContentType("application/json");

            res.getWriter().write("""
            {
              "error": "LIMIT",
              "type": "PAYMENT",
              "message": "Upgrade to PRO"
            }
            """);

            res.flushBuffer();
            return;
        }

        chain.doFilter(request, response);
    }
}