package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void findById_UserExists() {
        // Configuration des mocks
        Long userId = 1L;
        User mockUser = new User();
        UserDto mockUserDTO = new UserDto();
        when(userService.findById(userId)).thenReturn(mockUser);
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDTO);

        // Test
        ResponseEntity<?> response = userController.findById(userId.toString());

        // Vérifications
        assertEquals(HttpStatus.OK, response.getStatusCode(), "La réponse devrait être OK");
        assertEquals(mockUserDTO, response.getBody(), "Le corps de la réponse doit contenir le DTO de l'utilisateur");
    }

    @Test
    void findById_UserNotFound() {
        // Configurer UserService pour retourner null
        when(userService.findById(anyLong())).thenReturn(null);

        // Test
        ResponseEntity<?> response = userController.findById("1");

        // Vérifier
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "La réponse devrait être NOT_FOUND");
    }

    @Test
    void findById_InvalidUserIdFormat() {
        // Test avec un ID non numérique
        ResponseEntity<?> response = userController.findById("invalid-id");

        // Vérifier
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "La réponse devrait être BAD_REQUEST");
    }



    @Test
    void deleteUser_ExistingUser_Authorized() {
        // Configuration des mocks
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        when(userService.findById(userId)).thenReturn(mockUser);
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("user@example.com");
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        // Test
        ResponseEntity<?> response = userController.save(userId.toString());

        // Vérifications
        assertEquals(HttpStatus.OK, response.getStatusCode(), "La réponse devrait être OK");
        verify(userService, times(1)).delete(userId);
    }

    @Test
    void deleteUser_NonExistingUser() {
        // Configurer UserService pour retourner null
        when(userService.findById(anyLong())).thenReturn(null);

        // Test
        ResponseEntity<?> response = userController.save("1");

        // Vérifier
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "La réponse devrait être NOT_FOUND");
    }

    @Test
    void deleteUser_UnauthorizedUser() {
        // Configurer UserService et SecurityContext
        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        when(userService.findById(anyLong())).thenReturn(mockUser);
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("other@example.com");
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        // Test
        ResponseEntity<?> response = userController.save("1");

        // Vérifier
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "La réponse devrait être UNAUTHORIZED");
    }

    @Test
    void deleteUser_InvalidUserIdFormat() {
        // Test avec un ID non numérique
        ResponseEntity<?> response = userController.save("invalid-id");

        // Vérifier
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "La réponse devrait être BAD_REQUEST");
    }

}
