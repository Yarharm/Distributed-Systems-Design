## Distributed Supply Management System (DSMS) using Java RMI

### Generate binaries
Navigate to the root dir and execute __generateBin.sh__ located in tools
```
. ./tools/generateBin.sh
```

### Initalize
* Registry (Always executed from __bin__ folder)
```
rmiregistry &
```
* Server
```
java -cp ./bin:./bin/communicate.jar -Djava.rmi.server.codebase=file:/Users/yaroslav/school/423/Distributed-Systems-Design/Supply_Management_System/bin/communicate.jar -Djava.rmi.server.hostname=127.0.0.1 -Djava.security.policy=./security/server.policy server.StoreMaster
```
* Client
```
java -cp ./bin:./bin/communicate.jar -Djava.rmi.server.codebase=file:/Users/yaroslav/school/423/Distributed-Systems-Design/Supply_Management_System/bin/ -Djava.security.policy=./security/client.policy client.UserDriver
```

### Debugging
Fetch all UDP ports that _LISTEN_
```
lsof -Pan -i udp | grep java
```

### Tests
* Add new Item
* Update an existing Item
* Test __findItem__ with target store having no entries

### Use Case clarification
* What if itemID does not belong to the store where manager is trying to put it in.
* Are BC users exist in BC store only? If that's the case, how can I assign an item to them, without UDP.
* There can be only 1 manager per store? And if it's the case, should it block other managers?
* If customer in queue is from a different store, do we need to send UDP call to the appropriate store.
