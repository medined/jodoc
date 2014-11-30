#!/bin/bash
ID=$(docker run -e ZOOKEEPER_PORT=20000 -d --net=host -t jodoc:1)
sleep 1
ZOOKEEPER_PORT=$(docker logs $ID)
echo $ZOOKEEPER_PORT
