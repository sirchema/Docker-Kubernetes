package com.chema.springcloud.msvc.cursos.services;

import com.chema.springcloud.msvc.cursos.clients.UsuarioClientRest;
import com.chema.springcloud.msvc.cursos.models.UsuarioDto;
import com.chema.springcloud.msvc.cursos.models.entity.Curso;
import com.chema.springcloud.msvc.cursos.models.entity.CursoUsuario;
import com.chema.springcloud.msvc.cursos.repositories.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;

    private final UsuarioClientRest clientRest;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) this.cursoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return cursoRepository.findById(id);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<UsuarioDto> asignarUsuario(UsuarioDto usuario, Long cursoId) {
        Optional<Curso> cursoOpt = cursoRepository.findById(cursoId);

        if (cursoOpt.isPresent()) {
            UsuarioDto usuarioDto = clientRest.detalle(usuario.getId());

            Curso curso = cursoOpt.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioDto.getId());

            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioDto);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<UsuarioDto> crearUsuario(UsuarioDto usuario, Long cursoId) {
        Optional<Curso> cursoOpt = cursoRepository.findById(cursoId);

        if (cursoOpt.isPresent()) {
            UsuarioDto usuarioNuevo = clientRest.crear(usuario);

            Curso curso = cursoOpt.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioNuevo.getId());

            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioNuevo);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<UsuarioDto> eliminarUsuario(UsuarioDto usuario, Long cursoId) {
        Optional<Curso> cursoOpt = cursoRepository.findById(cursoId);

        if (cursoOpt.isPresent()) {
            UsuarioDto usuarioDto = clientRest.detalle(usuario.getId());

            Curso curso = cursoOpt.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioDto.getId());

            curso.deleteCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioDto);
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        Optional<Curso> cursoOpt = cursoRepository.findById(id);

        if (cursoOpt.isPresent()) {
            Curso curso = cursoOpt.get();
            if (!curso.getCursoUsuarios().isEmpty()) {
                List<Long> ids = curso.getCursoUsuarios().stream().map(CursoUsuario::getUsuarioId).toList();

                List<UsuarioDto> usuarios = clientRest.detallesAlumnos(ids);
                curso.setUsuarioDtos(usuarios);
            }

            return Optional.of(curso);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        cursoRepository.eliminarCursoUsuarioPorId(id);
    }
}
