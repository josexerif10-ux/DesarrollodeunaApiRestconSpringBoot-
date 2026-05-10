package com.openwebinars.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserCommand(

        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres")
        @Schema(description = "Nombre de usuario", example = "pepe")
        String username,

        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El email debe tener un formato válido")
        @Schema(description = "Correo electrónico del usuario", example = "pepe@openwebinars.net")
        String email,

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        @Schema(description = "Contraseña del usuario", example = "123456")
        String password
) {
}