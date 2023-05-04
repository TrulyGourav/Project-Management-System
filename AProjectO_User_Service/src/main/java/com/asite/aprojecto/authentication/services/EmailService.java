package com.asite.aprojecto.authentication.services;

import com.asite.aprojecto.authentication.models.OtpModel;
import com.asite.aprojecto.authentication.models.UserModel;
import com.asite.aprojecto.authentication.repositories.TokenRepository;
import com.asite.aprojecto.authentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    private ConcurrentHashMap<String, OtpModel> otpMap = new ConcurrentHashMap<>();
    @Autowired
    private AuthService authService;
    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;



    public String sendOtpEmail(String toEmail) throws MessagingException {

        Optional<UserModel> model = Optional.ofNullable(userRepository.findByEmail(toEmail));
        if (model.isPresent()){
            String otp = generateOtp();
            OtpModel otpModel = new OtpModel(otp,System.currentTimeMillis() + 300000,toEmail);
            otpMap.put(toEmail, otpModel);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("bhavikbhatia9@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("OTP for password reset");
            helper.setText("Your OTP is " + otp);
            mailSender.send(message);

            return "OTP sent successfully to " + toEmail;
        }
        return "User Not found!";
    }

    private String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 999999));
    }

    public String validateOtp(String email, String otp,String deviceID) {
        OtpModel otpModel = otpMap.get(email);

        if (otpModel != null && otpModel.getOtp().equals(otp) && otpModel.getExpiry() >= System.currentTimeMillis()) {
            otpMap.remove(email);
            UserModel user=userRepository.findByEmail(email);

            Map<String, Object> map = new HashMap<>();
            map.put("uid",user.getUid());
            String generatedToken=jwtService.generateToken(map,user);
            authService.saveUserToken(user,generatedToken,deviceID);

            return generatedToken;
        } else {
            return null;
        }
    }
}
