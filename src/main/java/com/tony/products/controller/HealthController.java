package com.tony.products.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        String ipAddress;
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            ipAddress = "unknown";
        }
        
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "version", "v6",
            "time", LocalDateTime.now(),
            "ipAddress", ipAddress
        ));
    }


}