## Distributed Supply Management System (DSMS) using CORBA

### Start ORB
orbd -ORBInitialPort 1050 -ORBInitialHost localhost&

### Start Server
java server.StoreDriverQC -ORBInitialPort 1050 -ORBInitialHost localhost
java server.StoreDriverBC -ORBInitialPort 1050 -ORBInitialHost localhost
java server.StoreDriverON -ORBInitialPort 1050 -ORBInitialHost localhost

### Start Client
java client.UserDriver -ORBInitialPort 1050 -ORBInitialHost localhost