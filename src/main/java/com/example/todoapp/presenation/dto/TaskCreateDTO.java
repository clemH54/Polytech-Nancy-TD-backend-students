package com.example.todoapp.presenation.dto;

public record TaskCreateDTO(String title, String description) {

    public TaskCreateDTO {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("title:Le titre est obligatoire.");
        if (title.length() > 50)
            throw new IllegalArgumentException("title:Le titre ne peut pas dépasser 50 caractères.");
        if (description != null && description.length() > 255)
            throw new IllegalArgumentException("description:La description ne peut pas dépasser 255 caractères.");
    }
}