package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void whenFindById_thenReturnsUserDto() throws Exception {
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

        UserDto mockUserDto = new UserDto(
                1L, "user@example.com", "Doe", "John", false, "password",
                LocalDateTime.now().minusDays(10), LocalDateTime.now()
        );

        when(userService.findById(1L)).thenReturn(mockUser);
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        mockMvc.perform(get("/api/user/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    public void whenFindById_withNonExistentUser_thenReturnsNotFound() throws Exception {
        Long nonExistentUserId = 2L;
        when(userService.findById(nonExistentUserId)).thenReturn(null);

        mockMvc.perform(get("/api/user/{id}", nonExistentUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenFindById_withInvalidIdFormat_thenReturnsBadRequest() throws Exception {
        String invalidId = "abc";

        mockMvc.perform(get("/api/user/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }


}

