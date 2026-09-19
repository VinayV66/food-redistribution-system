package com.foodrescue.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts every HTTP request and checks for a JWT in the Authorization header.
 *
 * If a valid JWT is found:
 *  1. Extract the email from the token.
 *  2. Load the user from the database.
 *  3. Set the user as authenticated in Spring Security's context.
 *
 * This filter runs ONCE per request (extends OncePerRequestFilter).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            String email = tokenProvider.getEmailFromToken(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Create an authentication object with user's details and roles
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                          // credentials not needed after login
                            userDetails.getAuthorities()   // roles/permissions
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // Tell Spring Security that this request is authenticated
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue the filter chain (pass to the next filter or the controller)
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT from the "Authorization: Bearer <token>" header.
     *
     * @return the token string, or null if not present
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
