package bbb.HttpServer;

public class HttpRequestBuilder {
    public static String buildGetRequest(String uri, String sessionId) {
        return "GET " + uri + " HTTP/1.1\r\n" +
               "x-session-id: " + sessionId + "\r\n" +
               "\r\n";
    }

    public static String buildPostRequest(String uri, String sessionId, String body) {
        return "POST " + uri + " HTTP/1.1\r\n" +
               "x-session-id: " + sessionId + "\r\n" +
               "Content-Length: " + body.length() + "\r\n" +
               "Content-Type: text/plain; charset=utf-8\r\n\r\n" +
               body;
    }
}
