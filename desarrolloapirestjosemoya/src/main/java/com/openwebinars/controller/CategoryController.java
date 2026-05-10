package com.openwebinars.controller;

import com.openwebinars.model.Category;
import com.openwebinars.repos.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category/")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Categorías", description = "Gestión y consulta de categorías")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @Operation(
            summary = "Listar categorías",
            description = "Permite obtener todas las categorías disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de categorías",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Category.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": 1,
                                        "title": "Trabajo"
                                      },
                                      {
                                        "id": 2,
                                        "title": "Estudios"
                                      }
                                    ]
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Operation(
            summary = "Obtener categoría por ID",
            description = "Permite obtener una categoría concreta mediante su identificador"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public Category getById(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id
    ) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    @Operation(
            summary = "Crear categoría",
            description = "Permite a un administrador o gestor crear una nueva categoría"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo ADMIN o GESTOR pueden crear categorías")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryRepository.save(category));
    }

    @Operation(
            summary = "Editar categoría",
            description = "Permite a un administrador o gestor modificar una categoría existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría editada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo ADMIN o GESTOR pueden editar categorías"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @PutMapping("/{id}")
    public Category edit(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Category category
    ) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        c.setTitle(category.getTitle());

        return categoryRepository.save(c);
    }

    @Operation(
            summary = "Eliminar categoría",
            description = "Permite a un administrador o gestor eliminar una categoría"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo ADMIN o GESTOR pueden eliminar categorías"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id
    ) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}