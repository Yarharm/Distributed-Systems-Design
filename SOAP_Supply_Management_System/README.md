# Supply Management System using SOAP

## Instructions

- Generate `jaxws`
```
wsgen -keep -cp . server.StoreProxy
```

- Publish
```
java server.StoreDriverQC
java server.StoreDriverBC
java server.StoreDriverON
```

- Import
```
wsimport -keep http://localhost:8887/QC\?wsdl
wsimport -keep http://localhost:8889/BC\?wsdl
wsimport -keep http://localhost:8888/ON\?wsdl
```

- Run Client
```
java client.UserDriver
```