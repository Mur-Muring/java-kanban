package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeConflictException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class TaskHandler extends BaseHttpHandler{
    private static final Gson gson = new Gson();
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        try {
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
        } catch (Exception e){
            e.printStackTrace();
            sendInternalError(exchange);
        }
        }


    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Task> taskList = manager.getAllTasks();
        String resp = gson.toJson(taskList);
        sendResponse(exchange, resp);
    }

    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        try {
            Task task = manager.getByIdTask(id);
            String resp = gson.toJson(task);
            sendResponse(exchange, resp);
        } catch (NotFoundException e){
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Task.class);
            if (task.getIdTask() == null) {
                manager.addTask(task);
            } else {
                manager.updateTask(task);
            }
            sendText(exchange, 201, "Задача успешно добавлена или обновлена");
        } catch (TimeConflictException e){
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteTasks();
        sendText(exchange, 200, "Все задачи удалены");
    }

    private void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        manager.deleteByIdTask(id);
        sendText(exchange, 200, "Задача с ID " + id + " удалена");
    }
}