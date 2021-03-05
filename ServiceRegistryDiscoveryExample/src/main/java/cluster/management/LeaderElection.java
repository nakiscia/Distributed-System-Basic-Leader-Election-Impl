package cluster.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LeaderElection implements Watcher {

    private static final String ELECTION_NAMESPACE = "/election";
    private String currentZNodeName = "";
    private final ZooKeeper zooKeeper;
    private OnElectionCallback onElectionCallback;

    public LeaderElection(ZooKeeper zooKeeper, OnElectionCallback onElectionCallback) {
        this.zooKeeper = zooKeeper;
        this.onElectionCallback = onElectionCallback;
    }

    public void volunteerForElection(){
        String zNodePrefix = ELECTION_NAMESPACE + "/c_";
        try {
            String s = zooKeeper.create(zNodePrefix , new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            currentZNodeName = s.replace(ELECTION_NAMESPACE+"/","");
            System.out.println("ZNode created for election. Full path : "+s);
            reElectLeader();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void reElectLeader(){
        Stat predecessorStat = null;
        while (predecessorStat == null){
            try {
                List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, this);
                Collections.sort(children);

                if(currentZNodeName.equals(children.get(0)))
                {
                    System.out.println("This node is the leader... "+currentZNodeName);
                    this.onElectionCallback.onElectedToBeLeader();
                    return;
                }

                // Find the created ZNode on the children;
                int index = Collections.binarySearch(children, currentZNodeName);
                int predecessorIndex = index -1; // for watch previous node by index;
                System.out.println("Looking for predecessor " + ELECTION_NAMESPACE+"/"+ children.get(predecessorIndex));
                predecessorStat = zooKeeper.exists(ELECTION_NAMESPACE+"/"+ children.get(predecessorIndex), this);
                this.onElectionCallback.onWorker();
                System.out.println("Watching the zNode "+children.get(predecessorIndex));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()){
            case NodeDeleted:
                reElectLeader();
                break;
        }
    }
}
