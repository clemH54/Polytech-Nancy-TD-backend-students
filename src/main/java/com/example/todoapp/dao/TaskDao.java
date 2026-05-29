package com.example.todoapp.dao;

import com.example.todoapp.business.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDao {

    private static final String DB_URL = "jdbc:sqlite:todoapp.db";

    public TaskDao() {
        initTable();
        seedData();
    }

    private void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    done INTEGER NOT NULL DEFAULT 0
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialise the tasks table.", e);
        }
    }

    private void seedData() {
        if (count() > 0) return;

        save(new Task(null, "Réviser DS de maths",       "Séries numériques et probabilités.", false));
        save(new Task(null, "Valider mon PIVE",           "PIVE Club Poker.",                  true));
        save(new Task(null, "Choisir mon parcours de 4A", "SIR ou SIA ?",                      false));
    }

    public Task save(Task task) {
        String sql = """
                INSERT INTO tasks (title, description, done)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.title());
            ps.setString(2, task.description());
            ps.setInt(3, task.done() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Task(keys.getInt(1), task.title(), task.description(), task.done());
                }
            }
            return task;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save task", e);
        }
    }

    public Optional<Task> findById(int id) {
        String sql = "SELECT id, title, description, done FROM tasks WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find task id=" + id, e);
        }
    }

    public List<Task> findAll() {
        String sql = "SELECT id, title, description, done FROM tasks";
        return queryList(sql);
    }

    public List<Task> findAllTodo() {
        String sql = "SELECT id, title, description, done FROM tasks WHERE done = 0";
        return queryList(sql);
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete task id=" + id, e);
        }
    }

    public boolean update(int id, Task task) {
        String sql = """
                UPDATE tasks
                   SET title       = ?,
                       description = ?,
                       done        = ?
                 WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.title());
            ps.setString(2, task.description());
            ps.setInt(3, task.done() ? 1 : 0);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update task id=" + id, e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private List<Task> queryList(String sql) {
        List<Task> result = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query: " + sql, e);
        }
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        return new Task(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("done") == 1
        );
    }

    private int count() {
        String sql = "SELECT COUNT(*) FROM tasks";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            return 0;
        }
    }
}