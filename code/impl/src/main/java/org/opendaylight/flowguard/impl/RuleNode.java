/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.flowguard.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
import org.openflow.protocol.action.OFActionDataLayerDestination;
import org.openflow.protocol.action.OFActionDataLayerSource;
import org.openflow.protocol.action.OFActionNetworkLayerDestination;
import org.openflow.protocol.action.OFActionNetworkLayerSource;
import org.openflow.protocol.action.OFActionVirtualLanIdentifier;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.action.*;

public class RuleNode {
	String switch_name;
	String rule_name;
	public short vlan = -1;
	public short length = 0;
    public short in_port = 0;
    public byte[] dl_src;
    public byte[] dl_dst;
    public short dl_type = 0;
    public int nw_src_prefix = 0;
    public int nw_src_maskbits = 0;
    public int nw_dst_prefix = 0;
    public int nw_dst_maskbits = 0;
    public short nw_proto = 0;
    public short tp_src = 0;
    public short tp_dst = 0;
    public int priority = 0;
    public int wildcards = 0;

    public short action_out_port = 0;
    public byte[] action_dl_src;
    public byte[] action_dl_dst;
    public int action_nw_src_prefix = 0;
    public int action_nw_src_maskbits = 32;
    public int action_nw_dst_prefix = 0;
    public int action_nw_dst_maskbits = 32;
    public short action_vlan = -1;
    public boolean active = true;
	public List<HeaderObject> diff;

    public FlowInfo flow_info;



	public List<RuleNode> addruletable(Map<String, OFFlowMod> row){

		List<RuleNode> ruletable = new ArrayList<RuleNode>();
		Set<String> keys = row.keySet();
		Iterator<String> itr = keys.iterator();
		while(itr.hasNext()){
		    Object key = itr.next();
		    OFFlowMod value = row.get(key.toString());
		    RuleNode instance = new RuleNode();
		    //instance.switch_name = ;
		    instance.rule_name = key.toString();
    		instance.length = value.getLength();
    		instance.in_port = value.getMatch().getInputPort();
    		instance.vlan = value.getMatch().getDataLayerVirtualLan();
    		instance.dl_src = value.getMatch().getDataLayerSource();
    		instance.dl_dst = value.getMatch().getDataLayerDestination();
    		instance.dl_type = value.getMatch().getDataLayerType();
    		instance.nw_src_prefix = value.getMatch().getNetworkSource();;
    		if(value.getMatch().getNetworkSourceMaskLen() == 0)
    			instance.nw_src_maskbits = 32;
    		else
    			instance.nw_src_maskbits = value.getMatch().getNetworkSourceMaskLen();
    		instance.nw_dst_prefix = value.getMatch().getNetworkDestination();
    		if(value.getMatch().getNetworkDestinationMaskLen() == 0)
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
               /* TODO case SET_VLAN_ID:
                    instance.vlan = ((OFActionVirtualLanIdentifier)a).getVirtualLanIdentifier();
                    break; */
                default:
                    System.out.println("Could not decode action: {}");
                    break;
                }
    		}
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

	public static List<RuleNode> computedependency(List<RuleNode> ruletable){
		//compute intra table dependency
		//compare i th rule and j th rule such that i th rule is prior to j th rule.
		for(int i = 0; i < ruletable.size(); i++){
			ruletable.get(i).active = true;
			if(ruletable.get(i).diff != null)
				ruletable.get(i).diff.clear();
		}

		for(int i = 0; i < ruletable.size() - 1; i++){
			for(int j = i + 1; j < ruletable.size(); j++){
				if(ruletable.get(i).in_port == ruletable.get(j).in_port
						&&	ruletable.get(i).dl_type == 2048
						&& ruletable.get(j).dl_type == 2048
						&& ruletable.get(i).active == true
						&& ruletable.get(i).vlan == ruletable.get(j).vlan){
					//check if ip range is overlapped
					if(matchIPAddress(ruletable.get(i).nw_dst_prefix, ruletable.get(i).nw_dst_maskbits,
							ruletable.get(j).nw_dst_prefix, ruletable.get(j).nw_dst_maskbits))
						if(matchIPAddress(ruletable.get(i).nw_src_prefix, ruletable.get(i).nw_src_maskbits,
							ruletable.get(j).nw_src_prefix, ruletable.get(j).nw_src_maskbits)){

							if(ruletable.get(j).diff == null){
								ruletable.get(j).diff = new ArrayList<HeaderObject>();
							}
							if(ruletable.get(i).nw_dst_maskbits <= ruletable.get(j).nw_dst_maskbits &&
									ruletable.get(i).nw_src_maskbits <= ruletable.get(j).nw_src_maskbits){
								//handle for full overlap case
								ruletable.get(j).active = false;
							}
							else{
								//handle for partial overlap case
								HeaderObject ho = new HeaderObject();
								if(ruletable.get(i).nw_dst_maskbits < ruletable.get(j).nw_dst_maskbits){
									ho.nw_dst_prefix = ruletable.get(j).nw_dst_prefix;
									ho.nw_dst_maskbits = ruletable.get(j).nw_dst_maskbits;
								}else{
									ho.nw_dst_prefix = ruletable.get(i).nw_dst_prefix;
									ho.nw_dst_maskbits = ruletable.get(i).nw_dst_maskbits;
								}

								if(ruletable.get(i).nw_src_maskbits < ruletable.get(j).nw_src_maskbits){
									ho.nw_src_prefix = ruletable.get(j).nw_src_prefix;
									ho.nw_src_maskbits = ruletable.get(j).nw_src_maskbits;
								}else{
									ho.nw_src_prefix = ruletable.get(i).nw_src_prefix;
									ho.nw_src_maskbits = ruletable.get(i).nw_src_maskbits;
								}
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

	public static List<RuleNode> addrulenode(List<RuleNode> ruletable, String flowname, OFFlowMod newflowmod){

	    OFFlowMod value = newflowmod;
	    RuleNode instance = new RuleNode();
	    //instance.switch_name = ;
	    instance.rule_name = flowname;
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
               /* TODO case SET_VLAN_ID:
                   instance.vlan = ((OFActionVirtualLanIdentifier)a).getVirtualLanIdentifier();
                   break; */
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
   		}
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

    public static boolean matchIPAddress(int rule1_Prefix, int rule1_Bits, int rule2_Prefix, int rule2_Bits) {
        boolean matched = true;
        int maskbits = 0;
        //set maskbits as a lower integer to check overlaps
        if(rule1_Bits > rule2_Bits){
        	maskbits = rule2_Bits;
        }else{
        	maskbits = rule1_Bits;
        }
        int rule1_iprng = 32 - maskbits;
        int rule1_ipint = rule1_Prefix;
        int rule2_iprng = 32 - maskbits;
        int rule2_ipint = rule2_Prefix;
        // if there's a subnet range (bits to be wildcarded > 0)
        if (rule1_iprng > 0 || rule2_iprng > 0) {
            // right shift bits to remove rule_iprng of LSB that are to be
            // wildcarded
            rule1_ipint = rule1_ipint >> rule1_iprng;
			rule2_ipint = rule2_ipint >> rule2_iprng;
            // now left shift to return to normal range, except that the
            // rule_iprng number of LSB
            // are now zeroed
            rule1_ipint = rule1_ipint << rule1_iprng;
			rule2_ipint = rule2_ipint << rule2_iprng;
        }else{
        	if(rule1_ipint == rule2_ipint)
        		return true;
        	else
        		return false;
        }
        // check if we have a match
        if (rule1_ipint != rule2_ipint)
            matched = false;

        return matched;
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


		if(processingflow.current_ingress_port==rulenode.in_port && rulenode.dl_type==2048 ){
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
		}
		return rulenode;
	}

}
