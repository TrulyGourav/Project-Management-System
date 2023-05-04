package com.asite.aprojecto.authentication.services;

import com.asite.aprojecto.authentication.constant.ExceptionHandlingConstants;
import com.asite.aprojecto.authentication.dto.RegisterUserDetailsDTO;
import com.asite.aprojecto.authentication.dto.ResetPasswordAfterVerificationDTO;
import com.asite.aprojecto.authentication.dto.ResponseUserDetailsDTO;
import com.asite.aprojecto.authentication.dto.UpdatePasswordDTO;
import com.asite.aprojecto.authentication.exceptions.DeviceIdNotFoundException;
import com.asite.aprojecto.authentication.exceptions.UserAlreadyExistsException;
import com.asite.aprojecto.authentication.models.TokenModel;
import com.asite.aprojecto.authentication.models.UserModel;
import com.asite.aprojecto.authentication.repositories.TokenRepository;
import com.asite.aprojecto.authentication.repositories.UserRepository;
import com.asite.aprojecto.authentication.util.MappingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    TokenRepository tokenRepository;
    @PersistenceContext
    EntityManager entityManager;

    public ResponseUserDetailsDTO checkLogin(String email, String password,String deviceId) throws BadCredentialsException, UsernameNotFoundException, DeviceIdNotFoundException {

            if (deviceId.isEmpty()){
                throw new DeviceIdNotFoundException("X-Token is required");
            }

            Authentication authenticate=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
            UserModel user = (UserModel) authenticate.getPrincipal();
            Map<String, Object> map = new HashMap<>();
            map.put("uid",user.getUid());

            String generatedToken=jwtService.generateToken(map,user);
            System.out.println("Generated Token "+generatedToken);
            saveUserToken(user,generatedToken,deviceId);

            ResponseUserDetailsDTO responseUserDetailsDTO=MappingHelper.getResponseUserDetailsDtoFromUserModel(user);
            System.out.println("Token Being sent "+generatedToken);
            responseUserDetailsDTO.setToken(generatedToken);

            return responseUserDetailsDTO;
    }
    public String register(RegisterUserDetailsDTO registerUserDetailsDTO) throws UserAlreadyExistsException,IOException{
        Query query = entityManager.createNativeQuery("select count(*) from public.user_udetails_tbl where email=:email");
        query.setParameter("email",registerUserDetailsDTO.getEmail());
        if (Integer.parseInt(query.getSingleResult().toString()) > 0){
            throw new UserAlreadyExistsException(ExceptionHandlingConstants.EMAIL_EXISTS);
        }

        System.out.println(query.getSingleResult());
        System.out.println(registerUserDetailsDTO.getEmail());

        String defaultPassword = "AsiteSolution@123";
        UserModel user=UserModel.builder().firstName(registerUserDetailsDTO.getFirstName()).lastName(registerUserDetailsDTO.getLastName()).
                designationTitle(registerUserDetailsDTO.getDesignationTitle()).
                mobileNumber(registerUserDetailsDTO.getMobileNumber()).
                email(registerUserDetailsDTO.getEmail()).
                password(passwordEncoder.encode(defaultPassword)).
                role(registerUserDetailsDTO.getRole()).
                build();

        userRepository.save(user);
        return "User Added";
    }

    public void saveUserToken(UserModel user, String jwtToken,String deviceId) {
        TokenModel tokenModel=tokenRepository.findByDeviceId(deviceId);
        if (tokenModel!=null){
            tokenModel.setUser(user);
            tokenModel.setToken(jwtToken);
            tokenRepository.save(tokenModel);
        }else {
            TokenModel newTokenModel = TokenModel.
                    builder().
                    user(user).
                    token(jwtToken).
                    deviceId(deviceId).build();

            tokenRepository.save(newTokenModel);
        }
    }

    public String resetPasswordOnVerification(ResetPasswordAfterVerificationDTO dto) {
        if (!dto.getNewPassword().equals(dto.getNewConfirmPassword())){
            return "New password and confirm password should match!";
        }
        UserModel userModel = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userModel.setPassword(passwordEncoder.encode(dto.getNewConfirmPassword()));
        userRepository.save(userModel);
        return "Password Updated!";
    }

    public String updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        if (!updatePasswordDTO.getNewPassword().equals(updatePasswordDTO.getNewConfirmedPassword())){
            return "New password and confirm password should match!";
        }
        UserModel userModel = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!passwordEncoder.matches(updatePasswordDTO.getCurrentPassword(),userModel.getPassword())){
            return "Invalid current password!";
        }
        userModel.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewConfirmedPassword()));
        userRepository.save(userModel);
        return "Password Updated!";

    }
}
