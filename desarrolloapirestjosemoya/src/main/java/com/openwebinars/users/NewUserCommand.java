package com.openwebinars.users;

import io.swagger.v3.oas.annotations.media.Schema;

public record NewUserCommand(

        @Schema(description = "Nombre de usuario", example = "pepe")
        String username,

        @Schema(description = "Correo electrónico del usuario", example = "pepe@openwebinars.net")
        String email,

        @Schema(description = "Contraseña del usuario", example = "123456")
        String password
) {}