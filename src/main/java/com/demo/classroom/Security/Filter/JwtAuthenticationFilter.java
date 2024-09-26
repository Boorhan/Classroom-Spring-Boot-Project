package com.demo.classroom.Security.Filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.classroom.Security.Service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String refreshToken = request.getHeader("Refresh-Token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7); //"Bearer "=7
            final String userName = jwtService.extractUsername(jwt);

            var roles = jwtService.extractRoles(jwt);
            var userId = jwtService.extractUserId(jwt);

            if (roles == null) {
                roles = Collections.emptyList();
            }

            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userName != null && (authentication == null || !authentication.getName().equals(userName))) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else if (refreshToken != null) {
                    String newAccessToken = jwtService.refreshAccessToken(refreshToken, userDetails);
                    if (newAccessToken != null) {
                        response.setHeader("Authorization", "Bearer " + newAccessToken);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token is invalid or expired");
                        return; 
                    }
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }
  
}
