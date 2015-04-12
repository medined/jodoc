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
  -v $WORKDIR:/accumulo \
  -e JAVA_USER=${USER} \
  -e jodoc.tserver.count=3 \
  -e jodoc.accumulo.schema=D4M \
  -e jodoc.monitor.port=$MONITOR_PORT \
  -e jodoc.zookeeper.port=$ZOOKEEPER_PORT \
  --net=host \
  -t medined/jodoc

echo "  ACCUMULO_DIR: $WORKDIR"
echo "  MONITOR_PORT: $MONITOR_PORT"
echo "ZOOKEEPER_PORT: $ZOOKEEPER_PORT"
