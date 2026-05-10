package com.openwebinars.service;

import com.openwebinars.model.User;
import com.openwebinars.model.UserRole;
import com.openwebinars.repos.UserRepository;
import com.openwebinars.users.NewUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(NewUserCommand cmd) {
        User user = User.builder()
                .username(cmd.username())
                .email(cmd.email())
                .password(passwordEncoder.encode(cmd.password()))
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User promoteToGestor(Long id) {

        User user = findById(id);

        user.setRole(UserRole.GESTOR);

        return userRepository.save(user);
    }

    public User demoteToUser(Long id) {

        User user = findById(id);

        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }

    public User updateProfile(Long id, NewUserCommand cmd) {
        User user = findById(id);

        if (cmd.username() != null) {
            user.setUsername(cmd.username());
        }

        if (cmd.email() != null) {
            user.setEmail(cmd.email());
        }

        if (cmd.password() != null) {
            user.setPassword(passwordEncoder.encode(cmd.password()));
        }

        return userRepository.save(user);
    }
}