package server;


import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;


import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.server =  HttpServer.create(new InetSocketAddress(PORT),0);
        this.manager=manager;
        createHandlers();
    }

    private void  createHandlers(){
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/tasks/{id}", new TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/subtasks/{id}",new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/epics/{id}", new EpicHandler(manager));
        server.createContext("/epics/{id}/subtasks", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
        server.setExecutor(null);
    }

    public void start(){
        server.start();
        System.out.println("Сервер запущен на порту: "+PORT);
    }

    public void stop(){
        server.stop(0);
        System.out.println("Сервер остановлен на порту: "+PORT);
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = new InMemoryTaskManager();
        HttpTaskServer httpTaskServer=new HttpTaskServer(manager);
        httpTaskServer.start();
    }

}
