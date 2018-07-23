package com.radium4ye.webflux.dao;

import com.radium4ye.webflux.model.User;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author radium4ye
 */
@Repository
public class UserRepository implements ReactiveCrudRepository<User, Integer> {

    private final String PRE = "USER:";
    private final String ID_KEY = "USER:ID";

    @Autowired
    RedisTemplate<Object, Object> template;

    private Integer getNewId() {
        ValueOperations vo = template.opsForValue();
        return vo.increment(ID_KEY, 1).intValue();
    }


    @Override
    public <S extends User> Mono<S> save(S s) {
        return null;
    }

    @Override
    public <S extends User> Flux<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public <S extends User> Flux<S> saveAll(Publisher<S> publisher) {
        return Flux.from(publisher).map((user) -> {
            Integer id = getNewId();
            user.setId(id);
            this.template.opsForValue().set(PRE + id, user);
            return user;
        });
    }

    @Override
    public Mono<User> findById(Integer id) {
        return Mono.justOrEmpty((User) template.opsForValue().get(PRE + id));
    }

    @Override
    public Mono<User> findById(Publisher<Integer> publisher) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(Integer aLong) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(Publisher<Integer> publisher) {
        return null;
    }

    @Override
    public Flux<User> findAll() {
        ValueOperations vo = template.opsForValue();
        Integer maxId = (Integer) vo.get(ID_KEY);
        if (maxId == null) {
            maxId = 1;
        }
        return Flux.range(1, maxId.intValue()).map(id -> (User) template.opsForValue().get(PRE + id));
    }

    @Override
    public Flux<User> findAllById(Iterable<Integer> iterable) {
        return null;
    }

    @Override
    public Flux<User> findAllById(Publisher<Integer> publisher) {
        return null;
    }

    @Override
    public Mono<Long> count() {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Integer aLong) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Publisher<Integer> publisher) {
        return null;
    }


    @Override
    public Mono<Void> delete(User v) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends User> iterable) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends User> publisher) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return null;
    }
}
