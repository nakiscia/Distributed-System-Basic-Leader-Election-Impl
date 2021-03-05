import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class NodeOperationWatcher  implements Watcher {

    private ZooKeeper zooKeeper;
    private boolean isWatchContinuous = false;
    private String nodePath;

    public NodeOperationWatcher(ZooKeeper zooKeeper, String nodePath) {
        this.zooKeeper = zooKeeper;
        this.nodePath = nodePath;
    }

    public NodeOperationWatcher(ZooKeeper zooKeeper, String nodePath , boolean isWatchContinuous) {
        this.zooKeeper = zooKeeper;
        this.isWatchContinuous = isWatchContinuous;
        this.nodePath = nodePath;
    }

    public void setWatcher(){
        try {
            Stat stat = zooKeeper.exists(nodePath, this);
            if(stat == null)
                return;

            byte[] data = zooKeeper.getData(nodePath, this, stat);
            List<String> children = zooKeeper.getChildren(nodePath, this, stat);

            System.out.println("Watcher Info : \n"+
                    "Data : " + data.toString() + "\n"
                    +"Children : "+children.toString());

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()){
            case NodeCreated:
                System.out.println("The Node created");
                LeaderElector.electLeader(nodePath,this);
                break;
            case NodeDeleted:
                System.out.println("Node deleted");
                // For re-election..
                LeaderElector.electLeader(nodePath,this);
                break;
            case NodeDataChanged:
                System.out.println("Node data changed");
                break;
            case NodeChildrenChanged:
                System.out.println("Node children changed");
                break;
        }

        if(isWatchContinuous)
            setWatcher();
    }

    public void setWatchContinuous(boolean watchContinuous) {
        isWatchContinuous = watchContinuous;
    }
}
