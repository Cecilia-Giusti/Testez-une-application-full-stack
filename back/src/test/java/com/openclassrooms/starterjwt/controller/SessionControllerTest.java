package com.openclassrooms.starterjwt.controller;


import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
    }

    @Test
    public void whenFindById_thenReturnSession() throws Exception {
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(new Date())
                .description("Relaxing yoga session")
                .build();

        SessionDto sessionDto = new SessionDto(1L, "Yoga Session", new Date(), 1L, "Relaxing yoga session", new ArrayList<>(), null, null);

        given(sessionService.getById(1L)).willReturn(session);
        given(sessionMapper.toDto(session)).willReturn(sessionDto);

        mockMvc.perform(get("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1))) // Assurez-vous que c'est cohérent avec ce que renvoie sessionDto
                .andExpect(jsonPath("$.name", is("Yoga Session")))
                .andExpect(jsonPath("$.description", is("Relaxing yoga session")));
    }

    @Test
    public void whenFindById_andSessionNotFound_thenRespondNotFound() throws Exception {
        Long sessionId = 1L;
        given(sessionService.getById(sessionId)).willReturn(null);

        mockMvc.perform(get("/api/session/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenFindById_withInvalidId_thenRespondBadRequest() throws Exception {
        String invalidId = "abc";

        mockMvc.perform(get("/api/session/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenFindAll_thenReturnsListOfSessions() throws Exception {
        List<Session> sessions = new ArrayList<>();
        sessions.add(Session.builder().id(1L).name("Yoga Session 1").date(new Date()).description("Session 1 Description").build());
        sessions.add(Session.builder().id(2L).name("Yoga Session 2").date(new Date()).description("Session 2 Description").build());

        List<SessionDto> sessionDtos = sessions.stream()
                .map(session -> new SessionDto(
                        session.getId(),
                        session.getName(),
                        session.getDate(),
                        1L,
                        session.getDescription(),
                        Arrays.asList(1L, 2L),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList());


        given(sessionService.findAll()).willReturn(sessions);
        given(sessionMapper.toDto(anyList())).willReturn(sessionDtos);

        mockMvc.perform(get("/api/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(sessions.size())))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    public void whenCreateSession_thenReturnSession() throws Exception {
        SessionDto sessionDto = new SessionDto(1L, "Yoga Session", new Date(), 1L, "Relaxing yoga session", new ArrayList<>(), null, null);
        Session session = Session.builder()
                .id(1L)
                .name("New Yoga Session")
                .date(new Date())
                .description("New session description")
                .build();

        given(sessionMapper.toEntity(any(SessionDto.class))).willReturn(session);
        given(sessionService.create(any(Session.class))).willReturn(session);
        given(sessionMapper.toDto(any(Session.class))).willReturn(sessionDto);

        String jsonRequest = new ObjectMapper().writeValueAsString(sessionDto);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(sessionDto.getName())))
                .andExpect(jsonPath("$.description", is(sessionDto.getDescription())));

    }

    @Test
    public void whenUpdateSession_thenReturnsUpdatedSession() throws Exception {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(LocalDateTime.now().minusYears(1))
                .updatedAt(LocalDateTime.now().minusMonths(1))
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .lastName("Smith")
                .firstName("Alice")
                .password("password1")
                .admin(false)
                .createdAt(LocalDateTime.now().minusMonths(2))
                .updatedAt(LocalDateTime.now().minusWeeks(1))
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .lastName("Johnson")
                .firstName("Bob")
                .password("password2")
                .admin(false)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .updatedAt(LocalDateTime.now().minusDays(10))
                .build();


        Long sessionId = 1L;
        SessionDto sessionDtoRequest = new SessionDto(sessionId, "Updated Yoga Session", new Date(), 1L, "Updated session description", new ArrayList<>(), null, null);

        // Session avant la mise à jour
        Session session = Session.builder()
                .id(1L)
                .name("Original Yoga Session")
                .date(new Date())
                .description("Original session description")
                .teacher(teacher)
                .users(Collections.singletonList(user1))
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

// Session après la mise à jour
        Session updatedSession = Session.builder()
                .id(1L)
                .name("Updated Yoga Session")
                .date(new Date())
                .description("Updated session description")
                .teacher(teacher)
                .users(Arrays.asList(user1, user2))
                .createdAt(session.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();


        // SessionDto qui reflète la session mise à jour
        SessionDto sessionDtoResponse = new SessionDto(
                updatedSession.getId(),
                updatedSession.getName(),
                updatedSession.getDate(),
                updatedSession.getTeacher().getId(),
                updatedSession.getDescription(),
                updatedSession.getUsers().stream().map(User::getId).collect(Collectors.toList()),
                updatedSession.getCreatedAt(),
                updatedSession.getUpdatedAt()
        );


        given(sessionMapper.toEntity(any(SessionDto.class))).willReturn(session);
        given(sessionService.update(eq(sessionId), any(Session.class))).willReturn(updatedSession);
        given(sessionMapper.toDto(any(Session.class))).willReturn(sessionDtoResponse);

        String jsonRequest = new ObjectMapper().writeValueAsString(sessionDtoRequest);

        mockMvc.perform(put("/api/session/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(sessionDtoResponse.getId().intValue())))
                .andExpect(jsonPath("$.name", is(sessionDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(sessionDtoResponse.getDescription())));
    }

    @Test
    public void whenUpdateSession_withInvalidId_thenRespondBadRequest() throws Exception {
        String invalidId = "abc";
        SessionDto sessionDtoRequest = new SessionDto(null, "Updated Yoga Session", new Date(), 1L, "Updated session description", new ArrayList<>(), null, null);
        String jsonRequest = new ObjectMapper().writeValueAsString(sessionDtoRequest);

        mockMvc.perform(put("/api/session/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDeleteSession_thenRespondOk() throws Exception {
        Long sessionId = 1L;
        Teacher teacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(LocalDateTime.now().minusYears(1))
                .updatedAt(LocalDateTime.now().minusMonths(1))
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .lastName("Smith")
                .firstName("Alice")
                .password("password1")
                .admin(false)
                .createdAt(LocalDateTime.now().minusMonths(2))
                .updatedAt(LocalDateTime.now().minusWeeks(1))
                .build();

        Session session = Session.builder()
                .id(1L)
                .name("Original Yoga Session")
                .date(new Date())
                .description("Original session description")
                .teacher(teacher)
                .users(Collections.singletonList(user1))
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        given(sessionService.getById(sessionId)).willReturn(session);
        doNothing().when(sessionService).delete(sessionId);

        mockMvc.perform(delete("/api/session/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteNonExistingSession_thenRespondNotFound() throws Exception {
        Long sessionId = 1L;
        given(sessionService.getById(sessionId)).willReturn(null);

        mockMvc.perform(delete("/api/session/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenDeleteSession_withInvalidId_thenRespondBadRequest() throws Exception {
        String invalidId = "abc";

        mockMvc.perform(delete("/api/session/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenParticipate_thenRespondOk() throws Exception {
        Long sessionId = 1L;
        Long userId = 1L;

        doNothing().when(sessionService).participate(sessionId, userId);

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", sessionId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenParticipate_withInvalidId_thenRespondBadRequest() throws Exception {
        String invalidSessionId = "abc";
        Long userId = 1L;

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", invalidSessionId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenParticipate_withInvalidUserId_thenRespondBadRequest() throws Exception {
        Long sessionId = 1L;
        String invalidUserId = "abc";

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", sessionId, invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenNoLongerParticipate_thenRespondOk() throws Exception {
        Long sessionId = 1L;
        Long userId = 1L;

        doNothing().when(sessionService).noLongerParticipate(sessionId, userId);

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", sessionId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenNoLongerParticipate_withInvalidSessionId_thenRespondBadRequest() throws Exception {
        String invalidSessionId = "abc";
        Long userId = 1L;

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", invalidSessionId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenNoLongerParticipate_withInvalidUserId_thenRespondBadRequest() throws Exception {
        Long sessionId = 1L;
        String invalidUserId = "abc";

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", sessionId, invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
