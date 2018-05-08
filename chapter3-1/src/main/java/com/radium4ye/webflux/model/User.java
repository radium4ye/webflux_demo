package com.radium4ye.webflux.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * @author radium4ye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private ObjectId id;

    /**
     * 用户名
     */
    @JsonView(NoPasswordView.class)
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 年龄
     */
    @JsonView(NoPasswordView.class)
    private int age;

    /**
     * 是否被禁用
     */
    @JsonView(NoPasswordView.class)
    private boolean ban;


    /**
     * 无密码界面
     */
    public interface NoPasswordView {}

}
