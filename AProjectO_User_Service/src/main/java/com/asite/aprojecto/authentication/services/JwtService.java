package com.asite.aprojecto.authentication.services;

import com.asite.aprojecto.authentication.constant.ExceptionHandlingConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


/*
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
        private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }



public String encrypt(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(getSignInKey().getEncoded(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec key = new SecretKeySpec(getSignInKey().getEncoded(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data", e);
        }
    }
*/

/**
    getSignInKey():

    We can't use SECRET_KEY directly because it is currently stored as a base64-encoded string.
    In order to use this string as the signing key for JWT tokens, we need to first decode it into its raw byte
    representation.

    The getSignInKey() method performs this decoding step by calling the Decoders.BASE64.decode method on the
    SECRET_KEY string, which returns a byte array containing the decoded bytes. Then, the
    Keys.macShaKeyFor method is called with the decoded bytes as an argument, which returns an
    javax.crypto.SecretKey object that can be used as the signing key for JWT tokens.
 */

@Service
public class JwtService {
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
//    @Value("${access.security.secret-key}")
    public static final String SIGNING_KEY="294A404E635166546A576E5A7234753778214125442A472D4B6150645367556B58703273357638792F423F4528482B4D6251655468576D597133743677397A24";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateToken(Map<String, Object> extraClaims,UserDetails userDetails) {
        long expirationTime = 1000 * 60 * 60 * 24;
        return buildToken(extraClaims, userDetails,SIGNING_KEY, expirationTime);
    }

    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            String SIGNING_KEY,
            long expirationTime
    ) {
        try{
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(SIGNING_KEY), SIGNATURE_ALGORITHM)
                .compact();
    } catch (JwtException e) {
            throw new RuntimeException(ExceptionHandlingConstants.FAILED_TO_PARSE_JWT, e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String SIGNING_KEY) {
        byte[] keyBytes = Decoders.BASE64.decode(SIGNING_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
