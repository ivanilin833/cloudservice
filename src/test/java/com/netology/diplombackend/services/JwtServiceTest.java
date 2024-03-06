package com.netology.diplombackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        String jwtSigningKey = "53A73E5F1C4E0A2D3B5F2D784E6A1B423D6F247D1F6E5C3A596D635A75327855";
        jwtService.setJwtSigningKey(jwtSigningKey);
        userDetails = new User("user1", "user_one", new ArrayList<>());
    }

    @Test
    void extractUserName_ValidToken_ReturnsCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUserName(token);
        assertEquals(userDetails.getUsername(), extractedUsername);
    }

    @Test
    void generateToken_ValidUserDetails_ReturnsValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertEquals(userDetails.getUsername(), jwtService.extractUserName(token));
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void invalidateToken_AddsTokenToInvalidList() {
        String token = jwtService.generateToken(userDetails);
        jwtService.invalidateToken(token);
        assertFalse(jwtService.isTokenValid(token, userDetails));
    }
}
