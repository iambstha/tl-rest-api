package com.iambstha.tl_rest_api.security;

import com.iambstha.tl_rest_api.entity.User;
import com.iambstha.tl_rest_api.service.UserLoadServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    private UserLoadServiceImpl userLoadService;

    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        assert webApplicationContext != null;
        userLoadService = webApplicationContext.getBean(UserLoadServiceImpl.class);

        final String authHeader = request.getHeader(HEADER);
        if (authHeader == null || !authHeader.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = getToken(request);
        final String username = jwtUtil.extractUsername(jwtToken);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userLoadService.loadUserByUsername(username);
            if (jwtUtil.isTokenValid(jwtToken, userDetails)) {
                Claims claims = jwtUtil.extractAllClaims(jwtToken);

                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) claims.get("role");
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();
                logger.info("Extracted roles: " + authorities);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        (User) userDetails, null, authorities);

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } else {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }


    private String getToken(HttpServletRequest request) {
        return request.getHeader(HEADER).replace(PREFIX, "");
    }

}
