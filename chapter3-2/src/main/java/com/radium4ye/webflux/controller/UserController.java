package com.radium4ye.webflux.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.radium4ye.webflux.dao.UserRepository;
import com.radium4ye.webflux.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author radium4ye
 */
@RestController
@RequestMapping("/mvc/user")
public class UserController {

    @Autowired
    UserRepository repository;

    /**
     * hello
     * @param request
     * @return
     */
    @RequestMapping("/hello")
    public String hello(ServerHttpRequest request) {
        return "hello world.";
    }

    /**
     * 保存用户
     * @param userFlux 用户数据  如下，两种格式都支持 <br/>
     * [{"username":"a","password":"123456","age":10,"ban":false},{"username":"b","password":"abcdef","age":99,"ban":true},{"username":"c","password":"654321","age":30,"ban":false}] <br/>
     * {"username":"a","password":"123456","age":10,"ban":false}
     * @return
     */
    @PostMapping
    public Flux<User> save(@RequestBody Flux<User> userFlux){
        return this.repository.saveAll(userFlux);
    }

    /**
     * 根据ID查询用户信息
     * e.g. 127.0.0.1:8080/mvc/user/5af1663a75978b963315d7ba
     * @param id 这里采用的 ObjectId，只需将 ObjectId 的16进制的结果传入即可。
     * @return
     */
    @GetMapping("/{id}")
    public Mono<User> findById(@PathVariable Integer id){
        return this.repository.findById(id);
    }

    /**
     * 获取所有用户
     * @return
     */
    @GetMapping("/all")
    @JsonView(User.NoPasswordView.class)
    public Flux<User> findByAll(){
        return this.repository.findAll();

    }
}
