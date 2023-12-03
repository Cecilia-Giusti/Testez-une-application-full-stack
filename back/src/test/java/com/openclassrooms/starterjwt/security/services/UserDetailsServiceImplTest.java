package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists() {
        // Créer un utilisateur factice
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

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Appeler loadUserByUsername
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername("test@example.com");

        // Vérifier les résultats
        assertNotNull(userDetails, "UserDetails ne devrait pas être null");
        assertEquals(mockUser.getEmail(), userDetails.getUsername());
        assertEquals(mockUser.getFirstName(), userDetails.getFirstName());
        assertEquals(mockUser.getLastName(), userDetails.getLastName());
        assertEquals(mockUser.getPassword(), userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Configurer le userRepository pour retourner un Optional vide
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Vérifier que la méthode lance une UsernameNotFoundException
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown@example.com");
        });
    }
}

