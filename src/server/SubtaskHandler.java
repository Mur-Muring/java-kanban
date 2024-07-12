package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeConflictException;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    handleGet(exchange);
                    break;
                }
                case "POST": {
                    handlePost(exchange);
                    break;
                }
                case "DELETE": {
                    handleDelete(exchange);
                    break;
                }
                default: {
                    sendNotFound(exchange, "Несуществующий ресурс");
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private int parsePathID(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (Pattern.matches("^/subtasks$", path)) {
            try {
                String response = gson.toJson(taskManager.getAllSubtasks());
                sendText(exchange, response);
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        }
        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            try {
                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathID(pathId);
                if (id != -1) {
                    String response = gson.toJson(taskManager.getByIdSubtask(id));
                    sendText(exchange, response);
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Подзадача не найдена");
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (Pattern.matches("^/subtasks$", path)) {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                taskManager.addSubtask(subtask);
                sendAdd(exchange, "Подзадача создана");
            } catch (TimeConflictException exception) {
                sendHasInteractions(exchange, "Данное время занято");
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Эпик не найден");
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        }
        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            try {
                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathID(pathId);
                if (id != -1) {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    taskManager.updateTask(subtask);
                    sendAdd(exchange, "Подзадача обновлена");
                }
            } catch (TimeConflictException e) {
                sendHasInteractions(exchange, "Данное время занято");
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (Pattern.matches("^/tasks/\\d+$", path)) {
            try {
                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathID(pathId);
                if (id != -1) {
                    taskManager.deleteByIdSubtask(id);
                    sendText(exchange, "Подзадача удалена");
                }
            } catch (NotFoundException exception) {
                sendNotFound(exchange, "Подзадача не найдена");
            } catch (Exception exception) {
                sendInternalServerError(exchange);
            }
        }
    }
}