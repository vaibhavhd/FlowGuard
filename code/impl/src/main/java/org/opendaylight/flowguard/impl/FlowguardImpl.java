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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddDynamicFwruleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddDynamicFwruleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddDynamicFwruleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddStaticFwruleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddStaticFwruleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddStaticFwruleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.ConflictInfo.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.ConflictInfo.Protocol;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.ConflictInfoRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.Controls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.ControlsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardControlInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardControlOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardControlOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardStatusBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FwruleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.GetConflictsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.GetConflictsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.GetConflictsOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.RuleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.RuleRegistryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.ConflictGroupEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.ConflictGroupEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.conflict.info.registry.ConflictGroupEntryKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.get.conflicts.output.ConflictGroupList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.get.conflicts.output.ConflictGroupListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.rule.registry.RuleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.rule.registry.RuleRegistryEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.rule.registry.RuleRegistryEntryKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FlowguardImpl implements FlowguardService  {
    private static final Logger LOG = LoggerFactory.getLogger(FlowguardImpl.class);
    private DataBroker db;
    public ShiftedGraph sg;
    public Flowguard fg;
    private FirewallRuleParser static_rule_parser;

    public FlowguardImpl(DataBroker db) {
        this.db = db;
        this.initializeDataTree(db);
    }

    @Override
    public Future<RpcResult<FlowguardControlOutput>> flowguardControl(FlowguardControlInput input) {
        LOG.info("Preparing to start the FlowGuard - Firewall and dynamic conflict resolution tool");

        fg = new Flowguard(this.db);
        fg.start();

        FlowguardControlOutput output =
        new FlowguardControlOutputBuilder().setGreeting("Flowguard Control activated").build();
        //writeToGreetingRegistry(input, output);
        return RpcResultBuilder.success(output).buildFuture();
    }

    @Override
    public Future<RpcResult<AddRuleOutput>> addRule(AddRuleInput input) {

        LOG.info("Preparing to add the FIREWALL rule");
        AddRuleOutput output =
            new AddRuleOutputBuilder().setGreeting("Added firewall rule").build();
        writeToGreetingRegistry(input, output);
        return RpcResultBuilder.success(output).buildFuture();
    }

    /**
     * RPC for Adding dynamic rules
     */
    @Override
    public Future<RpcResult<AddDynamicFwruleOutput>> addDynamicFwrule(AddDynamicFwruleInput input) {
        LOG.info("Preparing to add DYNAMIC firewall rule");
        AddDynamicFwruleOutput output =
            new AddDynamicFwruleOutputBuilder().setGreeting("Added dynamic firewall rule").build();
        writeToGreetingRegistry(input, output);
        return RpcResultBuilder.success(output).buildFuture();
    }

    /**
     * RPC for adding static rules
     */
    @Override
    public Future<RpcResult<AddStaticFwruleOutput>> addStaticFwrule(AddStaticFwruleInput input) {
        LOG.info("Preparing to add STATIC firewall rule");
        static_rule_parser = new FirewallRuleParser(db,input.getFilePath());
        static_rule_parser.start();
        AddStaticFwruleOutput output = new AddStaticFwruleOutputBuilder().setGreeting("Added static firewall rule(s)").build();
        return  RpcResultBuilder.success(output).buildFuture();
    }
    /**
     * RPC for fetching existing conflicts
     */
    @Override
	public Future<RpcResult<GetConflictsOutput>> getConflicts(GetConflictsInput input) {
		LOG.info("Preparing to pull the flows and calculate conflicts");
		List<ConflictGroupList> list = new ArrayList<ConflictGroupList>();

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

		return RpcResultBuilder.success(output).buildFuture();
	}

    private void writeToConflictRegistry(GetConflictsInput input) {
    	WriteTransaction transaction = db.newWriteOnlyTransaction();

    	/* Update the conflict data */
    	/*InstanceIdentifier<ConflictGroupEntry> iid = InstanceIdentifier.create(ConflictInfoRegistry.class)
    	            .child(ConflictGroupEntry.class, new ConflictGroupEntryKey(flow_index));
    	ConflictGroupEntry node = new ConflictGroupEntryBuilder()
    			.setAction(Action.DROP).setConflictGroupNumber(1).setConflictType("1.5;5.4;2.3;2.1")
				.setCorCount(0).setDlDst("1.1.1.1").setDlSrc("1.1.1.1").setGenCount(0).setId(new Long(1)).setInPort("Openflow:1:1")
				.setL4Dst("").setL4Src("").setNwDst("").setNwSrc("").setOverCount(1).setPriority(1).setProtocol(Protocol.ANY).setRedCount(0)
				.setShCount(0).setVlanId(new Long(0)).build();
        transaction.put(LogicalDatastoreType.CONFIGURATION, iid, node);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed to write greeting to greeting registry", LOG));
        */
        /* Update the status for the visualization engine */
        transaction = db.newWriteOnlyTransaction();
        InstanceIdentifier<FlowguardStatus> statusIid = InstanceIdentifier.create(FlowguardStatus.class);
    	FlowguardStatus status = new FlowguardStatusBuilder().setFlowguardStatus(10).build();
        transaction.put(LogicalDatastoreType.CONFIGURATION, statusIid, status);
        CheckedFuture<Void, TransactionCommitFailedException> futureStatus = transaction.submit();
        Futures.addCallback(futureStatus, new LoggingFuturesCallBack<Void>("Failed to update the Flowguard status", LOG));

	}

	private void initializeDataTree(DataBroker db) {
        LOG.info("Preparing to initialize the controls and rules registry");
        WriteTransaction transaction = db.newWriteOnlyTransaction();

        // Create and initialize the rule_registry tree
        InstanceIdentifier<RuleRegistry> rulesId = InstanceIdentifier.create(RuleRegistry.class);
        RuleRegistry ruleRegistry = new RuleRegistryBuilder().build();
        transaction.put(LogicalDatastoreType.OPERATIONAL, rulesId, ruleRegistry);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new LoggingFuturesCallBack<>("Failed to create rules registry",
            LOG));
    }

    private void writeToGreetingRegistry(AddRuleInput input, AddRuleOutput output) {
        WriteTransaction transaction = db.newWriteOnlyTransaction();
        InstanceIdentifier<RuleRegistryEntry> iid = toInstanceIdentifier(input);
        RuleRegistryEntry rule = new RuleRegistryEntryBuilder()
                .setGreeting(output.getGreeting())
                .setNode(input.getNode())
                .setName(input.getName())
                .setDestinationIpAddress(input.getDestinationIpAddress())
                .setDestinationPort(input.getDestinationPort())
                .build();
        transaction.put(LogicalDatastoreType.CONFIGURATION, iid, rule);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed to write greeting to greeting registry", LOG));
    }

    private InstanceIdentifier<RuleRegistryEntry> toInstanceIdentifier(AddRuleInput input) {
        InstanceIdentifier<RuleRegistryEntry> iid = InstanceIdentifier.create(RuleRegistry.class)
            .child(RuleRegistryEntry.class, new RuleRegistryEntryKey(input.getName()));
        return iid;
    }

    private void writeToGreetingRegistry(AddDynamicFwruleInput input, AddDynamicFwruleOutput output) {
        WriteTransaction transaction = db.newWriteOnlyTransaction();
        InstanceIdentifier<FwruleRegistryEntry> iid = toInstanceIdentifier(input);
        FwruleRegistryEntry rule = new FwruleRegistryEntryBuilder()
                .setNode(input.getNode())
                .setRuleId(input.getRuleId())
                .setInPort(input.getInPort())
                .setSourceIpAddress(input.getSourceIpAddress())
                .setDestinationIpAddress(input.getDestinationIpAddress())
                .setSourcePort(input.getSourcePort())
                .setDestinationPort(input.getDestinationPort())
                .setAction(input.getAction())
                .build();
        transaction.put(LogicalDatastoreType.CONFIGURATION, iid, rule);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed to write greeting to greeting registry", LOG));
        LOG.info("\n*****************");
        LOG.info("Added DYNAMIC Firewall Rule");
        LOG.info("*****************");
        LOG.info("*****************");
        LOG.info("input rule_id {}", input.getRuleId());
        LOG.info("input node {}", input.getNode());
        LOG.info("input src ip {} ", input.getSourceIpAddress());
        LOG.info("input dst ip {} ", input.getDestinationIpAddress());
        LOG.info("input src port {}", input.getSourcePort());
        LOG.info("input dst port {}", input.getDestinationPort());
        LOG.info("input action {}", input.getAction());
        LOG.info("*****************\n");
    }

    private InstanceIdentifier<FwruleRegistryEntry> toInstanceIdentifier(AddDynamicFwruleInput input) {
        InstanceIdentifier<FwruleRegistryEntry> iid = InstanceIdentifier.create(FwruleRegistry.class)
            .child(FwruleRegistryEntry.class, new FwruleRegistryEntryKey(input.getRuleId()));
        return iid;
    }
}
