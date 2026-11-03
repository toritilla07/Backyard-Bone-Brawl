package bbb.HttpServer;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;

public class HttpStream implements Closeable {

    private final BufferedReader br;

    public HttpStream(InputStream in) {
        this.br = new BufferedReader(new InputStreamReader(in));
    }

    public Stream<String> lines() {
        return br.lines()
                .takeWhile(line -> !line.isEmpty())
                .onClose(() -> {
                // IMPORTANT: do NOT close br (would close socket).
                });
    }

    @Override public void close() throws IOException {
        br.close();
    }

    public String readLine () {
        try {
            return br.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String readChars(int n) {
        char[] buffer = new char[n];
        try {
            int read = br.read(buffer);
            if (read == -1) {
                return null;
            }   
            return new String(buffer, 0, read);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(8001);
        System.out.println("Listening on " + 8001);
        Socket clientSocket = serverSocket.accept();
        try (HttpStream streamer = new HttpStream(clientSocket.getInputStream())) {
            streamer.lines().forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
        serverSocket.close();
    }
}
