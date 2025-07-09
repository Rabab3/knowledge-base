package com.example.knowledgebase.service;

import com.example.knowledgebase.dto.UserDto;
import com.example.knowledgebase.mapper.UserMapper;
import com.example.knowledgebase.model.*;
import com.example.knowledgebase.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));
        return userMapper.toDto(user);
    }

    public UserDto save(UserDto dto, String rawPassword) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Nom d’utilisateur déjà utilisé");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(getRolesFromStrings(dto.getRoles()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));

        user.setUsername(dto.getUsername());
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());
        user.setDateNaissance(dto.getDateNaissance());
        user.setCin(dto.getCin());
        user.setRoles(getRolesFromStrings(dto.getRoles()));

        return userMapper.toDto(user);
    }

    @Transactional
    public void updatePassword(Long id, String rawPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));
        user.setPassword(passwordEncoder.encode(rawPassword));
    }


    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private Set<Role> getRolesFromStrings(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            ERole roleEnum = ERole.valueOf(roleName);
            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new NoSuchElementException("Rôle non trouvé: " + roleName));
            roles.add(role);
        }
        return roles;
    }
}
