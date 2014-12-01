FROM medined/java:zulu7
ADD target/jodoc-1.0-SNAPSHOT.jar .
ADD startup.sh .
CMD [ "./startup.sh" ]
