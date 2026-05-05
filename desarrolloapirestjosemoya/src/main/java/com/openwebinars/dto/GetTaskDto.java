package com.openwebinars.dto;

import com.openwebinars.model.Task;
import com.openwebinars.model.TaskPriority;
import com.openwebinars.model.TaskStatus;
import com.openwebinars.users.NewUserResponse;
import com.openwebinars.model.Category;
import com.openwebinars.model.Tag;
import java.util.Set;
import java.util.stream.Collectors;

import java.time.LocalDateTime;


public record GetTaskDto(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deadline,
        TaskPriority priority,
        TaskStatus status,
        boolean important,
        Integer estimatedMinutes,
        NewUserResponse author,
        Set<String> categories,
        Set<String> tags
) {
    public static GetTaskDto of(Task t) {
        return new GetTaskDto(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.getDeadline(),
                t.getPriority(),
                t.getStatus(),
                t.isImportant(),
                t.getEstimatedMinutes(),
                NewUserResponse.of(t.getAuthor()),
                t.getCategories().stream().map(Category::getTitle).collect(Collectors.toSet()),
                t.getTags().stream().map(Tag::getName).collect(Collectors.toSet())
        );
    }
}