package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        parseGetParameters(exchange, parameters);
        parsePostParameters(exchange, parameters);
        exchange.setAttribute("parameters", parameters);
        chain.doFilter(exchange);
    }

    private void parseGetParameters(HttpExchange exchange, Map<String, Object> parameters) throws IOException {
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
    }

    private void parsePostParameters(HttpExchange exchange, Map<String, Object> parameters) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        InputStream inputStream = exchange.getRequestBody();
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "";

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
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());

            if (!parameters.containsKey(key)) {
                parameters.put(key, value);
            } else {
                Object current = parameters.get(key);
                if (current instanceof List<?>) {
                    ((List<String>)current).add(value);
                } else {
                    List<String> values = new ArrayList<>();
                    values.add((String)current);
                    values.add(value);
                    parameters.put(key, values);
                }
            }
        }
    }
}
