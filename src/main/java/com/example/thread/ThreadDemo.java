package com.example.thread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zhangkh on 2018/5/8.
 */
@Service
public class ThreadDemo {
    @Autowired
    TodoHelper helper;

    public List<Future<String>> launch() {
        List<Future<String>> futureList = new ArrayList<>();

        Future<String> waitFutureA = helper.getTodoA();
        Future<String> waitFutureB = helper.getTodoB();
        futureList.add(waitFutureA);
        futureList.add(waitFutureB);

        return futureList;
    }

    public void wait(List<Future<String>> futureList, long timeout) {
        int count = futureList.size();
        long taskStart = System.currentTimeMillis();
        //等待异步任务完成
        while (true) {
            int doneNum = 0;
            for (int index = 0; index < futureList.size(); index++) {
                if (futureList.get(index).isDone()) {
                    doneNum++;
                } else {
                    try {
                        //任务未完成阻塞等待一段时间
                        futureList.get(index).get(100, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        //等待超时不处理
                    }
                }
            }

            if (count == doneNum) {
                break;
            }
            long curr = System.currentTimeMillis();
            long diff = curr - taskStart;
            if (diff > timeout) {
                System.out.println("获取待办任务超时," + diff);
                break;
            }
        }
    }


    public void handle(List<Future<String>> futureList) {
        for (int index = 0; index < futureList.size(); index++) {
            if (!futureList.get(index).isDone()) {
                futureList.get(index).cancel(true);
//                logger.warn("任务未完成，取消任务");
                continue;
            }
            Future<String> taskListFuture = futureList.get(index);
            if (taskListFuture == null) {
                continue;
            }
            try {
                String one = taskListFuture.get();
                //todo 处理返回值
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    public void test() {
        long start = System.currentTimeMillis();
        List<Future<String>> futureList = launch();
        System.out.println("1111111111111111111111111");
        System.out.println(System.currentTimeMillis() - start);
        wait(futureList, 5 * 1000);
        System.out.println("22222222222222222222222");
        System.out.println(System.currentTimeMillis() - start);
        handle(futureList);
        System.out.println("3333333333333333333");
        System.out.println(System.currentTimeMillis() - start);
    }
}
