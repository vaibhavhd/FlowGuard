# Flowguard

Flowguard is an SDN security mechansim to detect flow and firewall policy conflicts. It also dynamically resolves the conflicts by adding, modifying or deleting the conflicting flows in the OF switches.  

## Getting Started

Follow all the steps below if the target system is a fresh installation. Skip the steps if the machine is already configured.

### Prerequisites
Common requirements for testing and development:
Java JDK
```
	sudo apt-get install openjdk-8-jdk
```
Maven 
```
	sudo apt-get install maven
```

Create a maven file specifying ODL repositories
```
	cp -n ~/.m2/settings.xml{,.orig} ; \
wget -q -O - https://raw.githubusercontent.com/opendaylight/odlparent/master/settings.xml > ~/.m2/settings.xml
```

### Building
Clone the repo and enter into the working directory
```
	cd ~/workspace/FlowGuard/
```

ODL archetype consists of multiple sub projects. For the first time users, entire build is required: 
```
	mvn clean install 
```

If the build fails with warnigns about JavaDoc, skip the JavaDoc test
```
	mvn clean install -Dmaven.javadoc.skip=true
```

Similarly, clone and build the "openflow-visualizer" project
```
	mvn clean install
```

The simple feature test might take a longer than 10 minutes to finish, depending on the hardware. This is a required step for the first time and can be skipped later if there are no changes to feature project. 

If the "BUILD SUCCESS" message is received, the project is ready for testing.

### Deploying Flowguard
Enter the "karaf" project
```
	cd karaf
```

Use the karaf binary to start the ODL container
```
	./target/assembly/bin/karaf
```

The ODL container will start loading for the deployment. Once loaded fully, the "karaf" container prompt will be ready to use.
Verify that the Flowguard feature is installed in the ODL container
```
	feature:list | grep -i flowguard
```

In the browser, open ODL container
```
	http://127.0.0.1:8181/index.html
```

Credentials - Username: "admin" Password: "admin"

### Deploying OFVisualizer
Enter the visualizer project
```
	cd openflow-visualizer/ofvisualizer-bundle/target
```
The openflow-visualizer project is a bundle ready as a JAR file after a successful build. Copy the JAR bundle in the deploy folder of ODL controller.
```
	cp ./ofvisualizer-bundle-1.0.0-SNAPSHOT.jar ~/workspace/FlowGuard/code/karaf/target/assembly/deploy/
```

Open the DLUX webpage to ensure that the bundle has been deployed successfully
```
	http://127.0.0.1:8080/index.html#/ofvisualizer
```

##Running the tests

### Starting network

Use the mininet script for a network of OVS switches and hosts. The script starts the network and waits for the Openflow connection between controller and switches.
It also does a ping tests on the netork.
```
	sudo FlowGuard/results/scripts/linear_topo.py
```

Alternatively, start the mininet network manually
```
	sudo mn --topo linear,3 --mac --controller=remote,ip=127.0.0.1,port=6633 --switch ovs,protocols=OpenFlow13
```

Open the DLUX homepage on the browser and the topology should appear in the window
```
	http://127.0.0.1:8080/index.html#/topology
```


### Starting Flowguard
Go to DLUX
```
	http://127.0.0.1:8080/index.html#/yangui/index
```
Search for flowguard module
```
	flowguard rev.2017-05-05
```
Enter the "flowguard rev.2017-05-05" module and click the operations button. Following modules implemented as part of FlowGuard should appear.
For a detailed description of each module, refer the Flowguard documentation in the doc folder.
```
	flowguard-control
	add-fwrule
	get-conflicts
``` 
The format of a firewall rule file is kept in sync with traditional IPTABLES
```
    http://localhost:8181/restconf/operations/flowguard:add-fwrule
	{
	    "input": {
	        "ruleId": "1",
	        "priority": "50",
	        "sourceIpAddress": "10.0.0.1/32",
	        "destinationIpAddress": "10.0.0.3/32",
	        "sourcePort": "1",
	        "destinationPort": "80",
	        "action": "deny"
	    }
	}	

```

A sample list of firewall rules is also present at
```
	FlowGuard/results/scripts/data/FirewallRules.json
``` 

It is good practise to ensure the RPC has done its job by checking the logs for every web request made on DLUX

```
	opendaylight-user@root>log:display
Sample:
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | 

	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | Added Rule
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | *****************
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | *****************
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | input ruleid 10
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | input node openflow:3
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | input inport openflow:1:1
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | input src ip * 
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | input dst ip * 
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | input src port 1
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | input dst port 80
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | input action Block
	org.opendaylight.flowguard.impl - 0.1.0.SNAPSHOT | *****************

```

Finally, start the Flowguard policy analyzer by sending POST request to "flowguard-control" RPC.
```
	http://127.0.0.1:8181/restconf/operations/flowguard:flowguard-control

	{
	    "input": {
			"action": "Enable"
	    }
	}
```
Check logs to ensure Flowguard is doing the job. Header Space anaylysis logs will be shown
```
	DEBUG3 NO FLOW FOR: SWITCHDPID=openflow:1
	 sample dpid:openflow:1target dpid:openflow:2
	<<<<< current_HeaderObject >>>>>
	{ vlan = 0, src_IP = /10.0.0.0/8, dst_IP = /10.0.0.0/8 }
	current_switch_info = openflow:1 / openflow:1:1
	<<<<< next_HeaderObject >>>>>
	{ vlan = 0, src_IP = /10.0.0.0/8, dst_IP = /10.0.0.0/8 }
	next_switch_info = openflow:1 / openflow:1:1
	*************** This is 1 th flow ***************

```

### Starting Openflow Visualizer
On the left panel of DLUX, click on ofvisualizer app
```
	http://127.0.0.1:8080/index.html#/ofvisualizer
```

All the Openflow switches with corresponding tables and actions will be ready to be analyzed.
Click on CONFLICTS tab to see the D# visualization.

## Working with Science DMZ ODL VM
Follow following steps:

    Start ODL: On terminal, simply execute "my_karaf".
```
    $ my_karaf
```

If there are changes to the code( locally or pulled), build and start the Flowguard.
Note that the -all option takes much time to finish the tests. If there are no changes to feature project use -test option.
```
    $ ~/workspace/FlowGuard/results/scripts/build.sh
    Usage: ./build.sh <build_type>
    build_type: all / test
```

There is also a fast_build.sh script. This option disables ODL feature tests, which makes the build process much faster.

```
	$ ~/workspace/FlowGuard/results/scripts/build.sh
	Usage: ./fast_build.sh <build_type>
    build_type: all / test
```


Start Mininet on a different tab:
```
    $ sudo ~/workspace/FlowGuard/results/scripts/linear_topo.py
```

To install static firewall rules:
```
    $ ~/workspace/FlowGuard/results/scripts/flowguard.py -r ~/workspace/FlowGuard/results/scripts/data/FirewallRules.json
```

To start flowguard:
```
	$ ~/workspace/FlowGuard/results/scripts/flowguard.py -i
```

Verify that the two REST requests are successful(200):
```
    $ ~/workspace/FlowGuard/results/scripts/flowguard.py -r ~/workspace/FlowGuard/results/scripts/data/FirewallRules.json
    {'status': '204', 'transfer-encoding': 'chunked', 'set-cookie': 'JSESSIONID=1v6alx2rcgtihqtrs0tz78cse;Path=/restconf, rememberMe=deleteMe; Path=/restconf; Max-Age=0; Expires=Tue, 25-Jul-2017 00:49:25 GMT', 'expires': 'Thu, 01 Jan 1970 00:00:00 GMT', 'server': 'Jetty(8.1.19.v20160209)', 'content-type': 'application/yang.operation+json'}
    $ ~/workspace/FlowGuard/results/scripts/flowguard.py -i
    {'status': '200', 'transfer-encoding': 'chunked', 'set-cookie': 'JSESSIONID=18u080q7qaqunfbk4sz1mgt3b;Path=/restconf, rememberMe=deleteMe; Path=/restconf; Max-Age=0; Expires=Tue, 25-Jul-2017 00:49:25 GMT', 'expires': 'Thu, 01 Jan 1970 00:00:00 GMT', 'server': 'Jetty(8.1.19.v20160209)', 'content-type': 'application/yang.operation+json'}
```
The controller might take longer sometimes to install the flows and the second REST request might come as an error(500) untill then.


##### Important Note for Deployment #####
To deploy the module project Flowguard in a distribution controller downloaded from opendaylight website:
   	https://www.opendaylight.org/technical-community/getting-started-for-developers/downloads-and-documentation

First, download and build flowguard:
```
	$ cd ~/workspace/FlowGuard/code/
	$ mvn clean install
```

Secondly, download the Carbon (0.6.0) release from opendaylight webiste and unzip the file:
```	
	$ wget https://nexus.opendaylight.org/content/repositories/opendaylight.release/org/opendaylight/integration/distribution-karaf/0.6.0-Carbon/distribution-karaf-0.6.0-Carbon.tar.gz
```

Thirdly, access .m2/repository and copy flowguard folder and paste the flowguard folder in distribution-karaf-0.6.0-Carbon/system/
```
	$ cd ~/.m2/repository/org/opendaylight/
	$ cp -r flowguard/ ~/distribution-karaf-0.6.0-Carbon/system/org/opendaylight/.
```

Next, start distribution-karaf , add flowguard repo , and install flowguard features 
```	
	$ ~/distribution-karaf-0.6.0-Carbon/bin/karaf
	$ opendaylight-user@root> feature:repo-add mvn:org.opendaylight.flowguard/flowguard-features/0.1.0-SNAPSHOT/xml/features
	$ opendaylight-user@root> feature:install odl-flowguard odl-flowguard-api odl-flowguard-cli odl-flowguard-rest odl-flowguard-ui
```

Now, flowguard is ready!!!

## Contributing

## Versioning

## Authors

## License

## Acknowledgments

