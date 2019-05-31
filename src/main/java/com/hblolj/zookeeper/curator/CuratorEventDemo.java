package com.hblolj.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * @author: hblolj
 * @Date: 2019/5/31 10:46
 * @Description:
 * @Version:
 **/
public class CuratorEventDemo {

    /**
     * pathCache: 监听一个路径下的子节点的创建、删除、节点数据更新
     * NodeCache: 监听一个节点的创建、删除、更新
     * TreeCache: pathCache + NodeCache 的合体，缓存路径下的所有子节点的数据
     * @param args
     */
    public static void main(String[] args) throws Exception {

        CuratorFramework curatorFramework = CuratorClientUtil.getInstance();

        // NodeCache
//        NodeCache cache = new NodeCache(curatorFramework, "/event", false);
//        cache.start(true);
//
//        cache.getListenable().addListener(() -> System.out.println("节点 " + cache.getCurrentData().getPath() + " 数据发生变化，变化后的数据: " + new String(cache.getCurrentData().getData())));

        // pathCache
//        PathChildrenCache cache = new PathChildrenCache(curatorFramework, "/event", true);
        // 在初始化之后的事件
//        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
//
//        cache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent) -> {
//            System.out.println("Listener: " + pathChildrenCacheEvent.toString());
//            switch (pathChildrenCacheEvent.getType()){
//                case CHILD_ADDED:
//                    System.out.println("增加子节点");
//                    break;
//                case CHILD_UPDATED:
//                    System.out.println("更新子节点");
//                    break;
//                case CHILD_REMOVED:
//                    System.out.println("删除子节点");
//                    break;
//                default: break;
//            }
//        });

        // TreeCache
        TreeCache cache = new TreeCache(curatorFramework, "/event");
        cache.start();

        cache.getListenable().addListener((curatorFramework1, treeCacheEvent) -> {

            System.out.println("Listener: " + treeCacheEvent.toString());
            switch (treeCacheEvent.getType()){
                case NODE_ADDED:
                    System.out.println("增加节点");
                    break;
                case NODE_UPDATED:
                    System.out.println("更新节点");
                    break;
                case NODE_REMOVED:
                    System.out.println("删除节点");
                    break;
                default: break;
            }
        });

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/event/event1", "1".getBytes());

        TimeUnit.SECONDS.sleep(1);

        curatorFramework.setData().forPath("/event", "999".getBytes());

        TimeUnit.SECONDS.sleep(1);

        curatorFramework.setData().forPath("/event/event1", "xxx".getBytes());

        TimeUnit.SECONDS.sleep(1);

        curatorFramework.delete().forPath("/event/event1");

        TimeUnit.SECONDS.sleep(1);

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/event/event1/event1-1", "1".getBytes());

        TimeUnit.SECONDS.sleep(1);

        curatorFramework.setData().forPath("/event/event1/event1-1", "xxx".getBytes());

        TimeUnit.SECONDS.sleep(1);

        curatorFramework.delete().forPath("/event/event1/event1-1");

        TimeUnit.SECONDS.sleep(1);

        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/event");

        TimeUnit.SECONDS.sleep(1);
    }
}
