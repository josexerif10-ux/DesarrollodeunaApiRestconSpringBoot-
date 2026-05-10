package com.openwebinars.controller;

import com.openwebinars.dto.DashboardDto;
import com.openwebinars.dto.EditTaskDto;
import com.openwebinars.dto.GetTaskDto;
import com.openwebinars.model.TaskPriority;
import com.openwebinars.model.TaskStatus;
import com.openwebinars.model.User;
import com.openwebinars.service.TaskService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/task/")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Tareas", description = "Operaciones relacionadas con tareas")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Obtener todas las tareas del usuario",
            description = "Permite obtener todas las tareas del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de tareas del usuario",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetTaskDto.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping
    public List<GetTaskDto> getAll(@AuthenticationPrincipal User author) {
        return taskService.findByAuthor(author)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Obtener una tarea concreta",
            description = "Permite obtener una tarea por su ID si pertenece al usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetTaskDto.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos sobre esta tarea"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PostAuthorize("returnObject.author.username == authentication.principal.username")
    @GetMapping("/{id}")
    public GetTaskDto getById(
            @Parameter(description = "ID de la tarea", example = "1")
            @PathVariable Long id
    ) {
        return GetTaskDto.of(taskService.findById(id));
    }

    @Operation(summary = "Crear una tarea",
            description = "Permite crear una tarea asociada al usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarea creada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetTaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @PostMapping
    public ResponseEntity<GetTaskDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tarea a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EditTaskDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "title": "Aprender Spring Boot",
                                      "description": "Hacer todos los cursos de Spring Boot",
                                      "deadline": "2025-12-31T23:59:59",
                                      "priority": "HIGH",
                                      "status": "PENDING",
                                      "important": true,
                                      "estimatedMinutes": 120,
                                      "categoryIds": [1],
                                      "tagIds": [1, 2]
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody EditTaskDto cmd,
            @AuthenticationPrincipal User author
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GetTaskDto.of(taskService.save(cmd, author)));
    }

    @Operation(summary = "Editar una tarea",
            description = "Permite editar una tarea asociada al usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea editada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetTaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para editar esta tarea"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PreAuthorize("@ownerCheck.check(#id, authentication.principal.getId())")
    @PutMapping("/{id}")
    public GetTaskDto edit(
            @Valid @RequestBody EditTaskDto cmd,
            @Parameter(description = "ID de la tarea", example = "1")
            @PathVariable Long id
    ) {
        return GetTaskDto.of(taskService.edit(cmd, id));
    }

    @Operation(summary = "Eliminar una tarea",
            description = "Permite eliminar una tarea asociada al usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarea eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para eliminar esta tarea"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PreAuthorize("@ownerCheck.check(#id, authentication.principal.getId())")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID de la tarea", example = "1")
            @PathVariable Long id
    ) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar tareas por estado",
            description = "Devuelve las tareas del usuario autenticado filtradas por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/status/{status}")
    public List<GetTaskDto> getByStatus(
            @Parameter(description = "Estado de la tarea", example = "PENDING")
            @PathVariable TaskStatus status,
            @AuthenticationPrincipal User author
    ) {
        return taskService.findByAuthorAndStatus(author, status)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Buscar tareas por prioridad",
            description = "Devuelve las tareas del usuario autenticado filtradas por prioridad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/priority/{priority}")
    public List<GetTaskDto> getByPriority(
            @Parameter(description = "Prioridad de la tarea", example = "HIGH")
            @PathVariable TaskPriority priority,
            @AuthenticationPrincipal User author
    ) {
        return taskService.findByAuthorAndPriority(author, priority)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Buscar tareas por título",
            description = "Devuelve las tareas cuyo título contiene el texto indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/title/{title}")
    public List<GetTaskDto> getByTitle(
            @Parameter(description = "Texto a buscar en el título", example = "Spring")
            @PathVariable String title,
            @AuthenticationPrincipal User author
    ) {
        return taskService.findByAuthorAndTitle(author, title)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Buscar tareas importantes",
            description = "Devuelve las tareas marcadas como importantes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/important")
    public List<GetTaskDto> getImportantTasks(@AuthenticationPrincipal User author) {
        return taskService.findByAuthorAndImportant(author, true)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Buscar tareas vencidas",
            description = "Devuelve las tareas cuya fecha límite ya ha pasado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tareas vencidas obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/expired")
    public List<GetTaskDto> getExpiredTasks(@AuthenticationPrincipal User author) {
        return taskService.findExpiredTasks(author)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Buscar tareas por categoría",
            description = "Devuelve las tareas asociadas a una categoría concreta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/category/{categoryId}")
    public List<GetTaskDto> getByCategory(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long categoryId,
            @AuthenticationPrincipal User author
    ) {
        return taskService.findByCategory(author, categoryId)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Buscar tareas por tag",
            description = "Devuelve las tareas asociadas a un tag concreto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Tag no encontrado")
    })
    @GetMapping("/tag/{tagId}")
    public List<GetTaskDto> getByTag(
            @Parameter(description = "ID del tag", example = "1")
            @PathVariable Long tagId,
            @AuthenticationPrincipal User author
    ) {
        return taskService.findByTag(author, tagId)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Buscar tareas por tiempo estimado",
            description = "Devuelve tareas con tiempo estimado menor o igual al indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Minutos no válidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/estimated/{minutes}")
    public List<GetTaskDto> getByEstimatedMinutes(
            @Parameter(description = "Minutos estimados máximos", example = "60")
            @PathVariable Integer minutes,
            @AuthenticationPrincipal User author
    ) {
        return taskService.findByEstimatedMinutes(author, minutes)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Asignar tag a tarea",
            description = "Permite asignar un tag existente a una tarea del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag asignado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetTaskDto.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos sobre esta tarea"),
            @ApiResponse(responseCode = "404", description = "Tarea o tag no encontrado")
    })
    @PreAuthorize("@ownerCheck.check(#taskId, authentication.principal.getId())")
    @PutMapping("/{taskId}/tag/{tagId}")
    public GetTaskDto addTag(
            @Parameter(description = "ID de la tarea", example = "1")
            @PathVariable Long taskId,
            @Parameter(description = "ID del tag", example = "2")
            @PathVariable Long tagId
    ) {
        return GetTaskDto.of(taskService.addTag(taskId, tagId));
    }

    @Operation(summary = "Eliminar tag de tarea",
            description = "Permite eliminar un tag asociado a una tarea del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag eliminado de la tarea correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetTaskDto.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos sobre esta tarea"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PreAuthorize("@ownerCheck.check(#taskId, authentication.principal.getId())")
    @DeleteMapping("/{taskId}/tag/{tagId}")
    public GetTaskDto removeTag(
            @Parameter(description = "ID de la tarea", example = "1")
            @PathVariable Long taskId,
            @Parameter(description = "ID del tag", example = "2")
            @PathVariable Long tagId
    ) {
        return GetTaskDto.of(taskService.removeTag(taskId, tagId));
    }

    @Operation(summary = "Dashboard de tareas",
            description = "Muestra estadísticas generales de las tareas del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard generado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DashboardDto.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/dashboard")
    public DashboardDto dashboard(@AuthenticationPrincipal User author) {
        return taskService.getDashboard(author);
    }
}