package com.example.knowledgebase.repository;

import com.example.knowledgebase.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.knowledgebase.model.ERole;
    

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
