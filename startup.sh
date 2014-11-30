#!/bin/bash
export PS1="DOCKER:\h \W\$ "
java -DZOOKEEPER_PORT=$ZOOKEEPER_PORT -jar jodoc-1.0-SNAPSHOT.jar
