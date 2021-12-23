package com.it.controller;


import com.it.mapper.UserMapper;
import com.it.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {


    @Autowired
    private UserMapper userMapper;



    //查询所有用户
    @GetMapping("/queryUserList")
    public List<User> queryUserList()
    {
        List<User> userList=userMapper.queryUserList();
        for (User user : userList
             ) {
            System.out.println(user);
        }
        return userList;
    }


    //添加一个
    @GetMapping("/addUser")
    public String addUser()
    {
       userMapper.addUser(new User(5,"zz","zz"));
        return "addokk";
    }


    //添加一个
    @GetMapping("/updateUser")
    public String updateUser()
    {
        userMapper.updateUser(new User(1,"gg","gg"));
        return "updateokk";
    }

    //添加一个
    @GetMapping("/deleteUser")
    public String deleteUser()
    {
        userMapper.deleteUser(5);
        return "deleteokk";
    }






}
