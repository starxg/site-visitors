package com.starxg.site.visitors.handler;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.starxg.site.visitors.counter.Counter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * BaseHandler
 * 
 * @author huangxingguang
 */
abstract class BaseHandler implements HttpHandler {

    private final Counter counter;

    public BaseHandler(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Expose-Headers", "*");
    }

    protected long incr(HttpExchange exchange) {
        return counter.incr(hash(getPageId(exchange)));
    }

    private String getPageId(HttpExchange exchange) {
        Map<String, String> params = parseQueryString(exchange.getRequestURI().getQuery());

        String pageId = params.get("page_id");
        if (Objects.isNull(pageId)) {
            pageId = exchange.getRequestHeaders().getFirst("Referer");
        }

        if (Objects.isNull(pageId)) {
            throw new IllegalArgumentException("page_id must not be null");
        }

        return pageId;
    }

    protected static Map<String, String> parseQueryString(String qs) {
        if (Objects.isNull(qs) || qs.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();

        int last = 0, next, l = qs.length();
        while (last < l) {
            next = qs.indexOf('&', last);
            if (next == -1) {
                next = l;
            }
            if (next > last) {
                int eqPos = qs.indexOf('=', last);
                if (eqPos < 0 || eqPos > next) {
                    result.put(URLDecoder.decode(qs.substring(last, next), StandardCharsets.UTF_8), "");
                } else {
                    result.put(URLDecoder.decode(qs.substring(last, eqPos), StandardCharsets.UTF_8),
                            URLDecoder.decode(qs.substring(eqPos + 1, next), StandardCharsets.UTF_8));
                }
            }
            last = next + 1;
        }
        return result;
    }

    /**
     * encode
     */
    public static String hash(String plainText) {
        byte[] secretBytes;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        StringBuilder sb = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        for (int i = 0; i < 32 - sb.length(); i++) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }
}
