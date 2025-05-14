package org.example.oopproject1.service;

import org.example.oopproject1.model.User;
import org.example.oopproject1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        sampleUser = new User();
        sampleUser.setId("1");
        sampleUser.setUsername("test");
    }

    @Test
    void findByUsername_found() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(sampleUser));
        Optional<User> result = userService.findByUsername("test");
        assertTrue(result.isPresent());
    }

    @Test
    void findByUsername_notFound() {
        when(userRepository.findByUsername("nope")).thenReturn(Optional.empty());
        Optional<User> result = userService.findByUsername("nope");
        assertFalse(result.isPresent());
    }

    @Test
    void registerUser_savesUser() {
        User newUser = new User();
        newUser.setUsername("u");
        newUser.setEmail("e");
        newUser.setPassword("p");
        newUser.setRoles(null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        User result = userService.registerUser("u", "e", "p", "USER");
        assertEquals("u", result.getUsername());
        verify(userRepository).save(any(User.class));
    }
}
