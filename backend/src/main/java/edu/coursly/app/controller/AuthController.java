package edu.coursly.app.controller;

import edu.coursly.app.dto.UserLoginRequest;
import edu.coursly.app.dto.UserLoginResponse;
import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.exception.InvalidRefreshTokenException;
import edu.coursly.app.model.enums.Role;
import edu.coursly.app.service.UserService;
import edu.coursly.app.service.impl.CustomUserDetailsServiceImpl;
import edu.coursly.app.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(ApiPaths.API_V1_AUTH)
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsServiceImpl userDetailsService;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authManager,
            JwtUtil jwtUtil,
            CustomUserDetailsServiceImpl userDetailsService,
            UserService userService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostMapping(ApiPaths.API_V1_AUTH_LOGIN)
    public UserLoginResponse login(@RequestBody @Valid UserLoginRequest userLoginRequest) {

        Authentication authentication =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                userLoginRequest.username(), userLoginRequest.password()));

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String role = user.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), role);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return new UserLoginResponse(accessToken, refreshToken);
    }

    @PostMapping(ApiPaths.API_V1_AUTH_REFRESH)
    public UserLoginResponse refresh(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);
        String role = user.getAuthorities().iterator().next().getAuthority();
        String newAccessToken = jwtUtil.generateAccessToken(username, role);

        return new UserLoginResponse(newAccessToken, refreshToken);
    }

    @PostMapping(ApiPaths.API_V1_AUTH_REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public UserLoginResponse register(
            @RequestBody @Valid UserRegistrationRequest userRegistrationRequest) {

        userService.registerUser(userRegistrationRequest);

        String accessToken =
                jwtUtil.generateAccessToken(
                        userRegistrationRequest.username(), Role.ROLE_STUDENT.name());
        String refreshToken = jwtUtil.generateRefreshToken(userRegistrationRequest.username());

        return new UserLoginResponse(accessToken, refreshToken);
    }
}
