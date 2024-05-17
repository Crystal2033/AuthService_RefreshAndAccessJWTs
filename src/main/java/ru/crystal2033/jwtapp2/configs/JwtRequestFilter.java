/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.configs;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.persistence.Column;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.crystal2033.jwtapp2.util.JwtTokenUtils;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private static String PREFIX_AUTH = "Bearer ";
    private final JwtTokenUtils jwtTokenUtils;

    public JwtRequestFilter(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    //Creating new internal filter in filter chain. It will be work only if we want
    //to access the secured area
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(PREFIX_AUTH.length());
            try{
                username = jwtTokenUtils.getUsername(jwt);
            }
            catch(ExpiredJwtException exc){
                log.debug("Token`s lifetime expired");
            }
            catch(Exception exc){
                log.debug("Other exception:" + exc.getMessage());
            }
        }

        //second check is for not to override already existing data
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtTokenUtils.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);//continuous filtering
    }
}
