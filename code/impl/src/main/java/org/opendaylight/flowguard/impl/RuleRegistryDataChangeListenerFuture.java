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
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;

import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;

import org.opendaylight.flowguard.packet.Ethernet;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.RuleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.rule.registry.RuleRegistryEntry;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;

public class RuleRegistryDataChangeListenerFuture extends AbstractFuture<RuleRegistryEntry> implements DataChangeListener,AutoCloseable{

    DataBroker db;
    ShiftedGraph shiftedGraph;
    ReadTransaction readTx;
      private static final Logger LOG = LoggerFactory.getLogger(RuleRegistryDataChangeListenerFuture.class);
      private ListenerRegistration<DataChangeListener> registration;

      public RuleRegistryDataChangeListenerFuture(DataBroker db, ShiftedGraph shiftedGraph) {
        this.db = db;
        this.shiftedGraph = shiftedGraph;
        this.readTx  = db.newReadOnlyTransaction();

        InstanceIdentifier<RuleRegistry> ruleIID =
            InstanceIdentifier.builder(RuleRegistry.class).build();
        db.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL, ruleIID, this,
            AsyncDataBroker.DataChangeScope.SUBTREE);
        LOG.info("DataChangeListener registered with MD-SAL for path {}", ruleIID);

        /* Retrieve all the static flow enteries from the switches */

      }

      @Override
      public void close() throws Exception {
        if (registration != null) {
          registration.close();
        }
      }

      @Override
      public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        LOG.info("dataChanged");
        DataObject dataObject;

        // Iterate over any created nodes or interfaces
        for (Map.Entry<InstanceIdentifier<?>, DataObject> entry : change.getCreatedData().entrySet()) {
          dataObject = entry.getValue();
          if (dataObject instanceof RuleRegistryEntry) {
            addRule((RuleRegistryEntry) dataObject);
          }
        }

      }

      private void addRule(RuleRegistryEntry input) {

        NodeId nodeId = new NodeId(input.getNode());

        // Creating match object
        MatchBuilder matchBuilder = new MatchBuilder();
        MatchUtils.createDstL3IPv4Match(matchBuilder, new Ipv4Prefix(input.getDestinationIpAddress()));// getIpAddr()));
        MatchUtils.createSetDstTcpMatch(matchBuilder, new PortNumber((input.getDestinationPort())));//getPort())));

        /*
         * Create Flow
         */
        String flowId = "New firewall rule" + input.getName();
        FlowBuilder flowBuilder = new FlowBuilder();
        flowBuilder.setMatch(matchBuilder.build());
        flowBuilder.setId(new FlowId(flowId));
        FlowKey key = new FlowKey(new FlowId(flowId));
        flowBuilder.setBarrier(true);
        flowBuilder.setTableId((short) 0);
        flowBuilder.setKey(key);
        flowBuilder.setPriority(32768);
        flowBuilder.setFlowName(flowId);
        flowBuilder.setHardTimeout(0);
        flowBuilder.setIdleTimeout(0);

        //FirewallRule rule = createFirewallRule(input.getNode(), input.getSourcePort());
        //this.shiftedGraph.buildSourceProbeNode(rule);

        /*
         * Perform transaction to store rule.
         * The instance identifier here provides location where the flow would be written in CONFIG database.
         * Nodes -> Node -> "add" -> Table -> Flow -> build[flow]()
         */
        InstanceIdentifier<Flow> flowIID =
            InstanceIdentifier.builder(Nodes.class).child(Node.class, new NodeKey(nodeId))
                .augmentation(FlowCapableNode.class)
                .child(Table.class, new TableKey(flowBuilder.getTableId()))
                .child(Flow.class, flowBuilder.getKey())
                .build();

        Preconditions.checkNotNull(db);
        WriteTransaction transaction = db.newWriteOnlyTransaction();
        transaction.merge( LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(),true);

        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed add firewall rule", LOG));

        LOG.info("Added security rule with ip {} and port {} into node {}", input.getDestinationIpAddress(), input.getDestinationPort(),input.getNode());

        importStaticFlows();
      }
      private FirewallRule createFirewallRule(String dpid, Integer port) {
          FirewallRule rule = new FirewallRule();
          rule.ruleid = rule.genID();
          rule.priority = 32768;
          rule.dpid = dpid;
          rule.wildcard_dpid = false;
          rule.in_port = port.shortValue();
          rule.wildcard_in_port = false;
          rule.wildcard_nw_src = false;
          rule.wildcard_dl_type = false;
          rule.dl_type = Ethernet.TYPE_IPv4;
          rule.nw_src_prefix = 167772160; //TODO This is an IP addr. Why default? This is a default value which was used in paper. ho.nw_src_prefix;
          rule.nw_src_maskbits = 8; // TODO ho.nw_src_maskbits;
          rule.wildcard_nw_dst = false;
          rule.wildcard_dl_type = false;
          rule.dl_type = Ethernet.TYPE_IPv4;
          rule.nw_dst_prefix = 167772160; // TODO ho.nw_dst_prefix;
          rule.nw_dst_maskbits = 8; //ho.nw_dst_maskbits;
          rule.action = FirewallRule.FirewallAction.DENY;
          //this.firewall.addRule(rule);
          return rule;
      }
// TODO - move the static flows to the init of flowguard.
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
              }

          } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
          }

          /* Collecting information */

       // Get all nodes in MD-SAL
          List<Node> nodeList = getAllNodes();
          for (Node node : nodeList) {
              LOG.debug("node : {}", node.toString());
          }

          // Get a particular node in MD-SAL
          Node node2 = getNode("node_001");
          LOG.debug("node2 : {}", node2.toString());

          // Get all topologies in MD-SAL
          List<Topology> topoList = getAllTopologies();
          for (Topology topo : topoList) {
              LOG.debug("topo : {}", topo.toString());
          }

          // Get a particular toplogy in MD-SAL
          Topology flowTopo = getFlowTopology();
          LOG.debug("flowTopo : {}", flowTopo.toString());

          // Get all links in MD-SAL
          List<Link> linkList = getAllLinks();
          for (Link link : linkList) {
              LOG.debug("link : {}", link.toString());
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

      // Get a particular node in MD-SAL
      private Node getNode(String nodeName) {
          NodeId nodeId = new NodeId(nodeName);
          InstanceIdentifier<Node> instanceIdentifier = InstanceIdentifier.builder(Nodes.class).child(Node.class, new NodeKey(nodeId)).toInstance();
          try {
          Optional<Node> optNode = (Optional<Node>) readTx.read(LogicalDatastoreType.OPERATIONAL, instanceIdentifier).get();
          Node node = optNode.get();
          return node;
          }
          catch(InterruptedException | ExecutionException e) {
              LOG.warn("Exception during reading node from datastore: {}", e.getMessage());
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

    @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
          quietClose();
          return super.cancel(mayInterruptIfRunning);
      }

      private void quietClose() {
        try {
          this.close();
        } catch (Exception e) {
          throw new IllegalStateException("Unable to close registration", e);
        }
      }
}
