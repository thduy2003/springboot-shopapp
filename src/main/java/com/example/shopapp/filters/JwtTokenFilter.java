package com.example.shopapp.filters;

import com.example.shopapp.components.JwtTokenUtils;
import com.example.shopapp.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;


import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {

            if (isBypassToken(request)) {

                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("không có token");
                return;
            }

            final String token = authHeader.substring(7);
            final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if (jwtTokenUtils.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response); // enable bypass
        } catch (Exception e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Error" + e.getMessage());
//            System.out.println(e.getMessage());
        }
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
      final List<Pair<String, String>> bypassTokens = Arrays.asList(
              Pair.of(String.format("%s/products", apiPrefix), "GET"),
              Pair.of(String.format("%s/categories", apiPrefix), "GET"),
//              Pair.of(String.format("%s/orders", apiPrefix), "GET"),
              Pair.of(String.format("%s/roles", apiPrefix), "GET"),
              Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
              Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
              Pair.of("/swagger-ui/", "GET"),
              Pair.of("/swagger-ui.html", "GET"),
              Pair.of("/configuration/ui", "GET"),
              Pair.of("/configuration/security", "GET"),
              Pair.of("/swagger-resources/", "GET"),
              Pair.of("/swagger-resources", "GET"),
              Pair.of("/v2/api-docs", "GET"),
              Pair.of("/v3/api-docs", "GET"),
              Pair.of("/v3/api-docs/", "GET")

      );
      String requestPath = request.getServletPath();
      String requestMethod = request.getMethod();

      if(requestPath.equals(String.format("%s/orders", apiPrefix)) && requestMethod.equals("GET")) {
          return true;
      }
      for(Pair<String, String> bypassToken: bypassTokens) {
          if(requestPath.contains(bypassToken.getFirst()) && requestMethod.equals(bypassToken.getSecond())) {
              return true;
          }
      }
      return false;
    }


}
