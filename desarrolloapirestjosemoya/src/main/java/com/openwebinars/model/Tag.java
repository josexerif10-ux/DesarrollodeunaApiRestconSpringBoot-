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
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "El nombre del tag no puede estar vacío")
    @Size(max = 50, message = "El nombre del tag no puede superar los 50 caracteres")
    private String name;
}