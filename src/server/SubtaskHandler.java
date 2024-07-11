package server;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeConflictException;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
public class SubtaskHandler extends BaseHttpHandler {
    private static final Gson gson = new Gson();
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
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
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getAllSubtasks();
        String response = gson.toJson(subtasks);
        sendResponse(exchange, response);
    }

    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        try {
            Subtask subtask = manager.getByIdSubtask(id);
            String response = gson.toJson(subtask);
            sendResponse(exchange, response);
        } catch (NotFoundException e){
            sendNotFound(exchange);

        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Subtask.class);
            if (subtask.getIdTask() == null) {
                manager.addSubtask(subtask);
            } else {
                manager.updateSubtask(subtask);
            }
            sendText(exchange, 201, "Подзадача успешно добавлена или обновлена");
        } catch (TimeConflictException e){
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteSubtasks();
        sendText(exchange, 200, "Все подзадачи удалены");
    }

    private void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        manager.deleteByIdSubtask(id);
        sendText(exchange, 200, "Подзадача с ID " + id + " удалена");
    }
}
