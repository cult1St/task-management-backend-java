package com.task_management.first_backend.application.auth.filters;

import com.task_management.first_backend.application.users.models.CustomUserDetails;
import com.task_management.first_backend.application.users.models.User;
import com.task_management.first_backend.application.users.repositories.UserRepository;
import com.task_management.first_backend.application.shared.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) {
        try{
            final String authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request, response);
                return;
            }

            final String token = authHeader.substring(7);
            final String username = jwtUtils.extractUsername(token);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                User user = userRepository.findByEmail(username);

                if(user != null && jwtUtils.validateToken(token, username)){
                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }
            filterChain.doFilter(request, response);
        }catch (Exception exception){
            System.out.println(exception.getClass() + " cause: " + exception.getCause());
            throw new BadCredentialsException("Invalid Auth Token Passed");
        }
    }
}
