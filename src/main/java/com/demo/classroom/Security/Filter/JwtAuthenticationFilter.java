package com.demo.classroom.Security.Filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.classroom.Security.Service.JwtService;
import com.demo.classroom.Utility.Constants;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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

        final String authHeader = request.getHeader(Constants.AUTH_HEADER);
        final String refreshToken = request.getHeader(Constants.REFRESH_TOKEN_HEADER);

        if (authHeader == null || !authHeader.startsWith(Constants.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            final String jwt = authHeader.substring(7); //"Bearer "=7
            
            if (jwtService.isTokenBlacklisted(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been blacklisted. Please log in again.");
                return;
            }
    
            if (refreshToken != null && jwtService.isTokenBlacklisted(refreshToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token has been blacklisted. Please log in again.");
                return;
            }
            final String userName = jwtService.extractUsername(jwt);

            List<String> role = jwtService.extractRoles(jwt);
            
            SecurityContext securityContext = SecurityContextHolder.getContext();

            if (role == null) {
                role = Collections.emptyList();
            }

            Collection<GrantedAuthority> authorities = role.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if(userName != null){
                Authentication authentication = securityContext.getAuthentication();
                if (!userName.equals(authentication.getName())) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

                    if (jwtService.isTokenValid(jwt, userDetails)) {

                        var JwtAuthenticationToken= new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                        JwtAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        securityContext.setAuthentication(JwtAuthenticationToken);
                    } else {
                        Optional.ofNullable(refreshToken).ifPresentOrElse(token -> {
                            String newAccessToken = jwtService.refreshAccessToken(token, userDetails);
                            if (newAccessToken != null && !newAccessToken.isEmpty()) {
                                String rotatedRefreshToken = jwtService.rotateRefreshToken(refreshToken, userDetails);
                                response.setHeader(Constants.AUTH_HEADER, Constants.BEARER_PREFIX + newAccessToken);
                                response.setHeader(Constants.REFRESH_TOKEN_HEADER, rotatedRefreshToken);
                            } else {
                                try {
                                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Constants.REFRESH_TOKEN_INVALID.getMessage());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, () -> {
                            try {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Constants.REFRESH_TOKEN_REQUIRED.getMessage());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, Constants.INVALID_TOKEN_FORMAT.getMessage());
        } catch (ExpiredJwtException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Constants.TOKEN_EXPIRED.getMessage());
        } catch (UnsupportedJwtException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Constants.UNSUPPORTED_TOKEN.getMessage());
        } catch (UsernameNotFoundException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Constants.USER_NOT_FOUND.getMessage());
        }catch (IOException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Constants.INTERNAL_SERVER_ERROR.getMessage());
        }catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Constants.AUTHENTICATION_FAILED.getMessage());
        }
    }
  
}
