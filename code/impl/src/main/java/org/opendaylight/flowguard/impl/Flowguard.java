/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.flowguard.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
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
    public Map<NodeId, List<RuleNode>> flowStorage;
    public ShiftedGraph sg;

    Flowguard(DataBroker db){
        this.db = db;
        this.readTx  = db.newReadOnlyTransaction();
        this.topologyStorage = new ConcurrentHashMap<TopologyStruct, TopologyStruct>();
        this.flowStorage = new ConcurrentHashMap<NodeId, List<RuleNode>>();
    }

    public void start() {
        // Create the snapshot of existing network topology */
        this.buildTopology();
        this.importStaticFlows();

        this.sg = new ShiftedGraph();
        //sg.buildSourceProbeNode(this.ruleStorage);

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
                RuleNode rn = new RuleNode();

                flowStorage.put(node.getId(), rn.addruletable(flowList));
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
}
