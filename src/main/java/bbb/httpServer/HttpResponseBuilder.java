package bbb.HttpServer;

public class HttpResponseBuilder {
    public static String buildSuccessResponse(String content) {
        return "HTTP/1.1 200 OK\r\n" 
                + "Access-Control-Allow-Origin: *\r\n"
                + "Access-Control-Allow-Headers: x-session-id\r\n"
                + "Content-Length: " + content.length() + "\r\n"
                + "Content-Type: text/html; charset=utf-8\r\n\r\n"
                + content
                + "\r\n";
    }  
    
    public static String buildOptionsResponse() {
        return "HTTP/1.1 204 No Content\r\n"
                + "Access-Control-Allow-Origin: *\r\n"
                + "Access-Control-Allow-Headers: x-session-id\r\n\r\n";
    }
    
    public static String buildBadRequestErrorResponse(String message) {
        return "HTTP/1.1 400 Bad Request\r\n"
                + "Access-Control-Allow-Origin: *\r\n"
                + "Access-Control-Allow-Headers: x-session-id\r\n"
                + "Content-Length: " + message.length() + "\r\n"
                + "Content-Type: text/plain; charset=utf-8\r\n\r\n"
                + message
                + "\r\n";
    }

    public static String buildNotFoundErrorResponse(String message) {
        return "HTTP/1.1 404 Not Found\r\n"
                + "Access-Control-Allow-Origin: *\r\n"
                + "Access-Control-Allow-Headers: x-session-id\r\n"
                + "Content-Length: " + message.length() + "\r\n"
                + "Content-Type: text/plain; charset=utf-8\r\n\r\n"
                + message
                + "\r\n";
    }
}