/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.flowguard.impl;

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.AddFwruleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.AddFwruleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.AddFwruleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FlowguardControlInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FlowguardControlOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FlowguardControlOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FlowguardService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FwruleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.GetConflictsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.GetConflictsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntryKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FlowguardImpl implements FlowguardService  {
    private static final Logger LOG = LoggerFactory.getLogger(FlowguardImpl.class);
    private DataBroker db;
    public ShiftedGraph sg;
    public Flowguard flowguard;
    //private FirewallRuleParser static_rule_parser;

    public FlowguardImpl(DataBroker db) {
        this.db = db;
        this.initializeDataTree(db);
    }

    @Override
    public Future<RpcResult<FlowguardControlOutput>> flowguardControl(FlowguardControlInput input) {
        LOG.info("Preparing to start the FlowGuard - Firewall and dynamic conflict resolution tool");

        flowguard = new Flowguard(this.db);
        flowguard.start();

        FlowguardControlOutput output =
        new FlowguardControlOutputBuilder().setGreeting("Flowguard Control activated").build();
        //writeToGreetingRegistry(input, output);
        return RpcResultBuilder.success(output).buildFuture();
    }

    /**
     * RPC for Adding dynamic rules
     */
    @Override
    public Future<RpcResult<AddFwruleOutput>> addFwrule(AddFwruleInput input) {
        LOG.info("Preparing to add DYNAMIC firewall rule");
        AddFwruleOutput output =
            new AddFwruleOutputBuilder().setGreeting("Added dynamic firewall rule").build();
        writeToFwRuleRegistry(input, output);
        return RpcResultBuilder.success(output).buildFuture();
    }


    /**
     * RPC for fetching existing conflicts
     */
    @Override
	public Future<RpcResult<GetConflictsOutput>> getConflicts(GetConflictsInput input) {
		LOG.info("Preparing to pull the flows and calculate conflicts");
		List<ConflictGroupList> list = new ArrayList<ConflictGroupList>();
/*
		ConflictGroupList head = new ConflictGroupListBuilder().setId(new Long(1)).setVlanId(new Long(0))
		        .setDlDst("1.1.1.1").setDlSrc("1.1.1.1").setL4Dst("").setL4Src("").setNwDst("").setNwSrc("")
		        .setPriority(1).setProtocol(Protocol.ANY).setInPort("Openflow:1:1").setAction(Action.DROP)
		        .setConflictGroupNumber(1).setConflictType("3.3;3.11;3.19;3.20;3.15;1.12;3.4;3.17")
				.setShCount(0).setGenCount(1).setCorCount(0).setRedCount(7).setOverCount(1)
				.build();

		list.add(head);

		for (long i=2; i<=20 ; i++ ) {
		    ConflictGroupList element = new ConflictGroupListBuilder().setId(i).setVlanId(new Long(0))
	                .setDlDst("1.1.1.1").setDlSrc("1.1.1.1").setL4Dst("").setL4Src("").setNwDst("").setNwSrc("")
	                .setPriority(1).setProtocol(Protocol.ANY).setInPort("Openflow:1:1").setAction(Action.DROP)
	                .setConflictGroupNumber(1).setConflictType("")
	                .setShCount(0).setGenCount(0).setCorCount(0).setRedCount(0).setOverCount(0)
	                .build();

	        list.add(element);
		}
        GetConflictsOutput output = new GetConflictsOutputBuilder().setConflictGroupList(list).build();
        writeToConflictRegistry(input);
*/
		return null;// RpcResultBuilder.success(output).buildFuture();
	}

	private void initializeDataTree(DataBroker db) {
        LOG.info("Preparing to initialize the controls and rules registry");
    }

    private void writeToFwRuleRegistry(AddFwruleInput input, AddFwruleOutput output) {
        WriteTransaction transaction = db.newWriteOnlyTransaction();
        InstanceIdentifier<FwruleRegistryEntry> iid = toInstanceIdentifier(input);
        FwruleRegistryEntry rule = new FwruleRegistryEntryBuilder()
                .setNode(input.getNode())
                .setRuleId(input.getRuleId())
                .setInPort(input.getInPort())
                .setPriority(input.getPriority())
                .setSourceIpAddress(input.getSourceIpAddress())
                .setDestinationIpAddress(input.getDestinationIpAddress())
                .setSourcePort(input.getSourcePort())
                .setDestinationPort(input.getDestinationPort())
                .setAction(input.getAction())
                .build();
        transaction.put(LogicalDatastoreType.CONFIGURATION, iid, rule);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed to write greeting to greeting registry", LOG));
        LOG.info("\nAdded Firewall Rule");
        LOG.info("*****************");
        LOG.info("input rule_id {}", input.getRuleId());
        LOG.info("input node {}", input.getNode());
        LOG.info("input inport {}",input.getInPort());
        LOG.info("input priority {}",input.getPriority());
        LOG.info("input src ip {} ", input.getSourceIpAddress());
        LOG.info("input dst ip {} ", input.getDestinationIpAddress());
        LOG.info("input src port {}", input.getSourcePort());
        LOG.info("input dst port {}", input.getDestinationPort());
        LOG.info("input action {}", input.getAction());
        LOG.info("*****************\n");
    }

    private InstanceIdentifier<FwruleRegistryEntry> toInstanceIdentifier(AddFwruleInput input) {
        InstanceIdentifier<FwruleRegistryEntry> iid = InstanceIdentifier.create(FwruleRegistry.class)
            .child(FwruleRegistryEntry.class, new FwruleRegistryEntryKey(input.getRuleId()));
        return iid;
    }
}
