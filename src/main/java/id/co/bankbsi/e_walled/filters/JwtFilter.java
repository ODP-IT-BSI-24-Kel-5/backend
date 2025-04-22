package id.co.bankbsi.e_walled.filters;

import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.repositories.UserRepository;
import id.co.bankbsi.e_walled.services.UserDetailsService;
import id.co.bankbsi.e_walled.utils.JWTTokenUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

//
@Component("jwtFilter")
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JWTTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final UUID userId = UUID.fromString(jwtTokenUtils.extractSubject(jwt));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId.toString());
                Users userDetail = userRepository.findById(userId);

                if (jwtTokenUtils.isTokenValid(jwt, userDetail)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetail,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
//public class JwtFilter implements Filter {
//
//    private final JWTTokenUtils jwtTokenUtils;
//    private final UserRepository userRepository;
//
//    @Autowired
//    public JwtFilter(JWTTokenUtils jwtTokenUtils,
//                     UserRepository userRepository) {
//        this.jwtTokenUtils = jwtTokenUtils;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//        try {
//
//            String path = request.getRequestURI();
//
//            if (isPublicPath(path)) {
//                chain.doFilter(request, response);
//                return;
//            }
//            String authHeader = request.getHeader("Authorization");
//
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                UUID userId = jwtTokenUtils.validateTokenAndGetUserId(token);
//
//                boolean userExists = userRepository.existsById(userId);
//                Users users = userRepository.findById(userId);
//                if (userExists) {
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(users, null, null); // no roles
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    chain.doFilter(req, res);
//                    return;
//                }
//            }
//        } catch (JwtException | IllegalArgumentException ex) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"status\":\"failed\",\"message\": \"Unauthorized access. Invalid or missing token.\"}");
//
//            return;
//        }
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//        response.getWriter().write("{\"status\":\"failed\",\"message\": \"Unauthorized access. Invalid or missing token.\"}");
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) {
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//    private static final List<String> PUBLIC_PATHS = List.of(
//            "/swagger-ui", "/swagger-ui/", "/swagger-ui.html",
//            "/v3/api-docs", "/v3/api-docs/", "/swagger-resources", "/swagger-resources/",
//            "/webjars", "/webjars/", "/favicon.ico", "/api/v1/users/auth/", "/error"
//    );
//
//    private boolean isPublicPath(String path) {
//        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
//    }
//}