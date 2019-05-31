package com.hblolj.zookeeper.curator;

import com.hblolj.zookeeper.curator.CuratorClientUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/5/31 9:58
 * @Description:
 * @Version:
 **/
public class CuratorApiOperationDemo {

    public static void main(String[] args) throws Exception {

        CuratorFramework curatorFramework = CuratorClientUtil.getInstance();

        // 创建节点
//        String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/hblolj", "123".getBytes());
//        System.out.println("创建结果: " + result);

//        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/hblolj/child/xxx", "xxx".getBytes());

        // 为 null 表示节点不存在
//        Stat stat1 = curatorFramework.checkExists().forPath("/hblolj");
//        System.out.println(stat1);

        // 查询节点，查询一个不存在的节点会报错
//        Stat stat = new Stat();
//        byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath("/hblolj/child/xxx");
//        System.out.println("查询到的数据: " + new String(bytes) + " 节点状态: " + stat);

        // 修改节点
//        stat = curatorFramework.setData().forPath("/hblolj", "321".getBytes());

        // 查询节点

        // 删除节点，递归删除，从当前删除的节点及其子节点都删除掉了，但是其父节点任然存在
//        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/hblolj");

//        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 异步 -> 线程池
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        // 容器也就相当于一个父节点
//        curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT)
//                .inBackground((curatorFramework1, curatorEvent) -> {
//                    System.out.println(Thread.currentThread().getName());
//                    System.out.println("curatorEvent: " + curatorEvent.toString());
//                    countDownLatch.countDown();
//                }, executor).forPath("/xxxx");
//        countDownLatch.await();
//        executor.shutdown();

        // 事务 (curator 独有) 事务下不支持递归操作
        Collection<CuratorTransactionResult> results = curatorFramework.inTransaction().create().withMode(CreateMode.PERSISTENT)
                .forPath("/xxxx/yyyyy", "yyyyy".getBytes()).and().setData().forPath("/xxxx/zzzzz", "nobodycanlive".getBytes()).and().commit();
        for (CuratorTransactionResult result : results) {
            System.out.println("path: " + result.getForPath());
            System.out.println("resultPath: " + result.getResultPath());
            System.out.println("stat: " + result.getResultStat());
            System.out.println("type: " + result.getType());
        }
    }
}
