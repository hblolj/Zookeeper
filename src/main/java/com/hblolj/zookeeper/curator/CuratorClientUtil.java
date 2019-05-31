package com.hblolj.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkImpl;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;

/**
 * @author: hblolj
 * @Date: 2019/5/31 9:48
 * @Description:
 * @Version:
 **/
public class CuratorClientUtil {

    private static CuratorFramework curatorFramework;

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    public static CuratorFramework getInstance() {
//        ExponentialBackoffRetry()  衰减重试
//        RetryNTimes 指定最大重试次数
//        RetryOneTime 仅重试一次
//        RetryUnitilElapsed 一直重试直到规定的时间

        // 重试策略设置为衰减重试
//        curatorFramework = CuratorFrameworkFactory.newClient(CONNECTS_STRING, 5000, 5000,
//                new ExponentialBackoffRetry(1000, 3));

        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTS_STRING)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        curatorFramework.start();

        return curatorFramework;
    }
}
