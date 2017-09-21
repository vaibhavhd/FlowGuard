package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yangtools.yang.binding.Identifiable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * list conflict-group-entry {
 *     key "id"
 *     leaf priority {
 *         type uint16;
 *     }
 *     leaf id {
 *         type uint16;
 *     }
 *     leaf vlan-id {
 *         type uint32;
 *     }
 *     leaf in-port {
 *         type string;
 *     }
 *     leaf dl-src {
 *         type string;
 *     }
 *     leaf dl-dst {
 *         type string;
 *     }
 *     leaf nw-src {
 *         type string;
 *     }
 *     leaf nw-dst {
 *         type string;
 *     }
 *     leaf l4-src {
 *         type uint16;
 *     }
 *     leaf l4-dst {
 *         type uint16;
 *     }
 *     leaf action {
 *         type enumeration;
 *     }
 *     leaf protocol {
 *         type enumeration;
 *     }
 *     leaf gen-count {
 *         type uint16;
 *     }
 *     leaf sh-count {
 *         type uint16;
 *     }
 *     leaf red-count {
 *         type uint16;
 *     }
 *     leaf cor-count {
 *         type uint16;
 *     }
 *     leaf over-count {
 *         type uint16;
 *     }
 *     leaf conflict-type {
 *         type string;
 *     }
 *     leaf conflict-group-number {
 *         type uint16;
 *     }
 *     leaf resolution {
 *         type resolution;
 *     }
 *     leaf mechanism {
 *         type string;
 *     }
 *     uses conflict-info;
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>flowguard/conflict-info-registry/conflictSwitch/conflictTable/conflict-group-entry</i>
 *
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryBuilder
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryKey
 *
 */
public interface ConflictGroupEntry
    extends
    ChildOf<ConflictTable>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntry>,
    ConflictInfo,
    Identifiable<ConflictGroupEntryKey>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "conflict-group-entry").intern();

    /**
     * Returns Primary Key of Yang List Type
     *
     *
     *
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryKey</code> <code>key</code>, or <code>null</code> if not present
     */
    ConflictGroupEntryKey getKey();

}

