package com.openwebinars.service;

import com.openwebinars.dto.EditTaskDto;
import com.openwebinars.error.TaskNotFoundException;
import com.openwebinars.model.*;
import com.openwebinars.repos.CategoryRepository;
import com.openwebinars.repos.TagRepository;
import com.openwebinars.repos.TaskRepository;
import com.openwebinars.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findByAuthor(User author) {
        return taskRepository.findByAuthor(author);
    }

    public List<Task> findByAuthorAndStatus(User author, TaskStatus status) {
        return taskRepository.findByAuthorAndStatus(author, status);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task save(EditTaskDto cmd, User author) {
        return taskRepository.save(
                Task.builder()
                        .title(cmd.title())
                        .description(cmd.description())
                        .deadline(cmd.deadline())
                        .priority(cmd.priority() != null ? cmd.priority() : TaskPriority.MEDIUM)
                        .status(cmd.status() != null ? cmd.status() : TaskStatus.PENDING)
                        .important(cmd.important() != null && cmd.important())
                        .estimatedMinutes(cmd.estimatedMinutes())
                        .author(author)
                        .categories(getCategories(cmd.categoryIds()))
                        .tags(getTags(cmd.tagIds()))
                        .build()

        );
    }

    private Set<Category> getCategories(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(categoryRepository.findAllById(ids));
    }

    private Set<Tag> getTags(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(tagRepository.findAllById(ids));
    }

    public Task edit(EditTaskDto cmd, Long id) {
        return taskRepository.findById(id)
                .map(t -> {

                    if (cmd.title() != null) {
                        t.setTitle(cmd.title());
                    }

                    if (cmd.description() != null) {
                        t.setDescription(cmd.description());
                    }

                    if (cmd.deadline() != null) {
                        t.setDeadline(cmd.deadline());
                    }

                    if (cmd.priority() != null) {
                        t.setPriority(cmd.priority());
                    }

                    if (cmd.status() != null) {
                        t.setStatus(cmd.status());
                    }

                    if (cmd.important() != null) {
                        t.setImportant(cmd.important());
                    }

                    if (cmd.estimatedMinutes() != null) {
                        t.setEstimatedMinutes(cmd.estimatedMinutes());
                    }

                    if (cmd.categoryIds() != null) {
                        t.setCategories(getCategories(cmd.categoryIds()));
                    }

                    if (cmd.tagIds() != null) {
                        t.setTags(getTags(cmd.tagIds()));
                    }

                    return taskRepository.save(t);
                })
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }

        taskRepository.deleteById(id);
    }
}