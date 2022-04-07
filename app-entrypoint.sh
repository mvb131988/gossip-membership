#!/bin/bash

nohup java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:${DEBUG_PORT} /tmp/gossip-membership.jar > /tmp/log.txt 2>&1 &

while true; do sleep 1000; done