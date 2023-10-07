package com.chema.springcloud.msvc.usuarios.services;

import com.chema.springcloud.msvc.usuarios.models.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> listar();

    Optional<Usuario> porId(Long id);

    Usuario guardar(Usuario usuario);

    void eliminar(Long id);

    Optional<Usuario> porEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> listarPorIds(Iterable<Long> ids);
}
