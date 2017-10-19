package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913;
import org.opendaylight.yangtools.yang.binding.DataRoot;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * module flowguard {
 *     yang-version 1;
 *     namespace "urn:opendaylight:params:xml:ns:yang:flowguard";
 *     prefix "flowguard";
 *
 *     revision 2017-09-13 {
 *         description "";
 *     }
 *
 *     container fwrule-registry {
 *         list fwrule-registry-entry {
 *             key "ruleId"
 *             leaf ruleId {
 *                 type uint16;
 *             }
 *             leaf priority {
 *                 type uint16;
 *             }
 *             leaf sourceIpAddress {
 *                 type string;
 *             }
 *             leaf destinationIpAddress {
 *                 type string;
 *             }
 *             leaf sourcePort {
 *                 type string;
 *             }
 *             leaf destinationPort {
 *                 type string;
 *             }
 *             leaf action {
 *                 type enumeration;
 *             }
 *             uses fwrule;
 *         }
 *     }
 *     container conflict-info-registry {
 *         list conflictSwitch {
 *             key "switchId"
 *             leaf switchId {
 *                 type string;
 *             }
 *             list conflictTable {
 *                 key "tableId"
 *                 leaf tableId {
 *                     type uint16;
 *                 }
 *                 list conflict-group-entry {
 *                     key "id"
 *                     leaf priority {
 *                         type uint16;
 *                     }
 *                     leaf id {
 *                         type uint16;
 *                     }
 *                     leaf vlan-id {
 *                         type uint32;
 *                     }
 *                     leaf in-port {
 *                         type string;
 *                     }
 *                     leaf dl-src {
 *                         type string;
 *                     }
 *                     leaf dl-dst {
 *                         type string;
 *                     }
 *                     leaf nw-src {
 *                         type string;
 *                     }
 *                     leaf nw-dst {
 *                         type string;
 *                     }
 *                     leaf l4-src {
 *                         type uint16;
 *                     }
 *                     leaf l4-dst {
 *                         type uint16;
 *                     }
 *                     leaf action {
 *                         type enumeration;
 *                     }
 *                     leaf protocol {
 *                         type enumeration;
 *                     }
 *                     leaf gen-count {
 *                         type uint16;
 *                     }
 *                     leaf sh-count {
 *                         type uint16;
 *                     }
 *                     leaf red-count {
 *                         type uint16;
 *                     }
 *                     leaf cor-count {
 *                         type uint16;
 *                     }
 *                     leaf over-count {
 *                         type uint16;
 *                     }
 *                     leaf conflict-type {
 *                         type string;
 *                     }
 *                     leaf conflict-group-number {
 *                         type uint16;
 *                     }
 *                     leaf resolution {
 *                         type resolution;
 *                     }
 *                     leaf mechanism {
 *                         type string;
 *                     }
 *                     uses conflict-info;
 *                 }
 *             }
 *         }
 *     }
 *     container controls {
 *         leaf action {
 *             type enumeration;
 *         }
 *         leaf greeting {
 *             type string;
 *         }
 *     }
 *     container flowguard-status {
 *         leaf flowguard-status {
 *             type string;
 *         }
 *     }
 *
 *     grouping fwrule {
 *         leaf ruleId {
 *             type uint16;
 *         }
 *         leaf priority {
 *             type uint16;
 *         }
 *         leaf sourceIpAddress {
 *             type string;
 *         }
 *         leaf destinationIpAddress {
 *             type string;
 *         }
 *         leaf sourcePort {
 *             type string;
 *         }
 *         leaf destinationPort {
 *             type string;
 *         }
 *         leaf action {
 *             type enumeration;
 *         }
 *     }
 *     grouping conflict-info {
 *         leaf priority {
 *             type uint16;
 *         }
 *         leaf id {
 *             type uint16;
 *         }
 *         leaf vlan-id {
 *             type uint32;
 *         }
 *         leaf in-port {
 *             type string;
 *         }
 *         leaf dl-src {
 *             type string;
 *         }
 *         leaf dl-dst {
 *             type string;
 *         }
 *         leaf nw-src {
 *             type string;
 *         }
 *         leaf nw-dst {
 *             type string;
 *         }
 *         leaf l4-src {
 *             type uint16;
 *         }
 *         leaf l4-dst {
 *             type uint16;
 *         }
 *         leaf action {
 *             type enumeration;
 *         }
 *         leaf protocol {
 *             type enumeration;
 *         }
 *         leaf gen-count {
 *             type uint16;
 *         }
 *         leaf sh-count {
 *             type uint16;
 *         }
 *         leaf red-count {
 *             type uint16;
 *         }
 *         leaf cor-count {
 *             type uint16;
 *         }
 *         leaf over-count {
 *             type uint16;
 *         }
 *         leaf conflict-type {
 *             type string;
 *         }
 *         leaf conflict-group-number {
 *             type uint16;
 *         }
 *         leaf resolution {
 *             type resolution;
 *         }
 *         leaf mechanism {
 *             type string;
 *         }
 *     }
 *
 *     rpc get-conflicts {
 *         input {
 *             leaf conflict-type {
 *                 type enumeration;
 *             }
 *         }
 *         
 *         output {
 *             list conflict-group-list {
 *                 key "id"
 *                 leaf priority {
 *                     type uint16;
 *                 }
 *                 leaf id {
 *                     type uint16;
 *                 }
 *                 leaf vlan-id {
 *                     type uint32;
 *                 }
 *                 leaf in-port {
 *                     type string;
 *                 }
 *                 leaf dl-src {
 *                     type string;
 *                 }
 *                 leaf dl-dst {
 *                     type string;
 *                 }
 *                 leaf nw-src {
 *                     type string;
 *                 }
 *                 leaf nw-dst {
 *                     type string;
 *                 }
 *                 leaf l4-src {
 *                     type uint16;
 *                 }
 *                 leaf l4-dst {
 *                     type uint16;
 *                 }
 *                 leaf action {
 *                     type enumeration;
 *                 }
 *                 leaf protocol {
 *                     type enumeration;
 *                 }
 *                 leaf gen-count {
 *                     type uint16;
 *                 }
 *                 leaf sh-count {
 *                     type uint16;
 *                 }
 *                 leaf red-count {
 *                     type uint16;
 *                 }
 *                 leaf cor-count {
 *                     type uint16;
 *                 }
 *                 leaf over-count {
 *                     type uint16;
 *                 }
 *                 leaf conflict-type {
 *                     type string;
 *                 }
 *                 leaf conflict-group-number {
 *                     type uint16;
 *                 }
 *                 leaf resolution {
 *                     type resolution;
 *                 }
 *                 leaf mechanism {
 *                     type string;
 *                 }
 *                 uses conflict-info;
 *             }
 *         }
 *     }
 *     rpc flowguard-control {
 *         input {
 *             leaf action {
 *                 type enumeration;
 *             }
 *         }
 *         
 *         output {
 *             leaf greeting {
 *                 type string;
 *             }
 *         }
 *     }
 *     rpc add-fwrule {
 *         input {
 *             leaf ruleId {
 *                 type uint16;
 *             }
 *             leaf priority {
 *                 type uint16;
 *             }
 *             leaf sourceIpAddress {
 *                 type string;
 *             }
 *             leaf destinationIpAddress {
 *                 type string;
 *             }
 *             leaf sourcePort {
 *                 type string;
 *             }
 *             leaf destinationPort {
 *                 type string;
 *             }
 *             leaf action {
 *                 type enumeration;
 *             }
 *         }
 *         
 *         output {
 *             leaf greeting {
 *                 type string;
 *             }
 *         }
 *     }
 * }
 * </pre>
 *
 */
public interface FlowguardData
    extends
    DataRoot
{




    /**
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FwruleRegistry</code> <code>fwruleRegistry</code>, or <code>null</code> if not present
     */
    FwruleRegistry getFwruleRegistry();
    
    /**
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfoRegistry</code> <code>conflictInfoRegistry</code>, or <code>null</code> if not present
     */
    ConflictInfoRegistry getConflictInfoRegistry();
    
    /**
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Controls</code> <code>controls</code>, or <code>null</code> if not present
     */
    Controls getControls();
    
    /**
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FlowguardStatus</code> <code>flowguardStatus</code>, or <code>null</code> if not present
     */
    FlowguardStatus getFlowguardStatus();

}

