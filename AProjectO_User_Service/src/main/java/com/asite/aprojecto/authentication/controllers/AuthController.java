package com.asite.aprojecto.authentication.controllers;

import com.asite.aprojecto.authentication.constant.PermissionConstant;
import com.asite.aprojecto.authentication.constant.UserServiceURI;
import com.asite.aprojecto.authentication.dto.RegisterUserDetailsDTO;
import com.asite.aprojecto.authentication.dto.ResetPasswordAfterVerificationDTO;
import com.asite.aprojecto.authentication.dto.ResponseUserDetailsDTO;
import com.asite.aprojecto.authentication.dto.UpdatePasswordDTO;
import com.asite.aprojecto.authentication.exceptions.DeviceIdNotFoundException;
import com.asite.aprojecto.authentication.exceptions.UserAlreadyExistsException;
import com.asite.aprojecto.authentication.services.AuthService;
import com.asite.aprojecto.authentication.services.EmailService;
import com.asite.aprojecto.authentication.services.JwtService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(UserServiceURI.AUTH_API)
@Validated
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    EmailService emailService;
    @Autowired
    JwtService jwtService;

    @GetMapping(UserServiceURI.FORGOT_PASSWORD_OTP)
    public ResponseEntity<String> sendOtpEmail(@Valid @NotNull @Email @PathVariable String toEmail){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(emailService.sendOtpEmail(toEmail));
        }catch (MessagingException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping(UserServiceURI.OTP_IS_VALID)
    public ResponseEntity<String> isValidOtp(@PathVariable String toEmail, @PathVariable String otp,@Valid @NotNull HttpServletRequest httpRequest) {

        String deviceId=httpRequest.getHeader("X-Token");
        System.out.println(deviceId);
        if (deviceId.isEmpty()){
            return ResponseEntity.status(404).body("X-Token Not found!");
        }

        String token = emailService.validateOtp(toEmail, otp,deviceId);
        if (token!=null){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Token","Bearer " + token);
            jsonObject.put("Expires",String.valueOf(jwtService.extractExpiration(token)));
            jsonObject.put("Message","OTP verified!");

            return ResponseEntity.status(200).body(jsonObject.toJSONString());

        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP invalid!");
        }
    }
    @PutMapping(UserServiceURI.RESET_PASSWORD_AFTER_VERIFICATION)
    public ResponseEntity<String> resetPasswordAfterVerification(@Valid @RequestBody ResetPasswordAfterVerificationDTO dto){
        String result = authService.resetPasswordOnVerification(dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping(UserServiceURI.UPDATE_PASSWORD)
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO){
        String result = authService.updatePassword(updatePasswordDTO);
        return ResponseEntity.ok(result);
    }

    //Todo At Logout delete Device ID as Well
    @PostMapping(UserServiceURI.LOGIN_API)
    public ResponseEntity<?> login(@Valid @NotNull @RequestBody Map<String, String> request,@Valid @NotNull HttpServletRequest httpRequest){
        try {
            String deviceId=httpRequest.getHeader("X-Token");
            System.out.println(deviceId);
            ResponseUserDetailsDTO user=authService.checkLogin(request.get("email"), request.get("password"),deviceId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + user.getToken());
            headers.add("Expires", String.valueOf(jwtService.extractExpiration(user.getToken())));

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("message","Logged In");
            jsonObject.put("uid",user.getUid());
            jsonObject.put("firstName",user.getFirstName());
            jsonObject.put("role",user.getRole());

            return ResponseEntity.ok().headers(headers).body(jsonObject);
        }catch (BadCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials!");
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DeviceIdNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO CONSIDER A CASE IF USER NOT REGISTERED
    @PreAuthorize(PermissionConstant.MANAGE_USER)
    @PostMapping(value = UserServiceURI.REGISTER_API)
    public ResponseEntity<?> register(@Valid RegisterUserDetailsDTO userDetailsDto){
        try {
             String obj=authService.register(userDetailsDto);
            return ResponseEntity.ok().body(obj);
        }catch (UserAlreadyExistsException | IOException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
