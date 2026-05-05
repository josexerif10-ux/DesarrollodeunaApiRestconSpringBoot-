package com.openwebinars.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.openwebinars.model.TaskPriority;
import com.openwebinars.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record EditTaskDto(

        String title,
        String description,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deadline,

        TaskPriority priority,
        TaskStatus status,
        Boolean important,
        Integer estimatedMinutes,

        Set<Long> categoryIds,
        Set<Long> tagIds

) {}