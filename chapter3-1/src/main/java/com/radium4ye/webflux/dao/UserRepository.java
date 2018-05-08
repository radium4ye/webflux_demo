package com.radium4ye.webflux.dao;

import com.radium4ye.webflux.model.User;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author radium4ye
 */
@Repository
@Primary
public interface UserRepository extends ReactiveMongoRepository<User, ObjectId> {


}
