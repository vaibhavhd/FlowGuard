package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntry;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yangtools.yang.binding.Identifiable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * list conflictTable {
 *     key "tableId"
 *     leaf tableId {
 *         type uint16;
 *     }
 *     list conflict-group-entry {
 *         key "id"
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
 *         uses conflict-info;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>flowguard/conflict-info-registry/conflictSwitch/conflictTable</i>
 *
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTableBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTableBuilder
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTableKey
 *
 */
public interface ConflictTable
    extends
    ChildOf<ConflictSwitch>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTable>,
    Identifiable<ConflictTableKey>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "conflictTable").intern();

    /**
     * @return <code>java.lang.Integer</code> <code>tableId</code>, or <code>null</code> if not present
     */
    java.lang.Integer getTableId();
    
    /**
     * @return <code>java.util.List</code> <code>conflictGroupEntry</code>, or <code>null</code> if not present
     */
    List<ConflictGroupEntry> getConflictGroupEntry();
    
    /**
     * Returns Primary Key of Yang List Type
     *
     *
     *
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTableKey</code> <code>key</code>, or <code>null</code> if not present
     */
    ConflictTableKey getKey();

}

