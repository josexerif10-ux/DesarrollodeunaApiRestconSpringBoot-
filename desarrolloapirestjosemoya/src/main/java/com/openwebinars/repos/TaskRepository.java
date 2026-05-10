package com.openwebinars.repos;

import com.openwebinars.model.Task;
import com.openwebinars.model.TaskPriority;
import com.openwebinars.model.TaskStatus;
import com.openwebinars.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User author);

    List<Task> findByAuthorAndStatus(User author, TaskStatus status);

    List<Task> findByAuthorAndTitleContainingIgnoreCase(User author, String title);

    List<Task> findByAuthorAndPriority(User author, TaskPriority priority);

    List<Task> findByAuthorAndImportant(User author, boolean important);

    List<Task> findByAuthorAndDeadlineBefore(User author, LocalDateTime date);

    List<Task> findByAuthorAndEstimatedMinutesLessThanEqual(User author, Integer minutes);

    List<Task> findByAuthorAndCategories_Id(User author, Long categoryId);

    List<Task> findByAuthorAndTags_Id(User author, Long tagId);
}