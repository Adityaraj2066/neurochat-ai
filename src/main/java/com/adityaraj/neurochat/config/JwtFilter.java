package com.adityaraj.neurochat.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // ✅ CLEAN WAY — SPRING HANDLES SKIP
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        return path.startsWith("/login.html") ||
               path.startsWith("/register.html") ||
               path.startsWith("/chat.html") ||
               path.startsWith("/upgrade.html") ||
               path.startsWith("/css") ||
               path.startsWith("/js") ||
               path.startsWith("/images") ||
               path.startsWith("/api/auth") ||
               path.startsWith("/api/payment");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                   HttpServletResponse res,
                                   FilterChain chain)
            throws ServletException, IOException {

        // ✅ ONLY PROTECTED API COMES HERE

        String header = req.getHeader("Authorization");

        // 👤 GUEST
       if (header == null || !header.startsWith("Bearer ")) {

    // 👤 Guest user
    req.setAttribute("username", "guest");

    UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                    "guest",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_GUEST"))
            );

    SecurityContextHolder.getContext().setAuthentication(auth);

    chain.doFilter(req, res);
    return;
}

        String token = header.substring(7);

        try {
            if (JwtUtil.validateToken(token) && !JwtUtil.isTokenExpired(token)) {

                String username = JwtUtil.extractUsername(token);
                String role = JwtUtil.extractRole(token);

                req.setAttribute("username", username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("Invalid or expired token");
                return;
            }

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Token error");
            return;
        }

        chain.doFilter(req, res);
    }
}