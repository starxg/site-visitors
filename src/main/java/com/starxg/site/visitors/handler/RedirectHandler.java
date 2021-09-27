package com.starxg.site.visitors.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * RedirectHandler
 * 
 * @author huangxingguang@lvmama.com
 */
public class RedirectHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Location", "https://github.com/starxg/site-visitors");
        exchange.sendResponseHeaders(302, 0);
    }
}
