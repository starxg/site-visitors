
package com.starxg.site.visitors.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.starxg.badge4j.Badge;
import com.starxg.site.visitors.counter.Counter;
import com.sun.net.httpserver.HttpExchange;

/**
 * BadgeHandler
 * 
 * @author huangxingguang
 */
public class BadgeHandler extends BaseHandler {

    public BadgeHandler(Counter counter) {
        super(counter);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set("Content-Type", "image/svg+xml");

        super.handle(exchange);

        final Map<String, String> params = parseQueryString(exchange.getRequestURI().getQuery());

        final String lt = params.getOrDefault("left_text", "visitors");
        final String lc = params.getOrDefault("left_color", "#595959");
        final String rc = params.getOrDefault("right_color", "#1283c3");

        final long count = incr(exchange);

        String svg = "<xml>error</xml>";
        try {
            svg = Badge.create(lt, String.valueOf(count), lc, rc);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        exchange.sendResponseHeaders(200, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(svg.getBytes(StandardCharsets.UTF_8));
        }

    }

}
