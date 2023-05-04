package com.asite.apo.filter;

import com.asite.apo.api.ProjectServiceURI;
import com.asite.apo.api.ProjectURLConstant;
import com.asite.apo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtService jwtService;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String deviceHeader = request.getHeader("X-Token");
        final Optional<String> projectId = Optional.ofNullable(request.getHeader("ProjectId"));
        final String jwt;

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

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JwtService.SIGNING_KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        Long uid = claims.get("uid", Long.class);

        if ((request.getRequestURL().toString().endsWith(ProjectURLConstant.PROJECT_API) && request.getMethod().equals("PUT")
                || request.getRequestURL().toString().endsWith(ProjectURLConstant.TASK_API+ProjectServiceURI.CREATE_TASK_URI)
                || request.getRequestURL().toString().endsWith(ProjectURLConstant.TASK_API+ProjectServiceURI.UPDATE_TASK_URI)
                || request.getRequestURL().toString().endsWith(ProjectURLConstant.SUB_TASK_API+ProjectURLConstant.CREATE_SUB_TASK_URI)
                || request.getRequestURL().toString().endsWith(ProjectURLConstant.SUB_TASK_API+ProjectURLConstant.UPDATE_SUB_TASK_URI)
                || request.getRequestURL().toString().endsWith("/project/projectversion"+"/create")
                || request.getRequestURL().toString().endsWith("/project/projectversion"+"/update")
                || request.getRequestURL().toString().endsWith("/updateStatus")
        )){
            //If is Admin
            if (findIfUserIsAdmin(uid)){
                List<SimpleGrantedAuthority> authorities=getAuthorities(findPermissionsByUserId(uid));
                System.out.println(authorities);
                SecurityContextHolder(jwt,deviceHeader,authorities,request);
                filterChain.doFilter(request,response);
                return;
            }

            if (!projectId.isPresent()){
                ErrorResponse(response,"Project ID is required");
                return;
            }

            Long pid=null;
            try {
                pid = Long.valueOf(projectId.get());
            }catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("Project ID: "+pid);
            System.out.println("User ID: "+uid);
            System.out.println("Device ID: "+deviceHeader);

            List<SimpleGrantedAuthority> projectLevelAuthorities=getAuthorities(findPermissionsByUserIdAndProjectId(uid,pid));
            SecurityContextHolder(jwt,deviceHeader,projectLevelAuthorities,request);
            filterChain.doFilter(request, response);
        }

        List<SimpleGrantedAuthority> authorities=getAuthorities(findPermissionsByUserId(uid));

        SecurityContextHolder(jwt,deviceHeader,authorities,request);
        filterChain.doFilter(request,response);
    }

    private void SecurityContextHolder(String jwt,String deviceHeader,List<SimpleGrantedAuthority> authorities,HttpServletRequest request){
        if (SecurityContextHolder.getContext().getAuthentication() == null){
            if (findIfTokenExistsAndDeviceIdMatches(jwt,deviceHeader)){
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        jwtService.extractUsername(jwt),
                        null,
                        authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }
    private List<SimpleGrantedAuthority> getAuthorities(List<String> permissions){
        System.out.println(permissions);
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    private List<String> findPermissionsByUserId(Long userId){
        Query query = entityManager.createNativeQuery("SELECT p.permission_name FROM user_udetails_tbl ud JOIN user_ms_role_tbl r ON r.role_name = ud.default_role JOIN role_permission_lk_tbl pr ON pr.role_id = r.role_id JOIN user_ms_permission_tbl p ON p.permission_id = pr.permission_id WHERE ud.uid = :userId");
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    private List<String> findPermissionsByUserIdAndProjectId(Long userId, Long projectId) {

        Query query = entityManager.createNativeQuery("SELECT p.permission_name FROM user_lk_project_role_user_tbl urp JOIN user_ms_role_tbl r ON r.role_id = urp.role_id JOIN role_permission_lk_tbl pr ON pr.role_id = r.role_id JOIN user_ms_permission_tbl p ON p.permission_id = pr.permission_id WHERE urp.uid = :userId AND urp.project_id = :projectId");
        query.setParameter("userId", userId);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }
    private boolean findIfTokenExistsAndDeviceIdMatches(String token, String deviceId) {
        Query query = entityManager.createNativeQuery("SELECT token_id FROM token_tbl t WHERE t.token = :token AND t.device_id = :deviceId");
        query.setParameter("token", token);
        query.setParameter("deviceId", deviceId);

        List<String> result = query.getResultList();
        return !result.isEmpty();
    }
    private boolean findIfUserIsAdmin(Long uid) {
        Query query = entityManager.createNativeQuery("SELECT default_role FROM user_udetails_tbl where uid=:uid");
        query.setParameter("uid", uid);
        String result = (String) query.getSingleResult();
        if (result.equals("ADMIN")){
            return true;
        }
        return false;
    }

    private void ErrorResponse(HttpServletResponse response,String message){
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        try {
            response.getWriter().write("{ \"error\": \""+message+"\" }");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
