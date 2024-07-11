package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.TimeConflictException;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager manager = Managers.getDefaultTask();

    @Override
    public  void handle(HttpExchange exchange) throws IOException {
        try {
            handleRequest(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (TimeConflictException exception) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    protected abstract void handleRequest(HttpExchange exchange) throws IOException;

    protected void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }


    // Метод для отправки ответа в случае успеха
    protected void sendText(HttpExchange exchange, int statusCode, String text) throws IOException{
        byte [] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode,resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    //Метод для отправки ответа, если ответ не найден
    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "404 (Not Found)";
        sendText(exchange,404,response);
    }

    // Метод для отправки ответа, если при создании или обновлении задача пересекается с уже существующими
    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "406 (Not Acceptable)";
        sendText(exchange,406,response);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = "500 (Internal Server Error)";
        sendText(exchange,500,response);
    }
}
