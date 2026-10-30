
package com.smartchiclayo.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            ResourceLoader resourceLoader,
            @Value("${app.jwt.keystore}") String keyStorePath,
            @Value("${app.jwt.key-alias}") String keyAlias,
            @Value("${app.jwt.keystore-password}") String keyStorePassword,
            @Value("${app.jwt.issuer}") String issuer
    ) throws Exception {

        Resource resource = resourceLoader.getResource(
                keyStorePath.contains(":")
                        ? keyStorePath
                        : "file:" + keyStorePath
        );

        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (InputStream input = resource.getInputStream()) {
            keyStore.load(input, keyStorePassword.toCharArray());
        }

        Certificate certificate = keyStore.getCertificate(keyAlias);

        if (certificate == null
                || !(certificate.getPublicKey() instanceof RSAPublicKey publicKey)) {
            throw new IllegalStateException(
                    "El alias JWT no contiene un certificado RSA valido"
            );
        }

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withPublicKey(publicKey).build();

        OAuth2TokenValidator<Jwt> validator =
                JwtValidators.createDefaultWithIssuer(issuer);

        decoder.setJwtValidator(validator);

        return decoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Autenticacion con Google.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/google"
                        ).permitAll()

                        // Registro tradicional.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios/registro"
                        ).permitAll()

                        // Inicio de sesion tradicional.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios/login"
                        ).permitAll()

                        // Solicitar codigo de verificacion.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios/enviar-codigo-correo"
                        ).permitAll()

                        // Validar codigo de verificacion.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios/verificar-codigo-correo"
                        ).permitAll()

                        // Actualizacion de perfil protegida con JWT.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/usuarios/*"
                        ).authenticated()

                        // Los demas endpoints permanecen bloqueados.
                        .anyRequest().denyAll()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                );

        return http.build();
    }
}

