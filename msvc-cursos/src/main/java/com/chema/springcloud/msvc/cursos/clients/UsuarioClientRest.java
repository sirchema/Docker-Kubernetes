package com.chema.springcloud.msvc.cursos.clients;

import com.chema.springcloud.msvc.cursos.models.UsuarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-usuarios", url = "localhost:8001")
public interface UsuarioClientRest {

    @GetMapping("/{id}")
    UsuarioDto detalle(@PathVariable Long id);

    @PostMapping("/")
    UsuarioDto crear(@RequestBody UsuarioDto usuario);

    @GetMapping("/usuarios-curso")
    List<UsuarioDto> detallesAlumnos(@RequestParam Iterable<Long> ids);
}
