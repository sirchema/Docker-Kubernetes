package com.chema.springcloud.msvc.usuarios.controllers;

import com.chema.springcloud.msvc.usuarios.models.entity.Usuario;
import com.chema.springcloud.msvc.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public Map<String, List<Usuario>> listar() {
        return Collections.singletonMap("usuarios", service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Usuario> usuario = service.porId(id);

        return usuario.isPresent() ? ResponseEntity.ok(usuario.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    //@ResponseStatus(HttpStatus.CREATED) //Otra forma por si no se  pone ResponseEntity y se devuelve Usuario
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }

        if (service.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "Ya existe un usuario con ese" +
                    " email"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        return service.porId(id)
                .map(usuarioDb -> {
                    if (!usuario.getEmail().isEmpty() && !usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail()) && service.porEmail(usuario.getEmail()).isPresent()) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "Ya existe un " +
                                "usuario con ese" +
                                " email!!"));
                    }

                    usuarioDb.setNombre(usuario.getNombre());
                    usuarioDb.setEmail(usuario.getEmail());
                    usuarioDb.setPassword(usuario.getPassword());
                    return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuarioDb));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return service.porId(id)
                .map(usuarioDb -> {
                    service.eliminar(usuarioDb.getId());
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuarios-curso")
    public ResponseEntity<?> obtenerAlumnosPorCursos(@RequestParam List<Long> ids){
        return ResponseEntity.ok(service.listarPorIds(ids));
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
