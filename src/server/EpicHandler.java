package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeConflictException;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager, Gson gson) {
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
        if (Pattern.matches("^/epics$", path)) {
            try {
                String response = gson.toJson(taskManager.getAllEpics());
                sendText(exchange, response);
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        }
        if (Pattern.matches("^/epics/\\d+$", path)) {
            try {
                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathID(pathId);
                if (id != -1) {
                    String response = gson.toJson(taskManager.getByIdEpic(id));
                    sendText(exchange, response);
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Ошибка: Эпик не найден");
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        }
        if (Pattern.matches("^/subtasks$", path)) {
            try {
                String pathId = path.replaceFirst("/epics/", "").replaceFirst("/subtasks$", "");
                int id = parsePathID(pathId);
                if (id != -1) {
                    Epic epic = taskManager.getByIdEpic(id);
                    String response = gson.toJson(taskManager.getSubtasksEpic(epic.getIdTask()));
                    sendText(exchange, response);
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Эпик не найден");
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (Pattern.matches("^/epics$", path)) {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                taskManager.addEpic(epic);
                sendAdd(exchange, "Эпик создан");
            } catch (TimeConflictException exception) {
                sendHasInteractions(exchange, "Данное время занято");
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        }
        if (Pattern.matches("^/epics/\\d+$", path)) {
            try {
                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathID(pathId);
                if (id != -1) {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);
                    taskManager.updateEpic(epic);
                    sendAdd(exchange, "Эпик обновлен");
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

        if (Pattern.matches("^/epics/\\d+$", path)) {
            try {
                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathID(pathId);
                if (id != -1) {
                    taskManager.deleteByIdEpic(id);
                    System.out.println("Удален эпик под индексом " + id);
                    exchange.sendResponseHeaders(200, 0);
                }
            } catch (NotFoundException exception) {
                sendNotFound(exchange, "Эпик не найден");
            } catch (Exception exception) {
                sendInternalServerError(exchange);
            }
        }
    }

}