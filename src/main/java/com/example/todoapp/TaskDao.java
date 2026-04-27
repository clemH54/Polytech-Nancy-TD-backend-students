package com.example.todoapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object for {@link Task} model.
 */
public class TaskDao {

    private final Map<Integer, Task> storage = new HashMap<>();

    {
        save(new Task(1, "Réviser DS de maths", "Séries numériques et probabilités.", false));
        save(new Task(2, "Valider mon PIVE", "PIVE Club Poker.", true));
        save(new Task(3, "Choisir mon parcours de 4A", "SIR ou SIA ?", false));
    }

    /**
     * Persist {@link Task} model.
     * @param task task to save.
     * @return task model.
     */
    public Task save(Task task) {
        storage.put(task.id(), task);
        return task;
    }

    /**
     * Retrieve {@link Task} model by id.
     * @param id identifier of the {@link Task}.
     * @return {@link Task} model wrapped by Optional.
     */
    public Optional<Task> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Retrieve all {@link Task} models.
     * @return list of {@link Task} models.
     */
    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Retrieve all {@link Task} models that are not done.
     * @return list of {@link Task} models to do.
     */
    public List<Task> findAllTodo() {
        return storage.values().stream()
                .filter(task -> Boolean.FALSE.equals(task.done()))
                .toList();
    }

    /**
     * Delete {@link Task} model by id.
     * @param id identifier of the {@link Task}.
     * @return true if deleted, false if not found.
     */
    public boolean deleteById(int id) {
        return storage.remove(id) != null;
    }

    /**
     * Update {@link Task} model by id.
     * @param id identifier of the {@link Task}.
     * @param task task to update.
     * @return true if updated, false if not found.
     */
    public boolean update(int id, Task task) {
        if (!storage.containsKey(id)) {
            return false;
        }
        storage.put(id, task);
        return true;
    }
}