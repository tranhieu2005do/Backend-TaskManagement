package com.java_spring_boot.first_demo.util;

import com.java_spring_boot.first_demo.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {
    private final UserRepository userRepository;
    private final String signerKey;

    public JwtUtil(
            UserRepository userRepository,
            @Value("${JWT_SECRET}") String signerKey
    ) {
        this.userRepository = userRepository;
        this.signerKey = signerKey;
    }

    private String generateToken(String email, String type, int hours) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(email)
                .issuer("TranHieu")
                .claim("type", type)
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(hours, ChronoUnit.HOURS).toEpochMilli()
                ))
                .build();

        Payload payload = new Payload(claims.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateAccessToken(String email) {
        return generateToken(email, "access", 1); // 1 hour
    }
    public String generateVerifyToken(String email){
        return generateToken(email, "verify", 12);
    }


    public String extractType(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return (String) jwt.getJWTClaimsSet().getClaim("type");
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String extractJti(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public long getRemainingTime(String token) {
        try {
            Date exp = SignedJWT.parse(token)
                    .getJWTClaimsSet()
                    .getExpirationTime();
            return exp.getTime() - System.currentTimeMillis();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public boolean validateVerifyToken(String token){
        return validateToken(token) && "verify".equals(extractType(token));
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token) && "access".equals(extractType(token));
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // verify signature
            JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            // verify expiration
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            return exp != null && exp.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}


