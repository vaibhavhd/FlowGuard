/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.flowguard.impl;


import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;

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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FwruleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.RuleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.rule.registry.RuleRegistryEntry;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleRegistryDataChangeListenerFuture extends AbstractFuture<RuleRegistryEntry> implements DataChangeListener,AutoCloseable{

	  DataBroker db;
      private static final Logger LOG = LoggerFactory.getLogger(RuleRegistryDataChangeListenerFuture.class);
      private ListenerRegistration<DataChangeListener> registration;

      public RuleRegistryDataChangeListenerFuture(DataBroker db) {
        this.db = db;

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
            addFlowRule((RuleRegistryEntry) dataObject);
          }
        }

      }

    private void addFlowRule(RuleRegistryEntry input) {

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
