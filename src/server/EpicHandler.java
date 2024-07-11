package server;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
public class EpicHandler extends BaseHttpHandler {
    private static final Gson gson = new Gson();
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        if (segments.length == 2) {
            switch (method) {
                case "GET":
                    handleGetAll(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDeleteAll(exchange);
                    break;
                default:
                    sendInternalError(exchange);
            }
        } else if (segments.length == 3) {
            int id = Integer.parseInt(segments[2]);
            switch (method) {
                case "GET":
                    handleGetById(exchange, id);
                    break;
                case "DELETE":
                    handleDeleteById(exchange, id);
                    break;
                default:
                    sendInternalError(exchange);
            }
        } else if (segments.length == 4 && "subtasks".equals(segments[3])) {
            int id = Integer.parseInt(segments[2]);
            if ("GET".equals(method)) {
                handleGetSubtasksByEpicId(exchange, id);
            } else {
                sendInternalError(exchange);
            }
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        String response = gson.toJson(epics);
        sendResponse(exchange, response);
    }

    private void handleGetById(HttpExchange exchange, int id) throws IOException {
       try {
           Epic epic = manager.getByIdEpic(id);

            String response = gson.toJson(epic);
            sendResponse(exchange, response);
       } catch (NotFoundException e){
           sendNotFound(exchange);
       }

    }

    private void handleGetSubtasksByEpicId(HttpExchange exchange, int id) throws IOException {
        try {
            List<Subtask> subtasks = manager.getSubtasksEpic(id);

            String response = gson.toJson(subtasks);
            sendResponse(exchange, response);
        } catch (NotFoundException e){
            sendNotFound(exchange);
        }

    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Epic epic = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Epic.class);
        if (epic.getIdTask() == null) {
            manager.addEpic(epic);
        } else {
            manager.updateEpic(epic);
        }
        sendText(exchange, 201, "Эпик успешно добавлен или обновлен");
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteEpics();
        sendText(exchange, 200, "Все эпики удалены");
    }

    private void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        manager.deleteByIdEpic(id);
        sendText(exchange, 200, "Эпик с ID " + id + " удален");
    }
}
