package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public interface IRequestHandler {
    void handle(HttpExchange exchange, Map<String, Object> params) throws IOException;
    boolean isPublic();
}
