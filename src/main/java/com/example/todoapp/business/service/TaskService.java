package com.example.todoapp.business.service;

import com.example.todoapp.business.model.Task;
import com.example.todoapp.dao.TaskDao;
import com.example.todoapp.presenation.dto.TaskCreateDTO;
import com.example.todoapp.presenation.dto.TaskResponseDTO;
import com.example.todoapp.presenation.dto.TaskUpdateDTO;

import java.util.List;
import java.util.Optional;

public class TaskService {

    private final TaskDao dao = new TaskDao();

    public TaskResponseDTO create(TaskCreateDTO dto) {
        // id=0 car SQLite va l'autogénérer, done=false par défaut
        Task task = new Task(0, dto.title(), dto.description(), false);
        Task saved = dao.save(task);
        return toResponseDTO(saved);
    }

    public Optional<TaskResponseDTO> findById(int id) {
        return dao.findById(id).map(this::toResponseDTO);
    }

    public List<TaskResponseDTO> findAll() {
        return dao.findAll().stream().map(this::toResponseDTO).toList();
    }

    public List<TaskResponseDTO> findAllTodo() {
        return dao.findAllTodo().stream().map(this::toResponseDTO).toList();
    }

    public boolean delete(int id) {
        return dao.deleteById(id);
    }

    public boolean update(int id, TaskUpdateDTO dto) {
        Task task = new Task(id, dto.title(), dto.description(), dto.done());
        return dao.update(id, task);
    }

    // Conversion Task → TaskResponseDTO
    private TaskResponseDTO toResponseDTO(Task task) {
        return new TaskResponseDTO(task.id(), task.title(), task.description(), task.done());
    }
}