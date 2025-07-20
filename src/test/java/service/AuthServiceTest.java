package service;

import com.example.knowledgebase.dto.JwtResponse;
import com.example.knowledgebase.dto.LoginRequest;
import com.example.knowledgebase.dto.RefreshTokenRequest;
import com.example.knowledgebase.model.ERole;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.RoleRepository;
import com.example.knowledgebase.repository.UserRepository;
import com.example.knowledgebase.security.JwtUtils;
import com.example.knowledgebase.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_shouldReturnJwtResponse_whenCredentialsValid() {
        // GIVEN
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        User user = new User();
        user.setUsername("admin");
        Role role = new Role(1L, ERole.ROLE_ADMIN);
        user.setRoles(Set.of(role));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn("fakeAccessToken");
        when(jwtUtils.generateRefreshToken(user)).thenReturn("fakeRefreshToken");

        // WHEN
        JwtResponse response = authService.login(loginRequest);

        // THEN
        assertNotNull(response);
        assertEquals("fakeAccessToken", response.getAccessToken());
        assertEquals("fakeRefreshToken", response.getRefreshToken());
        assertEquals("ROLE_ADMIN", response.getRole());
    }

    @Test
    void refreshToken_shouldReturnNewToken_whenValidRefreshToken() {
        // GIVEN
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("validRefreshToken");

        User user = new User();
        user.setUsername("admin");
        Role role = new Role(1L, ERole.ROLE_ADMIN);
        user.setRoles(Set.of(role));

        when(jwtUtils.extractUsername("validRefreshToken")).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtUtils.validateToken("validRefreshToken")).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("newAccessToken");

        // WHEN
        JwtResponse response = authService.refreshToken(request);

        // THEN
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("validRefreshToken", response.getRefreshToken());
        assertEquals("ROLE_ADMIN", response.getRole());
    }
}
