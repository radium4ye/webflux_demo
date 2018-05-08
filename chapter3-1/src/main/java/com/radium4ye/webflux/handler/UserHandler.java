package com.radium4ye.webflux.handler;

import com.fasterxml.jackson.annotation.JsonView;
import com.radium4ye.webflux.dao.UserRepository;
import com.radium4ye.webflux.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author radium4ye
 */
@Component
public class UserHandler {
    @Autowired
    UserRepository repository;


    /**
     * 保存用户
     * 用户数据  如下，两种格式都支持 <br/>
     * [{"username":"a","password":"123456","age":10,"ban":false},{"username":"b","password":"abcdef","age":99,"ban":true},{"username":"c","password":"654321","age":30,"ban":false}] <br/>
     * {"username":"a","password":"123456","age":10,"ban":false}
     */
    public Mono<ServerResponse> save(ServerRequest request) {
        return ServerResponse.ok().body(repository.insert(request.bodyToFlux(User.class)), User.class);

    }

    /**
     * 根据ID查询用户信息
     * e.g. 127.0.0.1:8080/mvc/user/5af1663a75978b963315d7ba
     * id 这里采用的 ObjectId，只需将 ObjectId 的16进制的结果传入即可。
     *
     * @return
     */
    public Mono<ServerResponse> findById(ServerRequest request) {
        ObjectId id = new ObjectId(request.pathVariable("id"));
        return ServerResponse.ok().body(this.repository.findById(id), User.class);
    }

    /**
     * 获取所有用户
     *
     * @return
     */
    public Mono<ServerResponse> findByAll(ServerRequest request) {
        return ServerResponse.ok().body(repository.findAll(), User.class);
    }

}
