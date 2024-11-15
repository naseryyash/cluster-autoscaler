
import org.apache.zookeeper.*;

import java.io.File;
import java.io.IOException;

public class Autoscaler implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;

    // Parent Znode where each worker stores an ephemeral child to indicate it is alive
    private static final String AUTOSCALER_ZNODES_PATH = "/workers";

    // Path to the worker jar
    private final String pathToWorkerBinary;

    // The number of worker instances we need to maintain at all times
    private final int numberOfWorkers;
    private ZooKeeper zooKeeper;

    public Autoscaler(int numberOfWorkers, String pathToWorkerBinary) {
        this.numberOfWorkers = numberOfWorkers;
        this.pathToWorkerBinary = pathToWorkerBinary;
    }

    public void startWatchingWorkers() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(AUTOSCALER_ZNODES_PATH, false) == null) {
            zooKeeper.create(AUTOSCALER_ZNODES_PATH, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        launchWorkersIfNecessary();
    }

    public void connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    }

    public void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Successfully connected to Zookeeper");
                } else {
                    synchronized (zooKeeper) {
                        System.out.println("Disconnected from Zookeeper event");
                        zooKeeper.notifyAll();
                    }
                }
                break;
            /**
             * Add states code here to respond to the relevant events
             */
        }
    }

    /**
     * Method to watch and launch new workers if necessary
     */
    private void launchWorkersIfNecessary() {

    }

    /**
     * Helper method to start a single worker
     * @throws IOException
     */
    private void startNewWorker() throws IOException {
        File file = new File(pathToWorkerBinary);
        String command = "java -jar " + file.getCanonicalPath();
        System.out.println(String.format("Launching worker instance : %s ", command));
        Runtime.getRuntime().exec(command, null, file.getParentFile());
    }
}
