package com.openclassrooms.starterjwt.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;


    @Test
    public void whenCreateSession_thenSaveSession() {
        // Créer un objet Teacher
        Teacher teacher = Teacher.builder()
                .id(1L)
                .lastName("Dupont")
                .firstName("Jean")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Créer et initialiser le premier utilisateur
        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .lastName("Dupont")
                .firstName("Jean")
                .password("password1")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Créer et initialiser le deuxième utilisateur
        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .lastName("Martin")
                .firstName("Alice")
                .password("password2")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Créer une liste d'utilisateurs
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        // Créer et initialiser un objet Session
        Session session = Session.builder()
                .id(null)
                .name("Yoga Class")
                .date(new Date())
                .description("Description of the yoga class")
                .teacher(teacher)
                .users(users)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session created = sessionService.create(session);

        assertNotNull(created);
        verify(sessionRepository).save(session);
    }

    @Test
    public void whenDeleteSession_thenRepositoryDeleteCalled() {
        Long sessionId = 1L;

        // Appel de la méthode delete du service
        sessionService.delete(sessionId);

        // Vérifie que sessionRepository.deleteById a été appelé une fois avec le bon ID
        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    public void whenFindAll_thenRepositoryFindAllCalled() {
        // Création des mocks de sessions
        Teacher teacher1 = Teacher.builder()
                .id(1L)
                .lastName("Dupont")
                .firstName("Jean")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .lastName("Dupont")
                .firstName("Jean")
                .password("password1")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<User> users1 = new ArrayList<>();
        users1.add(user1);

        Session session1 = Session.builder()
                .id(1L)
                .name("Session 1")
                .date(new Date())
                .description("Description for session 1")
                .teacher(teacher1)
                .users(users1)
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .build();

        Teacher teacher2 = Teacher.builder()
                .id(2L)
                .lastName("Smith")
                .firstName("John")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .lastName("Martin")
                .firstName("Alice")
                .password("password2")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<User> users2 = new ArrayList<>();
        users2.add(user2);

        Session session2 = Session.builder()
                .id(2L)
                .name("Session 2")
                .date(new Date())
                .description("Description for session 2")
                .teacher(teacher2)
                .users(users2)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now())
                .build();

        // Créer une liste de session
        List<Session> mockSessions = new ArrayList<>();
        mockSessions.add(session1);
        mockSessions.add(session2);

        // Configuration du comportement mocké
        when(sessionRepository.findAll()).thenReturn(mockSessions);

        // Appel de la méthode findAll du service
        List<Session> sessions = sessionService.findAll();

        // Vérifie que la liste retournée correspond à celle attendue
        assertEquals(mockSessions, sessions);
    }

    @Test
    public void whenSessionExists_thenReturnsSession() {
        // Création des mocks de sessions
        Teacher teacher1 = Teacher.builder()
                .id(1L)
                .lastName("Dupont")
                .firstName("Jean")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .lastName("Dupont")
                .firstName("Jean")
                .password("password1")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<User> users1 = new ArrayList<>();
        users1.add(user1);

        Session session = Session.builder()
                .id(1L)
                .name("Session 1")
                .date(new Date())
                .description("Description for session 1")
                .teacher(teacher1)
                .users(users1)
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .build();

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

        Session found = sessionService.getById(session.getId());

        assertNotNull(found, "Session should not be null");
        assertEquals(session, found, "Retrieved session should match the mock session");
    }

    @Test
    public void whenSessionDoesNotExist_thenReturnsNull() {

        Long sessionId = 2L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        Session found = sessionService.getById(sessionId);

        assertNull(found, "Session should be null for non-existing ID");
    }

    @Test
    public void whenUpdateSession_thenSaveSessionWithCorrectId() {

        // Création des mocks de sessions
        Teacher teacher1 = Teacher.builder()
                .id(1L)
                .lastName("Dupont")
                .firstName("Jean")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .lastName("Dupont")
                .firstName("Jean")
                .password("password1")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<User> users1 = new ArrayList<>();
        users1.add(user1);

        Session session = Session.builder()
                .id(1L)
                .name("Session 1")
                .date(new Date())
                .description("Description for session 1")
                .teacher(teacher1)
                .users(users1)
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .build();


        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session updatedSession = sessionService.update(session.getId(), session);

        assertNotNull(updatedSession, "Updated session should not be null");
        assertEquals(session.getId(), updatedSession.getId(), "Updated session should have the correct ID");
        verify(sessionRepository).save(session);
    }

    @Test
    public void whenParticipateInSession_thenUserIsAdded() {
        // Créez une session existante
        Session session = Session.builder()
                .id(1L)
                .name("Session 1")
                .date(new Date())
                .description("Description for session 1")
                .teacher(null) // Vous pouvez ajouter un enseignant si nécessaire
                .users(new ArrayList<>()) // Aucun utilisateur associé pour commencer
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .build();

        // Créez un utilisateur existant
        User user = User.builder()
                .id(1L)
                .email("user1@example.com")
                .lastName("Dupont")
                .firstName("Jean")
                .password("password1")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Assurez-vous que ni la session ni l'utilisateur ne sont déjà associés
        assertFalse(session.getUsers().stream().anyMatch(o -> o.getId().equals(user.getId())));

        // Configurez les comportements de vos mocks (sessionRepository et userRepository)
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Appelez la méthode participate
        sessionService.participate(session.getId(), user.getId());

        // Vérifiez que la session a été mise à jour
        assertTrue(session.getUsers().stream().anyMatch(o -> o.getId().equals(user.getId())));

        // Vérifiez que la méthode sessionRepository.save a été appelée
        verify(sessionRepository).save(session);
    }


    @Test
    public void whenSessionIsNull_thenThrowNotFoundException() {
        Long sessionId = 1L;
        Long userId = 1L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test
    public void whenUserIsNull_thenThrowNotFoundException() {
        Long sessionId = 1L;
        Long userId = 1L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(new Session()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
    }


    @Test
    public void whenNoLongerParticipateInSession_thenUserIsRemoved() {
        // Créez une session existante
        Session session = Session.builder()
                .id(1L)
                .name("Session 1")
                .date(new Date())
                .description("Description for session 1")
                .teacher(null) // Vous pouvez ajouter un enseignant si nécessaire
                .users(new ArrayList<>()) // Aucun utilisateur associé pour commencer
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .build();

        // Créez un utilisateur existant
        User user = User.builder()
                .id(1L)
                .email("user1@example.com")
                .lastName("Dupont")
                .firstName("Jean")
                .password("password1")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Ajoutez l'utilisateur à la session
        session.getUsers().add(user);

        // Assurez-vous que l'utilisateur est déjà associé à la session
        assertTrue(session.getUsers().stream().anyMatch(o -> o.getId().equals(user.getId())));

        // Configurez le comportement de votre mock (sessionRepository)
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

        // Appelez la méthode noLongerParticipate
        sessionService.noLongerParticipate(session.getId(), user.getId());

        // Vérifiez que l'utilisateur a été retiré de la session
        assertFalse(session.getUsers().stream().anyMatch(o -> o.getId().equals(user.getId())));

        // Vérifiez que la méthode sessionRepository.save a été appelée
        verify(sessionRepository).save(session);
    }


}
