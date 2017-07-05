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
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FwruleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryKey;
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
            rule.in_port = new NodeConnectorId(entry.getInPort());
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
                flowStorage.put(node.getId().getValue(), rn.addruletable(flowList));
                LOG.info("{} flows added for switch {}", flowStorage.get(node.getId().getValue()).size(), node.getId().getValue());

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
}
