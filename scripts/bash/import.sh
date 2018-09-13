#!/bin/bash
#import json file to cluster
#step back two folder (from script/bash folder where you currently are running the script_cluster.sh)
cd ..
cd ..
#establish current path & clustername (command line argument)
path=`pwd`
#SHRINK CHUNK SIZE TO 1MB
mongo --port 62000 --eval "printjson(db.getSiblingDB('config'));db.setting.save({_id:'chunksize',value:1});db.setting.find()" >> $path/log.file
#==============================
#==============================

#IMPORT DB .json is 95MB
mongoimport --db restaurantDB --collection restaurant --drop --file ~/Downloads/restaurant.json --port 62000 >> $path/log.file
mongo --port 62000 --eval "JSON.stringify(db.adminCommand('listDatabases'))" >> $path/log.file
#==============================
#==============================

#ENABLE DB PARTITION
mongo --port 62000 --eval "printjson(db.adminCommand({enableSharding:'restaurantDB'}))">> $path/log.file
#==============================
#==============================

#ENABLE SHARDING of a collaction in a particular db    --  create index
mongo localhost:62000/restaurantDB --eval "db.restaurant.createIndex({cuisine:1,borough:1});db.restaurant.getIndexes()" >> $path/log.file
mongo localhost:62000/restaurantDB --eval "db.getSiblingDB('config').databases.find()" >> $path/log.file                                          
#==============================
#==============================

#CREATE SHARD KEY for a collections
mongo --port 62000 --eval "printjson(db.adminCommand({shardCollection:'restaurantDB.restaurant',key:{cuisine:1,borough:1}}))" >> $path/log.file
mongo --port 62000 --eval "printjson(db.getSiblingDB('config'));db.setting.find();">> $path/log.file
#leave it work in the background
sleep 40
mongo --port 62000 --eval "sh.status({verbose:1})" >> $path/log.file
#Prints the data distribution statistics for a sharded collection
mongo localhost:62000/restaurantDB --eval "db.restaurant.getShardDistribution()" >> $path/log.file
#==============================
#==============================
#leave it work in the background
sleep 30
for((n=0;n<=3;n++));do
        mongo localhost:"27"$n"00"/restaurantDB --eval "db.restaurant.count()" >> log.file 
done
