package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    public TeacherServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllTeachers() {
        // Créez une liste de professeurs simulée pour le repository
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

        // Configurez le comportement du repository simulé
        when(teacherRepository.findAll()).thenReturn(teachers);

        // Appelez la méthode findAll de TeacherService
        List<Teacher> result = teacherService.findAll();

        // Vérifiez que la liste renvoyée correspond à la liste simulée
        assertEquals(teachers, result);
    }

    @Test
    public void testFindTeacherById() {
        // Créez un professeur simulé pour le repository
        Teacher mockTeacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(LocalDateTime.now().minusYears(1))
                .updatedAt(LocalDateTime.now())
                .build();

        // Configurez le comportement du repository simulé
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(mockTeacher));

        // Appelez la méthode findById de TeacherService
        Teacher result = teacherService.findById(1L);

        // Vérifiez que le professeur renvoyé correspond au professeur simulé
        assertEquals(mockTeacher, result);
    }
}
