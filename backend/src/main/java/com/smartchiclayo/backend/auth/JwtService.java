package com.smartchiclayo.backend.auth;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.smartchiclayo.backend.usuario.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final RSAPrivateKey privateKey;
    private final String issuer;
    private final Duration expiration;

    public JwtService(
            ResourceLoader resourceLoader,
            @Value("${app.jwt.keystore}") String keyStorePath,
            @Value("${app.jwt.key-alias}") String keyAlias,
            @Value("${app.jwt.keystore-password}") String keyStorePassword,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) throws Exception {
        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException("La expiracion JWT debe ser positiva");
        }
        Resource resource = resourceLoader.getResource(
                keyStorePath.contains(":") ? keyStorePath : "file:" + keyStorePath
        );
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] password = keyStorePassword.toCharArray();
        try (InputStream input = resource.getInputStream()) {
            keyStore.load(input, password);
        }
        PrivateKey key = (PrivateKey) keyStore.getKey(keyAlias, password);
        if (!(key instanceof RSAPrivateKey rsaPrivateKey)) {
            throw new IllegalStateException("El alias JWT no contiene una clave privada RSA");
        }
        this.privateKey = rsaPrivateKey;
        this.issuer = issuer;
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public String generarToken(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("El usuario debe estar registrado en MySQL");
        }
        Instant ahora = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(usuario.getIdUsuario().toString())
                .issueTime(Date.from(ahora))
                .expirationTime(Date.from(ahora.plus(expiration)))
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        try {
            jwt.sign(new RSASSASigner(privateKey));
        } catch (com.nimbusds.jose.JOSEException e) {
            throw new IllegalStateException("No se pudo firmar el JWT", e);
        }
        return jwt.serialize();
    }
}
