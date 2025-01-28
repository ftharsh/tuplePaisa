package org.harsh.tuple.paisa.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void shouldGenerateToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);
        assertNotNull(token);
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void shouldValidateValidToken() {
        String token = jwtUtil.generateToken("testUser");
        assertTrue(jwtUtil.validateToken(token));
    }



    @Test
    void shouldExtractClaimsFromToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);
        Claims claims = jwtUtil.extractClaims(token);
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
    }
}
