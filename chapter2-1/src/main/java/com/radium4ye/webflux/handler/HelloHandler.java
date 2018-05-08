package com.radium4ye.webflux.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * hello 处理器
 *
 * @author radium4ye
 */
@Component
public class HelloHandler {

    /**
     * 输出一个hello world
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("hello world."), String.class);
    }

    public Mono<ServerResponse> hi(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("hi world."), String.class);
    }
}
