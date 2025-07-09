package com.example.knowledgebase.service;

import com.example.knowledgebase.dto.JwtResponse;
import com.example.knowledgebase.dto.LoginRequest;
import com.example.knowledgebase.dto.RefreshTokenRequest;
import com.example.knowledgebase.model.ERole;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.RoleRepository;
import com.example.knowledgebase.repository.UserRepository;
import com.example.knowledgebase.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        // ➕ Ajouter ici le rôle principal
        Role role = user.getRoles().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun rôle trouvé"));

        return new JwtResponse(accessToken, refreshToken, role.getName().name());
    }



    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtUtils.extractUsername(request.getRefreshToken());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!jwtUtils.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("Refresh token invalide");
        }

        String newAccessToken = jwtUtils.generateToken(user);

        Role role = user.getRoles().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun rôle trouvé"));

        return new JwtResponse(newAccessToken, request.getRefreshToken(), role.getName().name());
    }

    public void register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));

        roles.add(userRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);

        userRepository.save(user);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initDefaultUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNom("Admin");
            admin.setPrenom("Super");

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));

            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            System.out.println("✅ Utilisateur admin créé : admin / admin123");
        }

        if (!userRepository.existsByUsername("moderator")) {
            User mod = new User();
            mod.setUsername("moderator");
            mod.setPassword(passwordEncoder.encode("moderator123"));
            mod.setNom("Moderateur");
            mod.setPrenom("Compte");

            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATEUR)
                    .orElseThrow(() -> new RuntimeException("Rôle MODERATEUR non trouvé"));

            mod.setRoles(Set.of(modRole));
            userRepository.save(mod);
            System.out.println("✅ Utilisateur moderateur créé : moderator / moderator123");
        }

        if (!userRepository.existsByUsername("contributor")) {
            User contrib = new User();
            contrib.setUsername("contributor");
            contrib.setPassword(passwordEncoder.encode("contributor123"));
            contrib.setNom("Contributeur");
            contrib.setPrenom("Compte");

            Role contribRole = roleRepository.findByName(ERole.ROLE_CONTRIBUTEUR)
                    .orElseThrow(() -> new RuntimeException("Rôle CONTRIBUTEUR non trouvé"));

            contrib.setRoles(Set.of(contribRole));
            userRepository.save(contrib);
            System.out.println("✅ Utilisateur contributeur créé : contributor / contributor123");
        }

        if (!userRepository.existsByUsername("reader")) {
            User reader = new User();
            reader.setUsername("reader");
            reader.setPassword(passwordEncoder.encode("reader123"));
                reader.setNom("Lecteur");
            reader.setPrenom("Compte");

            Role readerRole = roleRepository.findByName(ERole.ROLE_LECTEUR)
                    .orElseThrow(() -> new RuntimeException("Rôle LECTEUR non trouvé"));

            reader.setRoles(Set.of(readerRole));
            userRepository.save(reader);
            System.out.println("✅ Utilisateur lecteur créé : reader / reader123");
        }
    }

}
