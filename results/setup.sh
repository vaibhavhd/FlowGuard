#!/bin/bash

#Start the mininet with specified configuration
sudo mn --topo linear --mac --controller=remote,ip=127.0.0.1,port=6633 --switch ovs,protocols=OpenFlow13

#Start the ODL container
KARAF_HOME="/home/local/ASUAD/vdixit2/workspace/FlowGuard/code/karaf/target/assembly/bin/"

($KARAF_HOME/karaf)
