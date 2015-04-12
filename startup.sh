#!/bin/bash
export PS1="DOCKER:\h \W\$ "
useradd ${JAVA_USER}
su ${JAVA_USER} -c "java -DTSERVER_COUNT=$TSERVER_COUNT -DACCUMULO_SCHEMA=$ACCUMULO_SCHEMA -Dmonitor.port.client=$MONITOR_PORT -DZOOKEEPER_PORT=$ZOOKEEPER_PORT -jar jodoc-*.jar"
