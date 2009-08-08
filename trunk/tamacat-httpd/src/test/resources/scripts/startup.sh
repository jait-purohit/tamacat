#!/bin/sh
JAVA_HOME=/usr/java
JAVA_OPT=

if [ -z "$JAVA_HOME" ] ; then
  echo "Warning: JAVA_HOME environment variable is not set."
  exit;
fi

JAVA_EXE=$JAVA_HOME/bin/java

PRG="$0"
SERVER_HOME=`dirname $PRG`/..
CLASSPATH_JAR=$SERVER_HOME/conf

for i in `ls $SERVER_HOME/lib/*.jar`
do
  CLASSPATH_JAR=$CLASSPATH_JAR:$i
done

## echo $CLASSPATH_JAR

$JAVA_EXE $JAVA_OPT -classpath $CLASSPATH_JAR org.tamacat.httpd.Httpd

