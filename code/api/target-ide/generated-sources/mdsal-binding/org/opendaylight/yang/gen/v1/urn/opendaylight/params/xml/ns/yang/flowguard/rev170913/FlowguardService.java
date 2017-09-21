package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import java.util.concurrent.Future;


/**
 * Interface for implementing the following YANG RPCs defined in module <b>flowguard</b>
 * <pre>
 * rpc flowguard-control {
 *     input {
 *         leaf action {
 *             type enumeration;
 *         }
 *     }
 *     
 *     output {
 *         leaf greeting {
 *             type string;
 *         }
 *     }
 * }
 * rpc add-fwrule {
 *     input {
 *         leaf ruleId {
 *             type uint16;
 *         }
 *         leaf node {
 *             type string;
 *         }
 *         leaf inPort {
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
 *     
 *     output {
 *         leaf greeting {
 *             type string;
 *         }
 *     }
 * }
 * rpc get-conflicts {
 *     input {
 *         leaf conflict-type {
 *             type enumeration;
 *         }
 *     }
 *     
 *     output {
 *         list conflict-group-list {
 *             key "id"
 *             leaf priority {
 *                 type uint16;
 *             }
 *             leaf id {
 *                 type uint16;
 *             }
 *             leaf vlan-id {
 *                 type uint32;
 *             }
 *             leaf in-port {
 *                 type string;
 *             }
 *             leaf dl-src {
 *                 type string;
 *             }
 *             leaf dl-dst {
 *                 type string;
 *             }
 *             leaf nw-src {
 *                 type string;
 *             }
 *             leaf nw-dst {
 *                 type string;
 *             }
 *             leaf l4-src {
 *                 type uint16;
 *             }
 *             leaf l4-dst {
 *                 type uint16;
 *             }
 *             leaf action {
 *                 type enumeration;
 *             }
 *             leaf protocol {
 *                 type enumeration;
 *             }
 *             leaf gen-count {
 *                 type uint16;
 *             }
 *             leaf sh-count {
 *                 type uint16;
 *             }
 *             leaf red-count {
 *                 type uint16;
 *             }
 *             leaf cor-count {
 *                 type uint16;
 *             }
 *             leaf over-count {
 *                 type uint16;
 *             }
 *             leaf conflict-type {
 *                 type string;
 *             }
 *             leaf conflict-group-number {
 *                 type uint16;
 *             }
 *             leaf resolution {
 *                 type resolution;
 *             }
 *             leaf mechanism {
 *                 type string;
 *             }
 *             uses conflict-info;
 *         }
 *     }
 * }
 * </pre>
 *
 */
public interface FlowguardService
    extends
    RpcService
{




    Future<RpcResult<FlowguardControlOutput>> flowguardControl(FlowguardControlInput input);
    
    Future<RpcResult<AddFwruleOutput>> addFwrule(AddFwruleInput input);
    
    /**
     * @return <code>java.util.concurrent.Future</code> <code>conflicts</code>, or <code>null</code> if not present
     */
    Future<RpcResult<GetConflictsOutput>> getConflicts(GetConflictsInput input);

}

