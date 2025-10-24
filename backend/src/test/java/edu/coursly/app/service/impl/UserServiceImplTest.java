package edu.coursly.app.service.impl;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private UserMapper userMapper;

    @InjectMocks private UserServiceImpl userService;

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
}
