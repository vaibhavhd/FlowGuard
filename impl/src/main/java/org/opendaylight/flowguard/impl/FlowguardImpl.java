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
import java.util.concurrent.Future;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.AddRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FlowguardService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.RuleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.RuleRegistryBuilder;
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

    public FlowguardImpl(DataBroker db) {
        this.db = db;
        this.initializeDataTree(db);
    }

    @Override
      public Future<RpcResult<AddRuleOutput>> addRule(AddRuleInput input) {

        //TODO
    	LOG.info("Preparing to add the FIREWALL rule");
        AddRuleOutput output =
            new AddRuleOutputBuilder().setGreeting("Added firewall rule").build();
        writeToGreetingRegistry(input, output);
        return RpcResultBuilder.success(output).buildFuture();
    }


    private void initializeDataTree(DataBroker db) {
        LOG.info("Preparing to initialize the rules registry");
        WriteTransaction transaction = db.newWriteOnlyTransaction();
        InstanceIdentifier<RuleRegistry> iid = InstanceIdentifier.create(RuleRegistry.class);
        RuleRegistry ruleRegistry = new RuleRegistryBuilder().build();
        transaction.put(LogicalDatastoreType.OPERATIONAL, iid, ruleRegistry);
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
        transaction.put(LogicalDatastoreType.OPERATIONAL, iid, rule);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed to write greeting to greeting registry", LOG));
    }

    private InstanceIdentifier<RuleRegistryEntry> toInstanceIdentifier(AddRuleInput input) {
        InstanceIdentifier<RuleRegistryEntry> iid = InstanceIdentifier.create(RuleRegistry.class)
            .child(RuleRegistryEntry.class, new RuleRegistryEntryKey(input.getName()));
        return iid;
    }

}
