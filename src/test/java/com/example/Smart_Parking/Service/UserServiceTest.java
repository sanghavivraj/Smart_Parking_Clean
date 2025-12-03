package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.Model.User;
import com.example.Smart_Parking.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_returnsFalseIfEmailAlreadyExists() {
        User user = new User();
        user.setEmail("vraj@gmail.com");

        when(repo.findByEmail("vraj@gmail.com")).thenReturn(Optional.of(user));

        boolean result = userService.register(user);

        assertFalse(result);
        verify(repo, never()).save(any());
    }


    @Test
    void markEmailVerified_updatesUserIfExists() {
        User user = new User();
        user.setEmail("abc@gmail.com");
        user.setEmailVerified(false);

        when(repo.findByEmail("abc@gmail.com")).thenReturn(Optional.of(user));

        boolean result = userService.markEmailVerified("abc@gmail.com");

        assertTrue(result);
        assertTrue(user.isEmailVerified());
        verify(repo).save(user);
    }

    @Test
    void authenticate_returnsFalseIfUserNotVerified() {
        User user = new User();
        user.setEmailVerified(false);

        when(repo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        boolean result = userService.authenticate("test@gmail.com", "123");

        assertFalse(result);
    }

    @Test
    void getUserById_returnsOptionalUser() {
        User user = new User();
        user.setUserid(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserid());
    }
}
