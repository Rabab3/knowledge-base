package com.example.knowledgebase.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {

    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotEmpty(message = "Au moins un rôle doit être sélectionné")
    private Set<String> roles;

    @NotBlank(message = "La date de naissance est obligatoire")
    private String dateNaissance;

    @NotBlank(message = "Le CIN est obligatoire")
    private String cin;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;


}
