package com.iambstha.tl_rest_api.security;

import com.iambstha.tl_rest_api.constant.AppConstant;
import com.iambstha.tl_rest_api.entity.User;
import com.iambstha.tl_rest_api.exception.RecordNotFoundException;
import com.iambstha.tl_rest_api.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;


    private PrivateKey privateKey;
    private PublicKey publicKey;

    private final ResourceLoader resourceLoader;

    private final UserRepository userRepository;

    @PostConstruct
    public void init() throws Exception {
        // Load private key
        Resource privateKeyResource = resourceLoader.getResource("file:" + privateKeyPath);
        String privateKeyContent = new String(Files.readAllBytes(privateKeyResource.getFile().toPath()))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Load public key
        Resource publicKeyResource = resourceLoader.getResource("file:" + publicKeyPath);
        String publicKeyContent = new String(Files.readAllBytes(publicKeyResource.getFile().toPath()))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> additionalClaims) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RecordNotFoundException("User " + userDetails.getUsername() + " not found."));

        Map<String, Object> claims = new HashMap<>(additionalClaims);
        claims.put("role", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        additionalClaims.put("user_id", user.getUserId());
        additionalClaims.put("username", user.getUsername());

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + AppConstant.TOKEN_EXPIRATION_TIME))
                .signWith(privateKey)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails, HashMap<String, Object> claims) {
        return UUID.randomUUID().toString();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction
                .apply(Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload());
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
