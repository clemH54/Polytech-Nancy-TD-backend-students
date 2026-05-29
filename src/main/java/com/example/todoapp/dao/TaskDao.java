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
    /** Initialisation de la table*/
    private void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY,
                    title   TEXT    NOT NULL,
                    description TEXT,
                    done    INTEGER NOT NULL DEFAULT 0
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

        save(new Task(1, "Réviser DS de maths",  "Séries numériques et probabilités.", false));
        save(new Task(2, "Valider mon PIVE",      "PIVE Club Poker.",                  true));
        save(new Task(3, "Choisir mon parcours de 4A", "SIR ou SIA ?",                false));
    }

    public Task save(Task task) {
        String sql = """
                INSERT OR REPLACE INTO tasks (id, title, description, done)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, task.id());
            ps.setString(2, task.title());
            ps.setString(3, task.description());
            ps.setInt(4, task.done() ? 1 : 0);
            ps.executeUpdate();
            return task;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save task id=" + task.id(), e);
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
                   SET title   = ?,
                       description = ?,
                       done    = ?
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
            // Table may not exist yet — treated as empty.
            return 0;
        }
    }
}