# Cluster Autosclaer
Cluster Autoscaler - Handles the basic autoscaling function in a cluster of worker nodes

## To build the Autoscaler

Run `mvn clean package`

### To run the autohealer, which in turn would launch and maintain N workers

**Run** 
```
java -jar target/autoscaler-1.0-SNAPSHOT-jar-with-dependencies.jar <number of workers> <path to woker jar>
```

**Example:** 
```
java -jar target/autoscaler-1.0-SNAPSHOT-jar-with-dependencies.jar 10 "../flakyworker/target/flaky.worker-1.0-SNAPSHOT-jar-with-dependencies.jar"
```

## To build the FlakyWorker

Run `mvn clean package`

**Run** 
```
java -jar target/flaky.worker-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**Note:** While running the flaky worker, make sure there is the **/workers** node already created in zookeeper's
root folder. To do so, refer the official zookeeper docs for the latest create command. 
