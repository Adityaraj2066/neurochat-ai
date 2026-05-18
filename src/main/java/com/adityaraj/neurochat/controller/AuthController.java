package com.adityaraj.neurochat.controller;

import com.adityaraj.neurochat.config.JwtUtil;
import com.adityaraj.neurochat.entity.User;
import com.adityaraj.neurochat.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;   // ✅ ADDED
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // ✅ REGISTER (FIXED)
    // =========================
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> user) {

        String username = user.get("username");
        String password = user.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password required"); // ✅ FIX
        }

        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists"); // ✅ FIX
        }

        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User(username, encodedPassword);
        newUser.setRole("USER"); // ✅ IMPORTANT FIX (VERY IMPORTANT)

        userRepo.save(newUser);

        return ResponseEntity.ok("User registered successfully"); // ✅ FIX
    }

    // =========================
    // ✅ LOGIN (NO CHANGE)
    // =========================
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {

        String username = user.get("username");
        String password = user.get("password");

        User dbUser = userRepo.findByUsername(username).orElse(null);

        if (dbUser == null || !passwordEncoder.matches(password, dbUser.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = JwtUtil.generateToken(username, dbUser.getRole());

        return Map.of("token", token);
    }

    // =========================
    // 🔐 UPGRADE TO PRO (NO CHANGE)
    // =========================
    @PostMapping("/upgrade")
    public String upgrade(HttpServletRequest req) {

        String username = (String) req.getAttribute("username");

        if (username == null || username.equals("guest")) {
            return "Unauthorized";
        }

        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null) {
            return "User not found";
        }

        user.setRole("PRO");
        userRepo.save(user);

        return "User upgraded to PRO";
    }
}