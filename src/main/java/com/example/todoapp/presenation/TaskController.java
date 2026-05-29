package com.example.todoapp.presenation;

import com.example.todoapp.JsonUtils;
import com.example.todoapp.business.model.Task;
import com.example.todoapp.business.service.TaskService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;

public class TaskController {

    private static final Pattern ID_PATH = Pattern.compile("^/tasks/([0-9]+)$");

    private final TaskService service = new TaskService();

    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();

        // POST /tasks
        if ("POST".equals(method) && "/tasks".equals(path)) {
            Task input = JsonUtils.deserialize(
                    new String(exchange.getRequestBody().readAllBytes(), UTF_8), Task.class);
            Task created = service.create(input);

            exchange.getResponseHeaders().add("Location", "/tasks/" + created.id());
            sendResponse(exchange, 201, JsonUtils.serialize(created));
            return;
        }

        Matcher m = ID_PATH.matcher(path);

        // GET /tasks/{id}
        if ("GET".equals(method) && m.matches()) {
            int id = Integer.parseInt(m.group(1));
            Optional<Task> task = service.findById(id);

            if (task.isPresent()) {
                sendResponse(exchange, 200, JsonUtils.serialize(task.get()));
            } else {
                sendResponse(exchange, 404, null);
            }
            return;
        }

        // DELETE /tasks/{id}
        m = ID_PATH.matcher(path);
        if ("DELETE".equals(method) && m.matches()) {
            int id = Integer.parseInt(m.group(1));
            boolean deleted = service.delete(id);

            sendResponse(exchange, deleted ? 204 : 404, null);
            return;
        }

        // PUT /tasks/{id}
        m = ID_PATH.matcher(path);
        if ("PUT".equals(method) && m.matches()) {
            int id = Integer.parseInt(m.group(1));
            Task input = JsonUtils.deserialize(
                    new String(exchange.getRequestBody().readAllBytes(), UTF_8), Task.class);
            boolean updated = service.update(id, input);

            sendResponse(exchange, updated ? 204 : 404, null);
            return;
        }

        // GET /tasks  (+ ?todo-only=true)
        if ("GET".equals(method) && "/tasks".equals(path)) {
            String query    = exchange.getRequestURI().getQuery();
            boolean todoOnly = query != null && query.contains("todo-only=true");

            List<Task> tasks = todoOnly ? service.findAllTodo() : service.findAll();

            if (tasks.isEmpty()) {
                sendResponse(exchange, 204, null);
            } else {
                sendResponse(exchange, 200, JsonUtils.serialize(tasks));
            }
            return;
        }

        sendResponse(exchange, 404, null);
    }

    private void sendResponse(HttpExchange exchange, int status, String json) throws IOException {
        if (nonNull(json)) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            byte[] bytes = json.getBytes(UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            exchange.sendResponseHeaders(status, 0);
            exchange.close();
        }
    }
}