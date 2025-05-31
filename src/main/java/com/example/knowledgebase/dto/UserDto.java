package com.example.knowledgebase.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {

    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotEmpty
    private Set<String> roles;
}




