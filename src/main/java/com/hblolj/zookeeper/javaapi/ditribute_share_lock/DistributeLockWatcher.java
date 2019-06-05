package com.hblolj.zookeeper.javaapi.ditribute_share_lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author: hblolj
 * @Date: 2019/6/5 15:40
 * @Description:
 * @Version:
 **/
public class DistributeLockWatcher implements Watcher{

    private CountDownLatch countDownLatch;

    public DistributeLockWatcher(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        // 监听当前节点的删除事件
        if (watchedEvent.getType() == Event.EventType.NodeDeleted){
            // -1，释放阻塞
            this.countDownLatch.countDown();
        }
    }
}
