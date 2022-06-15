package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.dto.UserCredentialsDTO;
import net.edubovit.labyrinth.entity.User;
import net.edubovit.labyrinth.exception.UsernameAlreadyExistsException;
import net.edubovit.labyrinth.repository.db.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    SessionUtilService sessionUtilService;

    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    PasswordEncoder passwordEncoder;

    UserService userService;

    private final String testUsername = "test-username";

    private final String testPassword = "test-password";

    private final UserCredentialsDTO credentials = new UserCredentialsDTO(testUsername, testPassword);


    @BeforeEach
    void setUp() {
        userService = new UserService(sessionUtilService, userRepository, authenticationManager, passwordEncoder);
    }

    @Test
    void mocksWork() {
        assertNotNull(sessionUtilService);
        assertNotNull(userRepository);
        assertNotNull(authenticationManager);
        assertNotNull(passwordEncoder);
        assertNotNull(userService);
    }

    @Test
    void signup_positive() {
        var assignedId = UUID.randomUUID();
        when(userRepository.existsByUsername(testUsername))
                .thenReturn(false);
        when(userRepository.save(any()))
                .then(invocation -> {
                    var user = invocation.getArgument(0, User.class);
                    user.setId(assignedId);
                    return user;
                });
        var result = userService.signup(credentials);
        assertNotNull(result);
        assertEquals(assignedId, result.id());
        assertEquals(testUsername, result.username());
    }

    @Test
    void signup_alreadyExists() {
        when(userRepository.existsByUsername(testUsername)).thenReturn(true);
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.signup(credentials));
    }

    @Test
    void authenticate_positive() {
        var id = UUID.randomUUID();
        when(userRepository.findByUsername(testUsername))
                .thenReturn(Optional.of(
                        User.builder()
                                .id(id)
                                .username(testUsername)
                                .build()
                ));
        var result = userService.authenticate(credentials);
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(testUsername, result.username());
    }

    @Test
    void authenticate_wrongPassword() {
        when(userRepository.findByUsername(testUsername))
                .thenReturn(Optional.of(
                        User.builder()
                                .id(UUID.randomUUID())
                                .username(testUsername)
                                .build()
                ));
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
        assertThrows(BadCredentialsException.class, () -> userService.authenticate(credentials));
    }

    @Test
    void authenticate_notExists() {
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.authenticate(credentials));
    }

    @Test
    void getCurrent_positive() {
        when(sessionUtilService.getUsername()).thenReturn(testUsername);
        var id = UUID.randomUUID();
        when(userRepository.findByUsername(testUsername))
                .thenReturn(Optional.of(
                        User.builder()
                                .id(id)
                                .username(testUsername)
                                .build()
                ));
        var result = userService.getCurrent();
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(testUsername, result.username());
    }

    @Test
    void getCurrent_notFound() {
        when(sessionUtilService.getUsername()).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername))
                .thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getCurrent());
    }

}
