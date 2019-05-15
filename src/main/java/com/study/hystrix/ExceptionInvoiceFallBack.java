package com.study.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixTimeoutException;

/**
 *    测试各种异常是否能触发fallBack
 *
 */
public class ExceptionInvoiceFallBack extends HystrixCommand<String> {

    public ExceptionInvoiceFallBack(){
        super(HystrixCommandGroupKey.Factory.asKey("example"));
    }

    protected String run() throws Exception {
        //第一种：超时,执行时间过长，假如某个client执行时间过长是不是会触发fallback
    /*    int j = 0;
        while(true){
            j++;
        }*/

        //第二种：超时，睡眠,也触发了fallback
        //Thread.sleep(2000);

        //第三种：除0异常
        // int i = 1/0;

        //第四种：主动抛出异常
        //   throw new HystrixTimeoutException();
        // throw new RuntimeException("aaa");
        //throw new Exception("aaa");

        // HystrixBadRequestException异常由非法参数或非系统错误引起，不会触发fallback，也不会被计入熔断器
        //TODO 怎么让其他线程知道抛错的？？
        throw new HystrixBadRequestException("aaa");

       // return "成功";
    }

    @Override
    protected String getFallback() {
        System.out.println(">>>>>失败");
        return "失败";
    }

    public static void main(String[] args) {
        try {
            ExceptionInvoiceFallBack exceptionInvoiceFallBack = new ExceptionInvoiceFallBack();
            String result  = exceptionInvoiceFallBack.execute();
            System.out.println(">>>>>>>>>>结果："+result);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
