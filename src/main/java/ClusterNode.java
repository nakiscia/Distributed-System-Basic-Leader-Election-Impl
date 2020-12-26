import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ClusterNode {
    private final String NODE_PREFIX = "/c_";

    public String createZNodeByPath(String path) {
        ZooKeeper zooKeeper = ZooKeeperConnector.connect();
        String zNodeFullPath = null;
        try {
            zNodeFullPath = zooKeeper.create(path+NODE_PREFIX, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return zNodeFullPath;
    }

}
