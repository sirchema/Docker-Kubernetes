package com.chema.springcloud.msvc.cursos.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String email;
    private String password;
}
