/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.flowguard.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.flowguard.packet.IPv4;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.Fwrule.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.ConflictInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.ConflictInfo.Protocol;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.ConflictInfoRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardStatusBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FwruleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.GetConflictsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.ConflictSwitch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.ConflictSwitchKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.conflictswitch.ConflictTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.conflictswitch.ConflictTableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.get.conflicts.output.ConflictGroupListBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
//import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
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
    private DataBroker db;
    public Map<TopologyStruct, TopologyStruct> topologyStorage;
    public Map<String, List<FlowRuleNode>> flowStorage;
    public List<FirewallRule> ruleStorage;
    public ShiftedGraph sg;

    Flowguard(DataBroker db){
        this.db = db;
        this.readTx  = db.newReadOnlyTransaction();
        this.topologyStorage = new ConcurrentHashMap<TopologyStruct, TopologyStruct>();
        this.flowStorage = new ConcurrentHashMap<String, List<FlowRuleNode>>();
        this.ruleStorage = new ArrayList<FirewallRule>();
    }

    public void start() {
        // Create the snapshot of existing network topology */
        this.buildTopology();
        this.importStaticFlows();
        this.importStaticRules();

        this.sg = new ShiftedGraph(this, this.readTx, this.flowStorage, this.topologyStorage);
        RuleRegistryDataChangeListenerFuture future = new RuleRegistryDataChangeListenerFuture(this.db, this.sg);
        //Pull the FW Rules from a file.
        if(ruleStorage.size() != 0)
            sg.buildSourceProbeNode(this.ruleStorage);

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

            rule.ruleid = entry.getRuleId();
            int[] arr;
            arr = parseIP(entry.getSourceIpAddress());
            rule.nw_src_prefix = arr[0];
            rule.nw_src_maskbits = arr[1];

            arr = parseIP(entry.getDestinationIpAddress());
            rule.nw_dst_prefix = arr[0];
            rule.nw_dst_maskbits = arr[1];

            rule.tp_src = Short.parseShort(entry.getSourcePort());
            rule.tp_dst = Short.parseShort(entry.getDestinationPort());
            rule.action = (entry.getAction() == Action.Allow) ? FirewallRule.FirewallAction.ALLOW
                    : FirewallRule.FirewallAction.DENY;
            rule.in_port = new String(entry.getInPort());
            rule.dpid = entry.getNode();

            ruleStorage.add(rule);

            LOG.info("Rule for switch: {} addded to the list: id:{} ", rule.dpid, rule.ruleid);
        }
    }

    private int[] parseIP(String address) {
        int[] arr = new int[2];
        if(address.equals("*")) {
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
        for (Link link : linkList) {
        	String destId = link.getDestination().getDestNode().getValue();
        	String srcId = link.getSource().getSourceNode().getValue();

        	if(srcId.contains("openflow:") && destId.contains("openflow:")) {
        		String destPort = link.getDestination().getDestTp().getValue();
        		String srcPort = link.getSource().getSourceTp().getValue();
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
            Optional<Table> optTable = null;
            Optional<Flow> optFlow = null;

            List<Node> nodeList;
            List<Flow> flowList;

            /* Retrieve all the switches in the operational data tree */
            optNodes = readTx.read(LogicalDatastoreType.OPERATIONAL, nodesIdentifier).get();
            nodeList = optNodes.get().getNode();
            LOG.info("No. of detected nodes: {}", nodeList.size());

            /* Iterate through the list of nodes(switches) for flow tables per node */
            for(Node node : nodeList){
                InstanceIdentifier<Table> table = InstanceIdentifier.builder(Nodes.class).child(Node.class, new NodeKey(node.getId()))
                        .augmentation(FlowCapableNode.class)
                        .child(Table.class, new TableKey((short)0)).toInstance();  // TODO Table ID is hardcaoded to 0. What about other tables?
                optTable = readTx.read(LogicalDatastoreType.OPERATIONAL, table).get();
                flowList = optTable.get().getFlow();

                LOG.info("No. of flows in table ID {}: {}",optTable.get().getId(), flowList.size());

                /* Iterate through the list of flows */
                for(Flow flow : flowList){
                    LOG.info("Flow found with ID: {}, outport: {}, Match: {}", flow.getId(), flow.getOutPort(), flow.getMatch().getLayer3Match());
                }
                FlowRuleNode rn = new FlowRuleNode();
                String nodeID = node.getId().getValue();

                List<FlowRuleNode> list = rn.addruletable(flowList);
                writeToConflictRegistry(nodeID, list);

                this.flowStorage.put(nodeID, list);
                LOG.info("{} flows added for switch {}", this.flowStorage.get(node.getId().getValue()).size(), node.getId().getValue());

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

	private void writeToConflictRegistry(String nodeID, List<FlowRuleNode> list) {

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

	    	ConflictGroupEntry newFlow = new ConflictGroupEntryBuilder().setId(rule.flowId).setVlanId(new Long(0))
    	        .setDlDst(rule.dl_dst).setDlSrc(rule.dl_src).setL4Dst(rule.tp_dst).setL4Src(rule.tp_src)
    	        .setNwDst(IPv4.fromIPv4Address(rule.nw_dst_prefix)).setNwSrc(IPv4.fromIPv4Address(rule.nw_src_prefix))
    	        .setPriority(rule.priority).setProtocol(proto).setInPort(rule.in_port).setAction(action)
    	        .setConflictGroupNumber(1).setConflictType(rule.conflictList.toString())
    			.setShCount(rule.shCount).setGenCount(rule.genCount).setCorCount(rule.corCount).setRedCount(rule.redCount).setOverCount(rule.overCount)
                .setResolution(rule.resolution).setMechanism(rule.mechanism)
    			.build();

	    	InstanceIdentifier<ConflictGroupEntry> conflict = InstanceIdentifier.create(ConflictInfoRegistry.class)
	    			.child(ConflictSwitch.class, new ConflictSwitchKey(nodeID))
	    			.child(ConflictTable.class, new  ConflictTableKey(0))
	    			.child(ConflictGroupEntry.class, new ConflictGroupEntryKey(newFlow.getId()));

	    	transaction.merge(LogicalDatastoreType.CONFIGURATION, conflict , newFlow, true);
	        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
	        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed to write newFlow to conflict registry", LOG));

	        /* Update the status for the visualization engine */
	        /* The status is the synchronization flag for front-end with back-end */
	        transaction = db.newWriteOnlyTransaction();
	        InstanceIdentifier<FlowguardStatus> statusIid = InstanceIdentifier.create(FlowguardStatus.class);
	        /* Update the status flag with the Switch ID which has been updated */
	    	FlowguardStatus status = new FlowguardStatusBuilder().setFlowguardStatus(nodeID).build();
	        transaction.merge(LogicalDatastoreType.CONFIGURATION, statusIid, status, true);
	        CheckedFuture<Void, TransactionCommitFailedException> futureStatus = transaction.submit();
	        Futures.addCallback(futureStatus, new LoggingFuturesCallBack<Void>("Failed to update the Flowguard status", LOG));
		}
	}
}
