package bbb.HttpServer;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseParser {
    private static Map<String, String> parseHeaders(HttpStream reader) throws IOException {
        Map<String, String> headers = reader.lines()
            .map(s -> s.split(": ", 2))
            .collect(Collectors.toMap(p -> p[0].toLowerCase(), p -> p[1]));
        
        return headers;
    }

    private static String parseBody(HttpStream reader, Map<String, String> headers) throws IOException {
        int contentLength = 0;

        if (headers.containsKey("content-length"))
            contentLength = Integer.parseInt(headers.get("content-length"));
        
        if (contentLength > 0) {
            String body = reader.readChars(contentLength);
            return body;
        }
        
        return null;
    }
    
    public static HttpResponse parseResponse(HttpStream reader) throws IOException {
        String statusLine = reader.readLine();
        
        String[] tokens = statusLine.split(" ", 3);
       
        // tokens[0] is HTTP version, tokens[2] is reason phrase
        // HTTP/1.1 200 OK
        HttpResponse.Status status = HttpResponse.Status.fromCode(Integer.parseInt(tokens[1]));

        Map<String, String> headers = parseHeaders(reader);

        String body = parseBody(reader, headers);

        return new HttpResponse(status, headers, body);
    } 
}
