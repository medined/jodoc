jodoc
=====

Runs MiniAccumuloCluster inside a Docker container

I continue to look for the ways to begin working with Accumulo. With this project I may have the simplest possible setup assuming you're a Docker fan (and if not, you should become one!).

It's simple because you don't need Hadoop or Zookeeper installed.

# Quick Start

The following four commands will start the MAC inside without needing to clone this project.

```
export WORKDIR=~/my-miniaccumulo-cluster
rm -rf $WORKDIR/*
mkdir -p $WORKDIR
docker run \
  -v $WORKDIR:/accumulo \
  -e TSERVER_COUNT=20 \
  -e ACCUMULO_SCHEMA=D4M \
  -e JAVA_USER=${USER} \
  -e MONITOR_PORT=20001 \
  -e ZOOKEEPER_PORT=20000 \
  -d \
  --net=host \
  -t medined/jodoc
```

# Docker Image Dependency

This Docker image depends on my medined/java:zulu7 image from the Docker Hub. It's maintained on github at https://github.com/medined/java-zulu-7.

# Instructions

## Clone the project

```
git clone https://github.com/medined/jodoc.git
cd jodoc
```

## Compile the code

Compile the Java code and create a shaded jar file. This jar file is copied 
into the Docker image that runs the MAC. It's shaded so that all dependent 
classes are available inside the container.

```
mvn package
```

## Build the image

Now build the jodoc image. This is the image that runs the MAC.

```
./build-image.sh

```

## Run the image

Once the image is built, it can be run. The number "20000" will be displayed. 
It's the port number for the internal Zookeeper. You can easily change the port 
number, just look inside the script.

```
./run-image.sh
```

This script automatically creates the tables needed for the D4M schema because
it sets the ACCUMULO_SCHEMA environment variable.

# Run client program

Now, you can run the client program. It looks for a "demo" table, 
creating it if not found. The first time the client program is run, it will 
display a "TABLE DOES NOT EXIST" message. The second time it will display
"TABLE EXISTS" message proving that you are connecting to Accumulo and
effecting change.

```
mvn \
  exec:java \
  -Dexec.mainClass="com.codebits.jodoc.WriteAndReadDriver" \
  -DZOOKEEPER_PORT=20000
```

If you change the port number in the "run-image.sh" script, change the port 
number in the "mvn exec:java" command as well.

2014, Nov 29 - at the time of this writing, there is a outstanding issue[1] that
causes an exeception to be thrown as the client program is exiting. The 
exception message starts "java.lang.InterruptedException: sleep interrupted"
and can be ignored. Since the exception happens when the client application is
exiting, all resources are freed.

[1] https://issues.apache.org/jira/browse/ACCUMULO-2113

# Changes

## 1.0.1

Fix logic check for Monitor Port.