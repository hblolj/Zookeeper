package com.hblolj.zookeeper.curator.master_selector;

import com.hblolj.zookeeper.curator.CuratorClientUtil;
import org.apache.curator.framework.CuratorFramework;

import java.io.IOException;

/**
 * @author: hblolj
 * @Date: 2019/6/6 17:20
 * @Description:
 * @Version:
 **/
public class MasterSelector {

    private static final String MASTER_PATH = "/curator_master_path";

    public static void main(String[] args) throws IOException {

        for (int i = 0; i < 10; i++) {
            CuratorFramework client = CuratorClientUtil.getInstance();
            CustomLeaderSelectorListener selector = new CustomLeaderSelectorListener(client, "候选人" + i, MASTER_PATH);
            selector.start();
        }

        System.in.read();
    }
}
