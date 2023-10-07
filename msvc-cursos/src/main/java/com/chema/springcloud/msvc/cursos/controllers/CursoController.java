package com.chema.springcloud.msvc.cursos.controllers;

import com.chema.springcloud.msvc.cursos.models.UsuarioDto;
import com.chema.springcloud.msvc.cursos.models.entity.Curso;
import com.chema.springcloud.msvc.cursos.services.CursoService;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(cursoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Curso> cursoOpt = this.cursoService.porIdConUsuarios(id);//this.cursoService.porId(id);
        return cursoOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(curso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @PathVariable Long id, BindingResult result, @RequestBody Curso curso) {
        if (result.hasErrors()) {
            return validar(result);
        }

        return cursoService.porId(id).map(c -> {
            c.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(c));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return cursoService.porId(id).map(c -> {
            cursoService.eliminar(c.getId());
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody UsuarioDto usuario, @PathVariable Long cursoId) {
        Optional<UsuarioDto> usuarioOpt;

        try {
            usuarioOpt = cursoService.asignarUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje", "Error" +
                    " con el servicio para asignar curso: " + e.getMessage()));
        }

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioOpt.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDto usuario, @PathVariable Long cursoId) {
        Optional<UsuarioDto> usuarioOpt;

        try {
            usuarioOpt = cursoService.crearUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje", "Error " +
                    "con el servicio para asignar curso: " + e.getMessage()));
        }

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioOpt.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody UsuarioDto usuario, @PathVariable Long cursoId) {
        Optional<UsuarioDto> usuarioOpt;

        try {
            usuarioOpt = cursoService.eliminarUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje", "Error" +
                    " con el servicio para asignar curso: " + e.getMessage()));
        }

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(usuarioOpt.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id) {
        cursoService.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }

}
