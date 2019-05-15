package com.study.hystrix;

import com.netflix.hystrix.*;

/**
 * 设置ExecutionIsolationSemaphoreMaxConcurrentRequests为3，意味着信号量最多允许执行run的并发数为3，超过则触发降级，即不执行run而执行getFallback
 * 设置FallbackIsolationSemaphoreMaxConcurrentRequests为1，意味着信号量最多允许执行fallback的并发数为1，超过则抛异常fallback execution rejected
 */
public class SemaphoreStrategy extends HystrixCommand<String> {

    public SemaphoreStrategy(){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GROUP"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("command"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("thread"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(3) //
                .withFallbackIsolationSemaphoreMaxConcurrentRequests(1))
        );
    }

    @Override
    protected String run() throws Exception {
        Thread.sleep(100);

        return "成功";
    }

    @Override
    protected String getFallback() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "失败";
    }

    public static void main(String[] args) {
        for(int i=0;i<5;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(new SemaphoreStrategy().execute());
                    //System.out.println(new SemaphoreStrategy().execute());
                }
            }).start();
        }
    }
}
