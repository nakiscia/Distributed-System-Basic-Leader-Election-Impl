import cluster.management.ElectionAction;
import cluster.management.LeaderElection;
import cluster.management.OnElectionCallback;
import cluster.management.ServiceRegistry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Application implements Watcher {
    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private ZooKeeper zooKeeper;
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {
        int port = args.length == 1 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        Application application = new Application();
        ZooKeeper zooKeeper = application.connectToZookeeper();
        ServiceRegistry serviceRegistry = new ServiceRegistry(zooKeeper);
        OnElectionCallback callback = new ElectionAction(serviceRegistry,port);
        LeaderElection election = new LeaderElection(zooKeeper,callback);
        election.volunteerForElection();
        application.run();
        application.close();
    }

    public ZooKeeper connectToZookeeper() throws IOException{
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS,SESSION_TIMEOUT,this);
        return this.zooKeeper;
    }

    public void run(){
        synchronized (zooKeeper){
            try {
                zooKeeper.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(){
        try {
            zooKeeper.close();
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch(watchedEvent.getType()){
            case None:
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected)
                    System.out.println("Connected to ZooKeeper");
                else
                    System.out.println("Disconnected from ZooKeeper");
        }
    }
}
