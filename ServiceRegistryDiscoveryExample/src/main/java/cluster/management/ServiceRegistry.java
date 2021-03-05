package cluster.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceRegistry implements Watcher {

    private static final String SERVICE_REGISTRY_NAMESPACE = "/service_registry";
    private ZooKeeper  zooKeeper;
    private String currentZNode = null;
    private List serviceAddresses;

    public ServiceRegistry(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
        createServiceRegisterPath(); // create the path..
    }

    public void registerToCluster(String metadata) throws KeeperException, InterruptedException {
        currentZNode = this.zooKeeper.create(SERVICE_REGISTRY_NAMESPACE + "/n", metadata.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Registered to Service registery "+currentZNode);
    }

    public void unRegisterToCluster(){
        try {
            if(currentZNode != null && this.zooKeeper.exists(currentZNode,false) !=null)
                this.zooKeeper.delete(currentZNode,-1);
        } catch (InterruptedException e) {
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public void registerForUpdate(){
        try {
            updateClusterAddresses();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getAllServiceAddresses(){
        if(serviceAddresses == null) {
            try {
                updateClusterAddresses();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return this.serviceAddresses;
    }



    public void updateClusterAddresses() throws KeeperException, InterruptedException {
        List<String> children = this.zooKeeper.getChildren(SERVICE_REGISTRY_NAMESPACE, this);
        List addressList = new ArrayList();

        for(String workerNode : children)
        {
            Stat exists = this.zooKeeper.exists(SERVICE_REGISTRY_NAMESPACE+"/"+workerNode, false);
            if(exists == null)
                continue;

            byte[] addressBytes = zooKeeper.getData(SERVICE_REGISTRY_NAMESPACE+"/"+workerNode,false,exists);
            addressList.add(new String(addressBytes));
        }

        this.serviceAddresses = Collections.unmodifiableList(addressList);
        System.out.println("The cluster addresses are : "+this.serviceAddresses);
    }
    // This is a one time call, when any node create a Service Registery will try to create
    // the /service_registery node on zookeeper
    // Normally there is a race condition here
    // But, zookeeper handle it for us and dont let it...
    private void createServiceRegisterPath(){
        try {
            if (this.zooKeeper.exists(SERVICE_REGISTRY_NAMESPACE,false) == null)
            {
                this.zooKeeper.create(SERVICE_REGISTRY_NAMESPACE,new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            updateClusterAddresses();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
