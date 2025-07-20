package service;

import com.example.knowledgebase.dto.UserDto;
import com.example.knowledgebase.mapper.UserMapper;
import com.example.knowledgebase.model.ERole;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.RoleRepository;
import com.example.knowledgebase.repository.UserRepository;
import com.example.knowledgebase.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldSaveUser_withEncodedPassword_andReturnDto() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setNom("Nom");
        dto.setPrenom("Prenom");
        dto.setRoles(Set.of("ROLE_ADMIN"));

        User user = new User();
        user.setUsername("admin");
        user.setNom("Nom");
        user.setPrenom("Prenom");
        user.setPassword("encoded");

        Role role = new Role(1L, ERole.ROLE_ADMIN);
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        UserDto result = userService.save(dto, "pass");

        assertEquals("admin", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findAll_shouldReturnListOfUserDtos() {
        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        List<UserDto> users = userService.findAll();

        assertEquals(1, users.size());
        assertEquals("admin", users.get(0).getUsername());
    }

    @Test
    void updateUserRole_shouldUpdateRolesCorrectly() {
        Role role = new Role(1L, ERole.ROLE_MODERATEUR);
        User user = new User();
        user.setId(1L);

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(ERole.ROLE_MODERATEUR)).thenReturn(Optional.of(role));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.updateUserRole(1L, "ROLE_MODERATEUR");

        assertEquals("admin", result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void delete_shouldCallRepository() {
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }
}
