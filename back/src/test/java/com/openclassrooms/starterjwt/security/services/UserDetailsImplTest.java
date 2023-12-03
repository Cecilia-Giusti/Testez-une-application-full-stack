package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void testUserDetailsImpl() {
        Long expectedId = 1L;
        String expectedUsername = "testuser";
        String expectedFirstName = "Test";
        String expectedLastName = "User";
        Boolean expectedAdmin = true;
        String expectedPassword = "password";

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(expectedId)
                .username(expectedUsername)
                .firstName(expectedFirstName)
                .lastName(expectedLastName)
                .admin(expectedAdmin)
                .password(expectedPassword)
                .build();

        // Vérifier les attributs et les getters
        assertEquals(expectedId, userDetails.getId());
        assertEquals(expectedUsername, userDetails.getUsername());
        assertEquals(expectedFirstName, userDetails.getFirstName());
        assertEquals(expectedLastName, userDetails.getLastName());
        assertEquals(expectedAdmin, userDetails.getAdmin());
        assertEquals(expectedPassword, userDetails.getPassword());

        // Vérifier getAuthorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertTrue(authorities.isEmpty(), "La collection des autorités devrait être vide");

        // Vérifier les méthodes de UserDetails
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());

        // Tester la méthode equals
        UserDetailsImpl sameUserDetails = UserDetailsImpl.builder().id(expectedId).build();
        UserDetailsImpl differentUserDetails = UserDetailsImpl.builder().id(2L).build();

        assertEquals(userDetails, sameUserDetails, "Les deux instances avec le même ID devraient être égales");
        assertNotEquals(userDetails, differentUserDetails, "Les instances avec différents ID ne devraient pas être égales");
    }

    @Test
    void testEqualsMethod() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).build();

        // Tester this == o
        assertEquals(userDetails, userDetails, "Une instance devrait être égale à elle-même");

        // Tester o == null
        assertNotEquals(null, userDetails, "Une instance ne devrait pas être égale à null");

        // Tester getClass() != o.getClass()
        Object differentClassObject = new Object();
        assertNotEquals(userDetails, differentClassObject, "Une instance ne devrait pas être égale à un objet d'une autre classe");
    }
}
