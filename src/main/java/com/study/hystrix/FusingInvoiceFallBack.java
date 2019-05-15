package com.study.hystrix;

import com.netflix.hystrix.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断机制相当于电路的跳闸功能，例如：我们可以配置熔断策略为当请求错误比例在10s内>50%时，该服务将进入熔断状态，后续请求都会进入fallback
 * CircuitBreakerRequestVolumeThreshold设置为3，意味着10s内请求超过3次就触发熔断器(10s这个时间暂时不可配置)
 * run()中无限循环使命令超时进入fallback，10s内请求超过3次，将被熔断，进入降级，即不进入run()而直接进入fallback
 *如果未熔断，但是threadpool被打满，仍然会降级，即不进入run()而直接进入fallback
 */
public class FusingInvoiceFallBack extends HystrixCommand<String> {

    private static AtomicInteger atomicInteger = new AtomicInteger();

    public FusingInvoiceFallBack(){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("example"))
              .andCommandKey(HystrixCommandKey.Factory.asKey("command"))
              .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("pool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerEnabled(true)
                .withCircuitBreakerErrorThresholdPercentage(80)
                .withCircuitBreakerRequestVolumeThreshold(3))
              .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(200)));

    }

    @Override
    protected String run() throws Exception {
        int i =  atomicInteger.incrementAndGet();
        //有这里表示熔断还没打开
        System.out.println("running() ...");
        if(i<10){
            return "成功";
        }else{
            int j = 0;
            while(true){
                j++;
            }
        }
    }

    @Override
    protected String getFallback() {
        return "失败";
    }

    public static void main(String[] args) {
        for(int i=0;i<50;i++){
            try {
                FusingInvoiceFallBack fusingInvoiceFallBack = new FusingInvoiceFallBack();
                String result  = fusingInvoiceFallBack.execute();
                System.out.println(">>>>>>>>>>结果："+ result);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        System.out.println("===============================");
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        for (Thread thread:map.keySet()){
            System.out.println("线程名称："+ thread.getName());
        }
    }


}
