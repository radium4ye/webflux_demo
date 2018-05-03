package com.radium4ye.webflux.controller;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author radium4ye
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    Mono<String> hello(ServerHttpRequest request) {
        return Mono.just("hello world.");
    }

}
