import org.apache.zookeeper.ZooKeeper;

public class MainApplication {

    public static void main(String[] args) throws InterruptedException {
        ZooKeeper zooKeeper = ZooKeeperConnector.connect();

        ClusterNode clusterNode = new ClusterNode();
        String zNodeByPath = clusterNode.createZNodeByPath("/election");
        NodeOperationWatcher nodeOperationWatcher =
                new NodeOperationWatcher(zooKeeper,zNodeByPath,true);
        LeaderElector.electLeader(zNodeByPath,nodeOperationWatcher);
        nodeOperationWatcher.setWatcher();
        System.out.println("Current Node path :" +zNodeByPath);
        run(zooKeeper);
    }

    static void run(ZooKeeper zooKeeper) throws InterruptedException {
        synchronized (zooKeeper){
            zooKeeper.wait();
        }
    }

}
