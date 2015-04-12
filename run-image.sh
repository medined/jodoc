#!/bin/bash

WORKDIR=~/my-miniaccumulo-cluster

rm -rf $WORKDIR/*
mkdir -p $WORKDIR

ZOOKEEPER_PORT=20000
MONITOR_PORT=20001

#
# The MiniAccumuloCluster must be executed as the same user who is running this script so that
# the user can remove files in the shared directory. Otherwise, they are created as root.
#
docker run \
  -v $WORKDIR:/accumulo \
  -e TSERVER_COUNT=3 \
  -e ACCUMULO_SCHEMA=D4M \
  -e JAVA_USER=${USER} \
  -e ZOOKEEPER_PORT=$ZOOKEEPER_PORT \
  -e monitor.port.client=$MONITOR_PORT -d \
  --net=host \
  -t medined/jodoc

echo "  ACCUMULO_DIR: $WORKDIR"
echo "  MONITOR_PORT: $MONITOR_PORT"
echo "ZOOKEEPER_PORT: $ZOOKEEPER_PORT"
