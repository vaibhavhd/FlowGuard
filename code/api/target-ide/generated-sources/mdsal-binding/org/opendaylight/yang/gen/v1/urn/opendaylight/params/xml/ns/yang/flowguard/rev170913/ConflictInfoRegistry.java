package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch;
import org.opendaylight.yangtools.yang.common.QName;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * container conflict-info-registry {
 *     list conflictSwitch {
 *         key "switchId"
 *         leaf switchId {
 *             type string;
 *         }
 *         list conflictTable {
 *             key "tableId"
 *             leaf tableId {
 *                 type uint16;
 *             }
 *             list conflict-group-entry {
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
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>flowguard/conflict-info-registry</i>
 *
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfoRegistryBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfoRegistryBuilder
 *
 */
public interface ConflictInfoRegistry
    extends
    ChildOf<FlowguardData>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfoRegistry>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "conflict-info-registry").intern();

    /**
     * @return <code>java.util.List</code> <code>conflictSwitch</code>, or <code>null</code> if not present
     */
    List<ConflictSwitch> getConflictSwitch();

}

