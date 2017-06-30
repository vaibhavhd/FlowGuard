#!/bin/bash

ALL="$HOME/workspace/FlowGuard/code/"
IMPL="$HOME/workspace/FlowGuard/code/impl/"
KARAF_HOME="$HOME/workspace/FlowGuard/code/karaf/"
KARAF="$HOME/workspace/FlowGuard/code/karaf/target/assembly/bin"


if [ "$1" == "all" ]; then
    cd $ALL
    mvn clean install
elif [ "$1" == "test" ]; then
#Clean and build the changes
    cd $IMPL
    mvn clean install
    cd $KARAF_HOME
    mvn clean install
else
    echo "Usage: ./setup.sh <build_type>"
    echo "build_type: all / test"
    exit 0
fi

#Start the ODL container
($KARAF/karaf)
