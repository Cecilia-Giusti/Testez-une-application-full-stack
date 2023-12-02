package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        // Configurez le comportement du repository simulé
        when(userRepository.existsById(userId)).thenReturn(true);

        // Appelez la méthode delete de UserService
        userService.delete(userId);

        // Vérifiez que la méthode deleteById du repository a été appelée avec le bon ID
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void testFindUserById() {
        Long userId = 1L;

        // Créez un utilisateur simulé pour le repository
        User mockUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .build();

        // Configurez le comportement du repository simulé
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Appelez la méthode findById de UserService
        User result = userService.findById(userId);

        // Vérifiez que l'utilisateur renvoyé correspond à l'utilisateur simulé
        assertEquals(mockUser, result);
    }

    @Test
    public void testFindUserByIdNotFound() {
        Long userId = 1L;

        // Configurez le comportement du repository simulé pour un utilisateur non trouvé
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Appelez la méthode findById de UserService
        User result = userService.findById(userId);

        // Vérifiez que l'utilisateur renvoyé est null car l'utilisateur n'a pas été trouvé
        assertNull(result);
    }
}

