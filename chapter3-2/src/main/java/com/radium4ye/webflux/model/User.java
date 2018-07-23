package com.radium4ye.webflux.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author radium4ye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable{

    private static final long serialVersionUID = -7039315868581621936L;

    @Id
    private Integer id;

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
