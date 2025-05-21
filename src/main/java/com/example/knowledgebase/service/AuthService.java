package com.example.knowledgebase.service;

import com.example.knowledgebase.dto.JwtResponse;
import com.example.knowledgebase.dto.LoginRequest;
import com.example.knowledgebase.dto.RefreshTokenRequest;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.RoleRepository;
import com.example.knowledgebase.repository.UserRepository;
import com.example.knowledgebase.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String email = jwtUtils.extractUsername(request.getRefreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!jwtUtils.validateToken(request.getRefreshToken(), user)) {
            throw new RuntimeException("Refresh token invalide");
        }

        String newAccessToken = jwtUtils.generateToken(user);
        return new JwtResponse(newAccessToken, request.getRefreshToken());
    }

    public void register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER non trouvé"));
        roles.add(userRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);

        userRepository.save(user);
    }
}
