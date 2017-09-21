package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.GetConflictsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yangtools.yang.binding.Identifiable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * list conflict-group-list {
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
 * <i>flowguard/get-conflicts/output/conflict-group-list</i>
 *
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupListBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupListBuilder
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupListKey
 *
 */
public interface ConflictGroupList
    extends
    ChildOf<GetConflictsOutput>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>,
    ConflictInfo,
    Identifiable<ConflictGroupListKey>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "conflict-group-list").intern();

    /**
     * Returns Primary Key of Yang List Type
     *
     *
     *
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupListKey</code> <code>key</code>, or <code>null</code> if not present
     */
    ConflictGroupListKey getKey();

}

