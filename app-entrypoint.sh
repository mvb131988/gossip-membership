#!/bin/bash

nohup java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:${DEBUG_PORT} /tmp/gossip-membership.jar &

while true; do sleep 1000; done