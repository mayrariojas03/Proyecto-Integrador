
package com.smartchiclayo.backend.usuario.repository;

import com.smartchiclayo.backend.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar una cuenta vinculada con Google
    Optional<Usuario> findByGoogleSub(String googleSub);

    // Buscar un usuario por correo electrónico
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    // Comprobar si el correo ya está registrado
    boolean existsByCorreoIgnoreCase(String correo);
}
