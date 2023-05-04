package com.asite.aprojecto.authentication.repositories;

import com.asite.aprojecto.authentication.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {
     UserModel findByEmail(String email);
}
