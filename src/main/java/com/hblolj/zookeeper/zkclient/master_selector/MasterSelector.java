package com.hblolj.zookeeper.zkclient.master_selector;

import org.I0Itec.zkclient.ZkClient;

/**
 * @author: hblolj
 * @Date: 2019/6/5 17:21
 * @Description: Master 竞选
 * @Version:
 **/
public class MasterSelector {

    private ZkClient zkClient;

    private UserClient client;

    private UserClient master;

    public MasterSelector(ZkClient zkClient, UserClient client) {
        // client 去竞选 master
        this.zkClient = zkClient;
        this.client = client;
    }

    // 核心逻辑，竞选 master 的客户端，所有客户端都监听 master 节点，一起创建去临时 master 节点，创建成功者就是 master。
    // 当之前的 master 断开连接(节点被删除后)，监听触发，剩下的 client 继续去竞争创建 master 节点
}
