package edu.coursly.app.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.exception.UserAlreadyExistsException;
import edu.coursly.app.mapper.UserMapper;
import edu.coursly.app.model.User;
import edu.coursly.app.model.enums.Role;
import edu.coursly.app.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private UserMapper userMapper;

    @InjectMocks private UserServiceImpl userService;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_register_new_user_successfully() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest("john", "secret");
        User mappedUser = new User();
        mappedUser.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        // Act
        userService.registerUser(request);

        // Assert
        assertEquals("encoded-secret", mappedUser.getPassword());
        assertEquals(Role.ROLE_STUDENT, mappedUser.getRole());

        verify(userRepository).save(mappedUser);
    }

    @Test
    void should_throw_if_user_already_exists() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest("existing", "secret");
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void should_throw_if_username_is_null() {
        UserRegistrationRequest request = new UserRegistrationRequest(null, "secret");

        Exception ex =
                assertThrows(NullPointerException.class, () -> userService.registerUser(request));
        assertEquals("username is required", ex.getMessage());
    }

    @Test
    void should_throw_if_password_is_null() {
        UserRegistrationRequest request = new UserRegistrationRequest("john", null);

        Exception ex =
                assertThrows(NullPointerException.class, () -> userService.registerUser(request));
        assertEquals("password is required", ex.getMessage());
    }

    @Test
    void should_throw_if_dto_is_null() {
        Exception ex =
                assertThrows(NullPointerException.class, () -> userService.registerUser(null));
        assertEquals("DTO is required", ex.getMessage());
    }

    @Test
    @DisplayName("Should return current authenticated user when found in repository")
    void getCurrentUser_success() {
        // given
        var auth = new UsernamePasswordAuthenticationToken("daniyar", "password");
        SecurityContextHolder.getContext().setAuthentication(auth);

        var user = User.builder().id(1L).username("daniyar").build();
        when(userRepository.findByUsername("daniyar")).thenReturn(Optional.of(user));

        // when
        User result = userService.getCurrentUser();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("daniyar");
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findByUsername("daniyar");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void getCurrentUser_userNotFound() {
        // given
        var auth = new UsernamePasswordAuthenticationToken("ghost", "password");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Username not found");

        verify(userRepository).findByUsername("ghost");
    }

    @Test
    @DisplayName("Should throw NullPointerException when authentication is missing")
    void getCurrentUser_noAuthentication() {
        // given
        SecurityContextHolder.clearContext();

        // when + then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(NullPointerException.class);
    }
}
