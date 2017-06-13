#!/bin/bash

IMPL="$HOME/workspace/FlowGuard/code/impl/"
KARAF_HOME="$HOME/workspace/FlowGuard/code/karaf/"
KARAF="$HOME/workspace/FlowGuard/code/karaf/target/assembly/bin"

#Clean and build the changes 
cd $IMPL
mvn clean install
cd $KARAF_HOME
mvn clean install

#Start the ODL container
($KARAF/karaf)
