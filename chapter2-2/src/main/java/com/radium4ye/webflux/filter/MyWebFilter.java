package com.radium4ye.webflux.filter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author radium4ye
 */
@Component
@Order(0)
public class MyWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain next) {
        System.out.println("Before handler invocation: " + exchange.getRequest().getPath().pathWithinApplication());
        return next.filter(exchange);
    }
}