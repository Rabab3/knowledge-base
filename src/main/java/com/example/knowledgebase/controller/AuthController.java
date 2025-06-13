package com.example.knowledgebase.controller;

import com.example.knowledgebase.dto.JwtResponse;
import com.example.knowledgebase.dto.LoginRequest;
import com.example.knowledgebase.dto.RefreshTokenRequest;
import com.example.knowledgebase.dto.UserDto;
import com.example.knowledgebase.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.knowledgebase.mapper.UserMapper;
import com.example.knowledgebase.model.User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserMapper userMapper;

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse>  login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    // Nouveau endpoint d'enregistrement avec validation
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto); // conversion DTO -> entité
        authService.register(user);
        return ResponseEntity.ok("Utilisateur enregistré avec succès");
    }

}
