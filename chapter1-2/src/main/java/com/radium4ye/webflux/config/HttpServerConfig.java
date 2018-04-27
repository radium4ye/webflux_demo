package com.radium4ye.webflux.config;

import io.undertow.Undertow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;


/**
 * @author yeweilei
 */
@Configuration
public class HttpServerConfig {

    @Autowired
    private Environment environment;

    @Bean
    public Undertow httpServer(RouterFunction<?> routerFunction) {
        HttpHandler handler = RouterFunctions.toHttpHandler(routerFunction);
        int port = Integer.valueOf(environment.getProperty("server.port", "8080"));

        UndertowHttpHandlerAdapter adapter = new UndertowHttpHandlerAdapter(handler);
        Undertow server = Undertow.builder().addHttpListener(port, "localhost").setHandler(adapter).build();
        server.start();
        return server;
    }


}