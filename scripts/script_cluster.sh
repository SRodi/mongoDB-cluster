#!/bin/bash
#R00090111
#====functions
createshardsfolders()
{ 
#create & move to cluster folder
mkdir $1
echo "$1- Maincluster folder created" > log.file
cd $1
#create & move to shards folder
mkdir shards
echo "shards folder created" >> $2/log.file

cd shards
#create folder for each shard (loop)
for ((n=0; n<$3; n++)); do
    mkdir ${Shard[$n]}
    echo "shard:${Shard[$n]} -created" >> $2/log.file  
done
#create data directories for each shard (loop)
for ((n=0; n<$3; n++)); do
    #change to a specific shard directory according to loop cycle
    cd ${Shard[$n]}
    #create 3 nodes for each shard
    for k in 0 1 2; do
     mkdir node$k
     echo "node$k: shard:${Shard[$n]} -created" >> $2/log.file
    done
    #return to previous directory
    cd ..   
done
}
startnodes()
{
cd $2/$1/shards
#start each mongo for each node in shard (loop)
for ((n=0; n<$3; n++)); do
    #start 3 nodes for each shard (replicaSet) & create a log file each
    for k in 0 1 2; do
        mongod --shardsvr --replSet ${Shard[$n]} --dbpath ${Shard[$n]}/node$k --port "27"$n"0"$k --logpath node_${Shard[$n]}_$k.log --fork >> $2/log.file
    done
done
}
initiatenodes()
{
#change to main sharding directory
cd $2                    
#initiate shard (loop)
for ((n=0; n<$3; n++)); do
    replSetInitiate="{_id: '"${Shard[$n]}"',members:[
                    {_id : 0, host : 'localhost:27"$n"00'},
                    {_id : 1, host : 'localhost:27"$n"01'},
                    {_id : 2, host : 'localhost:27"$n"02', arbiterOnly: true}]}"
    mongo localhost:"27"$n"00" --eval "JSON.stringify(rs.initiate($replSetInitiate))" >> $2/log.file
done
}
createconfigfolders()
{
#change to main cluster directory
cd $2/$1
 #create & move to configServers folder
mkdir configServers
echo "configServers: -main folder created" >> $2/log.file
cd configServers
#create 3 nodes
for k in 0 1 2; do
    mkdir config$k
    echo "config$k: -config server $k folder created" >> $2/log.file
done
}
startconfig()
{
cd $2/$1/configServers
#start config servers
for k in 0 1 2; do
    mongod --configsvr --replSet configServers --dbpath config$k --port 2605$k --logpath config$k.log --fork >> $2/log.file
done
}
initiateconfig()
{
cd $2
#define replica set initiation details
replSetInitiate="{ _id: 'configServers', members:[
                    { _id : 0, host : 'localhost:26050'},
                    { _id : 1, host : 'localhost:26051'},
                    { _id : 2, host : 'localhost:26052'} ]}"
#initiate config
mongo localhost:26050 --eval "JSON.stringify(rs.initiate($replSetInitiate))" >> $2/log.file
}
startmongo()
{
#============================================
#==========CREATE MONGO ROUTER DIR===========
#============================================
#change to main sharding directory
cd $2/$1
#create a folder for mongos router
mkdir mongosData
echo "-mongos: folder created" >> $2/log.file
#============================================
#============START MONGO ROUTER==============
#============================================
cd $2
#start the mongos router(port 62000)
mongos --configdb configServers/localhost:26050,localhost:26051,localhost:26052 --port 62000 --logpath $1/mongosData/mongo.log --fork >> $2/log.file
}
populatecluster()
{
cd $2
#add shards to cluster
for ((n=0; n<$3; n++)); do
    mongo localhost:62000 --eval "JSON.stringify(db.adminCommand({addshard : '"${Shard[$n]}"/localhost:27"$n"00,localhost:27"$n"01',name : '**shard:"$n"**'}))" >> $2/log.file
done
#evaluate
mongo --port 62000 --eval "sh.status()" >> $2/log.file
}

#====MAIN CODE====
#step back one folder (from script folder where you currently are running the script_cluster.sh)
cd ..
#_____________________________________variables_________________________________________________
#establish current path & clustername (command line argument)
path=`pwd`
#assign clustername which is the command line argument when we call script_cluster.sh
clustername=$1
#declare and assign an array with each shard name
declare -a Shard=('cork_R00090111' 'dublin_R00090111' 'galway_R00090111' 'limerick_R00090111');
#establish number of shards
numShards=${#Shard[@]};
#____________________________________call functions__________________________________________________
#execute functions
createshardsfolders "$clustername" "$path" "numShards"
startnodes "$clustername" "$path" "numShards"
initiatenodes "$clustername" "$path" "numShards"
createconfigfolders "$clustername" "$path"
startconfig "$clustername" "$path"
initiateconfig "$clustername" "$path"
startmongo "$clustername" "$path"
populatecluster "$clustername" "$path" "numShards"
#end script execution
exit 0