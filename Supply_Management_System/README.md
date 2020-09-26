## Distributed Supply Management System (DSMS) using Java RMI

### Generate binaries
Navigate to the root dir and execute __generateBin.sh__ located in tools
```
. ./tools/generateBin.sh
```

### Initalize
* Registry
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

### Use Case clarification
* If we addItem with userID and registry exception happens then do we add it to the log or let exception flow.
* How server can know that addItem operation was not successful if we have a Registry exception.
* Do we need to log invalidUser
* What if itemID does not belong to the store where manager is trying to put it in.