package com.hblolj.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author: hblolj
 * @Date: 2019/5/31 9:52
 * @Description:
 * @Version:
 **/
public class CuratorCreateSessionDemo {

    private final static String CONNECTS_STRING = "47.97.228.113:2181";

    public static void main(String[] args) {
        // 创建会话的两种方式
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(CONNECTS_STRING,
                5000, 5000, new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();

        // 这里的 namespace 是为之后的所有操作的 path 的父节点
        CuratorFramework curatorFramework1 = CuratorFrameworkFactory.builder()
                .connectString(CONNECTS_STRING)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).namespace("curator").build();
        curatorFramework1.start();
    }
}
