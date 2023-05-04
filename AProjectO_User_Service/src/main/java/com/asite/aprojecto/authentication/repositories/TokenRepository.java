package com.asite.aprojecto.authentication.repositories;

import com.asite.aprojecto.authentication.models.TokenModel;
import com.asite.aprojecto.authentication.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenModel,Long> {
    TokenModel findByUser(UserModel user);
    TokenModel findByToken(String token);
    TokenModel findByDeviceId(String deviceId);
}
