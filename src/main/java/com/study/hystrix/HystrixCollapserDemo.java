package com.study.hystrix;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 完全搞不懂这个合并
 */
public class HystrixCollapserDemo extends HystrixCollapser<List<String>,String,Integer> {

    private Integer key;

    public HystrixCollapserDemo(Integer key){
        this.key = key;
    }
    /**
     * 这个是什么
     * @return
     */
    @Override
    public Integer getRequestArgument() {
        return key;
    }

    @Override
    protected HystrixCommand<List<String>> createCommand(Collection<CollapsedRequest<String, Integer>> collection) {
        return new BatchCommand(collection);
    }

    @Override
    protected void mapResponseToRequests(List<String> strings, Collection<CollapsedRequest<String, Integer>> collection) {
        int count = 0;
        for (CollapsedRequest<String, Integer> request : collection) {
            request.setResponse(strings.get(count++));
        }
    }

    static class BatchCommand extends HystrixCommand<List<String>>{

        private final Collection<CollapsedRequest<String,Integer>> requests;

        BatchCommand(Collection<CollapsedRequest<String,Integer>> requests){
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GROUP"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("COMMAND")));
            this.requests = requests;
        }

        @Override
        protected List<String> run() throws Exception {
            List<String> response = new ArrayList<>();
            for(CollapsedRequest<String,Integer> collapsedRequest:requests){
                response.add( "ssdf:"+ collapsedRequest.getArgument());
            }
            return response;
        }
    }

    public static void main(String[] args) {
        HystrixRequestContext context =  HystrixRequestContext.initializeContext();
        try {
            Future<String> f1 =  new HystrixCollapserDemo(1).queue();
            Future<String> f2 =  new HystrixCollapserDemo(2).queue();
            Future<String> f3 =  new HystrixCollapserDemo(3).queue();
            Future<String> f4 =  new HystrixCollapserDemo(4).queue();

            System.out.println(f1.get());
            System.out.println(f2.get());
            System.out.println(f3.get());
            System.out.println(f4.get());

            System.out.println(HystrixRequestLog.getCurrentRequest().getExecutedCommands().size());


            HystrixCommand<?> command = HystrixRequestLog.getCurrentRequest().getExecutedCommands().toArray(new HystrixCommand<?>[1])[0];
            // assert the command is the one we're expecting
            // assertEquals("GetValueForKey", command.getCommandKey().name());
            // confirm that it was a COLLAPSED command execution
            System.out.println(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
            // and that it was successful
            System.out.println(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            context.shutdown();
        }
    }
}
