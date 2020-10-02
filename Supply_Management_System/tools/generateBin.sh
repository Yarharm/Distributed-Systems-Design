#!/bin/bash
project_dir="/$HOME/school/423/Distributed-Systems-Design/Supply_Management_System"
cd $project_dir
rm -rf ./bin/
javac ./src/exceptions/*.java -d bin/
javac ./src/UDP/*.java -d bin/
javac -cp ./src/ ./src/communicate/*.java -d bin/
cd ./bin
jar cvf communicate.jar ./communicate/*.class ./exceptions/*.class ./UDP/*class
cd ..
javac -d bin/ -cp ./bin/communicate.jar ./src/server/*.java
javac -d bin/ -cp ./bin/communicate.jar ./src/client/*.java