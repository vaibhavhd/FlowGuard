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
import java.util.concurrent.ExecutionException;

//import org.openflow.protocol.OFFlowMod;
/*import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionStripVirtualLan;
import org.openflow.protocol.action.OFActionType;
import org.openflow.protocol.action.OFActionVirtualLanIdentifier;
import org.openflow.util.HexString;
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.net.InetAddresses;

import org.restlet.resource.Post;
import org.restlet.resource.Resource;
import org.restlet.resource.ServerResource;
import org.opendaylight.controller.liblldp.EtherTypes;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;

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
import org.opendaylight.flowguard.impl.FlowRuleNode.ActionList;
import org.opendaylight.flowguard.packet.Ethernet;
import org.opendaylight.flowguard.packet.IPv4;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.address.tracker.rev140617.AddressCapableNodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.address.tracker.rev140617.address.node.connector.Addresses;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.vlan.match.fields.VlanId;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;



public class ShiftedGraph {
    private static final Logger LOG = LoggerFactory.getLogger(Flowguard.class);
    private static ReadTransaction readTx;
    // Map<DPID, Map<Name, FlowMod>>; FlowMod can be null to indicate non-active
    public Map<String, Map<String, FlowBuilder>> entriesFromStorage;
    public Map<String, List<FlowRuleNode>> FlowRuleNodes;
    public Map<TopologyStruct, TopologyStruct> topologyStorage;
    public Map<String, Map<FlowInfo, TopologyStruct>> SourceProbeNodeStorage;
    public List<FlowInfo> flowstorage;
    public int current_flow_index = 0;
    //public StaticFlowEntryPusher static_pusher;
    public Flowguard firewall;
    // Entry Name -> DPID of Switch it's on
    protected boolean AUTOPORTFAST_DEFAULT = false;
    protected boolean autoPortFastFeature = AUTOPORTFAST_DEFAULT;
    //protected IResultSet topologyResult;
    public String RESULT_PATH="/tmp/";
    public int resolution_index = 0;
    public int resolution_method = 0;

    protected static Logger logger;

    protected List<FirewallRule> rules; // protected by synchronized
    protected boolean enabled;
    protected int subnet_mask = IPv4.toIPv4Address("255.255.255.0");

    public ShiftedGraph(Flowguard firewall, ReadTransaction readTx, Map<String, List<FlowRuleNode>> flowStorage, Map<TopologyStruct, TopologyStruct> topologyStorage) {
        this.firewall = firewall;
    	this.readTx = readTx;
        this.FlowRuleNodes = flowStorage;
        this.topologyStorage = topologyStorage;
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
            FlowRuleNode rn = new FlowRuleNode();

            /* Get the set of all the switches in the network */
            Set<String> set = this.FlowRuleNodes.keySet();
            Iterator<String> itr = set.iterator();
            while (itr.hasNext()) {
                Object key = itr.next();
                if (dpid.equals((String) key)) {
                    List<FlowRuleNode> ruletable = this.FlowRuleNodes.get((String) key);
                    for (int j = 0; j < ruletable.size(); j++) {
                        if (rulename.equals(ruletable.get(j).rule_name)) {
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
                Set<String> set2 = this.FlowRuleNodes.keySet();
                Iterator<String> itr2 = set2.iterator();
                while(itr2.hasNext()){
                    Object key = itr2.next();
                    List<FlowRuleNode> ruletable = this.FlowRuleNodes.get(key.toString());
                    int size = ruletable.size();
                    for(int j = 0; j < size; j++){
                        if(flowinfo.candidate_rule.equals(ruletable.get(j).rule_name)){
                            if(ho.nw_src_maskbits == 32 && ho.nw_dst_maskbits == 32){
                                this.FlowRuleNodes.get(key.toString()).remove(j);
                                // TODO this.storageSource.deleteRowAsync(STATICENTRY_TABLE_NAME, flowinfo.candidate_rule);
                                return ho;
                            }else if(ho.nw_src_maskbits == ruletable.get(j).nw_src_maskbits && ho.nw_dst_maskbits == ruletable.get(j).nw_dst_maskbits){
                                this.FlowRuleNodes.get(key.toString()).remove(j);
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
        if(this.firewall.ruleStorage != null){
            for(int i=0;i<this.firewall.ruleStorage.size(); i++){
                if(this.firewall.ruleStorage.get(i).ruleid == Integer.parseInt(flowinfo.firewall_ruldid)){
                    frule = this.firewall.ruleStorage.get(i);
                }
            }
        }

        if(FlowRuleNode.matchIPAddress(frule.nw_dst_prefix, frule.nw_dst_maskbits, flowinfo.next_ho.nw_dst_prefix, flowinfo.next_ho.nw_dst_maskbits)){
            //for example : substring of FlowRuleNode looks like 'flow1', 'flow2' and so on
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
            Set<String> set = this.FlowRuleNodes.keySet();
            Iterator<String> itr = set.iterator();
            while(itr.hasNext()){
                Object key = itr.next();
                List<FlowRuleNode> ruletable = this.FlowRuleNodes.get(key.toString());
                int size = ruletable.size();
                for(int i = size-1; i >= 0; i--){
                	if(ruletable.get(i) != null)
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
        Map<String, Map<String, FlowBuilder>> entries = new ConcurrentHashMap<String, Map<String, FlowBuilder>>();
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
            String FlowRuleNode = flowinfo.flow_history.get(i).rule_node_name.substring(0, 5);
            if(keyword.equals(FlowRuleNode)){
                set_vlan = true;
                break;
            }
        }

        if(set_vlan == true){
            //in this case, set_vlan_id actions which contain 'flow1' keyword
            String keyword2 = "egress";
            String keyword3 = "ingress";
            Set<String> set = this.FlowRuleNodes.keySet();
            Iterator<String> itr = set.iterator();
            while(itr.hasNext()){
                Object key = itr.next();
                List<FlowRuleNode> ruletable = this.FlowRuleNodes.get(key.toString());
                int size = ruletable.size();
                for(int j = 0; j < size; j++){
                    if(keyword.equals(ruletable.get(j).rule_name.substring(0,5))&&
                    		ruletable.get(j).dl_type == (long)(EtherTypes.IPv4.intValue())){
                        String rulename = ruletable.get(j).rule_name;
                        if(ruletable.get(j).rule_name.length()>=13 && keyword3.equals(ruletable.get(j).rule_name.substring(6,13))){
                            this.setVlan(entries, key.toString(),rulename, 1);
                        }else if(ruletable.get(j).rule_name.length()>=12 && keyword2.equals(ruletable.get(j).rule_name.substring(6,12))){
                            this.setVlan(entries, key.toString(),rulename, 2);
                        }else{
                            this.setVlan(entries, key.toString(),rulename, 3);
                        }
                        this.FlowRuleNodes.get(key.toString()).remove(j+1);
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

    public void setVlan(Map<String, Map<String, FlowBuilder>> entries, String dpid, String rulename, int strip){
    /*    System.out.println("S1-Dependency Breaking applied!!");
        this.resolution_method = 1;
        FlowBuilder flowmod = new FlowBuilder();
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
            if(action.getType() == OFActionType.SET_VLAN_VID){   TODO Prev it was SET_VLAN_ID
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
*/
    }


    public void addFirewallRule(HeaderObject ho, String dpid, String port){
        FirewallRule rule = new FirewallRule();
        rule.ruleid = rule.genID();
        rule.priority = 32768;
        rule.dpid = dpid;
        rule.wildcard_dpid = false;
        rule.in_port = new String(port);
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

    public void addFlowEntry(HeaderObject ho, String dpid, String port){
        Map<String, Object> entry = new HashMap<String, Object>();
        String rulename = "resolution"+Integer.toString(this.resolution_index);
        this.resolution_index++;
        System.out.println("Flow rule to be added" + rulename);
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
            System.out.println("Applied FlowRuleNode Name : "+fi.rule_node_name);
            FlowInfo.printFlowInfo(fi);
            System.out.println("-----------------------------------------------------------------------------------");
            i++;
        }
        /* InverseFlow is true when the sample flow has been propagated to the target */
        if(inverseflow){
            HeaderObject ho = new HeaderObject();
            ho = this.computeInverseFlow(flowinfo);
        }
        /* TODO Should the flowstorage be created only if inverseflow is true(or false?) and flow has been reached dest??
         * When a new flow is added, a flow from flowstorage is pulled and propagated in a logical nw with
         * the new flow in it. If the propagated flow reaches destination, the violation has occured.
         * */
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

        FlowInfo sample = FlowInfo.valueCopy(flowinfo);

        String targetdpid = target.dpid;
        String targetport = new String(target.port);
        System.out.println("Propagating to target dpid: "+targetdpid+" port: "+targetport);
        while(true){
            if (sample.is_finished) {
                System.out.println("Propagation finished already! Returning...");
                return;
            }
            String SWITCHDPID = sample.next_switch_dpid;
            // TODO the if check will always pass
            if(sample.next_switch_dpid.equals(SWITCHDPID.toString())){
                /* Get all the flow rules present in the next switch */
                List<FlowRuleNode> ruletable = this.FlowRuleNodes.get(SWITCHDPID);
                int i = 0;
                int table_size = 0;
                if(ruletable != null){
                    table_size = ruletable.size();
                    /* The switch has no flows. Stop the propagation */
                    if(table_size == 0) {
                        System.out.println("Returning: " + SWITCHDPID + " has no flows in the flowtable index 0!");
                        return;
                    }
                }
                else if(sample.next_switch_dpid.equals(targetdpid)){
                    /* After propagation,if the final dpid is same as target, the sample packet has been reached.
                     * This case happens when a switch with no installed flows is reached and happens to be the target
                     */
                    System.out.println("Flow reached to the Destination " + targetdpid + " . No installed flows in switch: " + SWITCHDPID);
                    this.printFlowInfo(sample, true);
                    sample.is_finished = true;
                    flowinfo.is_finished = true;
                    return;
                }else{
                	System.out.println("DEBUG1 NO FLOW FOR: SWITCHDPID=" + SWITCHDPID + "\n sample dpid:" + sample.next_switch_dpid + "target dpid:" + targetdpid  );
                    System.out.println("Flow is not reachable to target: " +targetdpid + "Node: " + SWITCHDPID + " has no flows!!!");
                    this.printFlowInfo(sample, false);
                    sample.is_finished = true;
                    flowinfo.is_finished = true;
                    return;
                }

                if(sample.is_finished){
                    System.out.println("Sample address space is covered is entire overlapped by existing Flow"
                            + "\n Propagation stopped at " + sample.rule_node_name + " !!!");
                    flowinfo.is_finished = true;
                    this.printFlowInfo(sample, false);
                    return;
                }
                /* Flows have not reached the destination switch */
                /* Loop through all the flows in the table from SWITCHDPID
                 * Index: index of the flow rule in a table. Set to 0 when a FW Rule is tested
                 * Counter:
                 */
                int counter = 0;
                if(index == 0){
                    for(i = index; i < table_size; i++){
                        // Check if the switch and port of the new flow rule are the same
                        /* check for flow rule matching IP packet */
                        System.out.println("sample ingress " + sample.next_ingress_port);
                        System.out.println("ruleNode {}" + ruletable.get(i).rule_name);
                        System.out.println(" Wildcarded {}" + FlowRuleNode.isWildcarded(ruletable.get(i).in_port));
                        if (FlowRuleNode.isWildcarded(ruletable.get(i).in_port) || (sample.next_ingress_port.toString().equals(ruletable.get(i).in_port.toString()))
                                && ruletable.get(i).dl_type == EtherTypes.IPv4.intValue() && ruletable.get(i).active == true) {
                            counter++;
                        }
                        /* The rule has matched more than one flow(Same: Dpid, inport and ETH_TYPE per switch */
                        if (counter >= 2
                                && sample.next_ho.vlan == ruletable.get(i).vlan
                                && FlowRuleNode.matchIPAddress(sample.next_ho.nw_dst_prefix, sample.next_ho.nw_dst_maskbits,
                                        ruletable.get(i).nw_dst_prefix, ruletable.get(i).nw_dst_maskbits)
                                && FlowRuleNode.matchIPAddress(sample.next_ho.nw_src_prefix, sample.next_ho.nw_src_maskbits,
                                        ruletable.get(i).nw_src_prefix, ruletable.get(i).nw_src_maskbits)){
                                FlowInfo sample_clone = new FlowInfo();
                                sample_clone = FlowInfo.valueCopy(sample);
                                this.current_flow_index++;
                                sample_clone.flow_index = this.current_flow_index;
                                /*
                                 * If two or more flow rules match the sample flow, propagate the cloned sample
                                 * from the matched index.
                                 * Match criteria: Same: (DPID, INPORT, ETHTYPE, VLAN, Overlapping SRC DST)
                                 * Propagate all the matching flows with the respective index in the flow table
                                 *  */
                                this.propagateFlow(sample_clone, target, i);
                                if (sample_clone.is_finished) {
                                	sample.is_finished = true;
                                	flowinfo.is_finished = true;
                                    System.out.println("Propagation finished already! Returning...");
                                    return;
                                }
                                
                        }
                    }
                }

                /*  IMPORTANT TODO
                 *  Reached here when ((index==0) && (Rule matches just one flow)) || index != 0
                 *  When a flow is installed/modified, index of the flow in flowtable is passed as "index".
                 *  Rules are sorted(descending) in the local table.
                 */
                // TODO Whenever any rule matches entirely, no need of further match. As this rule will instruct the packet further.
                int unmatch_count = 0;
                /* If the index is last rule, reset the index. The last rule is always table miss and packet gets dropped */
                if(index == table_size - 1)
                    index = 0;
                System.out.println("Start Index "+index);
                for(i = index; i < table_size; i++){
                    // TODO The if will never pass. Correction: switch_name is never set and is not required either.
                    // Moreover, dpid will be same ith rule is pulled from the node of sample flow.
                    System.out.println("RuleTable info: In_port "+ruletable.get(i).in_port+" Priority: "+ruletable.get(i).priority);
                    System.out.println("Sample packet info: "+sample.next_ingress_port.toString());
                    if(sample.next_switch_dpid.equals(SWITCHDPID) && ((FlowRuleNode.isWildcarded(ruletable.get(i).in_port))
                            || sample.next_ingress_port.toString().equals(ruletable.get(i).in_port.toString()))) {
                        FlowRuleNode flowRule = ruletable.get(i);
                        /// TODO INFINTE LOOP is seen when testing
                        System.out.println("Found a rule with same/wildcarded next ingress port");
                        /* check for flow rule matching ARP packet, ignore the rule of ARP is found*/
                        if(flowRule.dl_type == EtherTypes.ARP.intValue()){
                            System.out.println("ETHERNET TYPE=ARP! Unmatched flow");
                            unmatch_count++;
                            continue;
                        }
                        /* check for flow rule matching IP packet */
                        if((flowRule.dl_type == 0 || flowRule.dl_type  == EtherTypes.IPv4.intValue()) && flowRule.active == true){
                            System.out.println("ETH_TYPE matched/wildcarded: "+flowRule.dl_type);
                            //System.out.println("Number of actions in the rule: "+flowRule.actionList.size());
                            if(flowRule.actionList == null) {
                                flowRule = FlowRuleNode.computeFlow(flowRule, sample, null);
                                if(flowRule.flow_info.is_finished) {
                                	sample.is_finished = true;
                                	flowinfo.is_finished = true;
                                	sample = flowRule.flow_info;
                                    System.out.println("Flow has been dropped by the tablemiss entry");
                                    this.printFlowInfo(sample, false);
                                    return;
                                }
                                continue;
                            }
                            else {
                                for (ActionList actionNode : flowRule.actionList) {
                                    flowRule = FlowRuleNode.computeFlow(flowRule, sample, actionNode);
                                    /* Now sample should have some propagation history */
                                    if(flowRule.flow_info == null) {
                                        //unmatch_count++;
                                        System.out.println("flowRule.flow_info is null.");
                                        continue;
                                    }
                                    if(flowRule.flow_info.flow_history == null) {
                                        // TODO what is this? Is it failure or a success??
                                        /* This would mean that the sample flow could not be further matched and propogated */
                                        //unmatch_count++;
                                        System.out.println("Sample has no flow history: Returning");
                                        continue;
                                    }

                                    /* Reaching here would mean that the matching flow had been found */
                                    System.out.println("Sample information before propagation:");
                                    System.out.println("Sample: "+sample.next_switch_dpid+" Port: "+sample.next_ingress_port);
                                    FlowInfo old = sample;
                                    sample = flowRule.flow_info;
                                    System.out.println("Sample information after compute:");
                                    System.out.println("Sample: "+sample.next_switch_dpid+" Port: "+sample.next_ingress_port);
                                    if (sample.next_ingress_port == null) {
                                        System.out.println("Sample is not reachable: Packet drop by flow rule!");
                                        this.printFlowInfo(sample, false);
                                        sample.is_finished = true;
                                        old.is_finished = true;
                                        flowinfo.is_finished = true;
                                        return;
                                    } else {
                                        /* If the flow has not reached the destination, find the next node to hop and propagate */
                                        /* However, the OutputAction is a port number. Add dpid to it */
                                        sample.next_ingress_port = sample.next_switch_dpid + ":" + sample.next_ingress_port;
                                    }

                                    if(sample.next_switch_dpid.equals(targetdpid)){
                                        //normal execution
                                        if(sample.next_ingress_port.equals(targetport)) {
                                            System.out.println("Flows are reached to the Destination!!!");
                                            this.printFlowInfo(sample, true);
                                            sample.is_finished = true;
                                            old.is_finished = true;
                                            flowinfo.is_finished = true;
                                            return;
                                        }
                                    }

                                    /* Update the rule with its flow info and respective flow infow's flow history */
                                    this.FlowRuleNodes.get(SWITCHDPID).remove(i);
                                    if(i == 0){
                                        this.FlowRuleNodes.get(SWITCHDPID).add(flowRule);
                                    }else{
                                        this.FlowRuleNodes.get(SWITCHDPID).add(i, flowRule);
                                    }

                                    FlowInfo tempSample = this.findNextConnection(sample);
                                    if(tempSample == null) {
                                        /* The next connection to the port in the present switch is not a switch node */
                                        System.out.println("Reached a host(Or nothing) when searching for a node");
                                        /* After every action packet, the original packet before action has to be sent */
                                        sample = old;
                                        continue;
                                    } else {
                                        sample = tempSample;
                                    }

                                    System.out.println("Node information after propagation:");
                                    System.out.println("Node: "+sample.next_switch_dpid+" Port: "+sample.next_ingress_port);
                                    propagateFlow(sample, target,0);
                                    if(sample.is_finished) {
                                    	System.out.println("Should not reach here");
                                    	flowinfo.is_finished = true;
                                    	old.is_finished = true;
                                        return;
                                    }
                                }
                            }
                            break;
                        } else {
                            // TODO unmatch_count++ ???
                            //  Link Layer Discovery Protocol (LLDP) comes here.
                            System.out.println("Unrecognized Ethernet Type: " + flowRule.dl_type);
                            unmatch_count++;
                            continue;
                        }
                    }
                    else {
                        unmatch_count++;
                        if(table_size == unmatch_count){
                            /*
                             * None of the rules have matched the new flow. TODO Increase the unmatch count for every non match.
                             * If the index >= 1, the count can never be table_size!
                             * TODO Change the if match to count == (table_size - index!)
                             */
                            if(sample.next_switch_dpid.equals(targetdpid)){
                                //normal execution
                                System.out.println("No rules in the table matched the new flow. Flow reached to the Destination!!!");
                                System.out.println("Unmatch_count = "+unmatch_count);
                                System.out.println("NO FLOW FOR: SWITCHDPID=" + SWITCHDPID + "\n sample dpid:" + sample.next_switch_dpid + "target dpid:" + targetdpid  );
                                this.printFlowInfo(sample, true);
                                sample.is_finished = true;
                                flowinfo.is_finished = true;
                                return;
                            }
                            System.out.println("No rules in the next switch table matched: Flows are unreachable!!!");
                            System.out.println("sample next switch dpid" + sample.next_switch_dpid +
                            		"\n ruletable i switch name " + ruletable.get(i).switch_name +
                            		"\n sample next ingress port " + sample.next_ingress_port.toString() +
                            		"\nruletable i inport " + ruletable.get(i).in_port);
                            System.out.println("Unmatch_count = "+unmatch_count);
                            System.out.println("NO FLOW FOR: SWITCHDPID=" + SWITCHDPID + "\n sample dpid:" + sample.next_switch_dpid + "target dpid:" + targetdpid  );

                            this.printFlowInfo(sample, false);
                            sample.is_finished = true;
                            flowinfo.is_finished = true;
                            return;
                        }
                        continue;
                    }
                }

                //TODO When the sample has not changed from the entire for loop, (eg: Unrecog Eth type), this causes infinite loop(while(1)) in the code.
                if(unmatch_count == table_size - index) {
                    System.out.println("Sample has not matched any existing flow rule on the switch.");
                    break;
                }
            }
            /* Reset the index for the new table */
            index = 0;
            //end of while
        }
    }

    public FlowInfo findNextConnection(FlowInfo flowinfo){
        Set<TopologyStruct> st = this.topologyStorage.keySet();
        Iterator<TopologyStruct> iter = st.iterator();
        TopologyStruct nextLink = null;
        while (iter.hasNext())
        {
            TopologyStruct t = iter.next();

            // TODO Check port equivalence and URI type
            if(t.dpid.equals(flowinfo.next_switch_dpid) && t.port.equals(flowinfo.next_ingress_port)){
                nextLink = this.topologyStorage.get(t);
                flowinfo.next_switch_dpid = nextLink.dpid;
                flowinfo.next_ingress_port = new String(nextLink.port);
                System.out.println(t.dpid + " / " + t.port + " <--> " +nextLink.dpid + "/ " + nextLink.port);
                break;
            }
        }
        if (nextLink == null)
            return null;
        return flowinfo;
    }

    public Map<String, Map<String, FlowRuleNode>> updateFlowRuleNode(Map<String, Map<String, FlowRuleNode>> FlowRuleNodes, FlowRuleNode FlowRuleNode){
        return FlowRuleNodes;
    }
/*
 *  TODO: Partitioning of FW Auth space into ALLOW/DENY is not done.
 *  DENY rules are directly checked without checking for overlaps.
 *  Flaw: The dpid of the FW rule is not taken into account/ignored.
 *  Result: The network is search for reachability and issues by taking dpid
 *  forcefully as wildcarded. If src= X and dest = Y: For the deny case,
 *  if packet is still reached from X to Y, it is said to be a problem.
 *  But, it may not be a problem actually as the packet might have taken
 *  a different path to reach from X to Y without even going through the
 *  switch in which the rule is to be enforced.
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
                TopologyStruct source = findDpidPort(firewall_rules.get(i).nw_src_prefix);
                TopologyStruct probe = findDpidPort(firewall_rules.get(i).nw_dst_prefix);

                if(source == null) {
                    System.out.printf("Host %s not found in the network(no corresponding source node)\n",
                            IPv4.fromIPv4Address(firewall_rules.get(i).nw_src_prefix));
                    continue;
                }
                if(probe == null) {
                    System.out.printf("Host %s not found in the network(no corresponding probe node)\n",
                            IPv4.fromIPv4Address(firewall_rules.get(i).nw_dst_prefix));
                    continue;
                }
                System.out.printf("\nRule: %s | source node: %s | probe node: %s\n", firewall_rules.get(i).ruleid,
                        source.dpid, probe.dpid);

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
                sample.current_ingress_port = new String(source.port);
                sample.next_switch_dpid = source.dpid;
                sample.next_ingress_port = new String(source.port);
                sample.next_ho = new HeaderObject();
                sample.next_ho.nw_dst_prefix = 167772160;//IP=10.0.0.0
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
                System.out.print("<<<<<<<<<<<<<<<<<<<<");
                System.out.print("Starting to propagate sample. Flow Index:" + this.current_flow_index);
                System.out.println(">>>>>>>>>>>>>>>>>>>>");
                this.propagateFlow(sample, probe, 0);
                System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.print("Finished propagation!");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");

/*                long end = System.nanoTime();
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
*/            } else {
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
            if(source == null) {
            	LOG.info("The source device in firewall rule is not found"
            			+ "This situation has not been handlled yet!!");
            	return;
            }
            /* Get destination switch node */
            TopologyStruct probe = this.findDpidPort(rule.nw_dst_prefix);
            if(probe == null) {
            	LOG.info("The destination device in firewall rule is not found"
            			+ "This situation has not been handlled yet!!");
            	return;
            }
            FlowInfo sample = new FlowInfo();
            sample.firewall_ruldid = Integer.toString(rule.ruleid);
            this.current_flow_index++;
            sample.flow_index = this.current_flow_index;
            sample.rule_node_name = "SourceNode";
            sample.current_ho = new HeaderObject();
            sample.current_ho.nw_dst_prefix = 167772160; // IP = 10.0.0.0
            sample.current_ho.nw_dst_maskbits = 8;
            sample.current_ho.nw_src_prefix = 167772160;
            sample.current_ho.vlan = -1;
            sample.current_ho.nw_src_maskbits = 8;
            sample.current_switch_dpid = source.dpid;
            sample.current_ingress_port = new String(source.port);
            sample.next_switch_dpid = source.dpid;
            // TODO Current and next ingress ports are same!!
            sample.next_ingress_port = new String(source.port);
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

    /* Find switch based on the IP from firewall rule */
    /* How will this work ideally for both masked and non masked addresses */
    /* Devices are learned on the network by device manager.
     * When a packet-in is received by a switch, an attachment point is created for the device.
     * Devices are updated as they are learned.
     * A device can have at max one "att point" per OF island.
     */
    public static TopologyStruct findDpidPort(int IP_address){
        TopologyStruct dpid_port = new TopologyStruct();

        InstanceIdentifier<Nodes> nodesIdentifier = InstanceIdentifier.builder(Nodes.class).toInstance();

        try {
            Optional<Nodes> optNodes= null;
            Optional<Table> optTable = null;
            Optional<Flow> optFlow = null;

            List<Node> nodeList;
            List<Flow> flowList;

            /* Retrieve all the switches in the operational data tree */
            optNodes = readTx.read(LogicalDatastoreType.OPERATIONAL, nodesIdentifier).get();
            nodeList = optNodes.get().getNode();

            /* Iterate through the list of nodes(switches) for flow tables per node */
            for(Node node : nodeList){
            	List<NodeConnector> connectorList = node.getNodeConnector();
            	for (NodeConnector connector : connectorList) {
            		AddressCapableNodeConnector acnc = connector.getAugmentation(AddressCapableNodeConnector.class);
            		if(acnc != null && acnc.getAddresses() != null) {
            	        // get address list from augmentation.
            	        List<Addresses>  addresses = acnc.getAddresses();
            	        for(Addresses address:addresses) {
            	          //address.getMac();// to get MAC address observed on this port
            	        	Ipv4Address ip = address.getIp().getIpv4Address();// to get IP address observed on this port
            	        	int ipnum = InetAddresses.coerceToInteger(InetAddresses.forString(ip.getValue()));
            	        	if(ipnum == IP_address) {
            	        		dpid_port.dpid = node.getId().getValue();
            	        		dpid_port.port = connector.getId().getValue();
            	        		return dpid_port;
            	        	}
            	          //address.getFirstSeen(); // first time the tuple was observed on this port
            	          //address.getLastSeen(); // latest time the tuple was observed on this port
            	        }
            	      }
            	}
            }
        }
	    catch (InterruptedException | ExecutionException e) {
	        e.printStackTrace();
	    }
        /* Reaching here would mean that the matching device has not been found */
        return null;
        }

    public String getName() {
        return "ShiftedGraph";
    }

    public boolean findFlowRuleNode(String dpid, String rulename){
        boolean result = false;
        Set<String> set = this.FlowRuleNodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            if(dpid.equals((String) key)){
                List<FlowRuleNode> ruletable = this.FlowRuleNodes.get((String) key);
                for(int j = 0; j < ruletable.size(); j++){
                    if(rulename.equals(ruletable.get(j).rule_name)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public short getPriority(String rulename){
        short priority = -32767;
        Set<String> set = this.FlowRuleNodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            List<FlowRuleNode> ruletable = this.FlowRuleNodes.get((String) key);
            for(int i = 0; i < ruletable.size(); i++){
                if(rulename.equals(ruletable.get(i).rule_name)){
                    priority = (short)ruletable.get(i).priority;
                    break;
                }
            }
        }
        return priority;
    }
// TODO Correction: The rule should be specifically pulled from a particular switch. going through all the switches seems wrong.
    public int getRuleIndex(String rulename){
        int k = 0;
        Set<String> set = this.FlowRuleNodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            List<FlowRuleNode> ruletable = this.FlowRuleNodes.get(key.toString());
            for(int i = 0; i < ruletable.size(); i++){
                if(rulename.equals(ruletable.get(i).rule_name)){
                    k = i;
                    break;
                }
            }
        }
        return k;
    }


    public void staticEntryModified(String dpid, Flow newFlow){

        List<FlowRuleNode> ruletable;
        String rulename = newFlow.getFlowName();
        if(this.FlowRuleNodes == null){
            this.FlowRuleNodes = new ConcurrentHashMap<String, List<FlowRuleNode>>();
        }
        boolean newrule = false;
        if(this.findFlowRuleNode(dpid, rulename) == false){
            newrule = true;
        }
        ruletable = this.FlowRuleNodes.get(dpid);
        if(ruletable != null) {
            this.FlowRuleNodes.remove(dpid);
        }
        long start = System.nanoTime();
        ruletable = FlowRuleNode.addFlowRuleNode(ruletable, newFlow);
        /*long end = System.nanoTime();
        try {
            File f = new File(this.RESULT_PATH);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("staticEntryModified addFlowRuleNode time(micro seconds) : " + ( end - start )/1000 +"\n");
            bw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        this.FlowRuleNodes.put(dpid, ruletable);

        if(newrule){
            boolean onetimepass = true;
            //new rules inserted
            if(this.flowstorage != null){
                int flowstorage_size = this.flowstorage.size();
                for(int i = 0; i < flowstorage_size; i++){
                    if(this.flowstorage.get(i).flow_history != null && this.flowstorage.get(i).flow_history.size() > 0){
                        for(int j = 0; j < this.flowstorage.get(i).flow_history.size(); j++){
                            if(dpid.equals(this.flowstorage.get(i).flow_history.get(j).current_switch_dpid) &&
                                    newFlow.getPriority() >= this.getPriority(this.flowstorage.get(i).flow_history.get(j).rule_node_name)){
                                String rule_name = this.flowstorage.get(i).flow_history.get(j).rule_node_name;
                                int size = this.flowstorage.get(i).flow_history.size();
                                if(j >0){
                                    for(int k = j; k < size; k++){
                                        /* Remove all the flows from Jth flow in hostuiry till the end */
                                        this.flowstorage.get(i).flow_history.remove(j);
                                    }
                                }
                                FlowInfo fi = new FlowInfo();
                                // Copy the information from last rule in the flowhistory of ith rule.
                                fi = FlowInfo.valueCopy2(this.flowstorage.get(i));
                                fi.candidate_rule = rulename;
                                this.propagateFlow(fi, this.flowstorage.get(i).target, this.getRuleIndex(rule_name));
                                this.flowstorage.get(i).candidate_rule = null;
                                if(onetimepass && this.FlowRuleNodes.get(dpid).size()>=2){
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
                                    newFlow.getPriority() < this.getPriority(this.flowstorage.get(i).flow_history.get(j).rule_node_name) &&
                                    onetimepass){
                                String rule_name = this.flowstorage.get(i).flow_history.get(j).rule_node_name;
                                FlowInfo fi = new FlowInfo();
                                // Copy the information from the ith rule itself
                                fi = FlowInfo.valueCopy(this.flowstorage.get(i));
                                int size = fi.flow_history.size();
                                if(j>0){
                                 // TODO What is Correction: K loop not required | remove kth rule from history?
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
            if(this.flowstorage != null){
                int flowstorage_size = this.flowstorage.size();
                for(int i = 0; i < flowstorage_size; i++){
                    if(this.flowstorage.get(i).flow_history != null){
                        for(int j = 0; j < this.flowstorage.get(i).flow_history.size(); j++){
                            if(dpid.equals(this.flowstorage.get(i).flow_history.get(j).current_switch_dpid) &&
                                    newFlow.getPriority() >= this.getPriority(this.flowstorage.get(i).flow_history.get(j).rule_node_name)){
                                String rule_name = this.flowstorage.get(i).flow_history.get(j).rule_node_name;
                                int size = this.flowstorage.get(i).flow_history.size();
                                if(j>0){
                                 // TODO What is Correction: K loop not required | remove kth rule from history?
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
        }/*
        end = System.nanoTime();
        try {
            File f = new File(this.RESULT_PATH);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("staticEntryModified addFlowRuleNode time(micro seconds) : " + ( end - start )/1000 +"\n");
            bw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public boolean findRulename(String rulename){
        Set<String> set = this.FlowRuleNodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            List<FlowRuleNode> ruletable = this.FlowRuleNodes.get(key.toString());
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
        Set<String> set = this.FlowRuleNodes.keySet();
        Iterator<String> itr = set.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            List<FlowRuleNode> ruletable = this.FlowRuleNodes.get(key.toString());
            for(int j = 0; j < ruletable.size(); j++){
                if(rulename.equals(ruletable.get(j).rule_name)){
                    this.FlowRuleNodes.get(key.toString()).remove(ruletable.get(j));
                    dpid = key.toString();
                    break;
                }
            }
        }
        List<FlowRuleNode> ruletable = new ArrayList<FlowRuleNode>();
        ruletable = this.FlowRuleNodes.get(dpid);
        if(ruletable != null) {
            this.FlowRuleNodes.remove(dpid);
        }
        ruletable = FlowRuleNode.deleteFlowRuleNode(ruletable, rulename);
        this.FlowRuleNodes.put(dpid, ruletable);

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
/*        try {
            File f = new File(this.RESULT_PATH);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("staticEntryDeleted time(micro seconds) : " + ( end - start )/1000 +"\n");
            bw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
