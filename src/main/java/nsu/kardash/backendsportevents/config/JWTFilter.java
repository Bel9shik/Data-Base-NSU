package nsu.kardash.backendsportevents.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.exceptions.Person.PersonNotFoundException;
import nsu.kardash.backendsportevents.security.JWTTypes;
import nsu.kardash.backendsportevents.security.JWTUtil;
import nsu.kardash.backendsportevents.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final PersonDetailsService personDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || header.isBlank() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = header.substring(7);

        if (jwt.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT token in Bearer header");
            return;
        }

        try {
            long id = jwtUtil.extractClaim(jwt, JWTTypes.accessToken, "id").asLong();
            UserDetails userDetails = personDetailsService.loadUserById(id);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JWTVerificationException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        } catch (PersonNotFoundException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

}
