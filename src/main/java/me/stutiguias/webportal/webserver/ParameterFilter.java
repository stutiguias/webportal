package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ParameterFilter extends Filter {

    @Override
    public String description() {
        return "Parses the requested URI for parameters and supports various content types.";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        try {
            parseGetParameters(exchange, parameters);
            parsePostParameters(exchange, parameters);
        } catch (IllegalArgumentException ex) {
            sendBadRequest(exchange, "Invalid request parameters.");
            return;
        }
        exchange.setAttribute("parameters", parameters);
        chain.doFilter(exchange);
    }

    private void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.getResponseHeaders().set("Connection", "Close");
        exchange.sendResponseHeaders(400, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }

    private void parseGetParameters(HttpExchange exchange, Map<String, Object> parameters) throws IOException {
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
    }

    private void parsePostParameters(HttpExchange exchange, Map<String, Object> parameters) throws IOException {
        String method = exchange.getRequestMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method) && !"PATCH".equalsIgnoreCase(method)) {
            return;
        }

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        InputStream inputStream = exchange.getRequestBody();
        String body;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
            body = scanner.hasNext() ? scanner.next() : "";
        }

        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            parseQuery(body, parameters);
        } else if (contentType != null && contentType.contains("application/json")) {
            parseJson(body, parameters);
        }
    }

    private void parseJson(String body, Map<String, Object> parameters) throws IOException {
        // JSON IF NEED IT !
    }

    private void parseQuery(String query, Map<String, Object> parameters) throws IOException {
        if (query == null || query.isEmpty()) {
            return;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair == null || pair.isEmpty()) {
                continue;
            }
            int idx = pair.indexOf("=");
            if (idx < 0) {
                String key = URLDecoder.decode(pair, StandardCharsets.UTF_8.name());
                putParameter(parameters, key, "");
                continue;
            }
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());

            putParameter(parameters, key, value);
        }
    }

    private void putParameter(Map<String, Object> parameters, String key, String value) {
        if (key == null || key.isEmpty()) {
            return;
        }

        if (!parameters.containsKey(key)) {
            parameters.put(key, value);
            return;
        }

        Object current = parameters.get(key);
        if (current instanceof List<?>) {
            ((List<String>)current).add(value);
        } else {
            List<String> values = new ArrayList<>();
            if (current != null) {
                values.add(String.valueOf(current));
            }
            values.add(value);
            parameters.put(key, values);
        }
    }
}
