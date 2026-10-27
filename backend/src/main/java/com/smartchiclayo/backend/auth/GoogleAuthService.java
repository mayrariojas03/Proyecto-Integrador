
package com.smartchiclayo.backend.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthService(
            @Value("${google.oauth.client-id}") String clientId
    ) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException(
                    "Falta configurar google.oauth.client-id"
            );
        }

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public GoogleUserInfo verificarToken(String idToken)
            throws GeneralSecurityException, IOException {

        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException(
                    "El token de Google está vacío"
            );
        }

        GoogleIdToken token = verifier.verify(idToken);

        if (token == null) {
            throw new IllegalArgumentException(
                    "El token de Google no es válido"
            );
        }

        GoogleIdToken.Payload payload = token.getPayload();

        String googleSub = payload.getSubject();
        String correo = payload.getEmail();
        Boolean correoVerificado = payload.getEmailVerified();

        if (googleSub == null || googleSub.isBlank()
                || correo == null || correo.isBlank()
                || !Boolean.TRUE.equals(correoVerificado)) {

            throw new IllegalArgumentException(
                    "La cuenta de Google no tiene un correo verificado"
            );
        }

        return new GoogleUserInfo(
                googleSub,
                correo,
                (String) payload.get("given_name"),
                (String) payload.get("family_name"),
                (String) payload.get("picture")
        );
    }

    public record GoogleUserInfo(
            String googleSub,
            String correo,
            String nombres,
            String apellidos,
            String fotoPerfil
    ) {
    }
}

