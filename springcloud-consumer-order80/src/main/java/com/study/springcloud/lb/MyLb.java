package com.study.springcloud.lb;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyLb implements LoadBalancer{

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public final int getAndIncrement() {
        int current;
        int next;
        do {
            current = this.atomicInteger.get();
            next = current >= 2147483647 ? 0 : current + 1;
        }while(!this.atomicInteger.compareAndSet(current,next)); //第一个参数是期望值，第二个参数是修改值
        System.out.println("======第几次访问次数next:" + next);
        return next;
    }

    //负载均衡轮询算法原理：
    //rest接口第几次请求数 % 服务器集群总数量 = 实际调用服务器位置的下标，每次服务重新启动后rest接口数从1开始。
    @Override
    public ServiceInstance instances(List<ServiceInstance> serviceInstances) { //得到服务器的列表
        int index = getAndIncrement() % serviceInstances.size();  //得到服务器的下标位置
        return serviceInstances.get(index);
    }
}
