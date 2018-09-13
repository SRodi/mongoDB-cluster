#!/bin/bash
#go back one folder since we are in the scripts folder..
cd ..
#remove folder named as command line argument $1 which is user entry in javafx (cluster name)
rm -r $1
echo "============cluster deleted=========" > log.file