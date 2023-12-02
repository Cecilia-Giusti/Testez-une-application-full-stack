package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
    }

    @Test
    public void whenFindById_thenReturnsTeacher() throws Exception {
        // Création d'une instance fictive de Teacher
        Teacher mockTeacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(LocalDateTime.now().minusYears(1))
                .updatedAt(LocalDateTime.now())
                .build();

        // Création d'une instance fictive de TeacherDto
        TeacherDto mockTeacherDto = new TeacherDto(
                1L, "Doe", "John", LocalDateTime.now().minusYears(1), LocalDateTime.now()
        );

        when(teacherService.findById(1L)).thenReturn(mockTeacher);
        when(teacherMapper.toDto(mockTeacher)).thenReturn(mockTeacherDto);

        mockMvc.perform(get("/api/teacher/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    public void whenFindById_andTeacherNotFound_thenReturnsNotFound() throws Exception {
        Long teacherId = 1L;
        when(teacherService.findById(teacherId)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/{id}", teacherId))
                .andExpect(status().isNotFound());
    }


    @Test
    public void whenFindById_withInvalidId_thenReturnsBadRequest() throws Exception {
        String invalidId = "abc";

        mockMvc.perform(get("/api/teacher/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void whenFindAll_thenReturnsListOfTeacherDtos() throws Exception {
        List<Teacher> teachers = Arrays.asList(
                Teacher.builder()
                        .id(1L)
                        .lastName("Doe")
                        .firstName("John")
                        .createdAt(LocalDateTime.now().minusYears(1))
                        .updatedAt(LocalDateTime.now())
                        .build(),
                Teacher.builder()
                        .id(2L)
                        .lastName("Smith")
                        .firstName("Jane")
                        .createdAt(LocalDateTime.now().minusYears(2))
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        List<TeacherDto> teacherDtos = teachers.stream()
                .map(teacher -> new TeacherDto(
                        teacher.getId(),
                        teacher.getLastName(),
                        teacher.getFirstName(),
                        teacher.getCreatedAt(),
                        teacher.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }




}
