package com.example.todoapp.business.service;

import com.example.todoapp.business.model.Task;
import com.example.todoapp.dao.TaskDao;

import java.util.List;
import java.util.Optional;

public class TaskService {

    private final TaskDao dao = new TaskDao();

    public Task create(Task task) {
        return dao.save(task);
    }

    public Optional<Task> findById(int id) {
        return dao.findById(id);
    }

    public List<Task> findAll() {
        return dao.findAll();
    }

    public List<Task> findAllTodo() {
        return dao.findAllTodo();
    }

    public boolean delete(int id) {
        return dao.deleteById(id);
    }

    public boolean update(int id, Task task) {
        return dao.update(id, task);
    }
}