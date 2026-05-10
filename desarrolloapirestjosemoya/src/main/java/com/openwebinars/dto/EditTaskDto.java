package com.openwebinars.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.openwebinars.model.TaskPriority;
import com.openwebinars.model.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public record EditTaskDto(

        @Size(max = 100, message = "El título no puede superar los 100 caracteres")
        String title,

        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String description,

        @FutureOrPresent(message = "La fecha límite no puede estar en el pasado")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deadline,

        TaskPriority priority,

        TaskStatus status,

        Boolean important,

        @Min(value = 1, message = "Los minutos estimados deben ser como mínimo 1")
        Integer estimatedMinutes,

        Set<Long> categoryIds,

        Set<Long> tagIds

) {
}