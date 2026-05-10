package com.openwebinars.controller;

import com.openwebinars.model.User;
import com.openwebinars.service.UserService;
import com.openwebinars.users.NewUserCommand;
import com.openwebinars.users.NewUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro, gestión y perfil de usuarios")
public class UsersController {

    private final UserService userService;

    @Operation(
            summary = "Registrar usuario",
            description = "Permite registrar un nuevo usuario en el sistema"
    )
    @ApiResponses(value = {
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
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos")
    })
    @PostMapping("/auth/register")
    public ResponseEntity<NewUserResponse> createUser(
            @Valid @RequestBody NewUserCommand cmd
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NewUserResponse.of(userService.register(cmd)));
    }

    @Operation(
            summary = "Listar usuarios",
            description = "Permite al administrador obtener todos los usuarios registrados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de usuarios",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = NewUserResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo un administrador puede listar usuarios")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<NewUserResponse> getAllUsers() {
        return userService.findAll()
                .stream()
                .map(NewUserResponse::of)
                .toList();
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Permite al administrador obtener la información de un usuario concreto"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo un administrador puede consultar usuarios por ID"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public NewUserResponse getUserById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id
    ) {
        return NewUserResponse.of(userService.findById(id));
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Permite al administrador eliminar un usuario del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo un administrador puede eliminar usuarios"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Modificar perfil",
            description = "Permite al usuario autenticado modificar su perfil"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @PutMapping("/users/me")
    public NewUserResponse updateProfile(
            @Valid @RequestBody NewUserCommand cmd,
            @AuthenticationPrincipal User user
    ) {
        return NewUserResponse.of(userService.updateProfile(user.getId(), cmd));
    }

    @Operation(
            summary = "Promocionar usuario a gestor",
            description = "Permite al administrador promocionar un usuario estándar al rol GESTOR"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario promocionado correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo un administrador puede promocionar usuarios"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/promote")
    public NewUserResponse promote(
            @Parameter(description = "ID del usuario a promocionar", example = "2")
            @PathVariable Long id
    ) {
        return NewUserResponse.of(userService.promoteToGestor(id));
    }

    @Operation(
            summary = "Degradar gestor a usuario",
            description = "Permite al administrador degradar un gestor al rol USER"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario degradado correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo un administrador puede degradar usuarios"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/demote")
    public NewUserResponse demote(
            @Parameter(description = "ID del gestor a degradar", example = "2")
            @PathVariable Long id
    ) {
        return NewUserResponse.of(userService.demoteToUser(id));
    }
}