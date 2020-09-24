## Distributed Supply Management System (DSMS) using Java RMI

### Generate binaries
Navigate to the root dir and execute __generateBin.sh__ located in tools
```
. ./tools/generateBin.sh
```

### Initalize
* Registry
```
rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false &
```
* Server
```
java -cp ./bin:./bin/communicate.jar -Djava.rmi.server.codebase=file:/Users/yaroslav/school/423/Distributed-Systems-Design/Supply_Management_System/bin/communicate.jar -Djava.rmi.server.hostname=127.0.0.1 -Djava.security.policy=./security/server.policy server.MasterStore
```
* Client
```
java -cp ./bin:./bin/communicate.jar -Djava.rmi.server.codebase=file:/Users/yaroslav/school/423/Distributed-Systems-Design/Supply_Management_System/bin/ -Djava.security.policy=./security/client.policy client.User
```