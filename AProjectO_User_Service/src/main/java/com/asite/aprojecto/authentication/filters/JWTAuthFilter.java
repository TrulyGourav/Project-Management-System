package com.asite.aprojecto.authentication.filters;

import com.asite.aprojecto.authentication.constant.UserServiceURI;
import com.asite.aprojecto.authentication.repositories.TokenRepository;
import com.asite.aprojecto.authentication.services.JwtService;
import com.sun.istack.NotNull;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
     OncePerRequestFilter: Filter is called Once Per user request

     request.getHeader("Authorization"): We get token from authorization header

     jwt=authHeader.substring(7): Extracting Token
     email=jwtService.extractUsername(jwt): Extracting email from token

     (email!=null && SecurityContextHolder.getContext().getAuthentication()==null): Checking if email is null
     and if Security Context Holder has auth object. SecurityContextHolder stores each auth object
     in a thread local variable.

     UsernamePasswordAuthenticationToken authenticationToken = new
     UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities()): Making a Auth Object

     authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)): Assigning IP Address
     and Session ID

     SecurityContextHolder.getContext().setAuthentication(authenticationToken): Storing it in
     SecurityContextHolder Object

*/

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService = new JwtService();
    @Autowired
    private TokenRepository tokenRepository;
    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Runs Once per request to validate token and store authentication object in Security
     * Context Holder
     *
     * @param request from client
     * @param response from client
     * @param filterChain the filter chain object
     * @throws ServletException if there is a servlet exception
     * @throws IOException if there is an I/O exception
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String deviceHeader = request.getHeader("X-Token");
        final Optional<String> projectId = Optional.ofNullable(request.getHeader("projectId"));
        final String jwt;

        if (request.getRequestURL().toString().endsWith(UserServiceURI.LOGIN_URL) || request.getRequestURL().toString().contains("/user/api/auth/validate-otp/") || request.getRequestURL().toString().contains("/user/api/auth/send-otp/")) {
            filterChain.doFilter(request,response);
            return;
        }

        if (deviceHeader == null){
            ErrorResponse(response,"X-Token is not found");
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            ErrorResponse(response,"Token is invalid");
            return;
        }

        jwt=authHeader.substring(7);
        if (jwtService.isTokenExpired(jwt)){
            ErrorResponse(response,"Token is expired");
            return;
        }

        SecurityContextHolder(jwt,deviceHeader,request);
        filterChain.doFilter(request,response);
    }

    private void SecurityContextHolder(String jwt, String deviceHeader,HttpServletRequest request){
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtService.extractUsername(jwt));
        if (SecurityContextHolder.getContext().getAuthentication() == null){
            if (findIfTokenExistsAndDeviceIdMatches(jwt,deviceHeader)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }
    private void SecurityContextHolder(String jwt,List<SimpleGrantedAuthority> authorities, String deviceHeader,HttpServletRequest request){
        if (SecurityContextHolder.getContext().getAuthentication() == null){
            if (findIfTokenExistsAndDeviceIdMatches(jwt,deviceHeader)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtService.extractUsername(jwt),null,authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }
    private void ErrorResponse(HttpServletResponse response,String message)
    {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        try {
            response.getWriter().write("{ \"error\": \""+message+"\" }");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean findIfTokenExistsAndDeviceIdMatches(String token, String deviceId) {
        Query query = entityManager.createNativeQuery("SELECT token_id FROM token_tbl t WHERE t.token = :token AND t.device_id = :deviceId");
        query.setParameter("token", token);
        query.setParameter("deviceId", deviceId);

        List<String> result = query.getResultList();
        return !result.isEmpty();
    }
}
