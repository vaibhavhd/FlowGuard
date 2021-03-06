/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.flowguard.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opendaylight.controller.liblldp.EtherTypes;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.DottedQuad;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetFieldCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetVlanIdActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ClearActionsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.GoToTableCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.WriteActionsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.meter._case.Meter;

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;

import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.Layer3Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.Layer4Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4MatchArbitraryBitMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.TcpMatch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.UdpMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InetAddresses;
import com.google.common.primitives.UnsignedBytes;

public class FlowRuleNode {
    // TODO Rename fields to that of openflow 1.3
    private static final Logger LOG = LoggerFactory.getLogger(Flowguard.class);
    String switch_name;
    String rule_name;
    public int flowId;
    public int vlan;
    public short length = 0;
    public int in_port;
    public String dl_src;
    public String dl_dst;
    public int dl_type = 0;
    public int nw_src_prefix = 0;
    public int nw_src_maskbits = 0;
    public int nw_dst_prefix =0;
    public int nw_dst_maskbits = 0;
    public short nw_proto = 0;
    public int tp_src = 0;
    public int tp_dst = 0;
    public int udp_src = 0;
    public int udp_dst = 0;
    public int priority = 0;
    public int wildcards = 0;

    public boolean active = true;
    public List<HeaderObject> diff;

    public FlowInfo flow_info;
    public String eth_src;
    public String eth_dst;
    public List<Instruction> instructionList;

    public int corCount, genCount, redCount, shCount, overCount;
    public int policyCount;
    public StringBuilder conflictList = new StringBuilder();
    public boolean resolution;
    public String mechanism = new String();
    private static final int CORELATION =1;
    private static final int GENERALIZATION = 2;
    private static final int REDUNDANCY = 3;
    private static final int SHADOWING = 4;
    private static final int OVERLAP = 5;

    private static final String PREFIX_SEPARATOR = "/";
    private static final int IPV4_ADDRESS_LENGTH = 32;
    private static final String DEFAULT_ARBITRARY_BIT_MASK = "255.255.255.255";

    boolean actionDrop=false;
    /* An ActionList object is populated with all the actions
     * until output action. When an output action is received,
     * a copy of the object is sent to the respective port.
     */
    public class ActionList {
        private int action_out_port;
        private String action_dl_src;
        private String action_dl_dst;
        private int action_nw_src_prefix;
        private int action_nw_src_maskbits = 32;
        private int action_nw_dst_prefix;
        private int action_nw_dst_maskbits = 32;
        private int action_vlan;
        private int action_tcp_src;
        private int action_tcp_dst;
        private int action_udp_src;
        private int action_udp_dst;
        private int order;
        public ActionList() {

        }
        public ActionList(ActionList base) {
            action_dl_dst = base.action_dl_dst;
            action_dl_src = base.action_dl_src;
            action_nw_dst_maskbits = base.action_nw_dst_maskbits;
            action_nw_dst_prefix = base.action_nw_dst_prefix;
            action_nw_src_maskbits = base.action_nw_src_maskbits;
            action_nw_src_prefix = base.action_nw_src_prefix;
            //action_out_port = base.action_out_port;
            action_vlan = base.action_vlan;
            action_tcp_src = base.action_tcp_src;
            action_tcp_dst = base.action_tcp_dst;
            action_udp_src = base.action_udp_src;
            action_udp_dst = base.action_udp_dst;
            order = base.order;
        }
    }

    List<ActionList> actionList;
    public static List<FlowRuleNode> addruletable(List<Flow> flowList){

        List<FlowRuleNode> ruletable = new ArrayList<FlowRuleNode>();
        Iterator<Flow> itr = flowList.listIterator();//keys.iterator();

        while(itr.hasNext()){
            Flow flow = itr.next();//row.get(key.toString());
            FlowRuleNode instance = new FlowRuleNode();
            instance  = buildFlowInstance(instance, flow);
            //instance.switch_name = ;
                        /* Store the rules in a ruletable list in descending order */
            int i = 0;
            for(i = 0; i < ruletable.size(); i++){
                if (ruletable.get(i) != null && ruletable.get(i).priority <= instance.priority) {
                    break;
                }
            }
            if(i <= ruletable.size()){
                ruletable.add(i, instance);
            } else {
                ruletable.add(instance);
            }
        }
        ruletable = FlowRuleNode.computedependency(ruletable);
        return ruletable;
    }

    public static List<FlowRuleNode> addFlowRuleNode(List<FlowRuleNode> ruletable, Flow flow){

        FlowRuleNode instance = new FlowRuleNode();

        instance  = buildFlowInstance(instance, flow);
        int i = 0;
        int ruletable_size = 0;
        if(ruletable != null) {
            ruletable_size = ruletable.size();
        }
        for(i = 0; i < ruletable_size; i++){
            if (ruletable.get(i) != null && ruletable.get(i).priority <= instance.priority) {
                break;
            }
        }
        if(i <= ruletable_size){
            if(ruletable == null) {
                ruletable = new ArrayList<FlowRuleNode>();
            }
            ruletable.add(i, instance);
        } else {
            ruletable.add(instance);
        }
        ruletable = FlowRuleNode.computedependency(ruletable);
        return ruletable;
    }

    private static FlowRuleNode buildFlowInstance(FlowRuleNode instance, Flow flow) {
        instance.rule_name = flow.getId().getValue();//key.toString();
        /*
         * Spec:
         * A  zero-length  OpenFlow  match  (one  with  no  OXM  TLVs)  matches  every  packet.
         * Match  fields  that should be wildcarded are omitted from the OpenFlow match.         *
         */
        if (flow.getMatch().getInPort() != null) {
            instance.in_port = TopologyStruct.getPortfromURI((flow.getMatch().getInPort().getValue()));//.substring(beginIndex); // It should ne inport or physical input port??
        }
        else {
        	instance.in_port = 0;
        }
        if (flow.getMatch().getVlanMatch() != null) {
            instance.vlan = flow.getMatch().getVlanMatch().getVlanId().getVlanId().getValue();
        }

        // Extract L2 details
        if (flow.getMatch().getEthernetMatch() != null) {
            if(flow.getMatch().getEthernetMatch().getEthernetSource() != null) {
                instance.dl_src =   flow.getMatch().getEthernetMatch().getEthernetSource().getAddress().getValue();
            }
            if(flow.getMatch().getEthernetMatch().getEthernetDestination() != null) {
                instance.dl_dst =   flow.getMatch().getEthernetMatch().getEthernetDestination().getAddress().getValue();
            }
            if(flow.getMatch().getEthernetMatch().getEthernetType() != null) {
                instance.dl_type = flow.getMatch().getEthernetMatch().getEthernetType().getType().getValue().intValue();
            }
        }

        // Extract L3 details
        Layer3Match l3Match = flow.getMatch().getLayer3Match();
        // TODO Handle other L3Match cases: ARP, Ipv6
        if (l3Match != null) {
            if (l3Match instanceof Ipv4Match){
                Ipv4Prefix src = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Source();
                Ipv4Prefix dst = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Destination();
                if(src != null) {
                    instance.nw_src_prefix = calculateIpfromPrefix(src);
                    instance.nw_src_maskbits = calculateMaskfromPrefix(src);
                }
                if(dst != null) {
                    instance.nw_dst_prefix = calculateIpfromPrefix(dst);
                    instance.nw_dst_maskbits = calculateMaskfromPrefix(dst);
                }
            }
            else if (l3Match instanceof Ipv4MatchArbitraryBitMask){
                Ipv4Address addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceAddressNoMask();
                DottedQuad mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceArbitraryBitmask();
                Ipv4Prefix src = createPrefix(addr, convertArbitraryMaskToByteArray(mask));
                if(src != null) {
                    instance.nw_src_prefix = calculateIpfromPrefix(src);
                    instance.nw_src_maskbits = calculateMaskfromPrefix(src);
                }
                addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationAddressNoMask();
                mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationArbitraryBitmask();
                Ipv4Prefix dst = createPrefix(addr, convertArbitraryMaskToByteArray(mask));
                if(dst != null) {
                    instance.nw_dst_prefix = calculateIpfromPrefix(dst);
                    instance.nw_dst_maskbits = calculateMaskfromPrefix(dst);
                }
            }
        }

        // Extract L4 details
        // TODO Previois implementation worng? src is taken from dest!!
        Layer4Match l4Match = flow.getMatch().getLayer4Match();
        if(l4Match != null) {
            if(l4Match instanceof TcpMatch) {
            	//Check wildcard source TCP
            	if(((TcpMatch)l4Match).getTcpSourcePort() != null) { //wild cards checks
            		instance.tp_src = ((TcpMatch)l4Match).getTcpSourcePort().getValue();
            	}
            	else {
            		instance.tp_src = 0;
            	}
            	//Check wildcard destination TCP
                if(((TcpMatch)l4Match).getTcpDestinationPort() != null) { //wild cards checks
                	instance.tp_dst = ((TcpMatch)l4Match).getTcpDestinationPort().getValue();
                }
                else {
                	instance.tp_dst = 0;
                }
            }
            else if (l4Match instanceof UdpMatch) {
            	//Check wildcard source UDP
            	if(((UdpMatch)l4Match).getUdpSourcePort() != null) { //wild cards checks
            		instance.udp_src = ((UdpMatch)l4Match).getUdpSourcePort().getValue();
            	}
            	else {
            		instance.udp_src = 0;
            	}
            	//Check wildcard destination UDP
            	if(((UdpMatch)l4Match).getUdpDestinationPort() != null) { //wild cards checks
            		instance.udp_dst = ((UdpMatch)l4Match).getUdpDestinationPort().getValue();
            	}
            	else {
            		instance.udp_src = 0;
            	}
            }
        }
        if (flow.getMatch().getIpMatch() != null) {
            instance.nw_proto = flow.getMatch().getIpMatch().getIpProtocol();
        }

        instance.priority = flow.getPriority();
        int sendToControllerActionIndex = 0;
        if (flow.getInstructions() != null) {

            if(instance.actionList == null) {
                instance.actionList = new ArrayList<ActionList>();
            }
            List<Instruction> instList = flow.getInstructions().getInstruction();
            //TODO Many more instructions and actions to be checked and implemeted :Openflow 1.3
            for (Instruction i : instList) {
                org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.Instruction instruction = i.getInstruction();
                LOG.info("Instruction present in flow");
                if (instruction instanceof Meter){
                    // Optional
                }
                else if (instruction instanceof ApplyActionsCase) {
                    // Optional
                    LOG.info("Case: ApplyAction");
                    List<Action> action = (((ApplyActionsCase) instruction).getApplyActions()).getAction();
                    ActionList node = instance.new ActionList();
                    for (Action a : action) {
                        if (a.getAction() instanceof OutputActionCase) {
                            LOG.info("Setting the Output Action");
                            String actionPort =  ((OutputActionCase)(a.getAction())).getOutputAction()
                                    .getOutputNodeConnector().getValue();
                            if(actionPort.equals("CONTROLLER")) {
                            	node.action_out_port = 0;
                            	sendToControllerActionIndex = instance.actionList.size();
                            }
                            else if(actionPort.equals("LOCAL")) {
                                //TODO
                            }
                            else {
                                try {
                                    node.action_out_port = Integer.valueOf(actionPort);
                                } catch (NumberFormatException exp) {
                                    LOG.info("Port is not an integer value!! {} ", exp.getMessage());
                                }
                            }
                            instance.actionList.add(node);
                            /* The set-field actions untill next output action have been populated in the actionList Object
                             * This also takes care of the case where multiple actions are chained which modify same or
                             * different header fields.
                             */
                            node = instance.new ActionList(node);
                        }
                        else if (a.getAction() instanceof SetFieldCase){
                            LOG.info("Set Field OutputAction");

                            //Check to see if set field ether source != null
                            if(((SetFieldCase)(a.getAction())).getSetField().getEthernetMatch() != null){
                            	if(((SetFieldCase)(a.getAction())).getSetField().getEthernetMatch().getEthernetSource() != null) {
                            		node.action_dl_src = ((SetFieldCase)(a.getAction())).getSetField()
                                			.getEthernetMatch().getEthernetSource().getAddress().getValue();
                            	}
                            	if(((SetFieldCase)(a.getAction())).getSetField().getEthernetMatch().getEthernetDestination() != null) {
                            		node.action_dl_dst = ((SetFieldCase)(a.getAction())).getSetField()
                                     		.getEthernetMatch().getEthernetDestination().getAddress().getValue();
                            	}
                            }

                            //Check to see if set field layer3 != null
                            if(((SetFieldCase)a.getAction()).getSetField().getLayer3Match() != null) {
                            	l3Match = ((SetFieldCase)a.getAction()).getSetField().getLayer3Match();
                            }

                            // TODO Handle other L3Match cases: ARP, Ipv6
                            if (l3Match instanceof Ipv4Match){
                                Ipv4Prefix src  = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Source();
                                Ipv4Prefix dst = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Destination();
                                node.action_nw_src_prefix = calculateIpfromPrefix(src);
                                node.action_nw_src_maskbits = calculateMaskfromPrefix(src);
                                node.action_nw_dst_prefix = calculateIpfromPrefix(dst);
                                node.action_nw_dst_maskbits = calculateMaskfromPrefix(dst);
                            }
                            else if (l3Match instanceof Ipv4MatchArbitraryBitMask){
                                Ipv4Address addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceAddressNoMask();
                                DottedQuad mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceArbitraryBitmask();
                                Ipv4Prefix src = createPrefix(addr, convertArbitraryMaskToByteArray(mask));

                                addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationAddressNoMask();
                                mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationArbitraryBitmask();
                                Ipv4Prefix dst = createPrefix(addr, convertArbitraryMaskToByteArray(mask));
                                node.action_nw_src_prefix = calculateIpfromPrefix(src);
                                node.action_nw_src_maskbits = calculateMaskfromPrefix(src);
                                node.action_nw_dst_prefix = calculateIpfromPrefix(dst);
                                node.action_nw_dst_maskbits = calculateMaskfromPrefix(dst);
                            }
                            else if (l4Match instanceof TcpMatch ) {
                            	int src_port = 0;
                            	int dst_port = 0;
                            	if(((TcpMatch)flow.getMatch().getLayer4Match()).getTcpSourcePort() != null) {
                            		src_port = ((TcpMatch)flow.getMatch().getLayer4Match()).getTcpSourcePort().getValue();
                            	}

                            	if(((TcpMatch)flow.getMatch().getLayer4Match()).getTcpDestinationPort() != null) {
                            		dst_port = ((TcpMatch)flow.getMatch().getLayer4Match()).getTcpDestinationPort().getValue();
                            	}
                            	node.action_tcp_src = src_port;
                            	node.action_tcp_dst = dst_port;
                            }
                            else if(l4Match instanceof UdpMatch) {
                            	//Match UDP action here
                            }
                            //instance.actionList.add(node);
                        }
                        else if(a.getAction() instanceof SetVlanIdActionCase){
                            // TODO Check what is assigned
                            //node.action_vlan = ((SetField)(a.getAction())).getVlanMatch().getVlanId().getVlanId().getValue();
                        	node.action_vlan = ((SetVlanIdActionCase)(a.getAction())).getSetVlanIdAction().getVlanId().getValue();
                            //instance.actionList.add(node);
                        }
                    }
                }
                else if (instruction instanceof ClearActionsCase) {
                    // Optional
                    LOG.info("Case: ClearAction");
                }
                else if (instruction instanceof WriteActionsCase) {
                    // Optional
                    LOG.info("Case: ApplyAction");
                    List<Action> action = (((WriteActionsCase) instruction).getWriteActions()).getAction();
                    ActionList node = instance.new ActionList();
                    for (Action a : action) {
                        if (a.getAction() instanceof OutputActionCase) {
                            LOG.info("Setting the Output Action");
                            node.action_out_port = Integer.valueOf(((OutputActionCase)(a.getAction())).getOutputAction()
                                    .getOutputNodeConnector().getValue());
                            instance.actionList.add(node);
                            /* The set-field actions untill next output action have been populated in the actionList Object
                             * This also takes care of the case where multiple actions are chained which modify same or
                             * different header fields.
                             */
                            node = instance.new ActionList(node);
                        }
                        else if (a.getAction() instanceof SetFieldCase){
                            LOG.info("Set Field OutputAction");

                            //Check to see if set field ether source != null
                            if(((SetFieldCase)(a.getAction())).getSetField().getEthernetMatch() != null){
                                if(((SetFieldCase)(a.getAction())).getSetField().getEthernetMatch().getEthernetSource() != null) {
                                    node.action_dl_src = ((SetFieldCase)(a.getAction())).getSetField()
                                            .getEthernetMatch().getEthernetSource().getAddress().getValue();
                                }
                                if(((SetFieldCase)(a.getAction())).getSetField().getEthernetMatch().getEthernetDestination() != null) {
                                    node.action_dl_dst = ((SetFieldCase)(a.getAction())).getSetField()
                                            .getEthernetMatch().getEthernetDestination().getAddress().getValue();
                                }
                            }

                            //Check to see if set field layer3 != null
                            if(((SetFieldCase)a.getAction()).getSetField().getLayer3Match() != null) {
                                l3Match = ((SetFieldCase)a.getAction()).getSetField().getLayer3Match();
                            }

                            // TODO Handle other L3Match cases: ARP, Ipv6
                            if (l3Match instanceof Ipv4Match){
                                Ipv4Prefix src  = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Source();
                                Ipv4Prefix dst = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Destination();
                                node.action_nw_src_prefix = calculateIpfromPrefix(src);
                                node.action_nw_src_maskbits = calculateMaskfromPrefix(src);
                                node.action_nw_dst_prefix = calculateIpfromPrefix(dst);
                                node.action_nw_dst_maskbits = calculateMaskfromPrefix(dst);
                            }
                            else if (l3Match instanceof Ipv4MatchArbitraryBitMask){
                                Ipv4Address addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceAddressNoMask();
                                DottedQuad mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceArbitraryBitmask();
                                Ipv4Prefix src = createPrefix(addr, convertArbitraryMaskToByteArray(mask));

                                addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationAddressNoMask();
                                mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationArbitraryBitmask();
                                Ipv4Prefix dst = createPrefix(addr, convertArbitraryMaskToByteArray(mask));
                                node.action_nw_src_prefix = calculateIpfromPrefix(src);
                                node.action_nw_src_maskbits = calculateMaskfromPrefix(src);
                                node.action_nw_dst_prefix = calculateIpfromPrefix(dst);
                                node.action_nw_dst_maskbits = calculateMaskfromPrefix(dst);
                            }
                            else if (l4Match instanceof TcpMatch){
                            	int src_port = 0;
                            	int dst_port = 0;
                            	if(((TcpMatch)flow.getMatch().getLayer4Match()).getTcpSourcePort() != null) {
                            		src_port = ((TcpMatch)flow.getMatch().getLayer4Match()).getTcpSourcePort().getValue();
                            	}

                            	if(((TcpMatch)flow.getMatch().getLayer4Match()).getTcpDestinationPort() != null) {
                            		dst_port = ((TcpMatch)flow.getMatch().getLayer4Match()).getTcpDestinationPort().getValue();
                            	}
                            	node.action_tcp_src = src_port;
                            	node.action_tcp_dst = dst_port;
                            }
                            //instance.actionList.add(node);
                        }
                        else if(a.getAction() instanceof SetVlanIdActionCase){
                            // TODO Check what is assigned
                            //node.action_vlan = ((SetField)(a.getAction())).getVlanMatch().getVlanId().getVlanId().getValue();
                            node.action_vlan = ((SetVlanIdActionCase)(a.getAction())).getSetVlanIdAction().getVlanId().getValue();
                            //instance.actionList.add(node);
                        }
                    }
                }
                else if(instruction instanceof GoToTableCase) {
                    // Required
                    LOG.info("Case: GoToTableAction");
                }
                else {
                    // Error when a flow contains a packet with invalid instruction.
                }
            }
        }

        if (instance.actionList == null) {
            /* Spec 1.3.5:
             * Packets whose action sets have no output action and no group action should be dropped.
             * This result could come from empty instruction sets or empty action buckets in the
             * processing pipeline (see 5.10), or after executing a Clear-Actions instruction.
             */
            instance.actionDrop = true;
        }
        else if (sendToControllerActionIndex != 0) {
            /* The sequence of actions are not retrieved in the order which is seen from the rule.
             * The output action of sending to controller should always be the last output action.
             */
            ActionList lastNode = instance.actionList.get(instance.actionList.size()-1);
            ActionList newLastNode = instance.new ActionList(lastNode);
            newLastNode.action_out_port = 0;
            instance.actionList.remove(sendToControllerActionIndex);
            instance.actionList.add(newLastNode);
        }
        return instance;
    }

    public static List<FlowRuleNode> deleteFlowRuleNode(List<FlowRuleNode> ruletable, String rulename){

        int i = 0;
        int ruletable_size = 0;
        if(ruletable != null) {
            ruletable_size = ruletable.size();
        }
        for(i = 0; i < ruletable_size; i++){
            if (rulename.equals(ruletable.get(i).rule_name)){
                ruletable.remove(i);
                break;
            }
        }
        ruletable = FlowRuleNode.computedependency(ruletable);

        return ruletable;
    }

    /* TODO
     * For deciding the conflicts among the rules for visualization, actions are compared as:
     * Action is DROP or not. All the actions which are not DROP actions should ideally be
     * then checked for equality in terms of specific actions.
     */
    public static List<FlowRuleNode> computedependency(List<FlowRuleNode> ruletable){
        //compute intra table dependency
        //compare i th rule and j th rule such that i th rule is prior to j th rule.
        for(int i = 0; i < ruletable.size(); i++){
            ruletable.get(i).active = true;
            ruletable.get(i).flowId = i;
            if(ruletable.get(i).diff != null) {
                ruletable.get(i).diff.clear();
            }
        }
        /*
         * Compare all the rules in the rule table with each other.
         * Dependency is decided if following matches between two rules
         * in_port, dl_type, vlan, source IP, destination IP.
         * In case of full overlap of the rules, mark one of the rules as inactive.
         * Check dependency:
         * First IF: Same inport && Both IPv4 protocol && Both active && Same vlan ID
         * Second IF: The effective address ranges of IP should overlap for dest and src
         */
        for(int i = 0; i < ruletable.size() - 1; i++){
            for(int j = i + 1; j < ruletable.size(); j++){
                if((isWildcarded(ruletable.get(i).in_port) || (ruletable.get(i).in_port == 0)
                        || (ruletable.get(i).in_port == ruletable.get(j).in_port))
                        && ruletable.get(i).dl_type == (EtherTypes.IPv4.intValue())
                        && ruletable.get(j).dl_type == (EtherTypes.IPv4.intValue())
                        && ruletable.get(i).active == true //TODO The rule will always be active as above!
                        && ruletable.get(i).vlan == ruletable.get(j).vlan) {
                    //check if ip range is overlapped // TODO Wrong dependency when ip2 =0;
                    if(matchIPAddress(ruletable.get(i).nw_dst_prefix, ruletable.get(i).nw_dst_maskbits,
                            ruletable.get(j).nw_dst_prefix, ruletable.get(j).nw_dst_maskbits)) {
                        if(matchIPAddress(ruletable.get(i).nw_src_prefix, ruletable.get(i).nw_src_maskbits,
                                ruletable.get(j).nw_src_prefix, ruletable.get(j).nw_src_maskbits)){
                            if (ruletable.get(i).nw_proto == ruletable.get(j).nw_proto) {

                                if(ruletable.get(j).diff == null){
                                    ruletable.get(j).diff = new ArrayList<HeaderObject>();
                                }
                                /* If two rules have same IP range and higher priority rule has more(>=)0 IP range
	                            than the lower priority rule, mark the lower priority rule as disabled. */
                                if(ruletable.get(i).nw_dst_maskbits <= ruletable.get(j).nw_dst_maskbits &&
                                        ruletable.get(i).nw_src_maskbits <= ruletable.get(j).nw_src_maskbits){
                                    //handle for full overlap case
                                    ruletable.get(j).active = false;
                                    if(ruletable.get(i).actionDrop != ruletable.get(j).actionDrop) {
                                        ruletable.get(j).shCount++;
                                        ruletable.get(j).conflictList.append(SHADOWING);
                                        ruletable.get(j).conflictList.append("." + i + ";");
                                    }
                                    else {
                                        ruletable.get(j).redCount++;
                                        ruletable.get(j).conflictList.append(REDUNDANCY);
                                        ruletable.get(j).conflictList.append("." + i + ";");
                                    }
                                }
                                else{
                                    //handle for partial overlap case
                                    if(ruletable.get(i).actionDrop != ruletable.get(j).actionDrop) {
                                        ruletable.get(i).genCount++;
                                        ruletable.get(i).conflictList.append(GENERALIZATION);
                                        ruletable.get(i).conflictList.append("." + j + ";");
                                    } else {
                                        ruletable.get(i).redCount++;
                                        ruletable.get(i).conflictList.append(REDUNDANCY);
                                        ruletable.get(i).conflictList.append("." + j + ";");
                                    }

                                    HeaderObject ho = new HeaderObject();

                                    // Take the larger maskbits of the two rules for both dest and source.
                                    ho.nw_dst_prefix = ruletable.get(i).nw_dst_prefix;
                                    ho.nw_src_prefix = ruletable.get(i).nw_src_prefix;
                                    ho.nw_dst_maskbits = ruletable.get(i).nw_dst_maskbits;
                                    ho.nw_src_maskbits = ruletable.get(i).nw_src_maskbits;
                                    ho.tcp_src = ruletable.get(i).tp_src;
                                    ho.tcp_dst = ruletable.get(i).tp_dst;

                                    boolean donothing = false;
                                    /*
                                     * First part of "diff" calculation:
                                     * Diff: A list of header objects(of higher priority rules). A lesser priority rule
                                     * contains in its diff, an non overlappinf list of header objects of the higher prio rules.
                                     * For the top rule(i=1), the diff list will not be present. The Jth rule in this else part
                                     * is partially overlapping the ith rule. Thus, second rule will always have first rule in
                                     * its diff list.
                                     * While going through the existng diff list of lower priority rules, check if the list always
                                     * contains the header object of a rule whose IP range is higher.
                                     * TODO:  Since the list is already sorted, shouldn't the diff(higher prior) object be
                                     * never replaced with "ho", a lower prior object.
                                     */
                                    for(int k = 0; k < ruletable.get(j).diff.size(); k++){
                                        if(matchIPAddress(ho.nw_dst_prefix, ho.nw_dst_maskbits,
                                                ruletable.get(j).diff.get(k).nw_dst_prefix, ruletable.get(j).diff.get(k).nw_dst_maskbits)) {
                                            if(matchIPAddress(ho.nw_src_prefix, ho.nw_src_maskbits,
                                                    ruletable.get(j).diff.get(k).nw_src_prefix, ruletable.get(j).diff.get(k).nw_src_maskbits)) {
                                                if(ho.nw_dst_maskbits <= ruletable.get(j).diff.get(k).nw_dst_maskbits &&
                                                ho.nw_src_maskbits <= ruletable.get(j).diff.get(k).nw_src_maskbits){
                                                    //handle for full overlap case 1 : ho range contains diff
                                                    ruletable.get(j).diff.remove(k);
                                                }else if(ho.nw_dst_maskbits >= ruletable.get(j).diff.get(k).nw_dst_maskbits &&
                                                        ho.nw_src_maskbits >= ruletable.get(j).diff.get(k).nw_src_maskbits){
                                                    //handle for full overlap case 1 : diff range contains ho
                                                    //do nothing
                                                    donothing = true;
                                                    break;
                                                }else{
                                                    //handle for partial overlap case
                                                }
                                            }
                                        }
                                    }
                                    if(donothing){
                                        //do nothing
                                    }
                                    else {
                                        ruletable.get(j).diff.add(ho);
                                    }
                                }
                            } else if (ruletable.get(i).nw_proto == 0 || ruletable.get(j).nw_proto == 0) {
                                // Reaching here would mean that address space overlaps between i and j but wildcard protocols
                                // turns the full overlap into an intersection.
                                if(ruletable.get(i).actionDrop != ruletable.get(j).actionDrop) {
                                    ruletable.get(i).corCount++;
                                    ruletable.get(i).conflictList.append(CORELATION);
                                    ruletable.get(i).conflictList.append("." + j + ";");
                                    ruletable.get(j).corCount++;
                                    ruletable.get(j).conflictList.append(CORELATION);
                                    ruletable.get(j).conflictList.append("." + i + ";");
                                } else {
                                    ruletable.get(i).overCount++;
                                    ruletable.get(i).conflictList.append(OVERLAP);
                                    ruletable.get(i).conflictList.append("." + j + ";");
                                    ruletable.get(j).overCount++;
                                    ruletable.get(j).conflictList.append(OVERLAP);
                                    ruletable.get(j).conflictList.append("." + i + ";");
                                }
                            }
                        }
                    }

                }

            }
        }
        return ruletable;
    }

    /*
     * Calculation of flow path space. Space is defined by a set of source(IP+Port+Proto) and dest(IP+Port)
     * The incoming space has been set before propagation. The outgoing space may "shift" as the setfield
     * operations take place. The processing flow is a technique of flowing(moving a packet) along the
     * plumbing graph and updating the address space accordingly.
     *
     * @param flowRuleNode Existing IPv4 Flow with matching DPID and InPort as the new sample
     * @param inputFlow The flow which is being added/modified/deleted
     * @return The ruleNode(with the new flowinfo(with flow_history)) responsible for the propagation.
     */
    public static FlowRuleNode computeFlow(FlowRuleNode flowRuleNode, FlowInfo inputflow, ActionList actionNode){
        /*
         * Using a copy of a packet is necessary as:
         * Spec v1.3.5:
         * If the list of actions contains an output action, a "copy" of the packet is forwarded in its
         * current state to the desired port. If the output action references an non-existent port,
         * the copy of the packet is dropped.
         * IMP: The action set of the packet is unchanged by the execution of the list of actions.
         */
        FlowInfo processingflow = FlowInfo.valueCopy(inputflow);
        //processingflow inputflow to processing flow
        processingflow.rule_node_name = flowRuleNode.rule_name;
        processingflow.current_ingress_port = inputflow.next_ingress_port;
        processingflow.current_switch_dpid = inputflow.next_switch_dpid;

        processingflow.current_ho = inputflow.next_ho.get_ho();
        processingflow.flow_history = inputflow.flow_history;
        if(processingflow.next_ho == null) {
            processingflow.next_ho = new HeaderObject();
        }
        if(processingflow.flow_history != null && processingflow.flow_history.size() == 1){
            //initial sourceflow : set vlan as a first rule's vlan
            processingflow.current_ho.vlan = flowRuleNode.vlan;
        }


        // TODO if(processingflow.current_ingress_port==FlowRuleNode.in_port && FlowRuleNode.dl_type==2048 ){
        //initiate flowinfo in the corresponding FlowRuleNode
        //if(FlowRuleNode.dltype==2054) then it just forwards the packet;
        //10.0.0.1 = 167772161
        //L3 level match is being done here
        if(processingflow.current_ho.vlan == flowRuleNode.vlan &&
                matchIPAddress(flowRuleNode.nw_dst_prefix, flowRuleNode.nw_dst_maskbits,
                        processingflow.current_ho.nw_dst_prefix, processingflow.current_ho.nw_dst_maskbits)) {

        	//TODO failing IP checking
            if(matchIPAddress(flowRuleNode.nw_src_prefix, flowRuleNode.nw_src_maskbits,
                    processingflow.current_ho.nw_src_prefix, processingflow.current_ho.nw_src_maskbits)){
                System.out.println("Processing flow matched the IP of the Flow rule");

                if(matchL4(flowRuleNode.tp_src,processingflow.current_ho.tcp_src)
                		&& matchL4(flowRuleNode.tp_dst,processingflow.current_ho.tcp_dst)) { //Vu's TODO match UDP

                	if(actionNode == null) {
	                    //The flow rule has no output action; drop the packet
	                    System.out.println("Flow rule ACTION=DROP");
	                    processingflow.is_finished = true;
	                    inputflow.is_finished = true;
	                    //add flow information in the flow history
	                    if (processingflow.flow_history == null)
	                        processingflow.flow_history =new ArrayList<FlowInfo>();
	                    processingflow.flow_history.add(processingflow);
	                    flowRuleNode.flow_info = processingflow;
	                    return flowRuleNode;
	                }
	                if(actionNode.action_nw_dst_prefix==0){
	                    /*
	                     * When the instructions are not set, follow the natural course of action,
	                     * all matching flow will be forwarded. This means that forward the flow as
	                     * mentioned in the packet header. Unless, the higher priority flowRuleNode
	                     * has a narrower forwarding range.
	                     */
	                    if(flowRuleNode.nw_dst_maskbits <= processingflow.current_ho.nw_dst_maskbits){
	                        /* flowRuleNode has a wider range of addressing */
	                        processingflow.next_ho.nw_dst_prefix = processingflow.current_ho.nw_dst_prefix;
	                        processingflow.next_ho.nw_dst_maskbits = processingflow.current_ho.nw_dst_maskbits;
	                        processingflow.next_ho.vlan = processingflow.current_ho.vlan;

	                        processingflow.next_ho.tcp_dst = processingflow.current_ho.tcp_dst;
	                    }else{
	                        processingflow.next_ho.nw_dst_prefix = flowRuleNode.nw_dst_prefix;
	                        processingflow.next_ho.nw_dst_maskbits = flowRuleNode.nw_dst_maskbits;
	                        processingflow.next_ho.vlan = flowRuleNode.vlan;

	                        processingflow.next_ho.tcp_dst = flowRuleNode.tp_dst;
	                    }
	                }else if(actionNode.action_nw_dst_maskbits == 32){
	                    //rule matching condition and action set is N to 1
	                    processingflow.next_ho.nw_dst_prefix = actionNode.action_nw_dst_prefix;
	                    processingflow.next_ho.nw_dst_maskbits = actionNode.action_nw_src_maskbits;
	                    processingflow.next_ho.vlan = actionNode.action_vlan;
	                    //Need to add tcp port for actionNode???

	                }else{
	                    /*
	                     * Rules have matched and action in higher priority FlowRuleNode is set.
	                     * Action set is N to N case: destination is a network not a host.
	                     */
	                    if(flowRuleNode.nw_dst_maskbits <= processingflow.current_ho.nw_dst_maskbits){
	                        /* flowrulenode has a wider network range  Calculate the effective
	                         * IP address by setting the wildcarded part of IP to 0.
	                         */
	                        int rule_iprng = 32 - flowRuleNode.nw_dst_maskbits;
	                        int rule_ipint = actionNode.action_nw_dst_prefix >> rule_iprng;
	                        rule_ipint = rule_ipint << rule_iprng;
	                        int flow_iprng = 32 - processingflow.current_ho.nw_dst_maskbits;
	                        int flow_ipint = processingflow.current_ho.nw_dst_prefix >> flow_iprng;
	                        flow_ipint = flow_ipint << flow_iprng;
	                        // TODO Wrong calculation? flow=10.90.0.0/16 sample=10.90.1.2/24: result = 10.90.0.2/24!!
	                        processingflow.next_ho.nw_dst_prefix = rule_ipint + (processingflow.current_ho.nw_dst_prefix - flow_ipint);
	                        processingflow.next_ho.nw_dst_maskbits = processingflow.current_ho.nw_dst_maskbits;
	                        processingflow.next_ho.vlan = processingflow.current_ho.vlan;

	                        processingflow.next_ho.tcp_dst = processingflow.current_ho.tcp_dst;

	                    }else{
	                        /* flowrulenode has a narrower network range */
	                        processingflow.next_ho.nw_dst_prefix = actionNode.action_nw_dst_prefix;
	                        processingflow.next_ho.nw_dst_maskbits = actionNode.action_nw_src_maskbits;
	                        processingflow.next_ho.vlan = actionNode.action_vlan;
	                    }
	                }
	                // TODO same comments as above.
	                if(actionNode.action_nw_src_prefix==0){
	                    //all matching flow will be forwarded
	                    if(flowRuleNode.nw_src_maskbits <= processingflow.current_ho.nw_src_maskbits){
	                        processingflow.next_ho.nw_src_prefix = processingflow.current_ho.nw_src_prefix;
	                        processingflow.next_ho.nw_src_maskbits = processingflow.current_ho.nw_src_maskbits;
	                        processingflow.next_ho.vlan = processingflow.current_ho.vlan;

	                        processingflow.next_ho.tcp_dst = processingflow.current_ho.tcp_dst;
	                    }else{
	                        processingflow.next_ho.nw_src_prefix = flowRuleNode.nw_src_prefix;
	                        processingflow.next_ho.nw_src_maskbits = flowRuleNode.nw_src_maskbits;
	                        processingflow.next_ho.vlan = flowRuleNode.vlan;

	                        processingflow.next_ho.tcp_dst = flowRuleNode.tp_dst;
	                    }
	                }else if(actionNode.action_nw_src_maskbits == 32){
	                    //rule matching condition and action set is N to 1
	                    processingflow.next_ho.nw_src_prefix = actionNode.action_nw_src_prefix;
	                    processingflow.next_ho.nw_src_maskbits = actionNode.action_nw_src_maskbits;
	                    processingflow.next_ho.vlan = actionNode.action_vlan;
	                }else{
	                    //rule matching condition and action set is N to N case
	                    if(flowRuleNode.nw_src_maskbits <= processingflow.current_ho.nw_src_maskbits){
	                        int rule_iprng = 32 - flowRuleNode.nw_src_maskbits;
	                        int rule_ipint = actionNode.action_nw_src_prefix >> rule_iprng;
	                    rule_ipint = rule_ipint << rule_iprng;

	                    int flow_iprng = 32 - processingflow.current_ho.nw_src_maskbits;
	                    int flow_ipint = processingflow.current_ho.nw_src_prefix >> flow_iprng;
	                    flow_ipint = flow_ipint << flow_iprng;
	                    processingflow.next_ho.nw_src_prefix = rule_ipint + (processingflow.current_ho.nw_src_prefix - flow_ipint);
	                    processingflow.next_ho.nw_src_maskbits = processingflow.current_ho.nw_src_maskbits;
	                    processingflow.next_ho.vlan = processingflow.current_ho.vlan;
	                    }else{
	                        processingflow.next_ho.nw_src_prefix = actionNode.action_nw_src_prefix;
	                        processingflow.next_ho.nw_src_maskbits = actionNode.action_nw_src_maskbits;
	                        processingflow.next_ho.vlan = actionNode.action_vlan;
	                    }
	                }

	                //calculate diff part
	                if(actionNode.action_nw_dst_maskbits == 32 && actionNode.action_nw_src_maskbits == 32){
	                    //do nothing
	                }else{
	                    //1. calculate flowinfo itself : add current_ho's diff to the next_ho's diff
	                    if(processingflow.current_ho.diff != null){
	                        for(int i = 0; i < processingflow.current_ho.diff.size(); i++){
	                            HeaderObject ho = new HeaderObject();
	                            if(matchIPAddress(flowRuleNode.nw_dst_prefix, flowRuleNode.nw_dst_maskbits,
	                                    processingflow.current_ho.diff.get(i).nw_dst_prefix, processingflow.current_ho.diff.get(i).nw_dst_maskbits)){
	                                if(matchIPAddress(flowRuleNode.nw_src_prefix, flowRuleNode.nw_src_maskbits,
	                                        processingflow.current_ho.diff.get(i).nw_src_prefix, processingflow.current_ho.diff.get(i).nw_src_maskbits)){
	                                    if(flowRuleNode.nw_dst_maskbits >= processingflow.current_ho.diff.get(i).nw_dst_maskbits){
	                                        if(flowRuleNode.nw_src_maskbits >= processingflow.current_ho.diff.get(i).nw_src_maskbits){
	                                            //full overlap : FlowRuleNode ip_range is in current_ho.diff.get(i)
	                                            processingflow.is_finished = true;
	                                            inputflow.is_finished = true;
	                                            break;
	                                        }
	                                    }
	                                    //three cases : action_nw_dst_maskbits is 0(forward), 32(one point), or any value(ip range).
	                                    if(actionNode.action_nw_dst_prefix==0){
	                                        //all matching flow will be forwarded
	                                        ho.nw_dst_prefix = processingflow.current_ho.diff.get(i).nw_dst_prefix;
	                                        ho.nw_dst_maskbits = processingflow.current_ho.diff.get(i).nw_dst_maskbits;
	                                        ho.vlan = processingflow.current_ho.diff.get(i).vlan;
	                                    }else if(actionNode.action_nw_dst_maskbits == 32){
	                                        //rule matching condition and action set is N to 1
	                                        ho.nw_dst_prefix = actionNode.action_nw_dst_prefix;
	                                        ho.nw_dst_maskbits = actionNode.action_nw_src_maskbits;
	                                        ho.vlan = actionNode.action_vlan;
	                                    }else{
	                                        //rule matching condition and action set is N to N case
	                                        if(flowRuleNode.nw_dst_maskbits <= processingflow.current_ho.diff.get(i).nw_dst_maskbits){
	                                            int rule_iprng = 32 - flowRuleNode.nw_dst_maskbits;
	                                            int rule_ipint = actionNode.action_nw_dst_prefix >> rule_iprng;
	                                        rule_ipint = rule_ipint << rule_iprng;

	                                        int flow_iprng = 32 - processingflow.current_ho.diff.get(i).nw_dst_maskbits;
	                                        int flow_ipint = processingflow.current_ho.diff.get(i).nw_dst_prefix >> flow_iprng;
	                                        flow_ipint = flow_ipint << flow_iprng;
	                                        ho.nw_dst_prefix = rule_ipint + (processingflow.current_ho.diff.get(i).nw_dst_prefix - flow_ipint);
	                                        ho.nw_dst_maskbits = processingflow.current_ho.diff.get(i).nw_dst_maskbits;
	                                        ho.vlan = processingflow.current_ho.diff.get(i).vlan;
	                                        }
	                                    }
	                                    if(actionNode.action_nw_src_prefix==0){
	                                        //all matching flow will be forwarded
	                                        ho.nw_src_prefix = processingflow.current_ho.diff.get(i).nw_src_prefix;
	                                        ho.nw_src_maskbits = processingflow.current_ho.diff.get(i).nw_src_maskbits;
	                                        ho.vlan = processingflow.current_ho.diff.get(i).vlan;
	                                    }else if(actionNode.action_nw_src_maskbits == 32){
	                                        //rule matching condition and action set is N to 1
	                                        ho.nw_src_prefix = actionNode.action_nw_src_prefix;
	                                        ho.nw_src_maskbits = actionNode.action_nw_src_maskbits;
	                                        ho.vlan = actionNode.action_vlan;
	                                    }else{
	                                        //rule matching condition and action set is N to N case
	                                        if(flowRuleNode.nw_src_maskbits <= processingflow.current_ho.diff.get(i).nw_src_maskbits){
	                                            int rule_iprng = 32 - flowRuleNode.nw_src_maskbits;
	                                            int rule_ipint = actionNode.action_nw_src_prefix >> rule_iprng;
	                                        rule_ipint = rule_ipint << rule_iprng;

	                                        int flow_iprng = 32 - processingflow.current_ho.diff.get(i).nw_src_maskbits;
	                                        int flow_ipint = processingflow.current_ho.diff.get(i).nw_src_prefix >> flow_iprng;
	                                        flow_ipint = flow_ipint << flow_iprng;
	                                        ho.nw_src_prefix = rule_ipint + (processingflow.current_ho.diff.get(i).nw_src_prefix - flow_ipint);
	                                        ho.nw_src_maskbits = processingflow.current_ho.diff.get(i).nw_src_maskbits;
	                                        ho.vlan = processingflow.current_ho.diff.get(i).vlan;
	                                        }
	                                    }
	                                    if(ho.nw_dst_maskbits != 0 && ho.nw_dst_prefix != 0 && ho.nw_src_maskbits != 0 && ho.nw_src_prefix != 0){
	                                        if(processingflow.next_ho.diff == null) {
	                                            processingflow.next_ho.diff = new ArrayList<HeaderObject>();
	                                        }
	                                        processingflow.next_ho.diff.add(ho);
	                                    }
	                                }
	                            }
	                        }
	                    }
	                    //2. add FlowRuleNode's diff to the next_ho's diff
	                    if(flowRuleNode.diff != null){
	                        for(int i = 0; i < flowRuleNode.diff.size(); i++){
	                            HeaderObject ho = new HeaderObject();
	                            if(matchIPAddress(processingflow.current_ho.nw_dst_prefix, processingflow.current_ho.nw_dst_maskbits,
	                                    flowRuleNode.diff.get(i).nw_dst_prefix, flowRuleNode.diff.get(i).nw_dst_maskbits)){
	                                if(matchIPAddress(processingflow.current_ho.nw_src_prefix, processingflow.current_ho.nw_src_maskbits,
	                                        flowRuleNode.diff.get(i).nw_src_prefix, flowRuleNode.diff.get(i).nw_src_maskbits)){
	                                    if(processingflow.current_ho.nw_dst_maskbits >= flowRuleNode.diff.get(i).nw_dst_maskbits){
	                                        if(processingflow.current_ho.nw_src_maskbits >= flowRuleNode.diff.get(i).nw_src_maskbits){
	                                            //full overlap : current_ho ip_range is in FlowRuleNode.diff.get(i)
	                                            processingflow.is_finished = true;
	                                            inputflow.is_finished = true;
	                                            break;
	                                        }
	                                    }
	                                    //three cases : action_nw_dst_maskbits is 0(forward), 32(one point), or any value(ip range).
	                                    if(actionNode.action_nw_dst_prefix==0){
	                                        //all matching flow will be forwarded
	                                        ho.nw_dst_prefix = flowRuleNode.diff.get(i).nw_dst_prefix;
	                                        ho.nw_dst_maskbits = flowRuleNode.diff.get(i).nw_dst_maskbits;
	                                        ho.vlan = flowRuleNode.diff.get(i).vlan;
	                                    }else if(actionNode.action_nw_dst_maskbits == 32){
	                                        //rule matching condition and action set is N to 1
	                                        ho.nw_dst_prefix = actionNode.action_nw_dst_prefix;
	                                        ho.nw_dst_maskbits = actionNode.action_nw_src_maskbits;
	                                        ho.vlan = actionNode.action_vlan;
	                                    }else{
	                                        //rule matching condition and action set is N to N case
	                                        if(processingflow.current_ho.nw_dst_maskbits <= flowRuleNode.diff.get(i).nw_dst_maskbits){
	                                            int rule_iprng = 32 - processingflow.current_ho.nw_dst_maskbits;
	                                            int rule_ipint = actionNode.action_nw_dst_prefix >> rule_iprng;
	                                        rule_ipint = rule_ipint << rule_iprng;

	                                        int flow_iprng = 32 - flowRuleNode.diff.get(i).nw_dst_maskbits;
	                                        int flow_ipint = flowRuleNode.diff.get(i).nw_dst_prefix >> flow_iprng;
	                                        flow_ipint = flow_ipint << flow_iprng;
	                                        // TODO This is different from how it is calculated above. rule + processing
	                                        ho.nw_dst_prefix = rule_ipint + (flowRuleNode.diff.get(i).nw_dst_prefix - flow_ipint);
	                                        ho.nw_dst_maskbits = flowRuleNode.diff.get(i).nw_dst_maskbits;
	                                        ho.vlan = flowRuleNode.diff.get(i).vlan;
	                                        }
	                                    }
	                                    if(actionNode.action_nw_src_prefix==0){
	                                        //all matching flow will be forwarded
	                                        ho.nw_src_prefix = flowRuleNode.diff.get(i).nw_src_prefix;
	                                        ho.nw_src_maskbits = flowRuleNode.diff.get(i).nw_src_maskbits;
	                                        ho.vlan = flowRuleNode.diff.get(i).vlan;
	                                    }else if(actionNode.action_nw_src_maskbits == 32){
	                                        //rule matching condition and action set is N to 1
	                                        ho.nw_src_prefix = actionNode.action_nw_src_prefix;
	                                        ho.nw_src_maskbits = actionNode.action_nw_src_maskbits;
	                                        ho.vlan = actionNode.action_vlan;
	                                    }else{
	                                        //rule matching condition and action set is N to N case
	                                        if(processingflow.current_ho.nw_src_maskbits <= flowRuleNode.diff.get(i).nw_src_maskbits){
	                                            int rule_iprng = 32 - processingflow.current_ho.nw_src_maskbits;
	                                            int rule_ipint = actionNode.action_nw_src_prefix >> rule_iprng;
	                                        rule_ipint = rule_ipint << rule_iprng;

	                                        int flow_iprng = 32 - flowRuleNode.diff.get(i).nw_src_maskbits;
	                                        int flow_ipint = flowRuleNode.diff.get(i).nw_src_prefix >> flow_iprng;
	                                        flow_ipint = flow_ipint << flow_iprng;
	                                        ho.nw_src_prefix = rule_ipint + (flowRuleNode.diff.get(i).nw_src_prefix - flow_ipint);
	                                        ho.nw_src_maskbits = flowRuleNode.diff.get(i).nw_src_maskbits;
	                                        ho.vlan = flowRuleNode.diff.get(i).vlan;
	                                        }
	                                    }
	                                    if(ho.nw_dst_maskbits != 0 && ho.nw_dst_prefix != 0 && ho.nw_src_maskbits != 0 && ho.nw_src_prefix != 0){
	                                        if(processingflow.next_ho.diff == null) {
	                                            processingflow.next_ho.diff = new ArrayList<HeaderObject>();
	                                        }
	                                        processingflow.next_ho.diff.add(ho);
	                                    }
	                                }
	                            }
	                        }
	                    }
	                    //3. in the next_ho's diff, check dependency and handle full overlabs
	                    if(processingflow.next_ho.diff != null){
	                        for(int i = 0; i < processingflow.next_ho.diff.size() - 1; i++){
	                            for(int j = 1; j < processingflow.next_ho.diff.size(); j++){
	                                if(matchIPAddress(processingflow.next_ho.diff.get(i).nw_dst_prefix, processingflow.next_ho.diff.get(i).nw_dst_maskbits,
	                                        processingflow.next_ho.diff.get(j).nw_dst_prefix, processingflow.next_ho.diff.get(j).nw_dst_maskbits)){
	                                    if(matchIPAddress(processingflow.next_ho.diff.get(i).nw_src_prefix, processingflow.next_ho.diff.get(i).nw_src_maskbits,
	                                            processingflow.next_ho.diff.get(j).nw_src_prefix, processingflow.next_ho.diff.get(j).nw_src_maskbits)){
	                                        if(processingflow.next_ho.diff.get(i).nw_dst_maskbits <= processingflow.next_ho.diff.get(j).nw_dst_maskbits){
	                                            if(processingflow.next_ho.diff.get(i).nw_src_maskbits <= processingflow.next_ho.diff.get(j).nw_src_maskbits){
	                                                //full overlap : FlowRuleNode ip_range is in current_ho.diff.get(i)
	                                                processingflow.next_ho.diff.remove(j);
	                                            }
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }


	                /* L4 check */
	                if(actionNode.action_tcp_src != 0) {
	                	//TCP src is not wildcarded
	                	processingflow.next_ho.tcp_src = actionNode.action_tcp_src;
	                }

	                if(actionNode.action_tcp_dst != 0) {
	                	//wild carded action for TCP dst
	                	processingflow.next_ho.tcp_dst = actionNode.action_tcp_dst;
	                }

	                if(flowRuleNode.tp_src != 0) {
	                	processingflow.next_ho.tcp_src = flowRuleNode.tp_src;
	                }

	                if(flowRuleNode.tp_dst != 0) {
	                	processingflow.next_ho.tcp_dst = flowRuleNode.tp_dst;
	                }




	                //what does it mean if current_ho.tcp_src = 0


	                /* Propagate the flow to next switch. */
	                processingflow.next_ingress_port = actionNode.action_out_port;
	                processingflow.next_switch_dpid = processingflow.current_switch_dpid;
	                //processingflow.next_ho.dl_dst = FlowRuleNode.action_dl_dst;
	                //FlowRuleNode.action_dl_src.toString();

	                //add flow information in the flow history
	                if (processingflow.flow_history == null)
	                    processingflow.flow_history =new ArrayList<FlowInfo>();
	                processingflow.flow_history.add(processingflow);
	                flowRuleNode.flow_info = processingflow;
                }//end L4 match
                else {
                	System.out.println("L4 MATCH FAILED");
                }
            }
            else{
                //just return FlowRuleNode without any changes
                System.out.println("L3 SRC MATCH FAILED");
            }
        } else{
            //just return FlowRuleNode without any changes
            System.out.println("L3 DST MATCH FAILED");
        }
        return flowRuleNode;
    }

    private static boolean matchL4(int flow_rule_l4, int packet_l4) {
    	System.out.printf("flow_rule_l4 = %d\tpacket_l4 = %d\n", flow_rule_l4,packet_l4);
		if(flow_rule_l4 == 0) {
			return true;
		}
		else if(flow_rule_l4 == packet_l4) {
			return true;
		}
		else {
			LOG.info("L4 does not match");
			return false;
		}
	}

	/* All helper functions below this line */

    static boolean isWildcarded(Object matchObj) {
        return matchObj == null ? true : false;
    }

    public static int calculateIpfromPrefix(Ipv4Prefix prefix) {
        String[] parts;
        parts = prefix.getValue().split("/");
        return InetAddresses.coerceToInteger(InetAddresses.forString(parts[0]));
    }

    public static int calculateMaskfromPrefix(Ipv4Prefix prefix) {
        String[] parts;
        parts = prefix.getValue().split("/");
        if (parts.length < 2) {
            return 0;
        } else {
            return Integer.parseInt(parts[1]);
        }
    }



    public static boolean matchIPAddress(int ip1, int prefix1, int ip2, int prefix2) {
        int maskbits = 0;
        int range;

        //set maskbits as a lower integer to check overlaps
        maskbits = (prefix1 > prefix2) ? prefix2 : prefix1;
        range = 32 - maskbits;

        if (range == 0) {
            // The prefix length for both IPs is 32.
            return (ip1 == ip2);
        } else if (range == 32) {
            // Wildcarded match only when flowrule is wildcarded.
            if(prefix1 == 0)
                return true;
            else
                return false;
        }else {
            // Right shift the IP bits to remove the wildcarded range set by prefix
            ip1 = ip1 >> range;
            ip2 = ip2 >> range;
            return (ip1 == ip2);
        }
    }

    static int countBits(final byte[] mask) {
        int netmask = 0;
        for (byte b : mask) {
            netmask += Integer.bitCount(UnsignedBytes.toInt(b));
        }
        return netmask;
    }

    static Ipv4Prefix createPrefix(final Ipv4Address ipv4Address, final byte [] bytemask){
        return createPrefix(ipv4Address, String.valueOf(countBits(bytemask)));
    }

    static Ipv4Prefix createPrefix(final Ipv4Address ipv4Address, final String mask){
        /*
         * Ipv4Address has already validated the address part of the prefix,
         * It is mandated to comply to the same regexp as the address
         * There is absolutely no point rerunning additional checks vs this
         * Note - there is no canonical form check here!!!
         */
        if (null != mask && !mask.isEmpty()) {
            return new Ipv4Prefix(ipv4Address.getValue() + PREFIX_SEPARATOR + mask);
        } else {
            return new Ipv4Prefix(ipv4Address.getValue() + PREFIX_SEPARATOR + IPV4_ADDRESS_LENGTH);
        }
    }

    static final byte[] convertArbitraryMaskToByteArray(DottedQuad mask) {
        String maskValue;
        if (mask.getValue() != null) {
            maskValue  = mask.getValue();
        } else {
            maskValue = DEFAULT_ARBITRARY_BIT_MASK;
        }
        InetAddress maskInIpFormat = null;
        try {
            maskInIpFormat = InetAddress.getByName(maskValue);
        } catch (UnknownHostException e) {
            //LOG.error("Failed to recognize the host while converting mask ", e);
        }
        byte[] bytes = maskInIpFormat.getAddress();
        return bytes;
    }
}
