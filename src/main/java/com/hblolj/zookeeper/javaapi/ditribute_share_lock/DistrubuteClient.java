package com.hblolj.zookeeper.javaapi.ditribute_share_lock;

/**
 * @author: hblolj
 * @Date: 2019/6/5 13:37
 * @Description: ZooKeeper 实现分布式共享锁实现
 * @Version:
 **/
public class DistrubuteClient {

    public static void main(String[] args) {

        // 有序临时节点，节点分为两种类型，读节点与写节点。每个节点表示一个客户端对锁的一次请求
        // 读节点相互不影响，写节点需要前面没有任何节点，读节点需要前面没有任何写节点

        // 锁的释放，客户端断开连接。临时节点会被释放
        // 锁的竞争，获取指定父节点下的所有子节点，看前面是否存在节点。Watcher 前面最后一个阻塞的节点
    }
}
