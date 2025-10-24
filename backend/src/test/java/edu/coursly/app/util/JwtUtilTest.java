package edu.coursly.app.util;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.JwtException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Setup using test utility since @Value won't inject in unit tests
        ReflectionTestUtils.setField(
                jwtUtil,
                "jwtSecret",
                "mySuperSecretKey12345678901234567890"); // Must be 32+ chars for HMAC
        ReflectionTestUtils.setField(
                jwtUtil, "accessTokenExpirationMs", TimeUnit.MINUTES.toMillis(15));
        ReflectionTestUtils.setField(
                jwtUtil, "refreshTokenExpirationMs", TimeUnit.DAYS.toMillis(7));
    }

    @Test
    void should_generate_valid_access_token_and_extract_claims() {
        String username = "testuser";
        String role = "ROLE_STUDENT";

        String accessToken = jwtUtil.generateAccessToken(username, role);

        assertNotNull(accessToken);
        assertTrue(jwtUtil.validateToken(accessToken));
        assertEquals(username, jwtUtil.getUsernameFromToken(accessToken));
        assertEquals(role, jwtUtil.getRoleFromToken(accessToken));
    }

    @Test
    void should_generate_valid_refresh_token() {
        String username = "refreshuser";

        String refreshToken = jwtUtil.generateRefreshToken(username);

        assertNotNull(refreshToken);
        assertTrue(jwtUtil.validateToken(refreshToken));
        assertEquals(username, jwtUtil.getUsernameFromToken(refreshToken));
    }

    @Test
    void should_return_false_for_invalid_token() {
        String invalidToken = "this.is.not.a.valid.jwt";

        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    void should_throw_when_extracting_from_invalid_token() {
        String invalidToken = "malformed.token.here";

        assertThrows(JwtException.class, () -> jwtUtil.getUsernameFromToken(invalidToken));
        assertThrows(JwtException.class, () -> jwtUtil.getRoleFromToken(invalidToken));
    }
}
