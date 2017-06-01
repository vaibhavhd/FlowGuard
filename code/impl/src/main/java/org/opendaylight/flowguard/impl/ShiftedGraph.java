/*
 * Copyright © 2017 Vaibhav and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.flowguard.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionStripVirtualLan;
import org.openflow.protocol.action.OFActionType;
import org.openflow.protocol.action.OFActionVirtualLanIdentifier;
import org.openflow.util.HexString;

import org.slf4j.Logger;
import org.restlet.resource.Post;
import org.restlet.resource.Resource;
import org.restlet.resource.ServerResource;

/*
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;


import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.storage.IResultSet;
import net.floodlightcontroller.storage.IStorageSourceService;
import net.floodlightcontroller.storage.StorageException;
import net.floodlightcontroller.staticflowentry.*;
*/

import org.opendaylight.flowguard.impl.FirewallRule.FirewallAction;
import org.opendaylight.flowguard.packet.Ethernet;
import org.opendaylight.flowguard.packet.IPv4;


public class ShiftedGraph {
    // Map<DPID, Map<Name, FlowMod>>; FlowMod can be null to indicate non-active
    public Map<String, Map<String, OFFlowMod>> entriesFromStorage;
    public Map<String, List<RuleNode>> rulenodes;
    public Map<TopologyStruct, TopologyStruct> TopologyStorage;
    public Map<String, Map<FlowInfo, TopologyStruct>> SourceProbeNodeStorage;
    public List<FlowInfo> flowstorage;
    public int current_flow_index = 0;
    //public StaticFlowEntryPusher static_pusher;
    public Firewall firewall;
    // Entry Name -> DPID of Switch it's on
    protected boolean AUTOPORTFAST_DEFAULT = false;
    protected boolean autoPortFastFeature = AUTOPORTFAST_DEFAULT;
    //protected IResultSet topologyResult;
    public String RESULT_PATH="/home/whan7/SDN-test/result.txt";
    public int resolution_index = 0;
    public int resolution_method = 0;

    // service modules needed
    //protected IFloodlightProviderService floodlightProvider;
    //protected IStorageSourceService storageSource;
    //protected IDeviceService deviceSource;
    //protected IRestApiService restApi;
    protected static Logger logger;

    protected List<FirewallRule> rules; // protected by synchronized
    protected boolean enabled;
    protected int subnet_mask = IPv4.toIPv4Address("255.255.255.0");

    // constant strings for storage/parsing

    public static final String TOPOLOGY_TABLE_NAME = "controller_topologyconfig";
    public static final String TOPOLOGY_ID = "id";
    public static final String TOPOLOGY_AUTOPORTFAST = "autoportfast";

    public static final String LINK_TABLE_NAME = "controller_link";
    public static final String LINK_ID = "id";
    public static final String LINK_SRC_SWITCH = "src_switch_id";
    public static final String LINK_SRC_PORT = "src_port";
    public static final String LINK_SRC_PORT_STATE = "src_port_state";
    public static final String LINK_DST_SWITCH = "dst_switch_id";
    public static final String LINK_DST_PORT = "dst_port";
    public static final String LINK_DST_PORT_STATE = "dst_port_state";
    public static final String LINK_VALID_TIME = "valid_time";
    public static final String LINK_TYPE = "link_type";
    public static final String SWITCH_CONFIG_TABLE_NAME = "controller_switchconfig";
    public static final String SWITCH_CONFIG_CORE_SWITCH = "core_switch";
    public static String[] Topology_Columns = {
        TOPOLOGY_ID, TOPOLOGY_AUTOPORTFAST, LINK_TABLE_NAME, LINK_ID, LINK_SRC_SWITCH,
        LINK_SRC_PORT, LINK_SRC_PORT_STATE, LINK_DST_SWITCH, LINK_DST_PORT,
        LINK_DST_PORT_STATE, LINK_VALID_TIME, LINK_TYPE, SWITCH_CONFIG_TABLE_NAME,
        SWITCH_CONFIG_CORE_SWITCH
    };

    public static final String TABLE_NAME = "controller_firewallrules";
    public static final String COLUMN_RULEID = "ruleid";
    public static final String COLUMN_DPID = "dpid";
    public static final String COLUMN_IN_PORT = "in_port";
    public static final String COLUMN_DL_SRC = "dl_src";
    public static final String COLUMN_DL_DST = "dl_dst";
    public static final String COLUMN_DL_TYPE = "dl_type";
    public static final String COLUMN_NW_SRC_PREFIX = "nw_src_prefix";
    public static final String COLUMN_NW_SRC_MASKBITS = "nw_src_maskbits";
    public static final String COLUMN_NW_DST_PREFIX = "nw_dst_prefix";
    public static final String COLUMN_NW_DST_MASKBITS = "nw_dst_maskbits";
    public static final String COLUMN_NW_PROTO = "nw_proto";
    public static final String COLUMN_TP_SRC = "tp_src";
    public static final String COLUMN_TP_DST = "tp_dst";
    public static final String COLUMN_WILDCARD_DPID = "wildcard_dpid";
    public static final String COLUMN_WILDCARD_IN_PORT = "wildcard_in_port";
    public static final String COLUMN_WILDCARD_DL_SRC = "wildcard_dl_src";
    public static final String COLUMN_WILDCARD_DL_DST = "wildcard_dl_dst";
    public static final String COLUMN_WILDCARD_DL_TYPE = "wildcard_dl_type";
    public static final String COLUMN_WILDCARD_NW_SRC = "wildcard_nw_src";
    public static final String COLUMN_WILDCARD_NW_DST = "wildcard_nw_dst";
    public static final String COLUMN_WILDCARD_NW_PROTO = "wildcard_nw_proto";
    public static final String COLUMN_WILDCARD_TP_SRC = "wildcard_tp_src";
    public static final String COLUMN_WILDCARD_TP_DST = "wildcard_tp_dst";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_ACTION = "action";
    public static String[] ColumnNames = { COLUMN_RULEID, COLUMN_DPID,
            COLUMN_IN_PORT, COLUMN_DL_SRC, COLUMN_DL_DST, COLUMN_DL_TYPE,
            COLUMN_NW_SRC_PREFIX, COLUMN_NW_SRC_MASKBITS, COLUMN_NW_DST_PREFIX,
            COLUMN_NW_DST_MASKBITS, COLUMN_NW_PROTO, COLUMN_TP_SRC,
            COLUMN_TP_DST, COLUMN_WILDCARD_DPID, COLUMN_WILDCARD_IN_PORT,
            COLUMN_WILDCARD_DL_SRC, COLUMN_WILDCARD_DL_DST,
            COLUMN_WILDCARD_DL_TYPE, COLUMN_WILDCARD_NW_SRC,
            COLUMN_WILDCARD_NW_DST, COLUMN_WILDCARD_NW_PROTO, COLUMN_PRIORITY,
            COLUMN_ACTION };

    public static final String STATICENTRY_TABLE_NAME = "controller_staticflowtableentry";
    public static final String STATICENTRY_COLUMN_NAME = "name";
    public static final String STATICENTRY_COLUMN_SWITCH = "switch_id";
    public static final String STATICENTRY_COLUMN_ACTIVE = "active";
    public static final String STATICENTRY_COLUMN_IDLE_TIMEOUT = "idle_timeout";
    public static final String STATICENTRY_COLUMN_HARD_TIMEOUT = "hard_timeout";
    public static final String STATICENTRY_COLUMN_PRIORITY = "priority";
    public static final String STATICENTRY_COLUMN_COOKIE = "cookie";
    public static final String STATICENTRY_COLUMN_WILDCARD = "wildcards";
    public static final String STATICENTRY_COLUMN_IN_PORT = "in_port";
    public static final String STATICENTRY_COLUMN_DL_SRC = "dl_src";
    public static final String STATICENTRY_COLUMN_DL_DST = "dl_dst";
    public static final String STATICENTRY_COLUMN_DL_VLAN = "dl_vlan";
    public static final String STATICENTRY_COLUMN_DL_VLAN_PCP = "dl_vlan_pcp";
    public static final String STATICENTRY_COLUMN_DL_TYPE = "dl_type";
    public static final String STATICENTRY_COLUMN_NW_TOS = "nw_tos";
    public static final String STATICENTRY_COLUMN_NW_PROTO = "nw_proto";
    public static final String STATICENTRY_COLUMN_NW_SRC = "nw_src"; // includes CIDR-style
                                                        // netmask, e.g.
                                                        // "128.8.128.0/24"
    public static final String STATICENTRY_COLUMN_NW_DST = "nw_dst";
    public static final String STATICENTRY_COLUMN_TP_DST = "tp_dst";
    public static final String STATICENTRY_COLUMN_TP_SRC = "tp_src";
    public static final String STATICENTRY_COLUMN_ACTIONS = "actions";
    public static String[] STATICENTRY_ColumnNames = { STATICENTRY_COLUMN_NAME, STATICENTRY_COLUMN_SWITCH,
        STATICENTRY_COLUMN_ACTIVE, STATICENTRY_COLUMN_IDLE_TIMEOUT, STATICENTRY_COLUMN_HARD_TIMEOUT,
        STATICENTRY_COLUMN_PRIORITY, STATICENTRY_COLUMN_COOKIE, STATICENTRY_COLUMN_WILDCARD, STATICENTRY_COLUMN_IN_PORT,
        STATICENTRY_COLUMN_DL_SRC, STATICENTRY_COLUMN_DL_DST, STATICENTRY_COLUMN_DL_VLAN, STATICENTRY_COLUMN_DL_VLAN_PCP,
        STATICENTRY_COLUMN_DL_TYPE, STATICENTRY_COLUMN_NW_TOS, STATICENTRY_COLUMN_NW_PROTO, STATICENTRY_COLUMN_NW_SRC,
        STATICENTRY_COLUMN_NW_DST, STATICENTRY_COLUMN_TP_DST, STATICENTRY_COLUMN_TP_SRC, STATICENTRY_COLUMN_ACTIONS };



    /*public void initialize(IFloodlightProviderService floodlightProvider,IStorageSourceService storageSource,
            IDeviceService deviceSource, Firewall firewall){
        this.floodlightProvider = floodlightProvider;
        this.storageSource = storageSource;
        //this.storageSource.deleteRowAsync(STATICENTRY_TABLE_NAME, "flow1");
        this.deviceSource = deviceSource;
        this.firewall = firewall;
    }*/

    /**
     * Used for replicating the topology of switches.
     * The topology is a map of src-dest (key-val) pair.
     * @param topo
     */
    public void buildTopology(Map<TopologyStruct, TopologyStruct> topo){
        /* Clear the local topology before building one */
        if(this.TopologyStorage != null){
            this.TopologyStorage.clear();
        } else {
            this.TopologyStorage = new ConcurrentHashMap<TopologyStruct, TopologyStruct>();
        }

        for(TopologyStruct key : topo.keySet()){
            if(key==null || topo.get(key) == null) {
                break;
            }
            TopologyStruct dst = new TopologyStruct();
            dst.dpid = topo.get(key).dpid;
            dst.port = topo.get(key).port;
            this.TopologyStorage.put(key, dst);
            topo.remove(key);
        }

    }

    public void buildRuleNode(Map<String, Map<String, OFFlowMod>> entries){
        // TODO EnteriesFromStorage is not used or referenced. Remove it.
        if(this.entriesFromStorage != null){
            this.entriesFromStorage.clear();
        } else {
            this.entriesFromStorage = new ConcurrentHashMap<String, Map<String, OFFlowMod>>();
        }
        if(this.rulenodes != null){
            this.rulenodes.clear();
        } else {
            this.rulenodes = new ConcurrentHashMap<String, List<RuleNode>>();
        }

        long start = System.nanoTime();
        for(String key : entries.keySet()){
            Map<String, OFFlowMod> row;
            if(entries.get(key) == null) {
                break;
            }
            row = entries.get(key);
            this.entriesFromStorage.put(key, row);
            RuleNode rn = new RuleNode();
            this.rulenodes.put(key, rn.addruletable(row));
            entries.remove(key);
        }

        long end = System.nanoTime();
        try {
            File f = new File(RESULT_PATH);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("buildRuleNode time(micro seconds) : " + ( end - start )/1000 +"\n");
            bw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public HeaderObject computeInverseFlow(FlowInfo flowinfo){
        HeaderObject ho = new HeaderObject();
        ho.nw_dst_maskbits = flowinfo.next_ho.nw_dst_maskbits;
        ho.nw_dst_prefix = flowinfo.next_ho.nw_dst_prefix;
        ho.nw_src_maskbits = flowinfo.next_ho.nw_src_maskbits;
        ho.nw_src_prefix = flowinfo.next_ho.nw_src_prefix;
        for(int i = flowinfo.flow_history.size()-1; i > 0; i--){
            String rulename = flowinfo.flow_history.get(i).rule_node_name;
            String dpid = flowinfo.flow_history.get(i).current_switch_dpid;
            RuleNode rn = new RuleNode();
            Set<String> set = this.rulenodes.keySet();
            Iterator<String> itr = set.iterator();
            while(itr.hasNext()){
                Object key = itr.next();
                if(dpid.equals((String) key)){
                    List<RuleNode> ruletable = this.rulenodes.get((String) key);
                    for(int j = 0; j < ruletable.size(); j++){
                        if(rulename.equals(ruletable.get(j).rule_name)){
                            rn = ruletable.get(j);
                            break;
                        }
                    }
                }
            }
            if(rn.nw_dst_maskbits == 32){
                ho.nw_dst_prefix = rn.nw_dst_prefix;
            }else{
                int rule_iprng = 32 - rn.nw_dst_maskbits;
                int rule_ipint = rn.nw_dst_prefix >> rule_iprng;
                rule_ipint = rule_ipint << rule_iprng;

                int flow_ipint = ho.nw_dst_prefix >> rule_iprng;
                flow_ipint = flow_ipint << rule_iprng;
                ho.nw_dst_prefix = rule_ipint + (ho.nw_dst_prefix - flow_ipint);
            }
            if(rn.nw_src_maskbits == 32){
                ho.nw_src_prefix = rn.nw_src_prefix;
            }else{
                int rule_iprng = 32 - rn.nw_src_maskbits;
                int rule_ipint = rn.nw_src_prefix >> rule_iprng;
                rule_ipint = rule_ipint << rule_iprng;

                int flow_ipint = ho.nw_src_prefix >> rule_iprng;
                flow_ipint = flow_ipint << rule_iprng;
                ho.nw_src_prefix = rule_ipint + (ho.nw_src_prefix - flow_ipint);
            }
        }
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("<<< Inverse Flow Computation >>>");
        HeaderObject.printHeaderObject(ho);
        System.out.println("***********************************************************************************");
        //this is method 3(entire violation) : flow removing in the case of entire violation
        if(this.checkflowremoving(flowinfo, ho) == false){
            this.flowtagging(flowinfo);

            if(flowinfo.candidate_rule != null && false){
                System.out.println("S2-Update Rejecting applied!!");
                this.resolution_method = 2;
                Set<String> set2 = this.rulenodes.keySet();
                Iterator<String> itr2 = set2.iterator();
                while(itr2.hasNext()){
                    Object key = itr2.next();
                    List<RuleNode> ruletable = this.rulenodes.get(key.toString());
                    int size = ruletable.size();
                    for(int j = 0; j < size; j++){
                        if(flowinfo.candidate_rule.equals(ruletable.get(j).rule_name)){
                            if(ho.nw_src_maskbits == 32 && ho.nw_dst_maskbits == 32){
                                this.rulenodes.get(key.toString()).remove(j);
                                // TODO this.storageSource.deleteRowAsync(STATICENTRY_TABLE_NAME, flowinfo.candidate_rule);
                                return ho;
                            }else if(ho.nw_src_maskbits == ruletable.get(j).nw_src_maskbits && ho.nw_dst_maskbits == ruletable.get(j).nw_dst_maskbits){
                                this.rulenodes.get(key.toString()).remove(j);
                                //TODO this.storageSource.deleteRowAsync(STATICENTRY_TABLE_NAME, flowinfo.candidate_rule);
                                return ho;
                            }
                            break;
                        }
                    }
                }
            }

            System.out.println("S4-Packet Blocking applied!!");
            if(this.resolution_method == 1){
                this.resolution_method = 5;
            }else{
                this.resolution_method = 4;
            }
            //egress switch block
            this.addFlowEntry(flowinfo.flow_history.get(flowinfo.flow_history.size()-1).current_ho,
                    flowinfo.flow_history.get(flowinfo.flow_history.size()-1).current_switch_dpid,
                    flowinfo.flow_history.get(flowinfo.flow_history.size()-1).current_ingress_port);
            //ingress switch block
            this.addFlowEntry(ho, flowinfo.flow_history.get(1).current_switch_dpid, flowinfo.flow_history.get(1).current_ingress_port);
            //this is method 4(partial violation) : add firewall rule and add new flow entry for packet bloacking
            this.addFirewallRule(ho, flowinfo.flow_history.get(1).current_switch_dpid, flowinfo.flow_history.get(1).current_ingress_port);
        }
        return ho;
    }
    public boolean checkflowremoving(FlowInfo flowinfo, HeaderObject ho){
        FirewallRule frule = new FirewallRule();
        if(this.firewall.rules != null){
            for(int i=0;i<this.firewall.rules.size(); i++){
                if(this.firewall.rules.get(i).ruleid == Integer.parseInt(flowinfo.firewall_ruldid)){
                    frule = this.firewall.rules.get(i);
                }
            }
        }

        if(RuleNode.matchIPAddress(frule.nw_dst_prefix, frule.nw_dst_maskbits, flowinfo.next_ho.nw_dst_prefix, flowinfo.next_ho.nw_dst_maskbits)){
            //for example : substring of rulenode looks like 'flow1', 'flow2' and so on
            String keyword = flowinfo.rule_node_name.substring(0, 5);
            for(int i=1;i < flowinfo.flow_history.size();i++){
                if(keyword.equals(flowinfo.flow_history.get(i).rule_node_name.substring(0,5))){
                    continue;
                }else{
                    return false;
                }
            }
            //flow removing should be considered
            System.out.println("S3-Flow Removing applied!!");
            this.resolution_method = 3;
            Set<String> set = this.rulenodes.keySet();
            Iterator<String> itr = set.iterator();
            while(itr.hasNext()){
                Object key = itr.next();
                List<RuleNode> ruletable = this.rulenodes.get(key.toString());
                int size = ruletable.size();
                for(int i = size-1; i >= 0; i--){
                    if(keyword.equals(ruletable.get(i).rule_name.substring(0,5))){
                        // TODO this.storageSource.deleteRowAsync(STATICENTRY_TABLE_NAME, ruletable.get(i).rule_name);
                    }
                }
            }
            return true;
        }
        return false;
    }
    public void flowtagging(FlowInfo flowinfo){
        if(flowinfo.flow_history == null) {
            return;
        }
        String keyword = "ftag1";
        Map<String, Map<String, OFFlowMod>> entries = new ConcurrentHashMap<String, Map<String, OFFlowMod>>();
       /* try {
            Map<String, Object> row;
            // null1=no predicate, null2=no ordering
            IResultSet resultSet = storageSource.executeQuery(STATICENTRY_TABLE_NAME,
                    STATICENTRY_ColumnNames, null, null);
            for (Iterator<IResultSet> it = resultSet.iterator(); it.hasNext();) {
                row = it.next().getRow();
                this.firewall.parseRow(row, entries);
            }
        } catch (StorageException e) {
            logger.error("failed to access storage: {}", e.getMessage());
            // if the table doesn't exist, then wait to populate later via
            // setStorageSource()
        }*/

        boolean set_vlan = false;
        for(int i=0; i < flowinfo.flow_history.size();i++){
            String rulenode = flowinfo.flow_history.get(i).rule_node_name.substring(0, 5);
            if(keyword.equals(rulenode)){
                set_vlan = true;
                break;
            }
        }

        if(set_vlan == true){
            //in this case, set_vlan_id actions which contain 'flow1' keyword
            String keyword2 = "egress";
            String keyword3 = "ingress";
            Set<String> set = this.rulenodes.keySet();
            Iterator<String> itr = set.iterator();
            while(itr.hasNext()){
                Object key = itr.next();
                List<RuleNode> ruletable = this.rulenodes.get(key.toString());
                int size = ruletable.size();
                for(int j = 0; j < size; j++){
                    if(keyword.equals(ruletable.get(j).rule_name.substring(0,5))&&ruletable.get(j).dl_type==Ethernet.TYPE_IPv4){
                        String rulename = ruletable.get(j).rule_name;
                        if(ruletable.get(j).rule_name.length()>=13 && keyword3.equals(ruletable.get(j).rule_name.substring(6,13))){
                            this.setVlan(entries, key.toString(),rulename, 1);
                        }else if(ruletable.get(j).rule_name.length()>=12 && keyword2.equals(ruletable.get(j).rule_name.substring(6,12))){
                            this.setVlan(entries, key.toString(),rulename, 2);
                        }else{
                            this.setVlan(entries, key.toString(),rulename, 3);
                        }
                        this.rulenodes.get(key.toString()).remove(j+1);
                    }
                }
            }
        }
        /*
        else{
            for(int i = 1; i < flowinfo.flow_history.size(); i++){
                String rulename = flowinfo.flow_history.get(i).rule_node_name;
                String dpid = flowinfo.flow_history.get(i).current_switch_dpid;
                OFFlowMod flowmod = new OFFlowMod();
                try {
                    flowmod = entries.get(dpid).get(rulename).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                flowmod.setFlags((short)(this.current_flow_index*100));
                this.storageSource.deleteRowAsync(STATICENTRY_TABLE_NAME, rulename);
                Map<String, Object> fmMap = StaticFlowEntries.flowModToStorageEntry(flowmod, dpid, rulename);
                this.storageSource.insertRowAsync(STATICENTRY_TABLE_NAME, fmMap);
            }
        }
        */
    }

    public void setVlan(Map<String, Map<String, OFFlowMod>> entries, String dpid, String rulename, int strip){
        System.out.println("S1-Dependency Breaking applied!!");
        this.resolution_method = 1;
        OFFlowMod flowmod = new OFFlowMod();
        try {
            flowmod = entries.get(dpid).get(rulename).clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<OFAction> actions = new LinkedList<OFAction>();
        actions = flowmod.getActions();
        Iterator itr2 = actions.iterator();
        boolean has_vlan_value = false;
        while(itr2.hasNext()){
            OFAction action = (OFAction)itr2.next();
            if(action.getType() == OFActionType.SET_VLAN_VID){  /* TODO Prev it was SET_VLAN_ID */
                has_vlan_value = true;
                break;
            }
        }
        if(has_vlan_value){

        }else{
            if(strip == 1){
                OFActionVirtualLanIdentifier action = new OFActionVirtualLanIdentifier();
                action.setVirtualLanIdentifier((short)(this.current_flow_index*100));
                actions.add(action);
                flowmod.setActions(actions);
                //Map<String, Object> fmMap = StaticFlowEntries.flowModToStorageEntry(flowmod, dpid, rulename);
                // TODO this.storageSource.insertRowAsync(STATICENTRY_TABLE_NAME, fmMap);
            }else if(strip == 2){
                OFActionStripVirtualLan action = new OFActionStripVirtualLan();
                actions.add(action);
                flowmod.setActions(actions);
                //Map<String, Object> fmMap = StaticFlowEntries.flowModToStorageEntry(flowmod, dpid, rulename);
                // TODO this.storageSource.insertRowAsync(STATICENTRY_TABLE_NAME, fmMap);
            }else if(strip ==3){
                OFMatch match = flowmod.getMatch();
                match.setDataLayerVirtualLan((short)(this.current_flow_index*100));
                flowmod.setMatch(match);
                //Map<String, Object> fmMap = StaticFlowEntries.flowModToStorageEntry(flowmod, dpid, rulename);
                // TODO this.storageSource.insertRowAsync(STATICENTRY_TABLE_NAME, fmMap);
            }
        }

    }


    public void addFirewallRule(HeaderObject ho, String dpid, short port){
        FirewallRule rule = new FirewallRule();
        rule.ruleid = rule.genID();
        rule.priority = 32768;
        rule.dpid = dpid;
        rule.wildcard_dpid = false;
        rule.in_port = port;
        rule.wildcard_in_port = false;
        rule.wildcard_nw_src = false;
        rule.wildcard_dl_type = false;
        rule.dl_type = Ethernet.TYPE_IPv4;
        rule.nw_src_prefix = ho.nw_src_prefix;
        rule.nw_src_maskbits = ho.nw_src_maskbits;
        rule.wildcard_nw_dst = false;
        rule.wildcard_dl_type = false;
        rule.dl_type = Ethernet.TYPE_IPv4;
        rule.nw_dst_prefix = ho.nw_dst_prefix;
        rule.nw_dst_maskbits = ho.nw_dst_maskbits;
        rule.action = FirewallRule.FirewallAction.DENY;
        this.firewall.addRule(rule);
    }

    public void addFlowEntry(HeaderObject ho, String dpid, short port){
        Map<String, Object> entry = new HashMap<String, Object>();
        String rulename = "resolution"+Integer.toString(this.resolution_index);
        this.resolution_index++;
        /* TODO Push flow entry.put(StaticFlowEntryPusher.COLUMN_NAME, rulename);
        entry.put(StaticFlowEntryPusher.COLUMN_SWITCH, dpid);
        entry.put(StaticFlowEntryPusher.COLUMN_ACTIVE, Boolean.toString(true));
        entry.put(StaticFlowEntryPusher.COLUMN_PRIORITY, "32767");
        entry.put(StaticFlowEntryPusher.COLUMN_NW_DST, IPv4.fromIPv4Address(ho.nw_dst_prefix)+"/"+Integer.toString(ho.nw_dst_maskbits));
        entry.put(StaticFlowEntryPusher.COLUMN_NW_SRC, IPv4.fromIPv4Address(ho.nw_src_prefix)+"/"+Integer.toString(ho.nw_src_maskbits));
        entry.put(StaticFlowEntryPusher.COLUMN_DL_TYPE, Short.toString(Ethernet.TYPE_IPv4));
        entry.put(StaticFlowEntryPusher.COLUMN_IN_PORT, Short.toString(port));
        entry.put(StaticFlowEntryPusher.COLUMN_ACTIONS, "output= "); */
        /*
        OFFlowMod flowmod = new OFFlowMod();
        List<OFAction> actions = new LinkedList<OFAction>();
        OFActionOutput action = new OFActionOutput();
        action.setMaxLength((short) Short.MAX_VALUE);
        short output_port = OFPort.OFPP_NONE.getValue();
        actions.add(action);
        flowmod.setActions(actions);

        this.resolution_index++;
        Map<String, Object> fmMap = StaticFlowEntries.flowModToStorageEntry(flowmod, dpid, rulename);
        */
        //this.storageSource.insertRowAsync(STATICENTRY_TABLE_NAME, entry);

    }

    public void printFlowInfo(FlowInfo flowinfo, boolean inverseflow){

        FlowInfo.printFlowInfo(flowinfo);
        Iterator<FlowInfo> itr = flowinfo.flow_history.iterator();
        System.out.println("*************** This is "+Integer.toString(flowinfo.flow_index)+" th flow ***************");
        System.out.println("*************** Firewall RuleID : "+flowinfo.firewall_ruldid+" ***************");
        int i = 0;
        while(itr.hasNext()){
            FlowInfo fi = itr.next();
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("(((flow_history : this is "+Integer.toString(i)+" th visits.)))");
            System.out.println("Applied RuleNode Name : "+fi.rule_node_name);
            FlowInfo.printFlowInfo(fi);
            System.out.println("-----------------------------------------------------------------------------------");
            i++;
        }
        if(inverseflow){
            HeaderObject ho = new HeaderObject();
            ho = this.computeInverseFlow(flowinfo);
        }
        if(this.flowstorage == null) {
            this.flowstorage = new ArrayList<FlowInfo>();
        }
        int counter = 0;
        for(i = 0; i < this.flowstorage.size(); i++){
            FlowInfo fi = this.flowstorage.get(i);
            if(fi.flow_index == flowinfo.flow_index){
                this.flowstorage.remove(i);
                this.flowstorage.add(flowinfo);
                break;
            }else{
                counter++;
            }
            if(counter == this.flowstorage.size()){
                this.flowstorage.add(flowinfo);
                break;
            }
        }
        if(this.flowstorage.size()==0){
            this.flowstorage.add(flowinfo);
        }

    }

    /*
     * Take the sample "flowinfo" and propagate the flow in the network
     * to the "target" switch.
     */
    public void propagateFlow(FlowInfo flowinfo, TopologyStruct target, int index){

        FlowInfo sample = flowinfo;
        String targetdpid = target.dpid;
        short targetport = Short.parseShort(target.port);

        while(true){
            String SWITCHDPID = sample.next_switch_dpid;
            if(sample.next_switch_dpid.equals(SWITCHDPID.toString())){
                /* Get all the flow rules present in the next switch */
                List<RuleNode> ruletable = this.rulenodes.get(SWITCHDPID);
                int i = 0;
                int table_size = 0;
                if(ruletable != null){
                    table_size = ruletable.size();
                    /* The switch has no flows. Stop the propagation */
                    if(table_size == 0) {
                        return;
                    }
                }
                else if(sample.next_switch_dpid.equals(targetdpid)){
                    /* After propagation,if the final dpid is same as target, the sample packet has been reached. */
                    System.out.println("Flows are reached to the Destination!!!");
                    this.printFlowInfo(sample, true);
                    return;
                }else{
                    System.out.println("Flows are unreachable!!!");
                    this.printFlowInfo(sample, false);
                    return;
                }

                if(sample.is_finished == true){
                    System.out.println("Flows are stopped at " + sample.rule_node_name + " !!!");
                    this.printFlowInfo(sample, false);
                    return;
                }
                int counter = 0;
                if(index == 0){
                    for(i = index; i < table_size; i++){
                        if(sample.next_switch_dpid.equals(SWITCHDPID.toString()) && sample.next_ingress_port == ruletable.get(i).in_port
                                && ruletable.get(i).dl_type==2048 && ruletable.get(i).active == true) {
                            counter++;
                        }
                        if(counter >= 2
                                && sample.next_ho.vlan == ruletable.get(i).vlan
                                && RuleNode.matchIPAddress(sample.next_ho.nw_dst_prefix, sample.next_ho.nw_dst_maskbits, ruletable.get(i).nw_dst_prefix, ruletable.get(i).nw_dst_maskbits)
                                && RuleNode.matchIPAddress(sample.next_ho.nw_src_prefix, sample.next_ho.nw_src_maskbits, ruletable.get(i).nw_src_prefix, ruletable.get(i).nw_src_maskbits)){
                                FlowInfo sample_clone = new FlowInfo();
                                sample_clone = FlowInfo.valueCopy(sample);
                                this.current_flow_index++;
                                sample_clone.flow_index = this.current_flow_index;
                                this.propagateFlow(sample_clone, target, i);
                        }
                    }
                }

                int unmatch_count = 0;
                for(i = index; i < table_size;i++){
                    if(sample.next_switch_dpid.equals(SWITCHDPID.toString()) && sample.next_ingress_port == ruletable.get(i).in_port ){
                        RuleNode rulenode = ruletable.get(i);
                        if(rulenode.dl_type==2054){
                            unmatch_count++;
                            continue;
                        }
                        else if(rulenode.dl_type==2048 && rulenode.active == true){
                            rulenode = RuleNode.computeFlow(rulenode, sample);
                            if(rulenode.flow_info.flow_history == null) {
                                return;
                            }
                        } else {
                            System.out.println("THIS DATA LINK TYPE IS NOT SUPPORTED!!!");
                        }

                        this.rulenodes.get(SWITCHDPID).remove(i);
                        if(i == 0){
                            this.rulenodes.get(SWITCHDPID).add(rulenode);
                        }else{
                            this.rulenodes.get(SWITCHDPID).add(i, rulenode);
                        }
                        sample = rulenode.flow_info;

                        if(sample.next_switch_dpid.equals(targetdpid) && sample.next_ingress_port==targetport){
                            //normal execution
                            System.out.println("Flows are reached to the Destination!!!");
                            this.printFlowInfo(sample, true);
                            return;
                        }
                        sample = this.findNextConnection(sample);
                        break;

                    }else{
                        unmatch_count++;
                        if(table_size == unmatch_count){
                            if(sample.next_switch_dpid.equals(targetdpid)){
                                //normal execution
                                System.out.println("Flows are reached to the Destination!!!");
                                this.printFlowInfo(sample, true);
                                return;
                            }
                            System.out.println("Flows are unreachable!!!");
                            this.printFlowInfo(sample, false);
                            return;
                        }
                        continue;
                    }
                }

            }
            index = 0;
            //end of while
        }
    }

    public FlowInfo findNextConnection(FlowInfo flowinfo){
        Set<TopologyStruct> st = this.TopologyStorage.keySet();
        Iterator<TopologyStruct> iter = st.iterator();
        while (iter.hasNext())
        {
            TopologyStruct t = iter.next();
            if(t.dpid.equals(flowinfo.next_switch_dpid) && t.port.equals(Short.toString(flowinfo.next_ingress_port))){
                TopologyStruct t2 = this.TopologyStorage.get(t);
                flowinfo.next_switch_dpid = t2.dpid;
                flowinfo.next_ingress_port = Short.parseShort(t2.port);
                //System.out.println(t.dpid + " / " + t.port + " <--> " +t2.dpid + "/ " + t2.port);
                break;
            }
        }
        return flowinfo;
    }

    public Map<String, Map<String, RuleNode>> updateRuleNode(Map<String, Map<String, RuleNode>> rulenodes, RuleNode rulenode){
        return rulenodes;
    }

/* TODO This is done for "Partitioning FIREWALL Authorization space
 * This is mentioned in the Algorithm1 on paper.
 */
    public void buildSourceProbeNode(List<FirewallRule> rules){

        if(this.SourceProbeNodeStorage != null){
            this.SourceProbeNodeStorage.clear();
        } else {
            this.SourceProbeNodeStorage = new ConcurrentHashMap<String, Map<FlowInfo, TopologyStruct>>();
        }
        List<FirewallRule> firewall_rules = rules;
        //set and iterator to make sourcenode and probenode.
        int k = firewall_rules.size();
        for(int i = 0;i<k;i++){
            if(firewall_rules.get(i).action == FirewallAction.DENY) { //&& firewall_rules.get(i).dpid == -1){
                TopologyStruct source = this.findDpidPort(firewall_rules.get(i).nw_src_prefix);
                TopologyStruct probe = this.findDpidPort(firewall_rules.get(i).nw_dst_prefix);

                FlowInfo sample = new FlowInfo();
                sample.firewall_ruldid = Integer.toString(firewall_rules.get(i).ruleid);
                this.current_flow_index++;
                sample.flow_index = this.current_flow_index;
                sample.rule_node_name = "SourceNode";
                sample.current_ho = new HeaderObject();
                sample.current_ho.nw_dst_prefix = 167772160;
                sample.current_ho.nw_dst_maskbits = 8;
                sample.current_ho.nw_src_prefix = 167772160;
                sample.current_ho.nw_src_maskbits = 8;
                sample.current_switch_dpid = source.dpid;
                sample.current_ingress_port = Short.parseShort(source.port);
                sample.next_switch_dpid = source.dpid;
                sample.next_ingress_port = Short.parseShort(source.port);
                sample.next_ho = new HeaderObject();
                sample.next_ho.nw_dst_prefix = 167772160;
                sample.next_ho.nw_dst_maskbits = 8;
                sample.next_ho.nw_src_prefix = 167772160;
                sample.next_ho.nw_src_maskbits = 8;
                sample.target = new TopologyStruct();
                sample.target.dpid = probe.dpid;
                sample.target.port = probe.port;
                sample.flow_history = new ArrayList<FlowInfo>();
                sample.flow_history.add(sample);
                Map<FlowInfo, TopologyStruct> value = new ConcurrentHashMap<FlowInfo, TopologyStruct>();
                value.put(sample, probe);
                this.SourceProbeNodeStorage.put(Integer.toString(firewall_rules.get(i).ruleid), value);
                long start = System.nanoTime();

                this.propagateFlow(sample, probe, 0);

                long end = System.nanoTime();
                try {
                    File f = new File(this.RESULT_PATH);
                    FileWriter fw = new FileWriter(f, true);
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.write("buildSourceProbeNode time(micro seconds) : " + ( end - start )/1000 +"\n");
                    bw.flush();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                continue;
            }
        }

    }
    public void buildSourceProbeNode(FirewallRule rule){
        if(this.SourceProbeNodeStorage == null){
            this.SourceProbeNodeStorage = new ConcurrentHashMap<String, Map<FlowInfo, TopologyStruct>>();
        }

        if(rule.action == FirewallAction.DENY) { // && rule.dpid == -1){
            /* Get a source switch node */
            TopologyStruct source = this.findDpidPort(rule.nw_src_prefix);
            /* Get destination switch node */
            TopologyStruct probe = this.findDpidPort(rule.nw_dst_prefix);

            FlowInfo sample = new FlowInfo();
            sample.firewall_ruldid = Integer.toString(rule.ruleid);
            this.current_flow_index++;
            sample.flow_index = this.current_flow_index;
            sample.rule_node_name = "SourceNode";
            sample.current_ho = new HeaderObject();
            sample.current_ho.nw_dst_prefix = 167772160;
            sample.current_ho.nw_dst_maskbits = 8;
            sample.current_ho.nw_src_prefix = 167772160;
            sample.current_ho.vlan = -1;
            sample.current_ho.nw_src_maskbits = 8;
            sample.current_switch_dpid = source.dpid;
            sample.current_ingress_port = Short.parseShort(source.port);
            sample.next_switch_dpid = source.dpid;
            sample.next_ingress_port = Short.parseShort(source.port);
            sample.next_ho = new HeaderObject();
            sample.next_ho.nw_dst_prefix = 167772160;
            sample.next_ho.nw_dst_maskbits = 8;
            sample.next_ho.nw_src_prefix = 167772160;
            sample.next_ho.nw_src_maskbits = 8;
            sample.next_ho.vlan = -1;
            sample.target = new TopologyStruct();
            sample.target.dpid = probe.dpid;
            sample.target.port = probe.port;
            sample.flow_history = new ArrayList<FlowInfo>();
            sample.flow_history.add(sample);
            Map<FlowInfo, TopologyStruct> value = new ConcurrentHashMap<FlowInfo, TopologyStruct>();
            value.put(sample, probe);

            /* Put the new flow in the topology map.*/
            this.SourceProbeNodeStorage.put(Integer.toString(rule.ruleid), value);
            this.propagateFlow(sample, probe, 0);
        }
    }

    public TopologyStruct findDpidPort(int IP_address){
        TopologyStruct dpid_port = new TopologyStruct();

        // TODO Find a way to retrieve the switch details.
        /*Iterator<? extends IDevice> itr = this.deviceSource.queryDevices(null, null, IP_address, null, null);
        if(itr.hasNext()){
            IDevice idevice = itr.next();
            //System.out.println(idevice.getAttachmentPoints().toString());
            SwitchPort[] sp = idevice.getAttachmentPoints();
            dpid_port.dpid = HexString.toHexString((long) sp[0].getSwitchDPID());
            dpid_port.port = Integer.toString(sp[0].getPort());
        }*/
        return dpid_port;
    }

    public String getName() {
        return "ShiftedGraph";
    }

    public boolean findRuleNode(String dpid, String rulename){
        boolean result = false;
        Set<String> set = this.rulenodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            if(dpid.equals((String) key)){
                List<RuleNode> ruletable = this.rulenodes.get((String) key);
                for(int j = 0; j < ruletable.size(); j++){
                    if(rulename.equals(ruletable.get(j).rule_name)) {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    public short getPriority(String rulename){
        short priority = -32767;
        Set<String> set = this.rulenodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            List<RuleNode> ruletable = this.rulenodes.get((String) key);
            for(int i = 0; i < ruletable.size(); i++){
                if(rulename.equals(ruletable.get(i).rule_name)){
                    priority = (short)ruletable.get(i).priority;
                    break;
                }
            }
        }
        return priority;
    }

    public int getRuleIndex(String rulename){
        int k = 0;
        Set<String> set = this.rulenodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            List<RuleNode> ruletable = this.rulenodes.get(key.toString());
            for(int i = 0; i < ruletable.size(); i++){
                if(rulename.equals(ruletable.get(i).rule_name)){
                    k = i;
                    break;
                }
            }
        }
        return k;
    }


    public void staticEntryModified(String dpid, String rulename, OFFlowMod newFlowMod){

        List<RuleNode> ruletable = new ArrayList<RuleNode>();

        if(this.rulenodes == null){
            this.rulenodes = new ConcurrentHashMap<String, List<RuleNode>>();
        }
        boolean newrule = false;
        if(this.findRuleNode(dpid, rulename) == false){
            newrule = true;
        }
        ruletable = this.rulenodes.get(dpid);
        if(ruletable != null) {
            this.rulenodes.remove(dpid);
        }
        long start = System.nanoTime();
        ruletable = RuleNode.addrulenode(ruletable, rulename, newFlowMod);
        long end = System.nanoTime();
        try {
            File f = new File(this.RESULT_PATH);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("staticEntryModified addrulenode time(micro seconds) : " + ( end - start )/1000 +"\n");
            bw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.rulenodes.put(dpid, ruletable);
        int flowstorage_size = this.flowstorage.size();
        if(newrule){
            boolean onetimepass = true;
            //new rules inserted
            if(this.flowstorage != null && flowstorage_size > 0){
                for(int i = 0; i < flowstorage_size; i++){
                    if(this.flowstorage.get(i).flow_history != null && this.flowstorage.get(i).flow_history.size() > 0){
                        for(int j = 0; j < this.flowstorage.get(i).flow_history.size(); j++){
                            if(dpid.equals(this.flowstorage.get(i).flow_history.get(j).current_switch_dpid) &&
                                    newFlowMod.getPriority() >= this.getPriority(this.flowstorage.get(i).flow_history.get(j).rule_node_name)){
                                String rule_name = this.flowstorage.get(i).flow_history.get(j).rule_node_name;
                                int size = this.flowstorage.get(i).flow_history.size();
                                if(j >0){
                                    for(int k = j; k < size; k++){
                                        this.flowstorage.get(i).flow_history.remove(j);
                                    }
                                }
                                FlowInfo fi = new FlowInfo();
                                fi = FlowInfo.valueCopy2(this.flowstorage.get(i));
                                fi.candidate_rule = rulename;
                                this.propagateFlow(fi, this.flowstorage.get(i).target, this.getRuleIndex(rule_name));
                                this.flowstorage.get(i).candidate_rule = null;
                                if(onetimepass && this.rulenodes.get(dpid).size()>=2){
                                    //new flow
                                    //this.current_flow_index++;
                                    fi.flow_index = this.current_flow_index;
                                    fi.candidate_rule = rulename;
                                    this.propagateFlow(fi, this.flowstorage.get(i).target, this.getRuleIndex(rulename));
                                    this.flowstorage.get(i).candidate_rule = null;
                                    onetimepass = false;
                                }
                                break;
                            }else if(dpid.equals(this.flowstorage.get(i).flow_history.get(j).current_switch_dpid) &&
                                    newFlowMod.getPriority() < this.getPriority(this.flowstorage.get(i).flow_history.get(j).rule_node_name) &&
                                    onetimepass){
                                String rule_name = this.flowstorage.get(i).flow_history.get(j).rule_node_name;
                                FlowInfo fi = new FlowInfo();
                                fi = FlowInfo.valueCopy(this.flowstorage.get(i));
                                int size = fi.flow_history.size();
                                if(j>0){
                                    for(int k = j; k < size; k++){
                                        fi.flow_history.remove(j);
                                    }
                                }
                                FlowInfo fi2 = new FlowInfo();
                                fi2 = FlowInfo.valueCopy2(fi);
                                this.current_flow_index++;
                                fi2.flow_index = this.current_flow_index;
                                fi2.candidate_rule = rulename;
                                this.propagateFlow(fi2, this.flowstorage.get(i).target, this.getRuleIndex(rulename));
                                this.flowstorage.get(i).candidate_rule = null;
                                onetimepass = false;
                                break;
                            }else if(dpid.equals(this.flowstorage.get(i).flow_history.get(j).next_switch_dpid) &&
                                    (j+1) == this.flowstorage.get(i).flow_history.size()){
                                FlowInfo fi = new FlowInfo();
                                fi = FlowInfo.valueCopy2(this.flowstorage.get(i));
                                fi.candidate_rule = rulename;
                                this.propagateFlow(fi, this.flowstorage.get(i).target, this.getRuleIndex(rulename));
                                this.flowstorage.get(i).candidate_rule = null;
                                break;
                            }
                        }
                    }
                }
            }
        }else{
            //existing rule_node update case
            if(this.flowstorage != null && flowstorage_size > 0){
                for(int i = 0; i < flowstorage_size; i++){
                    if(this.flowstorage.get(i).flow_history != null){
                        for(int j = 0; j < this.flowstorage.get(i).flow_history.size(); j++){
                            if(dpid.equals(this.flowstorage.get(i).flow_history.get(j).current_switch_dpid) &&
                                    newFlowMod.getPriority() >= this.getPriority(this.flowstorage.get(i).flow_history.get(j).rule_node_name)){
                                String rule_name = this.flowstorage.get(i).flow_history.get(j).rule_node_name;
                                int size = this.flowstorage.get(i).flow_history.size();
                                if(j>0){
                                    for(int k = j; k < size; k++){
                                        this.flowstorage.get(i).flow_history.remove(j);
                                    }
                                }
                                FlowInfo fi = new FlowInfo();
                                fi = FlowInfo.valueCopy2(this.flowstorage.get(i));
                                fi.candidate_rule = rulename;
                                this.propagateFlow(fi, this.flowstorage.get(i).target, this.getRuleIndex(rule_name));
                                this.flowstorage.get(i).candidate_rule = null;
                                return;
                            }
                        }
                    }
                }
            }
        }
        end = System.nanoTime();
        try {
            File f = new File(this.RESULT_PATH);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("staticEntryModified addrulenode time(micro seconds) : " + ( end - start )/1000 +"\n");
            bw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean findRulename(String rulename){
        Set<String> set = this.rulenodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            List<RuleNode> ruletable = this.rulenodes.get(key.toString());
            for(int i = 0; i < ruletable.size(); i++){
                if(rulename.equals(ruletable.get(i).rule_name)){
                    return true;
                }
            }
        }
        return false;
    }

    public void staticEntryDeleted(String rulename){
        long start = System.nanoTime();

        if(this.findRulename(rulename) == false){
            //if there exist no such rulename, then exit this function.
            return;
        }
        String dpid = null;
        Set<String> set = this.rulenodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            List<RuleNode> ruletable = this.rulenodes.get(key.toString());
            for(int j = 0; j < ruletable.size(); j++){
                if(rulename.equals(ruletable.get(j).rule_name)){
                    this.rulenodes.get(key.toString()).remove(ruletable.get(j));
                    dpid = key.toString();
                    break;
                }
            }
        }
        List<RuleNode> ruletable = new ArrayList<RuleNode>();
        ruletable = this.rulenodes.get(dpid);
        if(ruletable != null) {
            this.rulenodes.remove(dpid);
        }
        ruletable = RuleNode.deleterulenode(ruletable, rulename);
        this.rulenodes.put(dpid, ruletable);

        int flowstorage_size = this.flowstorage.size();
        if(this.flowstorage != null && flowstorage_size > 0){
            for(int i = 0; i < flowstorage_size; i++){
                if(this.flowstorage.get(i).flow_history != null){
                    for(int j = 0; j < this.flowstorage.get(i).flow_history.size(); j++){
                        if(rulename.equals(this.flowstorage.get(i).flow_history.get(j).rule_node_name) && this.flowstorage.size() >= 1){
                            int size = this.flowstorage.get(i).flow_history.size();
                            for(int k = j; k < size; k++){
                                this.flowstorage.get(i).flow_history.remove(j);
                            }
                            FlowInfo fi = new FlowInfo();
                            fi = FlowInfo.valueCopy2(this.flowstorage.get(i));
                            fi.candidate_rule = rulename;
                            this.propagateFlow(fi, this.flowstorage.get(i).target, this.getRuleIndex(rulename));
                            this.flowstorage.get(i).candidate_rule = null;
                            break;
                        }
                    }
                }
            }
        }
        long end = System.nanoTime();
        try {
            File f = new File(this.RESULT_PATH);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("staticEntryDeleted time(micro seconds) : " + ( end - start )/1000 +"\n");
            bw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
