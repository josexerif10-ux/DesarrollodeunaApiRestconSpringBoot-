package com.openwebinars.users;

import com.openwebinars.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record NewUserResponse(

        @Schema(description = "Identificador del usuario", example = "1")
        Long id,

        @Schema(description = "Nombre de usuario", example = "pepe")
        String username,

        @Schema(description = "Correo electrónico", example = "pepe@openwebinars.net")
        String email
) {
    public static NewUserResponse of(User user) {
        return new NewUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}