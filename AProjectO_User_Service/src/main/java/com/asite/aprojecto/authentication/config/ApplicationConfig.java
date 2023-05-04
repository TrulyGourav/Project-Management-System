package com.asite.aprojecto.authentication.config;

import com.asite.aprojecto.authentication.models.PermissionModel;
import com.asite.aprojecto.authentication.models.RoleModel;
import com.asite.aprojecto.authentication.models.UserModel;
import com.asite.aprojecto.authentication.repositories.RoleRepository;
import com.asite.aprojecto.authentication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
    UserDetailService: UserDetailsService has loadUserByUserName() which returns UserModel Object.

    AuthenticationManager: Handles authentication requests (In Login API we use authenticate(username,password))

    It uses DaoAuthenticationProvider by AuthenticationProvider, DaoAuthenticationProvider internally calls
    UserDetailsService loadUserByUsername(), It then compares the provided password with the encoded password
    stored in the database for the fetched user.

    If the authentication fails, it throws a BadCredentialsException. (Login API)

    The returned Authentication instance can be used to set the authentication object for the
    current security context, which is used to enforce authorization policies throughout the
    application.
*/


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Add Permissions to user
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            UserModel user=userRepository.findByEmail(username);
            if (user==null){
                throw new UsernameNotFoundException("User not found");
            }
            String role = user.getRole();
            Optional<RoleModel> roleModel = Optional.ofNullable(roleRepository.findByRoleName(role));
            if (roleModel.isPresent()){
                Set<String> permissions = roleModel.get().getPermissions().stream()
                        .map(PermissionModel::getPermission_name)
                        .collect(Collectors.toSet());

                List<SimpleGrantedAuthority> authorities=permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                user.setAuthorities(authorities);
            }
            return user;
        };
    }

    /**
     * Authenticates based on user details.
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Using BCrypt to encode passwords
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
