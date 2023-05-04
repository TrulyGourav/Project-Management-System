package com.asite.aprojecto.authentication.services;

import com.asite.aprojecto.authentication.models.TokenModel;
import com.asite.aprojecto.authentication.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    @Autowired
    TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String deviceHeader = request.getHeader("X-Token");

        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        if (deviceHeader==null){
            return;
        }

        Optional<TokenModel> storedToken = Optional.ofNullable(tokenRepository.findByDeviceId(deviceHeader));
        if (storedToken.isPresent()) {
            tokenRepository.delete(storedToken.get());
            SecurityContextHolder.clearContext();
            System.out.println("Logged Out");
        }
    }
}
