# RMI Template
Generate PI number using Java RMI.

## Generate binaries
* Convert Remote Interface to _jar_ file **compute.jar** and move it to the root dir of the Binaries folder.
* Move Remote Interface binaries to the compute folder in the Binaries folder.
* Generate server binaries with -cp **compute.jar** and move it to the server package in the Binaries folder.
* Generate client binaries with -cp **compute.jar** and move it to the client package in the Binaries folder.

*Binaries must mimic package structure*

## Security
Specify security policy for the client and a server.
Sample security protocol for the complete access in local development.
```
grant {
    permission java.security.AllPermission;
};
```

## Registry
* Start registy
```
rmiregistry &
```

* Start registry on a specified port
```
rmiregistry 2001 &
```
## Start server
```
java -cp ./binaries:./binaries/compute.jar -Djava.rmi.server.codebase=file:/Users/yaroslav/school/423/binaries/compute.jar -Djava.rmi.server.hostname=127.0.0.1 -Djava.security.policy=./security/engine.policy engine.ComputeEngine
```
* rmi.server.codebase must point to **compute.jar**
* rmi.server.hostname must resolve to public IP Address 127.0.0.1. For simplicity specify 127.0.0.1 in the first place
* security.policy must point to the appropriate server secutiy policy

## Start client
```
java -cp ./binaries:./binaries/compute.jar -Djava.rmi.server.codebase=file:/Users/yaroslav/school/423/binaries/ -Djava.security.policy=./security/client.policy client.ComputePI 127.0.0.1 45
```
* rmi.server.codebase must point to the location of the client binary
* security.policy must point to the appropriate client secutiy policy
* hostname is supplied via _args_