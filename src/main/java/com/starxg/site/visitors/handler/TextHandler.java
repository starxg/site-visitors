
package com.starxg.site.visitors.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.starxg.site.visitors.counter.Counter;
import com.sun.net.httpserver.HttpExchange;

/**
 * TextHandler
 * 
 * @author huangxingguang
 */
public class TextHandler extends BaseHandler {

    public TextHandler(Counter counter) {
        super(counter);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        exchange.getResponseHeaders().set("Content-Type", "text/plain");

        final long count = incr(exchange);

        exchange.sendResponseHeaders(200, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(String.valueOf(count).getBytes(StandardCharsets.UTF_8));
        }

    }
}
