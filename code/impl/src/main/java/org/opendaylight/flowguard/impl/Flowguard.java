/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.flowguard.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.flowguard.packet.IPv4;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfoRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FlowguardStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FlowguardStatusBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FwruleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitchKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;

public class Flowguard {
    private static final Logger LOG = LoggerFactory.getLogger(Flowguard.class);
    private ReadTransaction readTx;
    private static DataBroker db;
    private Map<TopologyStruct, TopologyStruct> topologyStorage;
    private Map<String, List<FlowRuleNode>> flowStorage;
    private List<FirewallRule> ruleStorage;
    private Map<Integer, Set<String>> fwruleSwitchList; // firewall rule ID is an int thus mapping Integer makes more sense
    private ShiftedGraph sg;

    Flowguard(DataBroker db){
        this.db = db;
        this.readTx  = db.newReadOnlyTransaction();
        this.topologyStorage = new ConcurrentHashMap<TopologyStruct, TopologyStruct>();
        this.flowStorage = new ConcurrentHashMap<String, List<FlowRuleNode>>();
        this.ruleStorage = new ArrayList<FirewallRule>();
        this.fwruleSwitchList = new ConcurrentHashMap<Integer,Set<String>>();
    }

    public void start() {
        // Create the snapshot of existing network topology */
        this.buildTopology();
        if(this.topologyStorage.isEmpty()) {
            /* No node<->node links found in the network
             * Issue #6  : Check for standalone nodes */
            getStandaloneNodes();
            if(this.topologyStorage.isEmpty()) {
                LOG.info("No network found!!");
                return;
            }
        }

        this.importStaticFlows();
        this.importStaticRules();

        this.sg = new ShiftedGraph(this, this.readTx, this.flowStorage, this.topologyStorage, this.db, this.fwruleSwitchList,this.ruleStorage);

        if(ruleStorage.size() != 0)
            sg.buildSourceProbeNode(this.ruleStorage);
        /* After firewall rules are checked for violation, run listeners on any external flow modifications */

        // TODO OPERATIONAL LISTENER
        // TODO New nodes listener
        FWRuleRegistryDataChangeListenerFuture firewall_future = new FWRuleRegistryDataChangeListenerFuture(this.db, this, this.sg);
        RuleRegistryDataChangeListenerFuture future = new RuleRegistryDataChangeListenerFuture(this.db, this.sg);
    }

    private void getStandaloneNodes() {
        InstanceIdentifier<Nodes> nodesIdentifier = InstanceIdentifier.builder(Nodes.class).toInstance();

        try {
            Optional<Nodes> optNodes= null;
            Optional<Table> optTable = null;
            Optional<Flow> optFlow = null;

            List<Node> nodeList;
            List<Flow> flowList;

            /* Retrieve all the switches in the operational data tree */
            optNodes = readTx.read(LogicalDatastoreType.OPERATIONAL, nodesIdentifier).get();
            /* If there are no operational nodes in the network - return*/
            if(optNodes == null)
                return;
            nodeList = optNodes.get().getNode();
            LOG.info("No. of detected nodes: {}", nodeList.size());

            for(Node node : nodeList) {
                String srcID = node.getId().getValue();
                List<NodeConnector> ports = node.getNodeConnector();
                for (NodeConnector port : ports) {
                    LOG.info("Standalone Node {} with port {}", srcID, port.getId().getValue());
                    TopologyStruct srcNode = new TopologyStruct(srcID,
                    		TopologyStruct.getPortfromURI(port.getId().getValue()));
                    TopologyStruct dstNode = new TopologyStruct(null, 0);
                    this.topologyStorage.put(srcNode, dstNode);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        }
    }

    private void importStaticRules() {
        //InstanceIdentifier<ABCD> ruleIdentifier  = InstanceIdentifier.builder(ABCD.class);
        //InstanceIdentifier<FwruleRegistryEntry> iid = InstanceIdentifier.create(FwruleRegistry.class)
          //      .child(FwruleRegistryEntry.class, new FwruleRegistryEntryKey(input.getName()));

        InstanceIdentifier<FwruleRegistry> iid  = InstanceIdentifier.builder(FwruleRegistry.class).toInstance();
        List<FwruleRegistryEntry> entries = null;
        try {
            Optional<FwruleRegistry> fwRules = (Optional<FwruleRegistry>) readTx.read(LogicalDatastoreType.CONFIGURATION, iid).get();
            if(!fwRules.isPresent() || fwRules.get() == null) {
                LOG.info("No static firewall rules installed");
                return;
            }
            entries = fwRules.get().getFwruleRegistryEntry();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(FwruleRegistryEntry entry : entries) {
            FirewallRule rule = new FirewallRule();
            addRuleToStorage(rule, entry);
        }
        Collections.sort(ruleStorage, new Comparator<FirewallRule>()  //sort fwrule priority in descending order
        	{
        		public int compare(FirewallRule rule1,FirewallRule rule2) {
        			int priority1 = rule1.priority;
        			int priority2 = rule2.priority;
        			return priority2 - priority1;
        		}
        	});
    }

    public void addRuleToStorage(FirewallRule rule, FwruleRegistryEntry entry) {
        rule.ruleid = entry.getRuleId();
        int[] arr;
        arr = parseIP(entry.getSourceIpAddress());
        rule.nw_src_prefix = arr[0];
        rule.nw_src_maskbits = arr[1];
        arr = parseIP(entry.getDestinationIpAddress());
        rule.nw_dst_prefix = arr[0];
        rule.nw_dst_maskbits = arr[1];
        rule.priority = entry.getPriority(); //get priority of the firewall rule
        rule.tp_src = parseL4Port(entry.getSourcePort());
        rule.tp_dst = parseL4Port(entry.getDestinationPort());
        rule.action = (entry.getAction() == Action.Allow) ? FirewallRule.FirewallAction.ALLOW
                : FirewallRule.FirewallAction.DENY;

        ruleStorage.add(rule);
        LOG.info("Rule added to the list: rule_id:{} ", rule.ruleid);
    }


    public void removeRuleFromStorage(FirewallRule rule, FwruleRegistryEntry entry){
    	rule.ruleid = entry.getRuleId();
        int[] arr;
        arr = parseIP(entry.getSourceIpAddress());
        rule.nw_src_prefix = arr[0];
        rule.nw_src_maskbits = arr[1];
        arr = parseIP(entry.getDestinationIpAddress());
        rule.nw_dst_prefix = arr[0];
        rule.nw_dst_maskbits = arr[1];
        rule.priority = entry.getPriority(); //get priority of the firewall rule
        rule.tp_src = parseL4Port(entry.getSourcePort());
        rule.tp_dst = parseL4Port(entry.getDestinationPort());
        rule.action = (entry.getAction() == Action.Allow) ? FirewallRule.FirewallAction.ALLOW
                : FirewallRule.FirewallAction.DENY;

        LOG.info("Deleting flows that corresponding to a firewall rule");
        String rule_name = "resolution_"+entry.getRuleId()+"_[0-9]*";// regex resolution_firewallID_resID
        Set<String> setOfSwitches = fwruleSwitchList.get(rule.ruleid);
        if((setOfSwitches != null) && (setOfSwitches.size() > 0)) {		//if fwrule has resolution then delete
        	 for(String nodeId : setOfSwitches) {
             	List<FlowRuleNode> flowList = flowStorage.get(nodeId);
             	for(int i=0; i < flowList.size(); i++) {
           	    	if((flowList.get(i).rule_name).matches(rule_name)){
           	    		sg.delFlowEntry(nodeId, flowList.get(i));
           	    	}
           	    }
             }
        	 fwruleSwitchList.remove(rule.ruleid);		//delete firewall rule from fwruleSwitchList
        }
        else {
        	//Should never reach here
        	LOG.info("switchSet is null!");
        }
    }

    /**Helper method
     * This method is used to parse ports and check if ports = null
     * @param port
     * @return
     */
    private short parseL4Port(String port) {
    	short portResult = 0;
    	if(port == null || port.equals("") || port.equals("*")) {
    		portResult = 0;
    	}
    	else {
    		portResult = Short.parseShort(port);
    	}
    	return portResult;
    }

    /**Helper method
     * This method is used to parse IPAddr and checks if addr == null
     * @param address
     * @return
     */
    private int[] parseIP(String address) {
        int[] arr = new int[2];
        if(address.equals("*") || address.equals("")) {
            arr[0] = 0;
            arr[1] = 0;
            return arr;
        }
        Ipv4Prefix src_addr = new Ipv4Prefix(address);
        int ip =  FlowRuleNode.calculateIpfromPrefix(src_addr);
        int mask = FlowRuleNode.calculateMaskfromPrefix(src_addr);

        arr[0] = ip;
        arr[1] = mask;
        return arr;
    }

    private void buildTopology() {
        List<Link> linkList = getAllLinks();
        /* linklist will be null if the nodes are not connected with each other
         * This can be the case when ODL fails to install the default flows in
         * the nw.
         */
        if( linkList == null) {
            return;
        }
        for (Link link : linkList) {
        	String destId = link.getDestination().getDestNode().getValue();
        	String srcId = link.getSource().getSourceNode().getValue();

        	if(srcId.contains("openflow:") && destId.contains("openflow:")) {
        		int destPort = TopologyStruct.getPortfromURI(link.getDestination().getDestTp().getValue());
        		int srcPort = TopologyStruct.getPortfromURI(link.getSource().getSourceTp().getValue());

        		TopologyStruct srcNode = new TopologyStruct(srcId, srcPort);
        		TopologyStruct destNode = new TopologyStruct(destId, destPort);
        		this.topologyStorage.put(srcNode, destNode);
        		LOG.info("Link stored : Source DPID: {} Port: {}", srcNode.dpid, srcNode.port);
        		LOG.info("\tDestination DPID: {} Port: {}", destNode.dpid, destNode.port);
        	}
        }
    }

    private void importStaticFlows() {
        /* Nodes(root) -> Node -> Table -> Flow */

        InstanceIdentifier<Nodes> nodesIdentifier = InstanceIdentifier.builder(Nodes.class).toInstance();

        try {
            Optional<Nodes> optNodes= null;

            List<Node> nodeList;
            List<Flow> flowList;
            List<Table> tableList;
            /* Retrieve all the switches in the operational data tree */
            optNodes = readTx.read(LogicalDatastoreType.OPERATIONAL, nodesIdentifier).get();
            /* If there are no operational nodes in the network - return*/
            if(optNodes == null)
                return;
            nodeList = optNodes.get().getNode();
            LOG.info("No. of detected nodes: {}", nodeList.size());

            /* Iterate through the list of nodes(switches) for flow tables per node */
            for(Node node : nodeList){
            	tableList = node.getAugmentation(FlowCapableNode.class).getTable();
            	String nodeID = node.getId().getValue();

            	for(Table table : tableList) {
            		flowList = table.getFlow();

                    LOG.info("No. of flows in table ID {}: {}",table.getId(), flowList.size());
                    /* Iterate through the list of flows */
                    for(Flow flow : flowList){
                        LOG.info("Flow found with ID: {}, outport: {}, Instructions: {}", flow.getId().getValue()
                                , flow.getOutPort(), flow.getInstructions());
                    }

                    List<FlowRuleNode> newList = FlowRuleNode.addruletable(flowList);

                    List<FlowRuleNode> oldList = this.flowStorage.get(nodeID);
                    if( oldList != null) {
                    	for (FlowRuleNode rule : newList)
                    	oldList.add(rule);
                    }
                    else {
                    	this.flowStorage.put(nodeID, newList);
                    }
            	}
            	LOG.info("{} flows added for switch {}", this.flowStorage.get(node.getId().getValue()).size(), node.getId().getValue());
            	List<FlowRuleNode> list = this.flowStorage.get(nodeID);
                if( list != null) {
                	writeToConflictRegistry(nodeID, list);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    // Get all nodes in MD-SAL
    private List<Node> getAllNodes() {
        InstanceIdentifier<Nodes> nodesIdentifier = InstanceIdentifier.builder(Nodes.class).toInstance();
        try {
            Optional<Nodes> optNodes = readTx.read(LogicalDatastoreType.OPERATIONAL, nodesIdentifier).get();
            Nodes nodes = optNodes.get();
            return nodes.getNode();
        }
        catch(InterruptedException | ExecutionException e) {
            LOG.warn("Exception during reading nodes from datastore: {}", e.getMessage());
            return null;
        }
    }

    // Get all topologies in MD-SAL
    private List<Topology> getAllTopologies() {
         InstanceIdentifier<NetworkTopology> topoIdentifier =
                 InstanceIdentifier.builder(NetworkTopology.class).toInstance();
         System.out.println("topoIdentifier " + topoIdentifier);
         try {
             Optional<NetworkTopology> optTopos = (Optional<NetworkTopology>) readTx.read(LogicalDatastoreType.OPERATIONAL, topoIdentifier).get();
             List<Topology> topos = optTopos.get().getTopology();
             return topos;
         }
         catch(InterruptedException | ExecutionException e) {
             LOG.warn("Exception during reading node from datastore: {}", e.getMessage());
             return null;
         }
    }

    // Get a particular toplogy in MD-SAL
    private Topology getFlowTopology() {
        TopologyId topoId = new TopologyId("flow:1");
        InstanceIdentifier<Topology> topoIdentifier = InstanceIdentifier.builder(NetworkTopology.class).child(Topology.class, new TopologyKey(topoId)).toInstance();
        System.out.println("topoIdentifier " + topoIdentifier);
        try {
            //Topology topology = (Topology) dataProviderService.readOperationalData(topoIdentifier);
            Optional<Topology> optTopo = (Optional<Topology>)readTx.read(LogicalDatastoreType.OPERATIONAL, topoIdentifier).get();
            Topology topology = optTopo.get();
            return topology;
        }
        catch(InterruptedException | ExecutionException e) {
            LOG.warn("Exception during reading node from datastore: {}", e.getMessage());
            return null;
        }
    }

    // Get all links in MD-SAL
    private List<Link> getAllLinks() {
        Topology flowTopology = getFlowTopology();
        return flowTopology.getLink();
    }

	public void addRule(FirewallRule rule) {
		// TODO Auto-generated method stub

	}

	static void writeToConflictRegistry(String nodeID, List<FlowRuleNode> list) {

		for ( FlowRuleNode rule : list) {
			WriteTransaction transaction = db.newWriteOnlyTransaction();
	    	/* Update the conflict data */
			ConflictInfo.Action action;
	    	if(rule.actionDrop)
	    		action = ConflictInfo.Action.BLOCK;
	    	else
	    		action = ConflictInfo.Action.ALLOW;

	    	ConflictInfo.Protocol proto;
	    	if(rule.nw_proto == 6)
	    		proto = ConflictInfo.Protocol.TCP;
	    	else if (rule.nw_proto == 17)
	    		proto = ConflictInfo.Protocol.UDP;
	    	else
	    		proto = ConflictInfo.Protocol.ANY;

	    	StringBuilder conflictList = new StringBuilder(rule.conflictList);
	    	LOG.info("Pushing new Conflict with conflict list {}",conflictList);
	    	if(conflictList.length() != 0)
	    	    conflictList.deleteCharAt(conflictList.length()-1);
	    	ConflictGroupEntry newFlow = new ConflictGroupEntryBuilder().setId(new Long(rule.flowId)).setVlanId(new Long(0))
    	        .setDlDst(rule.dl_dst).setDlSrc(rule.dl_src).setL4Dst(rule.tp_dst).setL4Src(rule.tp_src)
    	        .setNwDst(IPv4.fromIPv4Address(rule.nw_dst_prefix)).setNwSrc(IPv4.fromIPv4Address(rule.nw_src_prefix))
    	        .setPriority(rule.priority).setProtocol(proto).setInPort(Integer.toString(rule.in_port)).setAction(action)
    	        .setConflictGroupNumber(1).setConflictType(conflictList.toString())
    			.setShCount(rule.shCount).setGenCount(rule.genCount).setCorCount(rule.corCount)
    			.setRedCount(rule.redCount).setOverCount(rule.overCount).setPolicyCount(rule.policyCount)
                .setResolution(rule.resolution).setMechanism(rule.mechanism)
    			.build();
/*
          ConflictGroupEntry newFlow = new ConflictGroupEntryBuilder().setId(rule.rule_name).setVlanId(new Long(0))
                  .setDlDst(rule.dl_dst).setDlSrc(rule.dl_src).setL4Dst(rule.tp_dst).setL4Src(rule.tp_src)
                  .setNwDst(IPv4.fromIPv4Address(rule.nw_dst_prefix)).setNwSrc(IPv4.fromIPv4Address(rule.nw_src_prefix))
                  .setPriority(rule.priority).setProtocol(proto).setInPort("TODO").setAction(action)
                  .setConflictGroupNumber(1).setConflictType("1.5;5.4;2.3;2.1")
                  .setShCount(1).setGenCount(2).setCorCount(3).setRedCount(4).setOverCount(5)
                  .setResolution(rule.resolution).setMechanism(rule.mechanism)
                  .build();
*/
	    	InstanceIdentifier<ConflictGroupEntry> conflict = InstanceIdentifier.create(ConflictInfoRegistry.class)
	    			.child(ConflictSwitch.class, new ConflictSwitchKey(nodeID))
	    			.child(ConflictTable.class, new  ConflictTableKey(0))
	    			.child(ConflictGroupEntry.class, new ConflictGroupEntryKey(newFlow.getId()));

	    	transaction.merge(LogicalDatastoreType.CONFIGURATION, conflict , newFlow, true);
	        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
	        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed to write newFlow to conflict registry", LOG));

	        /* Update the status for the visualization engine */
	        /* The status is the synchronization flag for front-end with back-end */
	        Optional<FlowguardStatus> optStatus;
	        InstanceIdentifier<FlowguardStatus> statusIid = InstanceIdentifier.builder(FlowguardStatus.class).build();
	        ReadWriteTransaction statusTransaction = db.newReadWriteTransaction();
	        try {
				optStatus = statusTransaction.read(LogicalDatastoreType.CONFIGURATION, statusIid).get();

				if(optStatus != null) {
					statusTransaction.delete(LogicalDatastoreType.CONFIGURATION, statusIid);
			        CheckedFuture<Void, TransactionCommitFailedException> futureStatus = statusTransaction.submit();
			        Futures.addCallback(futureStatus, new LoggingFuturesCallBack<Void>("Failed to update the Flowguard status", LOG));
				}
				else {
			        /* Update the status flag with the Switch ID which has been updated */
			    	FlowguardStatus status = new FlowguardStatusBuilder().setFlowguardNodeStatus(nodeID).build();
			    	statusTransaction.put(LogicalDatastoreType.CONFIGURATION, statusIid, status, true);
			        CheckedFuture<Void, TransactionCommitFailedException> futureStatus = statusTransaction.submit();
			        Futures.addCallback(futureStatus, new LoggingFuturesCallBack<Void>("Failed to update the Flowguard status", LOG));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
