/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.flowguard.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.Fwrule;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.FwruleRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170505.fwrule.registry.FwruleRegistryEntryKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;

public class FirewallRuleParser {

    private static final Logger LOG = LoggerFactory.getLogger(FirewallRuleParser.class);
    private DataBroker db;
    private String filePath;

    public FirewallRuleParser(DataBroker db, String filePath){
        this.db = db;
        this.filePath = filePath;
    }

    public void start(){
        ruleParser();
    }

    /**
     * This function is used to read in a file and parse it line by line
     * Once a line is retrieved the function will call parseLine()
     */
    public void ruleParser(){
    	LOG.info("Entering rule parser");
    	ArrayList<String> lines = new ArrayList<String>();
    	
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = null;

            reader.readLine(); // skip the first line because the first line is the Title ROW such rule id dpid src ip ...
            while ((line = reader.readLine()) != null) {
            	LOG.info("Adding lines into arraylist");
            	lines.add(line);       //adding the rules in a list
                //parseLine(line);
            }
        } catch (IOException e) {
            LOG.info("Error in rule Parser");
        }
        
        LOG.info("Before parse line in rule parser");
        for(int i = lines.size() - 1;i >= 0;i--) {
        	parseLine(lines.get(i));
        }
    }

    /**
     * This function is used to parse a firewall rule
     * @param line
     */
    private void parseLine(String line) {

        Rule rule = new Rule();
        //LOG.info("\nInside parse Line {}", line);
        String[] elements = line.split("\t");

        //for(int i = 0; i < elements.length;i++)
        //    LOG.info("elements at {} = {}",i,elements[i]);

        
        rule.ruleid = Integer.parseInt(elements[0]);
        rule.dpid = elements[1];
        rule.in_port = elements[2];
        rule.nw_src_prefix = elements[3]; //may need to validate ip address???
        rule.nw_dst_prefix = elements[4]; //may need to validate ip address???
        rule.tp_src = elements[5];
        rule.tp_dst = elements[6];
        
        if(elements[7].equals("DENY")) {
            rule.action = Fwrule.Action.Block;
        }
        else if(elements[7].equals("ALLOW")) {
            rule.action = Fwrule.Action.Allow;
        }
        else {
            LOG.info("Error in valid action for rule {}",rule.ruleid);
        }
        addStaticRule(rule);
    }





    /**
     * Add the rule in the YANG data store
     * @param firewallRule
     */
    private void addStaticRule(Rule firewallRule) {
        FwruleRegistryEntry entry = new FwruleRegistryEntryBuilder()
                 .setRuleId(firewallRule.ruleid)
                 .setNode(firewallRule.dpid)
                 .setInPort(firewallRule.in_port)
                 .setSourceIpAddress(firewallRule.nw_src_prefix)
                 .setDestinationIpAddress(firewallRule.nw_dst_prefix)
                 .setSourcePort(firewallRule.tp_src)
                 .setDestinationPort(firewallRule.tp_dst)
                 .setAction(firewallRule.action)
                 .build();

        WriteTransaction transaction = db.newWriteOnlyTransaction();
        InstanceIdentifier<FwruleRegistryEntry> iid = InstanceIdentifier.create(FwruleRegistry.class)
                 .child(FwruleRegistryEntry.class, new FwruleRegistryEntryKey(firewallRule.ruleid));
        transaction.merge(LogicalDatastoreType.CONFIGURATION, iid, entry);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        Futures.addCallback(future, new LoggingFuturesCallBack<Void>("Failed add firewall rule", LOG));
        LOG.info("*****************");
        LOG.info("Added STATIC Rule");
        LOG.info("*****************");
        LOG.info("*****************");
        LOG.info("input ruleid {}", firewallRule.ruleid);
        LOG.info("input node {}", firewallRule.dpid);
        LOG.info("input inport {}", firewallRule.in_port);
        LOG.info("input src ip {} ", firewallRule.nw_src_prefix);
        LOG.info("input dst ip {} ", firewallRule.nw_dst_prefix);
        LOG.info("input src port {}", firewallRule.tp_src);
        LOG.info("input dst port {}", firewallRule.tp_dst);
        LOG.info("input action {}", firewallRule.action);
        LOG.info("*****************");
    }

    public class Rule {
        public int ruleid;
        public String dpid;
        public String in_port;
        public String nw_src_prefix;
        public String nw_dst_prefix;
        public String tp_src;
        public String tp_dst;
        public Fwrule.Action action;



        public Rule()
        {
            this.ruleid= 0;
            this.dpid = "";
            this.in_port = "";
            this.nw_src_prefix = "";
            this.nw_dst_prefix = "";
            this.tp_src = "";
            this.tp_dst = "";
            this.action = null;
        }
    }

}