package com.quizzplatform.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // Set required fields that can't be injected via @InjectMocks
        ReflectionTestUtils.setField(authService, "jwtSecret", "test-secret-key-test-secret-key-test-secret-key");
        ReflectionTestUtils.setField(authService, "jwtExpiration", 3600000L);
        
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMy.M5c6S3b5J8JqLJ5J8JqLJ5J8JqLJ5J8"); // BCrypt encoded "password"
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = authService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("$2a$10$N9qo8uLOickgx2ZMRZoMy.M5c6S3b5J8JqLJ5J8JqLJ5J8JqLJ5J8", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername("nonexistent");
        });
    }

    @Test
    void authenticate_Success() {
        // Mock the loadUserByUsername to return a user with matching password
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // Encode the password properly to match what BCryptPasswordEncoder expects
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("password");
        testUser.setPassword(encodedPassword);
        
        String token = authService.authenticate("testuser", "password");

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void register_UsernameAlreadyExists() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        UserEntity existingUser = new UserEntity();
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("password");

        assertThrows(RuntimeException.class, () -> {
            authService.register(existingUser);
        });
    }


}