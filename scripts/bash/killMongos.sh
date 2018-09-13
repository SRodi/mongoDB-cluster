#!/bin/bash
#get PID of mongos process and kill it
#ps | pgrep mongos | xargs kill -2s
pkill mongod mongos mongo
#send message to log.file
echo "mongos processes: killed" > log.file