package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void testGenerateJwtToken() {
        // Simuler l'objet Authentication
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userPrincipal = mock(UserDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUsername()).thenReturn("testUser");

        // Générer le token
        String token = jwtUtils.generateJwtToken(authentication);

        // Vérifier si le token n'est pas null
        assertNotNull(token, "Le token ne devrait pas être null");
    }

    @Test
    public void testGetUserNameFromJwtToken() {
        // Simuler l'objet Authentication
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userPrincipal = mock(UserDetailsImpl.class);
        String expectedUsername = "testUser";
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUsername()).thenReturn(expectedUsername);

        // Générer le token
        String token = jwtUtils.generateJwtToken(authentication);

        // Tester getUserNameFromJwtToken
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals(expectedUsername, extractedUsername, "Le nom d'utilisateur extrait doit correspondre au nom d'utilisateur attendu");
    }

    @Test
    public void testValidateValidJwtToken() {
        // Simuler l'objet Authentication pour créer un token valide
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userPrincipal = mock(UserDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUsername()).thenReturn("testUser");

        String validToken = jwtUtils.generateJwtToken(authentication);

        // Vérifier que le token est valide
        assertTrue(jwtUtils.validateJwtToken(validToken), "Le token devrait être valide");
    }

    @Test
    public void testValidateJwtTokenWithInvalidSignature() throws NoSuchFieldException, IllegalAccessException {
        // Générer un token valide
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userPrincipal = mock(UserDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUsername()).thenReturn("testUser");

        String validToken = jwtUtils.generateJwtToken(authentication);

        // Utiliser la réflexion pour changer la clé secrète de JwtUtils
        Field jwtSecretField = JwtUtils.class.getDeclaredField("jwtSecret");
        ReflectionUtils.makeAccessible(jwtSecretField);
        String originalSecret = (String) jwtSecretField.get(jwtUtils);
        jwtSecretField.set(jwtUtils, "incorrectSecret");

        // Vérifier que la validation du token échoue
        assertFalse(jwtUtils.validateJwtToken(validToken), "Le token devrait être considéré comme invalide en raison d'une signature incorrecte");

        // Restaurer la clé secrète originale
        jwtSecretField.set(jwtUtils, originalSecret);
    }

    @Test
    public void testValidateInvalidJwtToken() {
        // Utiliser un token JWT invalide
        String invalidToken = "invalidToken";

        // Vérifier que le token est invalide
        assertFalse(jwtUtils.validateJwtToken(invalidToken), "Le token devrait être invalide");
    }

    @Test
    public void testValidateExpiredJwtToken() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Générer un token avec une très courte durée de vie
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userPrincipal = mock(UserDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUsername()).thenReturn("testUser");

        // Utiliser la réflexion pour changer jwtExpirationMs
        Field jwtExpirationMsField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
        ReflectionUtils.makeAccessible(jwtExpirationMsField);
        int originalExpirationMs = jwtExpirationMsField.getInt(jwtUtils);
        jwtExpirationMsField.setInt(jwtUtils, 1); // Durée de vie de 1 milliseconde

        String expiredToken = jwtUtils.generateJwtToken(authentication);

        // Attendre un peu pour que le token expire
        Thread.sleep(10);

        // Vérifier que la validation du token échoue à cause de l'expiration
        assertFalse(jwtUtils.validateJwtToken(expiredToken), "Le token devrait être considéré comme expiré");

        // Restaurer la durée de vie originale du token
        jwtExpirationMsField.setInt(jwtUtils, originalExpirationMs);
    }

    @Test
    public void testValidateJwtTokenWithEmptyString() {
        // Tester avec une chaîne vide
        assertFalse(jwtUtils.validateJwtToken(""), "Le token ne devrait pas être valide pour une chaîne vide");

        // Tester avec null
        assertFalse(jwtUtils.validateJwtToken(null), "Le token ne devrait pas être valide pour null");
    }
}
