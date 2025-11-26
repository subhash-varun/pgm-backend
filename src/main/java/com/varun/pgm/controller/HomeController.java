package com.varun.pgm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("PGM Backend API is running! 🚀\n\n" +
                "Available endpoints:\n" +
                "- Swagger UI: /swagger-ui.html\n" +
                "- API Docs: /v3/api-docs\n" +
                "- Authentication: POST /api/auth/login\n" +
                "- WebSocket: /ws (for real-time notifications)\n" +
                "- Health Check: /actuator/health (if enabled)");
    }
}
