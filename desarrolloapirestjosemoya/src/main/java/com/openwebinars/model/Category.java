package com.openwebinars.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "El título de la categoría no puede estar vacío")
    @Size(max = 50, message = "El título de la categoría no puede superar los 50 caracteres")
    private String title;
}