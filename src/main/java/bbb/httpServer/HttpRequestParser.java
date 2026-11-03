package bbb.HttpServer;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestParser {

    private static Map<String, String> parseHeaders(HttpStream reader) throws IOException {
        Map<String, String> headers = reader.lines()
            .map(s -> s.split(": ", 2))
            .collect(Collectors.toMap(p -> p[0].toLowerCase(), p -> p[1]));
        
        return headers;
    }

    private static String parseBody(HttpStream reader, Map<String, String> headers) throws IOException {
        
        int contentLength = 0;
        if (headers.containsKey("content-length")) {
            contentLength = Integer.parseInt(headers.get("content-length"));
        }
        
        if (contentLength > 0) {
            String body = reader.readChars(contentLength);
            return body;
        }
        
        return null;
    }
    
    public static HttpRequest parseRequest(HttpStream reader) throws IOException {
        String reqLine = reader.readLine();
        System.out.println("Request Line: " + reqLine);
        
        String[] tokens = reqLine.split(" ");
       
        HttpRequest.Method method = HttpRequest.Method.valueOf(tokens[0]);
        String uri = tokens[1];
        // tokens[2] is HTTP version

        Map<String, String> headers = parseHeaders(reader);

        String body = method == HttpRequest.Method.POST ? parseBody(reader,headers) : null;

        System.out.println();
        return new HttpRequest(method,uri,headers,body);
    }
}
