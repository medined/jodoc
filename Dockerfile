FROM medined/java:zulu7
ADD target/jodoc-1.0.1.jar .
ADD startup.sh .
CMD [ "./startup.sh" ]
