package com.study.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public class HystrixCommandCacheTest extends HystrixCommand<String> {

    private String key;
    private int value;

    public HystrixCommandCacheTest(int value,String key){
        super(HystrixCommandGroupKey.Factory.asKey("group"));
        this.key= key;
        this.value =value;
    }

    @Override
    protected String run() throws Exception {
        return key+value;
    }

    @Override
    protected String getCacheKey() {
        return key;
    }

    public static void main(String[] args) {
        HystrixRequestContext hystrixRequestContext =  HystrixRequestContext.initializeContext();

        try {
            System.out.println(new HystrixCommandCacheTest(1,"SSS").execute());
            System.out.println(new HystrixCommandCacheTest(2,"SSS").execute());
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            hystrixRequestContext.shutdown();
        }
    }
}
