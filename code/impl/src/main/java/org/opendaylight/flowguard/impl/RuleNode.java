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
import java.util.Map;
import java.util.Set;

import org.opendaylight.controller.liblldp.EtherTypes;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.DottedQuad;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.push.mpls.action._case.PushMplsAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.field._case.SetField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.vlan.id.action._case.SetVlanIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
// TODO import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
/*import org.openflow.protocol.action.OFActionDataLayerDestination;
import org.openflow.protocol.action.OFActionDataLayerSource;
import org.openflow.protocol.action.OFActionNetworkLayerDestination;
import org.openflow.protocol.action.OFActionNetworkLayerSource;
import org.openflow.protocol.action.OFActionVirtualLanIdentifier;
*/
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.clear.actions._case.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.write.actions._case.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.go.to.table._case.GoToTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.meter._case.Meter;

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetDestination;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetSource;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.Layer3Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.Layer4Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4MatchArbitraryBitMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.TcpMatch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.vlan.match.fields.VlanId;

import com.google.common.net.InetAddresses;
import com.google.common.primitives.UnsignedBytes;
/*import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.action.*;
*/
public class RuleNode {
	// TODO Rename fields to that of openflow 1.3
    String switch_name;
    String rule_name;
    public int vlan;
    public short length = 0;
    public NodeConnectorId in_port;
    public EthernetSource dl_src;
    public EthernetDestination dl_dst;
    public EthernetType dl_type;
    public int nw_src_prefix = 0;
    public int nw_src_maskbits = 0;
    public int nw_dst_prefix =0;
    public int nw_dst_maskbits = 0;
    public short nw_proto = 0;
    public int tp_src;
    public int tp_dst;
    public int priority = 0;
    public int wildcards = 0;

    public NodeConnectorId action_out_port;
    public EthernetSource action_dl_src;
    public EthernetDestination action_dl_dst;
    public int action_nw_src_prefix;
    public int action_nw_src_maskbits = 32;
    public int action_nw_dst_prefix;
    public int action_nw_dst_maskbits = 32;
    public int action_vlan;
    public boolean active = true;
    public List<HeaderObject> diff;

    public FlowInfo flow_info;
	private EthernetSource eth_src;
	private EthernetDestination eth_dst;
	private EthernetType eth_type;

	private static final String PREFIX_SEPARATOR = "/";
	private static final int IPV4_ADDRESS_LENGTH = 32;
	private static final String DEFAULT_ARBITRARY_BIT_MASK = "255.255.255.255";

    /*
     * Spec:
     * A  zero-length  OpenFlow  match  (one  with  no  OXM  TLVs)  matches  every  packet.
     * Match  fields  that should be wildcarded are omitted from the OpenFlow match.
     */
    public List<RuleNode> addruletable(List<Flow> flowList){

        List<RuleNode> ruletable = new ArrayList<RuleNode>();
        //Set<String> keys = row.keySet();
        Iterator<Flow> itr = flowList.listIterator();//keys.iterator();

        while(itr.hasNext()){
            Flow flow = itr.next();//row.get(key.toString());
            RuleNode instance = new RuleNode();
            //instance.switch_name = ;


            instance.rule_name = flow.getFlowName();//key.toString();
            //instance.length = value.getLength();

            //instance.in_port = value.getMatch().getInPort();//.getInputPort();
            instance.in_port = flow.getMatch().getInPort(); // It should ne inport or physical input port??
            //instance.vlan = value.getMatch().getDataLayerVirtualLan();
            instance.vlan = flow.getMatch().getVlanMatch().getVlanId().getVlanId().getValue();
            //instance.dl_src = value.getMatch().getDataLayerSource();
            instance.dl_src = flow.getMatch().getEthernetMatch().getEthernetSource();
            //instance.dl_dst = value.getMatch().getDataLayerDestination();
            instance.dl_dst = flow.getMatch().getEthernetMatch().getEthernetDestination();

            //instance.dl_type = value.getMatch().getDataLayerType();
            instance.dl_type = flow.getMatch().getEthernetMatch().getEthernetType();
            //instance.nw_src_prefix = value.getMatch().getNetworkSource();

            /*if(value.getMatch().getNetworkSourceMaskLen() == 0)
                instance.nw_src_maskbits = 32;
            else
                instance.nw_src_maskbits = value.getMatch().getNetworkSourceMaskLen();
            instance.nw_dst_prefix = value.getMatch().getNetworkDestination();
            if(value.getMatch().getNetworkDestinationMaskLen() == 0)
                instance.nw_dst_maskbits = 32;
            else
                instance.nw_dst_maskbits = value.getMatch().getNetworkDestinationMaskLen();
            */

            Layer3Match l3Match = flow.getMatch().getLayer3Match();
            // TODO Handle other L3Match cases: ARP, Ipv6
            if(l3Match instanceof Ipv4Match){
            	Ipv4Prefix src = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Source();
            	Ipv4Prefix dst = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Destination();
            	instance.nw_src_prefix = calculateIpfromPrefix(src);
                instance.nw_src_maskbits = calculateMaskfromPrefix(src);
                instance.nw_dst_prefix = calculateIpfromPrefix(dst);
                instance.nw_dst_maskbits = calculateMaskfromPrefix(dst);
            }
            else if (l3Match instanceof Ipv4MatchArbitraryBitMask){
            	Ipv4Address addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceAddressNoMask();
            	DottedQuad mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceArbitraryBitmask();
            	Ipv4Prefix src = createPrefix(addr, convertArbitraryMaskToByteArray(mask));
            	instance.nw_src_prefix = calculateIpfromPrefix(src);
                instance.nw_src_maskbits = calculateMaskfromPrefix(src);

            	addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationAddressNoMask();
            	mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationArbitraryBitmask();
            	Ipv4Prefix dst = createPrefix(addr, convertArbitraryMaskToByteArray(mask));
                instance.nw_dst_prefix = calculateIpfromPrefix(dst);
                instance.nw_dst_maskbits = calculateMaskfromPrefix(dst);
            }

            //instance.nw_proto = value.getMatch().getNetworkProtocol();
            instance.nw_proto = flow.getMatch().getIpMatch().getIpProtocol();
            //instance.tp_src = value.getMatch().getTransportDestination();
            // TODO Previois implementation worng? src is taken from dest!!
            Layer4Match l4Match = flow.getMatch().getLayer4Match();
            if(l4Match instanceof TcpMatch) {
            	instance.tp_src = ((TcpMatch)l4Match).getTcpSourcePort().getValue();
            	instance.tp_dst = ((TcpMatch)l4Match).getTcpDestinationPort().getValue();
            }
            //instance.tp_dst = value.getMatch().getTransportSource();
            //instance.priority = value.getPriority();
            instance.priority = flow.getPriority();
            //instance.wildcards = value.getMatch().getWildcards();
            // TODO instance.wildcards = flow.

            instance.flow_info = new FlowInfo();
            instance.flow_info.flow_history = new ArrayList<FlowInfo>();

            //List<OFAction> action = value.getActions();
            List<Instruction> instList = flow.getInstructions().getInstruction();
            // TODO System.out.println(StaticFlowEntries.flowModActionsToString(action));

            //TODO Many more instructions and actions to be checked and implemeted :Openflow 1.3
            for(Instruction i : instList) {
            	org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.Instruction instruction = i.getInstruction();

            	if(instruction instanceof Meter){
            		// Optional
            	}
            	else if(instruction instanceof ApplyActions) {
            		// Optional
            	}
            	else if(instruction instanceof ClearActions) {
            		// Optional
            	}
            	else if(instruction instanceof WriteActions) {
            		// Required
            		List<Action> action = ((WriteActions)i.getInstruction()).getAction();
            		for(Action a : action) {
            			if(a.getAction() instanceof OutputAction) {
            				instance.action_out_port = new NodeConnectorId(((OutputAction)(a.getAction())).getOutputNodeConnector());
            			}
            			else if(a.getAction() instanceof SetField){
            				instance.action_dl_src = ((SetField)(a.getAction())).getEthernetMatch().getEthernetSource();
            				instance.action_dl_dst = ((SetField)(a.getAction())).getEthernetMatch().getEthernetDestination();

            				l3Match = ((SetField)(a.getAction())).getLayer3Match();
            	            // TODO Handle other L3Match cases: ARP, Ipv6
            	            if(l3Match instanceof Ipv4Match){
            	            	Ipv4Prefix src  = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Source();
            	            	Ipv4Prefix dst = ((Ipv4Match)flow.getMatch().getLayer3Match()).getIpv4Destination();
            	            	instance.action_nw_src_prefix = calculateIpfromPrefix(src);
            	                instance.action_nw_src_maskbits = calculateMaskfromPrefix(src);
            	                instance.action_nw_dst_prefix = calculateIpfromPrefix(dst);
            	                instance.action_nw_dst_maskbits = calculateMaskfromPrefix(dst);
            	            }
            	            else if (l3Match instanceof Ipv4MatchArbitraryBitMask){
            	            	Ipv4Address addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceAddressNoMask();
            	            	DottedQuad mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4SourceArbitraryBitmask();
            	            	Ipv4Prefix src = createPrefix(addr, convertArbitraryMaskToByteArray(mask));

            	            	addr = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationAddressNoMask();
            	            	mask = ((Ipv4MatchArbitraryBitMask)flow.getMatch().getLayer3Match()).getIpv4DestinationArbitraryBitmask();
            	            	Ipv4Prefix dst = createPrefix(addr, convertArbitraryMaskToByteArray(mask));
            	            	instance.action_nw_src_prefix = calculateIpfromPrefix(src);
            	                instance.action_nw_src_maskbits = calculateMaskfromPrefix(src);
            	                instance.action_nw_dst_prefix = calculateIpfromPrefix(dst);
            	                instance.action_nw_dst_maskbits = calculateMaskfromPrefix(dst);
            	            }
            			}
            			else if(a.getAction() instanceof SetVlanIdAction){
            			// TODO Check what is assigned
            				instance.action_vlan = ((SetField)(a.getAction())).getVlanMatch().getVlanId().getVlanId().getValue();
            			}
            		}
            	}
            	else if(instruction instanceof GoToTable) {
            		// Required

            	}
            	else {
            		// Error when a flow contains a packet with invalid instruction.
            	}
            }
            /*for (OFAction a : action){
                switch(a.getType()) {
                case OUTPUT:
                    instance.action_out_port = ((OFActionOutput)a).getPort();
                    break;
                case SET_DL_SRC:
                    instance.action_dl_src = ((OFActionDataLayerSource)a).getDataLayerAddress();
                    break;
                case SET_DL_DST:
                    instance.action_dl_dst = ((OFActionDataLayerDestination)a).getDataLayerAddress();
                    break;
                case SET_NW_SRC:
                    //add spliter to support wildcards.
                    //after getting replies from adni, we need to fix it later.
                    instance.action_nw_src_prefix = ((OFActionNetworkLayerSource)a).getNetworkAddress();
                    break;
                case SET_NW_DST:
                    instance.action_nw_dst_prefix = ((OFActionNetworkLayerDestination)a).getNetworkAddress();
                    break;
              TODO case SET_VLAN_ID:
                    instance.vlan = ((OFActionVirtualLanIdentifier)a).getVirtualLanIdentifier();
                    break;
                default:
                    System.out.println("Could not decode action: {}");
                    break;
                }
            }*/

            int i = 0;
            for(i = 0; i < ruletable.size(); i++){
                if (ruletable.get(i) != null && ruletable.get(i).priority <= instance.priority)
                    break;
            }
            if(i <= ruletable.size()){
                ruletable.add(i, instance);
            } else {
                ruletable.add(instance);
            }
        }
        ruletable = RuleNode.computedependency(ruletable);
        return ruletable;
    }

    public static List<RuleNode> addrulenode(List<RuleNode> ruletable, String flowname, FlowBuilder newflowmod){

        FlowBuilder value = newflowmod;
        RuleNode instance = new RuleNode();
        //instance.switch_name = ;
        /*instance.rule_name = flowname;
           instance.length = value.getLength();
           instance.in_port = value.getMatch().getInputPort();
        instance.vlan = value.getMatch().getDataLayerVirtualLan();
           instance.dl_src = value.getMatch().getDataLayerSource();
           instance.dl_dst = value.getMatch().getDataLayerDestination();
           instance.dl_type = value.getMatch().getDataLayerType();
           instance.nw_src_prefix = value.getMatch().getNetworkSource();;
           if(value.getMatch().getNetworkSourceMaskLen() == 0 && instance.nw_src_prefix != 0)
               instance.nw_src_maskbits = 32;
           else
               instance.nw_src_maskbits = value.getMatch().getNetworkSourceMaskLen();
           instance.nw_dst_prefix = value.getMatch().getNetworkDestination();
           if(value.getMatch().getNetworkDestinationMaskLen() == 0 && instance.nw_dst_prefix != 0)
               instance.nw_dst_maskbits = 32;
           else
               instance.nw_dst_maskbits = value.getMatch().getNetworkDestinationMaskLen();
           instance.nw_proto = value.getMatch().getNetworkProtocol();
           instance.tp_src = value.getMatch().getTransportDestination();
           instance.tp_dst = value.getMatch().getTransportSource();
           instance.priority = value.getPriority();
           instance.wildcards = value.getMatch().getWildcards();

           instance.flow_info = new FlowInfo();
           instance.flow_info.flow_history = new ArrayList<FlowInfo>();

           List<OFAction> action = value.getActions();
           // TODO System.out.println(StaticFlowEntries.flowModActionsToString(action));

           for (OFAction a : action){
               switch(a.getType()) {
               case OUTPUT:
                   instance.action_out_port = ((OFActionOutput)a).getPort();
                   break;
               case SET_DL_SRC:
                   instance.action_dl_src = ((OFActionDataLayerSource)a).getDataLayerAddress();
                   break;
               case SET_DL_DST:
                   instance.action_dl_dst = ((OFActionDataLayerDestination)a).getDataLayerAddress();
                   break;
               case SET_NW_SRC:
                   //add spliter to support wildcards.
                   //after getting replies from adni, we need to fix it later.
                   instance.action_nw_src_prefix = ((OFActionNetworkLayerSource)a).getNetworkAddress();
                   break;
               case SET_NW_DST:
                   instance.action_nw_dst_prefix = ((OFActionNetworkLayerDestination)a).getNetworkAddress();
                   break;
                TODO case SET_VLAN_ID:
                   instance.vlan = ((OFActionVirtualLanIdentifier)a).getVirtualLanIdentifier();
                   break;
               default:
                   System.out.println("Could not decode action: {}");
                   break;
               }
           }
           int i = 0;
           int ruletable_size = 0;
           if(ruletable != null)
               ruletable_size = ruletable.size();
           for(i = 0; i < ruletable_size; i++){
               if (ruletable.get(i) != null && ruletable.get(i).priority <= instance.priority)
                   break;
           }
           if(i <= ruletable_size){
               if(ruletable == null)
                   ruletable = new ArrayList<RuleNode>();
               ruletable.add(i, instance);
           } else {
               ruletable.add(instance);
           }*/
           ruletable = RuleNode.computedependency(ruletable);

        return ruletable;
    }

    public static List<RuleNode> deleterulenode(List<RuleNode> ruletable, String rulename){

           int i = 0;
           int ruletable_size = 0;
           if(ruletable != null)
               ruletable_size = ruletable.size();
           for(i = 0; i < ruletable_size; i++){
               if (rulename.equals(ruletable.get(i).rule_name)){
                   ruletable.remove(i);
                   break;
               }
           }
           ruletable = RuleNode.computedependency(ruletable);

        return ruletable;
    }

    public static List<RuleNode> computedependency(List<RuleNode> ruletable){
        //compute intra table dependency
        //compare i th rule and j th rule such that i th rule is prior to j th rule.
        for(int i = 0; i < ruletable.size(); i++){
            ruletable.get(i).active = true;
            if(ruletable.get(i).diff != null)
                ruletable.get(i).diff.clear();
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
                if(ruletable.get(i).in_port.equals(ruletable.get(j).in_port)
                        && ruletable.get(i).dl_type.getType().getValue() == (long)(EtherTypes.IPv4.intValue())
                        && ruletable.get(j).dl_type.getType().getValue() == (long)(EtherTypes.IPv4.intValue())
                        && ruletable.get(i).active == true //TODO The rule will always be active as above!
                        && ruletable.get(i).vlan == ruletable.get(j).vlan) {
                    //check if ip range is overlapped
                    if(matchIPAddress(ruletable.get(i).nw_dst_prefix, ruletable.get(i).nw_dst_maskbits,
                    		ruletable.get(j).nw_dst_prefix, ruletable.get(j).nw_dst_maskbits))
                    	if(matchIPAddress(ruletable.get(i).nw_src_prefix, ruletable.get(i).nw_src_maskbits,
                        		ruletable.get(j).nw_src_prefix, ruletable.get(j).nw_src_maskbits)){

                            if(ruletable.get(j).diff == null){
                                ruletable.get(j).diff = new ArrayList<HeaderObject>();
                            }
                            /* If two rules have same IP range and higher priority rule has more(>=)0 IP range
                            than the lower priority rule, mark the lower priority rule as disabled. */
                            if(ruletable.get(i).nw_dst_maskbits <= ruletable.get(j).nw_dst_maskbits &&
                                    ruletable.get(i).nw_src_maskbits <= ruletable.get(j).nw_src_maskbits){
                                //handle for full overlap case
                                ruletable.get(j).active = false;
                            }
                            else{
                                //handle for partial overlap case
                                HeaderObject ho = new HeaderObject();

                                // Take the larger maskbits of the two rules for both dest and source.
                                ho.nw_dst_prefix = ruletable.get(i).nw_dst_prefix;
                                ho.nw_src_prefix = ruletable.get(i).nw_src_prefix;
                                ho.nw_dst_maskbits = ruletable.get(i).nw_dst_maskbits;
                                ho.nw_src_maskbits = ruletable.get(i).nw_src_maskbits;

                                boolean donothing = false;

                                for(int k = 0; k < ruletable.get(j).diff.size(); k++){
                                    if(matchIPAddress(ho.nw_dst_prefix, ho.nw_dst_maskbits,
                                            ruletable.get(j).diff.get(k).nw_dst_prefix, ruletable.get(j).diff.get(k).nw_dst_maskbits))
                                        if(matchIPAddress(ho.nw_src_prefix, ho.nw_src_maskbits,
                                                ruletable.get(j).diff.get(k).nw_src_prefix, ruletable.get(j).diff.get(k).nw_src_maskbits))
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
                                if(donothing == true){
                                    //do nothing
                                }
                                else {
                                    ruletable.get(j).diff.add(ho);
                                }
                            }
                        }
                }
            }
        }
        return ruletable;
    }

    public static RuleNode computeFlow(RuleNode rulenode, FlowInfo inputflow){
        FlowInfo processingflow = new FlowInfo();
        processingflow = FlowInfo.valueCopy(inputflow);
        //processingflow inputflow to processing flow
        processingflow.rule_node_name = rulenode.rule_name;
        processingflow.current_ingress_port = inputflow.next_ingress_port;
        processingflow.current_switch_dpid = inputflow.next_switch_dpid;
        if(processingflow.current_ho == null)
            processingflow.current_ho = new HeaderObject();
        processingflow.current_ho = inputflow.next_ho.get_ho();
        processingflow.flow_history = inputflow.flow_history;
        if(processingflow.next_ho == null)
            processingflow.next_ho= new HeaderObject();
        if(processingflow.flow_history != null && processingflow.flow_history.size() == 1){
            //initial sourceflow : set vlan as a first rule's vlan
            processingflow.current_ho.vlan = rulenode.vlan;
        }


       // TODO if(processingflow.current_ingress_port==rulenode.in_port && rulenode.dl_type==2048 ){
               //initiate flowinfo in the corresponding rulenode
               //if(rulenode.dltype==2054) then it just forwards the packet;
               //10.0.0.1 = 167772161
            if(processingflow.current_ho.vlan == rulenode.vlan &&
                    matchIPAddress(rulenode.nw_dst_prefix, rulenode.nw_dst_maskbits,
                    processingflow.current_ho.nw_dst_prefix, processingflow.current_ho.nw_dst_maskbits))
                if(matchIPAddress(rulenode.nw_src_prefix, rulenode.nw_src_maskbits,
                    processingflow.current_ho.nw_src_prefix, processingflow.current_ho.nw_src_maskbits)){

                    if(rulenode.action_nw_dst_prefix==0){
                        //all matching flow will be forwarded
                        if(rulenode.nw_dst_maskbits <= processingflow.current_ho.nw_dst_maskbits){
                               processingflow.next_ho.nw_dst_prefix = processingflow.current_ho.nw_dst_prefix;
                               processingflow.next_ho.nw_dst_maskbits = processingflow.current_ho.nw_dst_maskbits;
                               processingflow.next_ho.vlan = processingflow.current_ho.vlan;
                        }else{
                            processingflow.next_ho.nw_dst_prefix = rulenode.nw_dst_prefix;
                               processingflow.next_ho.nw_dst_maskbits = rulenode.nw_dst_maskbits;
                               processingflow.next_ho.vlan = rulenode.vlan;
                        }
                    }else if(rulenode.action_nw_dst_maskbits == 32){
                        //rule matching condition and action set is N to 1
                           processingflow.next_ho.nw_dst_prefix = rulenode.action_nw_dst_prefix;
                           processingflow.next_ho.nw_dst_maskbits = rulenode.action_nw_src_maskbits;
                           processingflow.next_ho.vlan = rulenode.action_vlan;
                    }else{
                        //rule matching condition and action set is N to N case
                        if(rulenode.nw_dst_maskbits <= processingflow.current_ho.nw_dst_maskbits){
                               int rule_iprng = 32 - rulenode.nw_dst_maskbits;
                               int rule_ipint = rulenode.action_nw_dst_prefix >> rule_iprng;
                               rule_ipint = rule_ipint << rule_iprng;

                               int flow_iprng = 32 - processingflow.current_ho.nw_dst_maskbits;
                               int flow_ipint = processingflow.current_ho.nw_dst_prefix >> flow_iprng;
                               flow_ipint = flow_ipint << flow_iprng;
                               processingflow.next_ho.nw_dst_prefix = rule_ipint + (processingflow.current_ho.nw_dst_prefix - flow_ipint);
                               processingflow.next_ho.nw_dst_maskbits = processingflow.current_ho.nw_dst_maskbits;
                               processingflow.next_ho.vlan = processingflow.current_ho.vlan;
                        }else{
                               processingflow.next_ho.nw_dst_prefix = rulenode.action_nw_dst_prefix;
                               processingflow.next_ho.nw_dst_maskbits = rulenode.action_nw_src_maskbits;
                               processingflow.next_ho.vlan = rulenode.action_vlan;
                        }
                    }

                    if(rulenode.action_nw_src_prefix==0){
                        //all matching flow will be forwarded
                        if(rulenode.nw_src_maskbits <= processingflow.current_ho.nw_src_maskbits){
                               processingflow.next_ho.nw_src_prefix = processingflow.current_ho.nw_src_prefix;
                               processingflow.next_ho.nw_src_maskbits = processingflow.current_ho.nw_src_maskbits;
                               processingflow.next_ho.vlan = processingflow.current_ho.vlan;
                        }else{
                            processingflow.next_ho.nw_src_prefix = rulenode.nw_src_prefix;
                               processingflow.next_ho.nw_src_maskbits = rulenode.nw_src_maskbits;
                               processingflow.next_ho.vlan = rulenode.vlan;
                        }
                    }else if(rulenode.action_nw_src_maskbits == 32){
                        //rule matching condition and action set is N to 1
                           processingflow.next_ho.nw_src_prefix = rulenode.action_nw_src_prefix;
                           processingflow.next_ho.nw_src_maskbits = rulenode.action_nw_src_maskbits;
                           processingflow.next_ho.vlan = rulenode.action_vlan;
                    }else{
                        //rule matching condition and action set is N to N case
                        if(rulenode.nw_src_maskbits <= processingflow.current_ho.nw_src_maskbits){
                               int rule_iprng = 32 - rulenode.nw_src_maskbits;
                               int rule_ipint = rulenode.action_nw_src_prefix >> rule_iprng;
                               rule_ipint = rule_ipint << rule_iprng;

                               int flow_iprng = 32 - processingflow.current_ho.nw_src_maskbits;
                               int flow_ipint = processingflow.current_ho.nw_src_prefix >> flow_iprng;
                               flow_ipint = flow_ipint << flow_iprng;
                               processingflow.next_ho.nw_src_prefix = rule_ipint + (processingflow.current_ho.nw_src_prefix - flow_ipint);
                               processingflow.next_ho.nw_src_maskbits = processingflow.current_ho.nw_src_maskbits;
                               processingflow.next_ho.vlan = processingflow.current_ho.vlan;
                        }else{
                               processingflow.next_ho.nw_src_prefix = rulenode.action_nw_src_prefix;
                               processingflow.next_ho.nw_src_maskbits = rulenode.action_nw_src_maskbits;
                               processingflow.next_ho.vlan = rulenode.action_vlan;
                        }
                    }

                    //calculate diff part
                    if(rulenode.action_nw_dst_maskbits == 32 && rulenode.action_nw_src_maskbits == 32){
                        //do nothing
                    }else{
                        //1. calculate flowinfo itself : add current_ho's diff to the next_ho's diff
                        if(processingflow.current_ho.diff != null){
                            for(int i = 0; i < processingflow.current_ho.diff.size(); i++){
                                HeaderObject ho = new HeaderObject();
                                if(matchIPAddress(rulenode.nw_dst_prefix, rulenode.nw_dst_maskbits,
                                        processingflow.current_ho.diff.get(i).nw_dst_prefix, processingflow.current_ho.diff.get(i).nw_dst_maskbits)){
                                    if(matchIPAddress(rulenode.nw_src_prefix, rulenode.nw_src_maskbits,
                                            processingflow.current_ho.diff.get(i).nw_src_prefix, processingflow.current_ho.diff.get(i).nw_src_maskbits)){
                                        if(rulenode.nw_dst_maskbits >= processingflow.current_ho.diff.get(i).nw_dst_maskbits){
                                            if(rulenode.nw_src_maskbits >= processingflow.current_ho.diff.get(i).nw_src_maskbits){
                                                //full overlap : rulenode ip_range is in current_ho.diff.get(i)
                                                processingflow.is_finished = true;
                                                break;
                                            }
                                        }
                                        //three cases : action_nw_dst_maskbits is 0(forward), 32(one point), or any value(ip range).
                                        if(rulenode.action_nw_dst_prefix==0){
                                            //all matching flow will be forwarded
                                               ho.nw_dst_prefix = processingflow.current_ho.diff.get(i).nw_dst_prefix;
                                               ho.nw_dst_maskbits = processingflow.current_ho.diff.get(i).nw_dst_maskbits;
                                               ho.vlan = processingflow.current_ho.diff.get(i).vlan;
                                        }else if(rulenode.action_nw_dst_maskbits == 32){
                                            //rule matching condition and action set is N to 1
                                               ho.nw_dst_prefix = rulenode.action_nw_dst_prefix;
                                               ho.nw_dst_maskbits = rulenode.action_nw_src_maskbits;
                                               ho.vlan = rulenode.action_vlan;
                                        }else{
                                            //rule matching condition and action set is N to N case
                                            if(rulenode.nw_dst_maskbits <= processingflow.current_ho.diff.get(i).nw_dst_maskbits){
                                                   int rule_iprng = 32 - rulenode.nw_dst_maskbits;
                                                   int rule_ipint = rulenode.action_nw_dst_prefix >> rule_iprng;
                                                   rule_ipint = rule_ipint << rule_iprng;

                                                   int flow_iprng = 32 - processingflow.current_ho.diff.get(i).nw_dst_maskbits;
                                                   int flow_ipint = processingflow.current_ho.diff.get(i).nw_dst_prefix >> flow_iprng;
                                                   flow_ipint = flow_ipint << flow_iprng;
                                                   ho.nw_dst_prefix = rule_ipint + (processingflow.current_ho.diff.get(i).nw_dst_prefix - flow_ipint);
                                                   ho.nw_dst_maskbits = processingflow.current_ho.diff.get(i).nw_dst_maskbits;
                                                   ho.vlan = processingflow.current_ho.diff.get(i).vlan;
                                            }
                                        }
                                        if(rulenode.action_nw_src_prefix==0){
                                            //all matching flow will be forwarded
                                               ho.nw_src_prefix = processingflow.current_ho.diff.get(i).nw_src_prefix;
                                               ho.nw_src_maskbits = processingflow.current_ho.diff.get(i).nw_src_maskbits;
                                               ho.vlan = processingflow.current_ho.diff.get(i).vlan;
                                        }else if(rulenode.action_nw_src_maskbits == 32){
                                            //rule matching condition and action set is N to 1
                                               ho.nw_src_prefix = rulenode.action_nw_src_prefix;
                                               ho.nw_src_maskbits = rulenode.action_nw_src_maskbits;
                                               ho.vlan = rulenode.action_vlan;
                                        }else{
                                            //rule matching condition and action set is N to N case
                                            if(rulenode.nw_src_maskbits <= processingflow.current_ho.diff.get(i).nw_src_maskbits){
                                                   int rule_iprng = 32 - rulenode.nw_src_maskbits;
                                                   int rule_ipint = rulenode.action_nw_src_prefix >> rule_iprng;
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
                                            if(processingflow.next_ho.diff == null)
                                                processingflow.next_ho.diff = new ArrayList<HeaderObject>();
                                            processingflow.next_ho.diff.add(ho);
                                        }
                                    }
                                }
                            }
                        }
                        //2. add rulenode's diff to the next_ho's diff
                        if(rulenode.diff != null){
                            for(int i = 0; i < rulenode.diff.size(); i++){
                                HeaderObject ho = new HeaderObject();
                                if(matchIPAddress(processingflow.current_ho.nw_dst_prefix, processingflow.current_ho.nw_dst_maskbits,
                                        rulenode.diff.get(i).nw_dst_prefix, rulenode.diff.get(i).nw_dst_maskbits)){
                                    if(matchIPAddress(processingflow.current_ho.nw_src_prefix, processingflow.current_ho.nw_src_maskbits,
                                            rulenode.diff.get(i).nw_src_prefix, rulenode.diff.get(i).nw_src_maskbits)){
                                        if(processingflow.current_ho.nw_dst_maskbits >= rulenode.diff.get(i).nw_dst_maskbits){
                                            if(processingflow.current_ho.nw_src_maskbits >= rulenode.diff.get(i).nw_src_maskbits){
                                                //full overlap : current_ho ip_range is in rulenode.diff.get(i)
                                                processingflow.is_finished = true;
                                                break;
                                            }
                                        }
                                        //three cases : action_nw_dst_maskbits is 0(forward), 32(one point), or any value(ip range).
                                        if(rulenode.action_nw_dst_prefix==0){
                                            //all matching flow will be forwarded
                                               ho.nw_dst_prefix = rulenode.diff.get(i).nw_dst_prefix;
                                               ho.nw_dst_maskbits = rulenode.diff.get(i).nw_dst_maskbits;
                                               ho.vlan = rulenode.diff.get(i).vlan;
                                        }else if(rulenode.action_nw_dst_maskbits == 32){
                                            //rule matching condition and action set is N to 1
                                               ho.nw_dst_prefix = rulenode.action_nw_dst_prefix;
                                               ho.nw_dst_maskbits = rulenode.action_nw_src_maskbits;
                                               ho.vlan = rulenode.action_vlan;
                                        }else{
                                            //rule matching condition and action set is N to N case
                                            if(processingflow.current_ho.nw_dst_maskbits <= rulenode.diff.get(i).nw_dst_maskbits){
                                                   int rule_iprng = 32 - processingflow.current_ho.nw_dst_maskbits;
                                                   int rule_ipint = rulenode.action_nw_dst_prefix >> rule_iprng;
                                                   rule_ipint = rule_ipint << rule_iprng;

                                                   int flow_iprng = 32 - rulenode.diff.get(i).nw_dst_maskbits;
                                                   int flow_ipint = rulenode.diff.get(i).nw_dst_prefix >> flow_iprng;
                                                   flow_ipint = flow_ipint << flow_iprng;
                                                   ho.nw_dst_prefix = rule_ipint + (rulenode.diff.get(i).nw_dst_prefix - flow_ipint);
                                                   ho.nw_dst_maskbits = rulenode.diff.get(i).nw_dst_maskbits;
                                                   ho.vlan = rulenode.diff.get(i).vlan;
                                            }
                                        }
                                        if(rulenode.action_nw_src_prefix==0){
                                            //all matching flow will be forwarded
                                               ho.nw_src_prefix = rulenode.diff.get(i).nw_src_prefix;
                                               ho.nw_src_maskbits = rulenode.diff.get(i).nw_src_maskbits;
                                               ho.vlan = rulenode.diff.get(i).vlan;
                                        }else if(rulenode.action_nw_src_maskbits == 32){
                                            //rule matching condition and action set is N to 1
                                               ho.nw_src_prefix = rulenode.action_nw_src_prefix;
                                               ho.nw_src_maskbits = rulenode.action_nw_src_maskbits;
                                               ho.vlan = rulenode.action_vlan;
                                        }else{
                                            //rule matching condition and action set is N to N case
                                            if(processingflow.current_ho.nw_src_maskbits <= rulenode.diff.get(i).nw_src_maskbits){
                                                   int rule_iprng = 32 - processingflow.current_ho.nw_src_maskbits;
                                                   int rule_ipint = rulenode.action_nw_src_prefix >> rule_iprng;
                                                   rule_ipint = rule_ipint << rule_iprng;

                                                   int flow_iprng = 32 - rulenode.diff.get(i).nw_src_maskbits;
                                                   int flow_ipint = rulenode.diff.get(i).nw_src_prefix >> flow_iprng;
                                                   flow_ipint = flow_ipint << flow_iprng;
                                                   ho.nw_src_prefix = rule_ipint + (rulenode.diff.get(i).nw_src_prefix - flow_ipint);
                                                   ho.nw_src_maskbits = rulenode.diff.get(i).nw_src_maskbits;
                                                   ho.vlan = rulenode.diff.get(i).vlan;
                                            }
                                        }
                                        if(ho.nw_dst_maskbits != 0 && ho.nw_dst_prefix != 0 && ho.nw_src_maskbits != 0 && ho.nw_src_prefix != 0){
                                            if(processingflow.next_ho.diff == null)
                                                processingflow.next_ho.diff = new ArrayList<HeaderObject>();
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
                                                    //full overlap : rulenode ip_range is in current_ho.diff.get(i)
                                                    processingflow.next_ho.diff.remove(j);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }


                       processingflow.next_ingress_port = rulenode.action_out_port;
                       processingflow.next_switch_dpid = processingflow.current_switch_dpid;
                       //processingflow.next_ho.dl_dst = rulenode.action_dl_dst;
                       //rulenode.action_dl_src.toString();

                       //add flow information in the flow history
                       processingflow.flow_history.add(processingflow);
                       rulenode.flow_info = processingflow;
                }
                else{
                    //just return rulenode without any changes

                }
            else{
                //just return rulenode without any changes
            }

        return rulenode;
    }

    public int calculateIpfromPrefix(Ipv4Prefix prefix) {
    	String[] parts;

    	parts = prefix.getValue().split("/");

    	return InetAddresses.coerceToInteger(InetAddresses.forString(parts[0]));
    }

    public int calculateMaskfromPrefix(Ipv4Prefix prefix) {
    	String[] parts;
    	parts = prefix.getValue().split("/");
    	if (parts.length < 2)
            return 0;
    	else
    		return Integer.parseInt(parts[1]);

    }

    public static boolean matchIPAddress(int ip1, int prefix1, int ip2, int prefix2) {
        boolean matched = true;
        int maskbits = 0;
        int range;
        //set maskbits as a lower integer to check overlaps
        maskbits = (prefix1 > prefix2) ? prefix2 : prefix1;
        range = 32 - maskbits;

        if(range == 0){
            // The prefix length for both IPs is 32.
            return (ip1 == ip2);
        }
        else {
            // Right shift the IP bits to remove the wildcarded range set by prefix
            ip1 = ip1 >> maskbits;
            ip2 = ip2 >> maskbits;
            // TODO Leftshit to return back to normal is required???
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
