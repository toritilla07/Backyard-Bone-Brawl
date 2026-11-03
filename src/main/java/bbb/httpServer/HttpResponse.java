package bbb.HttpServer;

import java.util.Map;

public class HttpResponse {
    public enum Status {
        OK(200, "OK"),
        NO_CONTENT(204, "No Content"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_ERROR(500, "Internal Server Error");

        private final int code;
        private final String reason;

        private Status(int code, String reason) {
            this.code = code;
            this.reason = reason;
        }

        public int getCode() { return code; }
        public String getReason() { return reason; }

        @Override
        public String toString() {
            return code + " " + reason;
        }

        public static Status fromCode(int code) {
            for (Status status : values()) {
                if (status.code == code)
                    return status;
            }
            return null;
        }
    }

    private final Status status;
    private final Map<String,String> headers;
    private final String body;

    public HttpResponse(Status status, Map<String,String> headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public Status getStatus() { return status; }
    public String getHeader(String name) { return headers.get(name); }
    public String getBody() { return body; }

    @Override
    public String toString() {
        return "HTTPResponse {status: " + status + ", " +
               "headers: " + headers + ", " +
               "body: " + body + "}";
    }
}
