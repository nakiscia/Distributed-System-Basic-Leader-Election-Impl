import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZooKeeperConnector {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT =3000;
    private static ConnectionWatcher watcher = new ConnectionWatcher();


    private ZooKeeperConnector(){}

    private static volatile ZooKeeper zooKeeper;

    public static ZooKeeper connect(){
        if(zooKeeper == null){
            synchronized (ZooKeeper.class){
                if(zooKeeper == null) {
                    try {
                        zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, watcher);
                        return zooKeeper;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return zooKeeper;
    }

    public static void disconnect(){
        try {
            synchronized (zooKeeper){
                zooKeeper.close();
                zooKeeper.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
