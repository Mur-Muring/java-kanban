package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler{
    private static final Gson gson = new Gson();
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            handleGet(exchange);
        } else {
            sendInternalError(exchange);
        }
    }
    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> history = manager.getHistory();
        String response = gson.toJson(history);
        sendResponse(exchange, response);
    }
}
