package com.openwebinars.controller;

import com.openwebinars.model.Tag;
import com.openwebinars.repos.TagRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag/")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class TagController {

    private final TagRepository tagRepository;

    @GetMapping
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @PostMapping
    public Tag create(@RequestBody Tag tag) {
        return tagRepository.save(tag);
    }
}