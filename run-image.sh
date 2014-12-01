#!/bin/bash
WORKDIR=~/my-miniaccumulo-cluster
sudo rm -rf $WORKDIR/*
mkdir -p $WORKDIR
ID=$(docker run -v $WORKDIR:/accumulo -e ZOOKEEPER_PORT=20000 -d --net=host -t jodoc:1)
sleep 1
ZOOKEEPER_PORT=$(docker logs $ID)
echo $ZOOKEEPER_PORT
