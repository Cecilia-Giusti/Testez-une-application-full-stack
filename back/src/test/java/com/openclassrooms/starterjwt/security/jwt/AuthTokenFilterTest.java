package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDoFilterInternal_ValidToken() throws Exception {
        // Configurer les mocks
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtils.validateJwtToken("validToken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validToken")).thenReturn("username");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("username")).thenReturn(mockUserDetails);

        // Appeler doFilterInternal
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Vérifier les interactions et les comportements
        verify(jwtUtils, times(1)).validateJwtToken("validToken");
        verify(userDetailsService, times(1)).loadUserByUsername("username");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_WithException() throws Exception {
        // Configurer les mocks
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtils.validateJwtToken("validToken")).thenThrow(new RuntimeException("Test Exception"));

        // Appeler doFilterInternal
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Vérifier que filterChain.doFilter est appelé même en cas d'exception
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_NoToken() throws Exception {
        // Configurer les mocks pour simuler une requête sans token JWT
        when(request.getHeader("Authorization")).thenReturn(null);

        // Appeler doFilterInternal
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Vérifier que validateJwtToken et loadUserByUsername ne sont pas appelés
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

}
