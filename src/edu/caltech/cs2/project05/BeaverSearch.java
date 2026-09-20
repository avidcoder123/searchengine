package edu.caltech.cs2.project05;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class BeaverSearch {
    public static final int PORT = 8001;
    private static JSONObject database;

    private static SearchEngine searchEngine;

    static class LocalFile implements HttpHandler {
        private String name;

        public LocalFile(String name) {
            this.name = name;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = new String(Files.readAllBytes(Paths.get(this.name)), StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        try {
            database = (JSONObject) JSON.parse(Files.readString(Paths.get("./tests/data/wikipedia.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        searchEngine = new SearchEngine(database);
        long end = System.currentTimeMillis();
        System.out.println("Creating search engine from database took " + (end - start) + " millis.");

        String parentPath = "./src/edu/caltech/cs2/project05/";

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new LocalFile(parentPath + "search.html"));
        server.createContext("/Logo.png", new PngHandler(parentPath + "Logo.png"));
        server.createContext("/search.css", new LocalFile(parentPath + "search.css"));
        server.createContext("/search.js", new LocalFile(parentPath + "search.js"));
        server.createContext("/byquery", new AllSearch());

        server.start();
        System.out.println("Server started!");

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://localhost:" + PORT + "/"));
        }
    }

    static class AllSearch implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Optional<String> query = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("query=")).map(x -> x.split("=")[1]).findAny();
            String response = "[]";

            if (query.isPresent()) {
                String queryStr = query.get();
                ArrayList<String> results = searchEngine.search(queryStr, 15);
                response = formatResultString(results);
            }

            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String formatResultString(ArrayList<String> results) {
            String resp = "[";
            for (String elem : results) {
                resp += elem + "\u0d9e";
            }
            if (resp.length() > 1) {
                resp = resp.substring(0, resp.length()-1);
            }
            return resp + "]";
        }
    }

    //thanks Google AI
    static class PngHandler implements HttpHandler {
        private final String filePath;

        public PngHandler(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File file = new File(filePath);

            if (!file.exists() || !file.isFile()) {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            // Set the Content-Type header to image/png
            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, file.length()); // Set content length

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = exchange.getResponseBody()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            exchange.close();
        }
    }
}
