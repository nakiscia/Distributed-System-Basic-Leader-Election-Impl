package cluster.management;

import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ElectionAction implements OnElectionCallback{

    private ServiceRegistry serviceRegistry;
    private int port;

    public ElectionAction(ServiceRegistry serviceRegistry,int port) {
        this.serviceRegistry = serviceRegistry;
        this.port = port;
    }

    @Override
    public void onElectedToBeLeader() {
        this.serviceRegistry.unRegisterToCluster();
        this.serviceRegistry.registerForUpdate();
    }

    @Override
    public void onWorker() {
        try {
            String currentServerAddress = String.format("http://%s:%d", InetAddress.getLocalHost().getCanonicalHostName(),port);
            this.serviceRegistry.registerToCluster(currentServerAddress);
        } catch (UnknownHostException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
