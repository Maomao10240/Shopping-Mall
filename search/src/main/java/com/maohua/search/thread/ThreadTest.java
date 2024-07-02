package com.maohua.search.thread;

import java.util.concurrent.*;

public class ThreadTest {
    //当前系统 线程池只有一两个， 每个任务提交给线程池
    public static ExecutorService service = Executors.newFixedThreadPool(10);

         public static void main(String[] args) throws ExecutionException, InterruptedException {
//        CompletableFuture.runAsync(()->{
//            System.out.println("current thread: " + Thread.currentThread().getId());
//            int i = 10/2;
//            System.out.println(("result: " + i));
//        }, service);
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(()->{
            System.out.println("current thread: " + Thread.currentThread().getId());
            int i = 10/2;
            System.out.println(("result: " + i));
            return i;
        },service).whenComplete((result,exception)->{
            System.out.println("异步任务完成了。。。。。。。result: "+ result +"   error: " + exception);

        });
       // Integer integer= future.get();
        //System.out.println("end........"+ integer);
    }




    public static void tread(String[] args){
//        System.out.println("main.....start.........");
//        Thread thread = new Thread01();
//        thread.start();
//        System.out.println(".....end>>>>>>.........");
//
        Runable01 run = new Runable01();
//        new Thread(run).start();
        service.execute(run );
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 200, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        //max 满了执行rejectexcutionhandler,
    }
    public static class Thread01 extends Thread{
        @Override
        public void run(){
            System.out.println("current thread: " + Thread.currentThread().getId());
            int i = 10/2;
            System.out.println(("result: " + i));
        }
    }

    public static class Runable01 implements Runnable{

        @Override
        public void run() {
               System.out.println("current thread: " + Thread.currentThread().getId());
            int i = 10/2;
            System.out.println(("result: " + i));
        }
    }
}
