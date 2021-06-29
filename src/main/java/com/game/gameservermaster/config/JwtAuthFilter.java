package com.game.gameservermaster.config;

import com.game.gameservermaster.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    private static final String BEARER_STRING_WITH_SPACE = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_STRING_WITH_SPACE)) {
            String tokenStr = authHeader.replace(BEARER_STRING_WITH_SPACE, "");
            try {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    Claims claims = jwtUtils.getClaims(tokenStr);
                    String role = claims.get(GMConstants.JWT_ROLE_KEY, String.class);
                    String subject = claims.getSubject();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        subject, claims, Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (MalformedJwtException e) {
                logger.error(e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        chain.doFilter(req, res);
    }
}