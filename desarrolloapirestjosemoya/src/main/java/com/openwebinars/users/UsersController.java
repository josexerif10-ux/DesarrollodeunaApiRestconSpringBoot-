package com.openwebinars.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro de usuarios")
public class UsersController {

    private final UserService userService;

    @Operation(
            summary = "Registrar un usuario",
            description = "Permite registrar un nuevo usuario en el sistema"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Usuario creado correctamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NewUserResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "username": "pepe",
                              "email": "pepe@openwebinars.net"
                            }
                            """)
            )
    )
    @PostMapping("/auth/register")
    public ResponseEntity<NewUserResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "username": "pepe",
                                      "email": "pepe@openwebinars.net",
                                      "password": "123456"
                                    }
                                    """)
                    )
            )
            @RequestBody NewUserCommand cmd
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NewUserResponse.of(userService.register(cmd)));
    }
}