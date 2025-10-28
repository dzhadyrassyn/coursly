package edu.coursly.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.coursly.app.config.SecurityConfig;
import edu.coursly.app.dto.UserLoginRequest;
import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.service.UserService;
import edu.coursly.app.service.impl.CustomUserDetailsServiceImpl;
import edu.coursly.app.util.JwtUtil;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AuthenticationManager authManager;

    @MockitoBean private JwtUtil jwtUtil;

    @MockitoBean private CustomUserDetailsServiceImpl userDetailsService;

    @MockitoBean private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /login should return access and refresh tokens")
    void testLogin() throws Exception {
        String username = "john";
        String password = "secret";

        UserDetails user =
                new User(username, password, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        Authentication auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateAccessToken(eq(username), eq("ROLE_STUDENT")))
                .thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(eq(username))).thenReturn("refresh-token");

        UserLoginRequest request = new UserLoginRequest(username, password);

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("POST /refresh with valid token should return new access token")
    void testRefresh() throws Exception {
        String username = "john";
        String refreshToken = "valid-refresh-token";

        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(refreshToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(
                        new User(
                                username,
                                "password",
                                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))));
        when(jwtUtil.generateAccessToken(username, "ROLE_STUDENT")).thenReturn("new-access-token");

        mockMvc.perform(
                        post("/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("refreshToken", refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    @DisplayName("POST /refresh with invalid token should return 400")
    void testRefreshInvalidToken() throws Exception {
        String refreshToken = "invalid-token";

        when(jwtUtil.validateToken(refreshToken)).thenReturn(false);

        mockMvc.perform(
                        post("/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("refreshToken", refreshToken))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register should return new tokens and status 201")
    void testRegister() throws Exception {
        String username = "newuser";
        String password = "password";

        UserRegistrationRequest request = new UserRegistrationRequest(username, password);

        Mockito.doNothing().when(userService).registerUser(any());
        when(jwtUtil.generateAccessToken(username, "ROLE_STUDENT")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(username)).thenReturn("refresh-token");

        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("Should return 400 when username and password are blank on login")
    void loginValidationError() throws Exception {
        var body = Map.of("username", "", "password", "");

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username is required"))
                .andExpect(jsonPath("$.password").value("Password is required"));
    }

    @Test
    @DisplayName("Should return 400 when username and password are blank on register")
    void registerValidationError() throws Exception {
        var body = Map.of("username", "", "password", "");

        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username is required"))
                .andExpect(jsonPath("$.password").value("Password is required"));
    }
}
