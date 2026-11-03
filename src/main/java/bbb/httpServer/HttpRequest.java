package bbb.HttpServer;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public enum Method {
        GET,
        POST,
        OPTIONS
    }

    private final Method method;
    private final String uri;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(Method method, String uri, Map<String,String> headers, String body) {
        this.method = method;
        this.uri = uri;
        this.headers = new HashMap<>(headers);
        this.body = body;
    }

    public Method getMethod() { return method; }
    public String getUri() { return uri;}
    public String getHeader(String name) { return headers.get(name); }
    public String getBody() { return body; }

    @Override
    public String toString() {
        return "HTTPRequest {method: " + method + ", " +
               "uri: " + uri + ", " +
               "headers: " + headers + ", " +
               "body: " + body + "}";
    }
}
