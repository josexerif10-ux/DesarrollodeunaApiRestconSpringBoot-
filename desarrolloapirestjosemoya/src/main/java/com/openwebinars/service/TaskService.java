package com.openwebinars.service;

import com.openwebinars.dto.EditTaskDto;
import com.openwebinars.error.TaskNotFoundException;
import com.openwebinars.model.*;
import com.openwebinars.repos.CategoryRepository;
import com.openwebinars.repos.TagRepository;
import com.openwebinars.repos.TaskRepository;
import com.openwebinars.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.openwebinars.dto.DashboardDto;


import java.time.LocalDateTime;
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

    public List<Task> findByAuthorAndTitle(User author, String title) {
        return taskRepository.findByAuthorAndTitleContainingIgnoreCase(author, title);
    }

    public List<Task> findByAuthorAndPriority(User author, TaskPriority priority) {
        return taskRepository.findByAuthorAndPriority(author, priority);
    }

    public List<Task> findByAuthorAndImportant(User author, boolean important) {
        return taskRepository.findByAuthorAndImportant(author, important);
    }

    public List<Task> findExpiredTasks(User author) {
        return taskRepository.findByAuthorAndDeadlineBefore(author, LocalDateTime.now());
    }

    public List<Task> findByEstimatedMinutes(User author, Integer minutes) {
        return taskRepository.findByAuthorAndEstimatedMinutesLessThanEqual(author, minutes);
    }

    public List<Task> findByCategory(User author, Long categoryId) {
        return taskRepository.findByAuthorAndCategories_Id(author, categoryId);
    }

    public List<Task> findByTag(User author, Long tagId) {
        return taskRepository.findByAuthorAndTags_Id(author, tagId);
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

    public Task addTag(Long taskId, Long tagId) {

        Task task = findById(taskId);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag no encontrado"));

        task.getTags().add(tag);

        return taskRepository.save(task);
    }

    public Task removeTag(Long taskId, Long tagId) {

        Task task = findById(taskId);

        task.getTags().removeIf(tag -> tag.getId().equals(tagId));

        return taskRepository.save(task);
    }

    public DashboardDto getDashboard(User author) {

        List<Task> tasks = taskRepository.findByAuthor(author);

        LocalDateTime now = LocalDateTime.now();

        return new DashboardDto(

                tasks.size(),

                tasks.stream()
                        .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                        .count(),

                tasks.stream()
                        .filter(t -> t.getStatus() == TaskStatus.PENDING)
                        .count(),

                tasks.stream()
                        .filter(Task::isImportant)
                        .count(),

                tasks.stream()
                        .filter(t ->
                                t.getDeadline() != null &&
                                        t.getDeadline().isBefore(now))
                        .count()

        );
    }
}