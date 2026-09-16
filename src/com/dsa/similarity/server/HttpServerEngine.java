package com.dsa.similarity.server;

import com.dsa.similarity.engine.SimilarityAnalyzer;
import com.dsa.similarity.model.AnalysisReport;
import com.dsa.similarity.model.BatchReport;
import com.dsa.similarity.model.Submission;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Lightweight Zero-Dependency HTTP Server using standard Java SE HttpServer.
 * Serves the modern Web Dashboard and provides REST API endpoints.
 */
public class HttpServerEngine {

    private final int port;
    private final File webRoot;
    private HttpServer server;

    public HttpServerEngine(int port, File webRoot) {
        this.port = port;
        this.webRoot = webRoot;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        File dataRoot = new File(webRoot.getParentFile().getParentFile(), "07_Data");

        // API Contexts
        server.createContext("/api/analyze", new AnalyzeHandler());
        server.createContext("/api/batch", new BatchHandler());
        server.createContext("/api/health", new HealthHandler());

        // Static Web Resource Context
        server.createContext("/data/", new StaticFileHandler(dataRoot, "/data"));
        server.createContext("/", new StaticFileHandler(webRoot, ""));

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("==================================================================");
        System.out.println(" [DSA-PROJECT] Server running at: http://localhost:" + port);
        System.out.println(" Serving Dashboard from: " + webRoot.getAbsolutePath());
        System.out.println(" Open http://localhost:" + port + " in your browser to view the UI!");
        System.out.println("==================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    static class AnalyzeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String body = readRequestBody(exchange);
            String nameA = extractJsonField(body, "candidateA", "Candidate_A");
            String codeA = extractJsonField(body, "codeA", "");
            String nameB = extractJsonField(body, "candidateB", "Candidate_B");
            String codeB = extractJsonField(body, "codeB", "");

            if (codeA.trim().isEmpty() || codeB.trim().isEmpty()) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Both codeA and codeB must be provided.\"}");
                return;
            }

            Submission subA = new Submission("sub_a", nameA, codeA);
            Submission subB = new Submission("sub_b", nameB, codeB);

            AnalysisReport report = SimilarityAnalyzer.compare(subA, subB);
            sendJsonResponse(exchange, 200, report.toJson());
        }
    }

    static class BatchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String body = readRequestBody(exchange);
            List<Submission> submissions = parseBatchSubmissions(body);

            if (submissions.size() < 2) {
                sendJsonResponse(exchange, 400, "{\"error\":\"At least 2 submissions required for batch analysis.\"}");
                return;
            }

            BatchReport batchReport = SimilarityAnalyzer.compareBatch(submissions);
            sendJsonResponse(exchange, 200, batchReport.toJson());
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String resp = "{\"status\":\"healthy\",\"course\":\"DSA-3 (25CS2103E)\",\"engine\":\"Pure-Java-Algorithms-Engine\"}";
            sendJsonResponse(exchange, 200, resp);
        }
    }

    static class StaticFileHandler implements HttpHandler {
        private final File baseDir;
        private final String routePrefix;

        public StaticFileHandler(File baseDir, String routePrefix) {
            this.baseDir = baseDir;
            this.routePrefix = routePrefix;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }
            if (!routePrefix.isEmpty() && path.startsWith(routePrefix)) {
                path = path.substring(routePrefix.length());
            }

            File target = new File(baseDir, path.replace('/', File.separatorChar));
            if (!target.exists() || target.isDirectory()) {
                String notFound = "<h1>404 Not Found</h1><p>The requested file does not exist.</p>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String contentType = getMimeType(target.getName());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            byte[] bytes = Files.readAllBytes(target.toPath());
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String getMimeType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            return "text/plain; charset=UTF-8";
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int r;
            while ((r = is.read(buf)) != -1) {
                baos.write(buf, 0, r);
            }
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Robust zero-dependency JSON field extractor.
     */
    public static String extractJsonField(String json, String field, String defaultValue) {
        if (json == null) return defaultValue;
        String pattern = "\"" + field + "\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) return defaultValue;

        int colon = json.indexOf(':', idx + pattern.length());
        if (colon == -1) return defaultValue;

        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;

        if (start < json.length() && json.charAt(start) == '"') {
            start++;
            StringBuilder sb = new StringBuilder();
            boolean escaped = false;
            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);
                if (escaped) {
                    switch (c) {
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        default: sb.append(c); break;
                    }
                    escaped = false;
                } else {
                    if (c == '\\') {
                        escaped = true;
                    } else if (c == '"') {
                        return sb.toString();
                    } else {
                        sb.append(c);
                    }
                }
            }
            return sb.toString();
        }
        return defaultValue;
    }

    /**
     * Parses batch candidate objects from JSON payload.
     * Looks for either "submissions" array or direct array of objects.
     */
    public static List<Submission> parseBatchSubmissions(String json) {
        List<Submission> list = new ArrayList<>();
        if (json == null || json.isEmpty()) return list;

        int pos = 0;
        int subCount = 1;

        while (pos < json.length()) {
            int nameIdx = json.indexOf("\"name\"", pos);
            if (nameIdx == -1) break;

            // Find closing brace of this object
            int openBrace = json.lastIndexOf('{', nameIdx);
            if (openBrace == -1) break;

            // Find matching closing brace
            int depth = 0;
            int closeBrace = -1;
            for (int i = openBrace; i < json.length(); i++) {
                if (json.charAt(i) == '{') depth++;
                else if (json.charAt(i) == '}') {
                    depth--;
                    if (depth == 0) {
                        closeBrace = i;
                        break;
                    }
                }
            }

            if (closeBrace == -1) break;

            String objStr = json.substring(openBrace, closeBrace + 1);
            String name = extractJsonField(objStr, "name", "Candidate " + subCount);
            String code = extractJsonField(objStr, "code", "");

            if (!code.trim().isEmpty()) {
                list.add(new Submission("sub_" + subCount, name, code));
                subCount++;
            }

            pos = closeBrace + 1;
        }

        return list;
    }
}
