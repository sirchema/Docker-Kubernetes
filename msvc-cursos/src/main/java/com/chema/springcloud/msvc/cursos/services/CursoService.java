package com.chema.springcloud.msvc.cursos.services;

import com.chema.springcloud.msvc.cursos.models.UsuarioDto;
import com.chema.springcloud.msvc.cursos.models.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoService {

    List<Curso> listar();

    Optional<Curso> porId(Long id);

    Curso guardar(Curso curso);

    void eliminar(Long id);

    Optional<UsuarioDto> asignarUsuario(UsuarioDto usuario, Long cursoId);

    Optional<UsuarioDto> crearUsuario(UsuarioDto usuarioDto, Long cursoId);

    Optional<UsuarioDto> eliminarUsuario(UsuarioDto usuarioDto, Long cursoId);

    Optional<Curso> porIdConUsuarios(Long id);

    void eliminarCursoUsuarioPorId(Long id);
}
