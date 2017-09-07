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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryKey;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier.PathArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleRegistryDataChangeListenerFuture implements DataChangeListener,AutoCloseable{

    DataBroker db;
    ShiftedGraph sg;
    private static final Logger LOG = LoggerFactory.getLogger(RuleRegistryDataChangeListenerFuture.class);
    private ListenerRegistration<DataChangeListener> registration;
    private ReadTransaction readTx;

    public RuleRegistryDataChangeListenerFuture(DataBroker db, ShiftedGraph sg) {
        this.db = db;
        this.readTx  = db.newReadOnlyTransaction();
        this.sg = sg;
        /* Implement listeners for any MDSAL Flow datastore
         * Pull all the switches present in the network and implement
         * listeners on the tables of each switch.
         * TODO: functionality when a new switch is added to the network.
         */
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
            LOG.info("No. of nodes to listen: {}", nodeList.size());

            /* Iterate through the list of nodes(switches) for flow tables per node */
            for(Node node : nodeList){
                InstanceIdentifier<Flow> flowID = InstanceIdentifier.builder(Nodes.class).child(Node.class, new NodeKey(node.getId()))
                        .augmentation(FlowCapableNode.class)
                        .child(Table.class, new TableKey((short)0))
                        .child(Flow.class)
                        .build();
                db.registerDataChangeListener(LogicalDatastoreType.CONFIGURATION, flowID, this, AsyncDataBroker.DataChangeScope.SUBTREE);
                LOG.info("DataChangeListener registered with MD-SAL for path {}", flowID);
            }
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (registration != null) {
            registration.close();
        }
    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        LOG.info("dataChanged while listening to Flow change");
        DataObject dataObject;
        /* Possible data changes: 1-New Flow 2- Modified Flow 3- Deleted Flows
         * getCreatedData(): Returns a map of paths and newly created objects, which were introduced by this change into conceptual data tree
         * getRemovedPaths(): Returns an immutable set of removed paths
         * getUpdatedData(): Returns a map of path and objs which were updated by this change in the conceptual tree.
         */
        Iterator<InstanceIdentifier<?>> iter = change.getCreatedData().keySet().iterator();

        while(iter.hasNext()) {
            InstanceIdentifier<?> iid = iter.next();
            /* change data returns a bunch of newly created objects.
             * Object of interest is a new Flow object and its path.
             */

            if(iid.getTargetType().getSimpleName().equals("Flow")) {
                InstanceIdentifier<Node> node = iid.firstIdentifierOf(Node.class);
                for (Map.Entry<InstanceIdentifier<?>, DataObject> entry : change.getCreatedData().entrySet()) {
                    dataObject = entry.getValue();
                    if (dataObject instanceof Flow) {
                        LOG.info("Node {} Flow {} ", node.firstKeyOf(Node.class).getId().getValue(), ((Flow)dataObject).getFlowName());
                        sg.staticEntryModified(node.firstKeyOf(Node.class).getId().getValue(), ((Flow)dataObject));
                    }
                }
                break;
            }
        }


        for (Map.Entry<InstanceIdentifier<?>, DataObject> entry : change.getUpdatedData().entrySet()) {
            dataObject = entry.getValue();
            if (dataObject instanceof Flow) {
                LOG.info("Leaf updated is Flow");
            }
        }
        Set<InstanceIdentifier<?>> set = change.getRemovedPaths();
        for (InstanceIdentifier<?> entry : set) {
            if (entry instanceof Flow ) {
                LOG.info("Leaf removed is a Flow");
            }
        }

    }

    private void quietClose() {
        try {
            this.close();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to close registration", e);
        }
    }
}
