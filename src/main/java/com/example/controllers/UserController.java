package com.example.controllers;


import com.codahale.metrics.annotation.Timed;
import com.example.entity.User;
import com.example.services.UserService;
import com.example.thread.ThreadDemo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhangkh on 2017/3/6.
 */
@Timed
@RestController(value = "userController")
@RequestMapping(value = "/v1", produces = {"application/json", "application/xml"})
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ThreadDemo threadDemo;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/person", method = RequestMethod.GET)
    @ApiOperation(notes = "getPerSon", httpMethod = "GET", value = "get person")
    public List<User> getPerson(@ApiParam(value = "姓名") @RequestParam(value = "name", required = false) String userName) {
        logger.debug("request " + userName);
        User a = new User();
        a.setName("zhangsan");
        a.setEmployeeNumber("123");
        a.setDep("dep1");
        User b = new User();
        b.setName("zhangsan");
        b.setEmployeeNumber("123");
        b.setDep("dep1");
        List<User> userList = new ArrayList<>();
        userList.add(a);
        userList.add(b);
        return userList;
    }


    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public void getData(@RequestParam(value = "grade") int grade) {
        userService.getData(grade);
    }

//        @Metered
//    @CustomLog(operationType = "check:", userName = "添加用户")
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public void check() {
        threadDemo.test();
        logger.info("测试自定注解");
    }

//    @Timed
//    @Metered
    @RequestMapping(value = "/data", method = RequestMethod.POST)
    @ApiOperation(notes = "getPerSon", httpMethod = "POST", value = "get person")
    public List<User> postUrlData(@RequestParam(value = "name", required = false) String userName) {
        long start = System.currentTimeMillis();
        logger.info("request " + userName);
//        return userService.get(userName);
//        throw new RuntimeException("getPerSon 异常");
        System.out.println("timed test");
        System.out.println(System.currentTimeMillis() -start);
        return null;
    }


}
