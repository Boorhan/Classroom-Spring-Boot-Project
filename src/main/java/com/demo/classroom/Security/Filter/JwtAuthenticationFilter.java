package com.demo.classroom.Security.Filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.classroom.Security.Service.JwtService;
import com.demo.classroom.Utility.Constants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader(Constants.AUTH_HEADER);
        
        if (authHeader == null || !authHeader.startsWith(Constants.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            
            if (jwtService.isTokenBlacklisted(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been blacklisted. Please log in again.");
                return;
            }

            if (!jwtService.isTokenExpired(jwt)) {
                requestAuthorization(jwt, request);
            } 
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Constants.AUTHENTICATION_FAILED.getMessage());
        }
    }

    private void requestAuthorization(String jwt, @NotNull HttpServletRequest request) {
        final String userName = jwtService.extractUsername(jwt);
        List<String> role = jwtService.extractRoles(jwt);
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (role == null) {
            role = Collections.emptyList();
        }

        Collection<GrantedAuthority> authorities = role.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        if (userName != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication == null || !userName.equals(authentication.getName())) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(authenticationToken);
                }
            }
        }
    }
}
