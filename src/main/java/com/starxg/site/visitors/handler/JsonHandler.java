package com.starxg.site.visitors.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.starxg.site.visitors.counter.Counter;
import com.sun.net.httpserver.HttpExchange;

/**
 * JsonHandler
 * 
 * @author huangxingguang
 */
public class JsonHandler extends BaseHandler {


    public JsonHandler(Counter counter) {
        super(counter);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        super.handle(exchange);

        String response = String.format("{\"count\":%s}", incr(exchange));

        exchange.sendResponseHeaders(200, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }


    }
}
