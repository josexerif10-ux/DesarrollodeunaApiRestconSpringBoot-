package com.openwebinars.controller;

import com.openwebinars.model.Tag;
import com.openwebinars.repos.TagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/tag/")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "Tags",
        description = "Gestión de etiquetas para tareas"
)
public class TagController {

    private final TagRepository tagRepository;

    @Operation(
            summary = "Listar tags",
            description = "Permite obtener todos los tags disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de tags",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Tag.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": 1,
                                        "name": "Urgente"
                                      },
                                      {
                                        "id": 2,
                                        "name": "Backend"
                                      }
                                    ]
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Operation(
            summary = "Obtener tag por ID",
            description = "Permite obtener un tag concreto mediante su identificador"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Tag no encontrado")
    })
    @GetMapping("/{id}")
    public Tag getById(
            @Parameter(description = "ID del tag", example = "1")
            @PathVariable Long id
    ) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag no encontrado"));
    }

    @Operation(
            summary = "Crear tag",
            description = "Permite crear un nuevo tag"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @PostMapping
    public ResponseEntity<Tag> create(@Valid @RequestBody Tag tag) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagRepository.save(tag));
    }

    @Operation(
            summary = "Editar tag",
            description = "Permite modificar un tag existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag editado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Tag no encontrado")
    })
    @PutMapping("/{id}")
    public Tag edit(
            @Parameter(description = "ID del tag", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Tag tag
    ) {
        Tag t = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag no encontrado"));

        t.setName(tag.getName());

        return tagRepository.save(t);
    }

    @Operation(
            summary = "Eliminar tag",
            description = "Permite eliminar un tag existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Tag no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID del tag", example = "1")
            @PathVariable Long id
    ) {
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}