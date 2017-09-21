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

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FwruleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntryBuilder;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;

public class FWRuleRegistryDataChangeListenerFuture extends AbstractFuture<FwruleRegistryEntry> implements DataChangeListener,AutoCloseable{


    DataBroker db;
    private static final Logger LOG = LoggerFactory.getLogger(FWRuleRegistryDataChangeListenerFuture.class);
    private ListenerRegistration<DataChangeListener> registration;
    private Flowguard flowguard;
    private ShiftedGraph shiftedGraph;

	 public FWRuleRegistryDataChangeListenerFuture(DataBroker db, Flowguard flowguard, ShiftedGraph sg) {
		   this.db = db;
		   this.flowguard = flowguard;
		   this.shiftedGraph = sg;
		   InstanceIdentifier<FwruleRegistry> ruleIID = InstanceIdentifier.builder(FwruleRegistry.class).build();

		   db.registerDataChangeListener(LogicalDatastoreType.CONFIGURATION, ruleIID, this, AsyncDataBroker.DataChangeScope.SUBTREE);

		   LOG.info("FIREWALL RULES DataChangeListener registered with MD-SAL for path {}", ruleIID);
	 }


	@Override
	public void close() throws Exception {
		 if (registration != null) {
	          registration.close();
	     }
	}

	@Override
	public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
		  LOG.info("Firewall rule dataChanged");
	        DataObject dataObject;

	        // Iterate over any created nodes or interfaces
	        for (Map.Entry<InstanceIdentifier<?>, DataObject> entry : change.getCreatedData().entrySet()) {
	        	dataObject = entry.getValue();
	        	 if((dataObject instanceof FwruleRegistryEntry)) {
	                 addFirewallRule((FwruleRegistryEntry) dataObject);
	                 //Redundancy here
	             }
	        }
	}


    /**
     * Don't need this yet. This is for comparing with the ShiftedGraph
     * @param firewallRule
     */
    private void addFirewallRule(FwruleRegistryEntry input) {

        FwruleRegistryEntry entry = new FwruleRegistryEntryBuilder()
                 .setRuleId(input.getRuleId())
                 .setNode(input.getNode())
                 .setInPort(input.getInPort())
                 .setSourceIpAddress(input.getSourceIpAddress())
                 .setDestinationIpAddress(input.getDestinationIpAddress())
                 .setSourcePort(input.getSourcePort())
                 .setDestinationPort(input.getDestinationPort())
                 .setAction(input.getAction())
                 .setPriority(input.getPriority())
                 .build();
        FirewallRule rule = new FirewallRule();
        flowguard.addRuleToStorage(rule, entry);
        //if(shiftedGraph != null)
            shiftedGraph.buildSourceProbeNode(rule);
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
