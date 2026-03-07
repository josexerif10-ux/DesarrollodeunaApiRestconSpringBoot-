package com.openwebinars.repos;

import com.openwebinars.model.Task;
import com.openwebinars.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User author);

}