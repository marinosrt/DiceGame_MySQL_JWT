package S05T2N1RoyoTerolMarina.model.security;

import S05T2N1RoyoTerolMarina.model.service.auth.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtServiceImpl;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        final String jwtToken;

        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) { // Bearer token should always start like that. If this two condiotions
            filterChain.doFilter(request, response);                   // are not fulfilled, we pass the request and response to the next filter
            return;                                                     // si vulguessim fer amb chrome, mirar de guardar token a cookie
        }
        jwtToken = authHeader.substring(7); //"Bearer " occupies 7 spaces
        userEmail = jwtServiceImpl.extractUsername(jwtToken);

        //checking if it is already authenticated to estalviar-nos tornar a fer tot el proces
        // si el metode retorna null, vol dir que no s'ha validat encara
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtServiceImpl.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Enforce the token with the details of our request

                SecurityContextHolder.getContext().setAuthentication(authToken); // Update security context holder
            }
        }
        filterChain.doFilter(request, response);

    }
}
