package com.smartchiclayo.backend.usuario.repository;

import com.smartchiclayo.backend.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}