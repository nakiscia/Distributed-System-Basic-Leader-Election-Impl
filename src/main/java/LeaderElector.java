import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;

public class LeaderElector {

    public static void electLeader(String path, Watcher watcher){
        ZooKeeper zooKeeper = ZooKeeperConnector.connect();

        try {
            Stat stat = null;
            while(stat == null){
                String electionPath = path.split("/")[1];
                List<String> children = zooKeeper.getChildren("/"+ electionPath, null);
                Collections.sort(children);

                String currentZNodeName = path.split("/")[2];
                if(children.get(0).equals(currentZNodeName)){
                    System.out.println("Elected as leader");
                    stat = zooKeeper.exists(path,null);
                }
                else{
                    int previousNodeIndex = Collections.binarySearch(children, currentZNodeName) -1;
                    String previousNodeName = children.get(previousNodeIndex);
                    stat = zooKeeper.exists("/"+electionPath + "/" + previousNodeName, watcher);
                    System.out.println("Watching the node " +previousNodeName);
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
