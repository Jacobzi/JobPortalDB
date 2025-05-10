package org.example.oopproject1.controller;

import org.example.oopproject1.model.User;
import org.example.oopproject1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable String id,
            @RequestBody Map<String, Boolean> status) {
        Boolean enabled = status.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.updateUserStatus(id, enabled));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(userService.getDashboardStats());
    }
}