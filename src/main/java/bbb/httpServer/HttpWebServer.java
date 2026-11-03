package bbb.HttpServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* Multi-threaded HTTP web server implementation */
public class HttpWebServer {
    private final int port;
    private final Supplier<RequestHandler> supplier;
    private final Map<String, RequestHandler> handlers;
    private final Lock handlersLock;

    public HttpWebServer(int port, Supplier<RequestHandler> handler) {
        this.port = port;
        this.supplier = handler;
        handlers = new HashMap<>();
        handlersLock = new ReentrantLock();
    }

    private void sendResponse(PrintWriter writer, String response) throws IOException {
        writer.println(response);
        writer.flush();
        System.out.println("Response: " + response);
    }

    private RequestHandler getRequestHandler(String clientId) {
        try {
            handlersLock.lock();
            if (handlers.containsKey(clientId)) {
                return handlers.get(clientId);
            } else {
                RequestHandler handler = this.supplier.get();
                handlers.put(clientId, handler);
                return handler;
            }
        } finally {
            handlersLock.unlock();
        }
    }
    public void handleClient(Socket clientSocket) {
        try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            HttpStream reader = new HttpStream(clientSocket.getInputStream())) {
            HttpRequest request = HttpRequestParser.parseRequest(reader);

            if (request.getMethod() == HttpRequest.Method.OPTIONS) {
                sendResponse(writer, HttpResponseBuilder.buildOptionsResponse());
                return;
            }

            String clientId = request.getHeader("x-session-id");
            RequestHandler handler = getRequestHandler(clientId);

            String response = handler.handleRequest(request);
            if (response == null) {
                sendResponse(writer,HttpResponseBuilder.buildNotFoundErrorResponse(request.getUri() + " not found"));
            } else {
                sendResponse(writer,HttpResponseBuilder.buildSuccessResponse(response));
            }
            
            clientSocket.close();

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    
    public void start() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit (()->handleClient(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}

