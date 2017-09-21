/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.flowguard.impl;


import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;


public class FlowInfo {
    public int flow_index = 0;
    public String firewall_ruldid;
    /* Do not convert this to premitive type boolean */
    public boolean is_finished = false;
    public HeaderObject ruleHO;
    public HeaderObject current_ho; //input header objects
    public HeaderObject next_ho; //output header objects
    public String next_switch_dpid; //next switch id to propagate
    public int next_ingress_port;
    public String current_switch_dpid; //next switch id to propagate
    public int current_ingress_port;
    public String rule_node_name;
    public TopologyStruct target;
    public String candidate_rule;

    public ArrayList<FlowInfo> flow_history;

    public static void printFlowInfo(FlowInfo flowinfo){
        System.out.println("<<<<< current_HeaderObject >>>>>");
        HeaderObject.printHeaderObject(flowinfo.current_ho);
        System.out.println("current_switch_info = "+flowinfo.current_switch_dpid+
                " / "+flowinfo.current_ingress_port);
        System.out.println("<<<<< next_HeaderObject >>>>>");
        HeaderObject.printHeaderObject(flowinfo.next_ho);
        System.out.println("next_switch_info = " + flowinfo.next_switch_dpid+
                " / "+flowinfo.next_ingress_port);
    }


    public static FlowInfo valueCopy(FlowInfo sample){
        /*
         * TODO The valuecopy should change. Some of the members of the FlowInfo are now objects
         * and not premitive data types. Pointing to same instance of object is not copying.
         */
        FlowInfo newflow = new FlowInfo();
        newflow.ruleHO = sample.ruleHO;
        newflow.firewall_ruldid = new String(sample.firewall_ruldid);
        newflow.flow_index = sample.flow_index;
        newflow.is_finished = sample.is_finished;
        newflow.current_ingress_port = sample.current_ingress_port;
        newflow.current_switch_dpid = new String(sample.current_switch_dpid);
        newflow.next_ingress_port = sample.next_ingress_port;
        newflow.next_switch_dpid = new String(sample.next_switch_dpid);
        newflow.rule_node_name = sample.rule_node_name;
        if(sample.candidate_rule != null)
            newflow.candidate_rule = new String(sample.candidate_rule);
        if(sample.target != null){
            newflow.target = new TopologyStruct();
            newflow.target.dpid = sample.target.dpid;
            newflow.target.port = sample.target.port;
        }
        if(sample.current_ho != null){
            newflow.current_ho = new HeaderObject();
            newflow.current_ho.dl_dst = sample.current_ho.dl_dst;
            newflow.current_ho.dl_src = sample.current_ho.dl_src;
            newflow.current_ho.dl_type = sample.current_ho.dl_type;
            newflow.current_ho.nw_dst_maskbits = sample.current_ho.nw_dst_maskbits;
            newflow.current_ho.nw_dst_prefix = sample.current_ho.nw_dst_prefix;
            newflow.current_ho.nw_src_maskbits = sample.current_ho.nw_src_maskbits;
            newflow.current_ho.nw_src_prefix = sample.current_ho.nw_src_prefix;
            newflow.current_ho.vlan = sample.current_ho.vlan;
            if(sample.current_ho.diff != null){
                newflow.current_ho.diff = new ArrayList<HeaderObject>();
                for(int i = 0; i < sample.current_ho.diff.size(); i++){
                    newflow.current_ho.diff.get(i).dl_dst = sample.current_ho.diff.get(i).dl_dst;
                    newflow.current_ho.diff.get(i).dl_src = sample.current_ho.diff.get(i).dl_src;
                    newflow.current_ho.diff.get(i).dl_type = sample.current_ho.diff.get(i).dl_type;
                    newflow.current_ho.diff.get(i).nw_dst_maskbits = sample.current_ho.diff.get(i).nw_dst_maskbits;
                    newflow.current_ho.diff.get(i).nw_dst_prefix = sample.current_ho.diff.get(i).nw_dst_prefix;
                    newflow.current_ho.diff.get(i).nw_src_maskbits = sample.current_ho.diff.get(i).nw_src_maskbits;
                    newflow.current_ho.diff.get(i).nw_src_prefix = sample.current_ho.diff.get(i).nw_src_prefix;
                }
            }
        }
        if(sample.next_ho != null){
            newflow.next_ho = new HeaderObject();
            newflow.next_ho.dl_dst = sample.next_ho.dl_dst;
            newflow.next_ho.dl_src = sample.next_ho.dl_src;
            newflow.next_ho.dl_type = sample.next_ho.dl_type;
            newflow.next_ho.nw_dst_maskbits = sample.next_ho.nw_dst_maskbits;
            newflow.next_ho.nw_dst_prefix = sample.next_ho.nw_dst_prefix;
            newflow.next_ho.nw_src_maskbits = sample.next_ho.nw_src_maskbits;
            newflow.next_ho.nw_src_prefix = sample.next_ho.nw_src_prefix;
            newflow.next_ho.vlan = sample.next_ho.vlan;
            if(sample.next_ho.diff != null){
                newflow.next_ho.diff = new ArrayList<HeaderObject>();
                for(int i = 0; i < sample.next_ho.diff.size(); i++){
                    newflow.next_ho.diff.get(i).dl_dst = sample.next_ho.diff.get(i).dl_dst;
                    newflow.next_ho.diff.get(i).dl_src = sample.next_ho.diff.get(i).dl_src;
                    newflow.next_ho.diff.get(i).dl_type = sample.next_ho.diff.get(i).dl_type;
                    newflow.next_ho.diff.get(i).nw_dst_maskbits = sample.next_ho.diff.get(i).nw_dst_maskbits;
                    newflow.next_ho.diff.get(i).nw_dst_prefix = sample.next_ho.diff.get(i).nw_dst_prefix;
                    newflow.next_ho.diff.get(i).nw_src_maskbits = sample.next_ho.diff.get(i).nw_src_maskbits;
                    newflow.next_ho.diff.get(i).nw_src_prefix = sample.next_ho.diff.get(i).nw_src_prefix;
                }
            }
        }
        if(sample.flow_history != null){
            newflow.flow_history = new ArrayList<FlowInfo>();
            for(int i = 0; i < sample.flow_history.size(); i++){
                newflow.flow_history.add(i,sample.flow_history.get(i));
            }
        }
        return newflow;
    }

    public static FlowInfo valueCopy2(FlowInfo sample){
        FlowInfo newflow = new FlowInfo();
        int k = 0;
        if(sample.flow_history != null){
            newflow.flow_history = new ArrayList<FlowInfo>();
            for(k = 0; k < sample.flow_history.size(); k++){
                newflow.flow_history.add(k,sample.flow_history.get(k));
            }
        }
        k = k-1;
        newflow.ruleHO = sample.ruleHO;
        newflow.firewall_ruldid = sample.firewall_ruldid;
        newflow.flow_index = sample.flow_history.get(k).flow_index;
        newflow.is_finished = sample.flow_history.get(k).is_finished;
        newflow.current_ingress_port = sample.flow_history.get(k).current_ingress_port;
        newflow.current_switch_dpid = sample.flow_history.get(k).current_switch_dpid;
        newflow.next_ingress_port = sample.flow_history.get(k).next_ingress_port;
        newflow.next_switch_dpid = sample.flow_history.get(k).next_switch_dpid;
        newflow.rule_node_name = sample.flow_history.get(k).rule_node_name;
        newflow.candidate_rule = sample.flow_history.get(k).candidate_rule;
        if(sample.target != null){
            newflow.target = new TopologyStruct();
            newflow.target.dpid = sample.target.dpid;
            newflow.target.port = sample.target.port;
        }
        if(sample.flow_history.get(k).current_ho != null){
            newflow.current_ho = new HeaderObject();
            newflow.current_ho.dl_dst = sample.flow_history.get(k).current_ho.dl_dst;
            newflow.current_ho.dl_src = sample.flow_history.get(k).current_ho.dl_src;
            newflow.current_ho.dl_type = sample.flow_history.get(k).current_ho.dl_type;
            newflow.current_ho.nw_dst_maskbits = sample.flow_history.get(k).current_ho.nw_dst_maskbits;
            newflow.current_ho.nw_dst_prefix = sample.flow_history.get(k).current_ho.nw_dst_prefix;
            newflow.current_ho.nw_src_maskbits = sample.flow_history.get(k).current_ho.nw_src_maskbits;
            newflow.current_ho.nw_src_prefix = sample.flow_history.get(k).current_ho.nw_src_prefix;
            newflow.current_ho.vlan = sample.flow_history.get(k).current_ho.vlan;
            if(sample.flow_history.get(k).current_ho.diff != null){
                newflow.current_ho.diff = new ArrayList<HeaderObject>();
                for(int i = 0; i < sample.flow_history.get(i).current_ho.diff.size(); i++){
                    newflow.current_ho.diff.get(i).dl_dst = sample.flow_history.get(i).current_ho.diff.get(i).dl_dst;
                    newflow.current_ho.diff.get(i).dl_src = sample.flow_history.get(i).current_ho.diff.get(i).dl_src;
                    newflow.current_ho.diff.get(i).dl_type = sample.flow_history.get(i).current_ho.diff.get(i).dl_type;
                    newflow.current_ho.diff.get(i).nw_dst_maskbits = sample.flow_history.get(i).current_ho.diff.get(i).nw_dst_maskbits;
                    newflow.current_ho.diff.get(i).nw_dst_prefix = sample.flow_history.get(i).current_ho.diff.get(i).nw_dst_prefix;
                    newflow.current_ho.diff.get(i).nw_src_maskbits = sample.flow_history.get(i).current_ho.diff.get(i).nw_src_maskbits;
                    newflow.current_ho.diff.get(i).nw_src_prefix = sample.flow_history.get(i).current_ho.diff.get(i).nw_src_prefix;
                    newflow.current_ho.diff.get(i).vlan = sample.flow_history.get(i).current_ho.diff.get(i).vlan;
                }
            }
        }
        if(sample.flow_history.get(k).next_ho != null){
            newflow.next_ho = new HeaderObject();
            newflow.next_ho.dl_dst = sample.flow_history.get(k).next_ho.dl_dst;
            newflow.next_ho.dl_src = sample.flow_history.get(k).next_ho.dl_src;
            newflow.next_ho.dl_type = sample.flow_history.get(k).next_ho.dl_type;
            newflow.next_ho.nw_dst_maskbits = sample.flow_history.get(k).next_ho.nw_dst_maskbits;
            newflow.next_ho.nw_dst_prefix = sample.flow_history.get(k).next_ho.nw_dst_prefix;
            newflow.next_ho.nw_src_maskbits = sample.flow_history.get(k).next_ho.nw_src_maskbits;
            newflow.next_ho.nw_src_prefix = sample.flow_history.get(k).next_ho.nw_src_prefix;
            newflow.next_ho.vlan = sample.flow_history.get(k).next_ho.vlan;
            if(sample.flow_history.get(k).next_ho.diff != null){
                newflow.next_ho.diff = new ArrayList<HeaderObject>();
                for(int i = 0; i < sample.flow_history.get(i).next_ho.diff.size(); i++){
                    newflow.next_ho.diff.get(i).dl_dst = sample.flow_history.get(i).next_ho.diff.get(i).dl_dst;
                    newflow.next_ho.diff.get(i).dl_src = sample.flow_history.get(i).next_ho.diff.get(i).dl_src;
                    newflow.next_ho.diff.get(i).dl_type = sample.flow_history.get(i).next_ho.diff.get(i).dl_type;
                    newflow.next_ho.diff.get(i).nw_dst_maskbits = sample.flow_history.get(i).next_ho.diff.get(i).nw_dst_maskbits;
                    newflow.next_ho.diff.get(i).nw_dst_prefix = sample.flow_history.get(i).next_ho.diff.get(i).nw_dst_prefix;
                    newflow.next_ho.diff.get(i).nw_src_maskbits = sample.flow_history.get(i).next_ho.diff.get(i).nw_src_maskbits;
                    newflow.next_ho.diff.get(i).nw_src_prefix = sample.flow_history.get(i).next_ho.diff.get(i).nw_src_prefix;
                    newflow.next_ho.diff.get(i).vlan = sample.flow_history.get(i).next_ho.diff.get(i).vlan;
                }
            }
        }

        return newflow;
    }

}