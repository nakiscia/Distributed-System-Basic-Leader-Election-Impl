import com.sun.javafx.binding.StringFormatter;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
public class ConnectionWatcher implements Watcher {

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()){
            case None:
                printCurrentState(watchedEvent.getState());
                break;
        }
    }

    private void printCurrentState(Event.KeeperState state) {
        switch (state){
            case SyncConnected:
                System.out.println("Successfully connected to ZooKeeper");
                break;
            case Disconnected:
                System.out.println("Disconnected from ZooKeeper");
                ZooKeeperConnector.disconnect();
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }
}
