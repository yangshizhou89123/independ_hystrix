package com.study.hystrix;


import com.netflix.hystrix.*;

import java.util.concurrent.TimeUnit;

/**
 * 因为线程不够失效的
 */
public class HystrixThreadPool   extends HystrixCommand<String> {

    public HystrixThreadPool(){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("example"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("command"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("pool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerEnabled(true).withExecutionTimeoutInMilliseconds(5000))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(3)));
    }

    @Override
    protected String run() throws Exception {
        System.out.println(">>>>>>>>>>>>>>");
        TimeUnit.SECONDS.sleep(2);
        return "成功";
    }

    @Override
    protected String getFallback() {
        System.out.println("失败");
        return "失败";
    }

    public static void main(String[] args) {
        for(int i=0;i<10;i++){
            new HystrixThreadPool().queue();
        }
    }
}
