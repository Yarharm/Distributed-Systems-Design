#!/bin/bash
project_dir="/$HOME/school/423/Distributed-Systems-Design/CORBA_Supply_Management_System"
cd $project_dir
rm -rf ./bin/*
javac ./src/communicate/ICommunicatePackage/*.java -d ./bin
javac ./src/communicate/*.java -d ./bin -cp ./bin/
javac ./src/server/data/inventory/*.java -d ./bin -cp ./bin/
javac ./src/server/data/sales/*.java -d ./bin -cp ./bin/
javac ./src/UDP/data/*.java -d ./bin -cp ./bin/
javac ./src/UDP/request/*.java -d ./bin -cp ./bin/
javac ./src/server/*.java -d ./bin -cp ./bin/
javac ./src/client/*.java -d ./bin -cp ./bin/