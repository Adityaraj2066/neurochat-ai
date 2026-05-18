package com.adityaraj.neurochat.controller;

import com.adityaraj.neurochat.entity.User;
import com.adityaraj.neurochat.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import jakarta.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    // 🔐 TODO: Move to application.properties later
    private static final String KEY_ID = "rzp_test_xxxxx";
    private static final String KEY_SECRET = "xxxxx";

    @Autowired
    private UserRepository userRepo;

    // =========================================
    // ✅ CREATE ORDER (Razorpay)
    // =========================================
    @PostMapping("/create-order")
    public Map<String, Object> createOrder(HttpServletRequest req) {

        try {
            // 🔐 Optional: Ensure user is logged in
            String username = (String) req.getAttribute("username");

            if (username == null || username.equals("guest")) {
                throw new RuntimeException("Unauthorized: Please login");
            }

            RazorpayClient client = new RazorpayClient(KEY_ID, KEY_SECRET);

            JSONObject options = new JSONObject();
            options.put("amount", 50000); // ₹500 (in paise)
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = client.orders.create(options);

            // ✅ RETURN CLEAN JSON
            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Payment error: " + e.getMessage());
        }
    }

    // =========================================
    // ✅ VERIFY PAYMENT + UPGRADE USER
    // =========================================
    @PostMapping("/verify-payment")
    public Map<String, String> verifyPayment(HttpServletRequest req) {

        Map<String, String> response = new HashMap<>();

        try {
            String username = (String) req.getAttribute("username");

            if (username == null || username.equals("guest")) {
                response.put("status", "error");
                response.put("message", "Unauthorized user");
                return response;
            }

            User user = userRepo.findByUsername(username).orElse(null);

            if (user == null) {
                response.put("status", "error");
                response.put("message", "User not found");
                return response;
            }

            // 💎 UPGRADE TO PRO
            user.setRole("PRO");
            userRepo.save(user);

            response.put("status", "success");
            response.put("message", "Payment successful. User upgraded to PRO");

            return response;

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Payment verification failed");

            return response;
        }
    }
}