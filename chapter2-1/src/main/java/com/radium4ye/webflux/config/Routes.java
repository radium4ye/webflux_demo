package com.radium4ye.webflux.config;

import com.radium4ye.webflux.handler.HelloHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author yeweilei
 */
@Configuration
public class Routes {

    @Autowired
    private HelloHandler helloHandler;

    @Bean
    public RouterFunction<?> routerFunction() {
        return nest(
                path("/hello"),
                route(GET("").or(GET("/world")), helloHandler::hello)
        )
                .andRoute(GET("/hi"), helloHandler::hi)
                .filter((request, next) -> {
                    System.out.println("Before handler invocation: " + request.path());
                    return next.handle(request);
                })
                ;
    }
}