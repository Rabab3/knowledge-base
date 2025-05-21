package com.example.knowledgebase.controller;

import com.example.knowledgebase.dto.JwtResponse;
import com.example.knowledgebase.dto.LoginRequest;
import com.example.knowledgebase.dto.RefreshTokenRequest;
import com.example.knowledgebase.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request)); // méthode login()
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request)); // méthode refreshToken()
    }
}
