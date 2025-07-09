package com.example.knowledgebase.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        System.out.println("⇨ PATH : " + method + " " + path);

        if (
                (method.equals("POST") && (
                        path.equals("/api/articles") ||
                                path.equals("/api/contribute/articles")
                )) ||
                        (method.equals("GET") && path.startsWith("/api/articles")) ||
                        path.startsWith("/api/themes") ||
                        path.startsWith("/api/auth") ||
                        path.startsWith("/api/public")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("TOKEN REÇU : " + jwt);

        try {
            Claims claims = jwtUtils.extractAllClaims(jwt); // ✅ remplacé JwtService
            String username = claims.getSubject();
            System.out.println("USERNAME EXTRAIT : " + username);

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            Set<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("AUTORISATIONS ATTRIBUÉES : " + authorities);
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la validation du token : " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
