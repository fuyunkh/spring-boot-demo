package com.example.thread;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * Created by Zhangkh on 2018/5/10.
 */
@Service
public class TodoHelper {
    @Async
    public Future<String> getTodoA() {
        String t = "todoA";
        System.out.println("getTodoA " + Thread.currentThread().getName());
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(t);
    }

    @Async
    public Future<String> getTodoB() {
        String t = "todoB";
        System.out.println("getTodoB " + Thread.currentThread().getName());
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//            System.out.println("getTodoB" + Thread.currentThread().getName());
        return new AsyncResult<>(t);
    }
}
