package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FwruleRegistry;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yangtools.yang.binding.Identifiable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * list fwrule-registry-entry {
 *     key "ruleId"
 *     leaf ruleId {
 *         type uint16;
 *     }
 *     leaf node {
 *         type string;
 *     }
 *     leaf inPort {
 *         type uint16;
 *     }
 *     leaf priority {
 *         type uint16;
 *     }
 *     leaf sourceIpAddress {
 *         type string;
 *     }
 *     leaf destinationIpAddress {
 *         type string;
 *     }
 *     leaf sourcePort {
 *         type string;
 *     }
 *     leaf destinationPort {
 *         type string;
 *     }
 *     leaf action {
 *         type enumeration;
 *     }
 *     uses fwrule;
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>flowguard/fwrule-registry/fwrule-registry-entry</i>
 *
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntryBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntryBuilder
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntryKey
 *
 */
public interface FwruleRegistryEntry
    extends
    ChildOf<FwruleRegistry>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>,
    Fwrule,
    Identifiable<FwruleRegistryEntryKey>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "fwrule-registry-entry").intern();

    /**
     * Returns Primary Key of Yang List Type
     *
     *
     *
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntryKey</code> <code>key</code>, or <code>null</code> if not present
     */
    FwruleRegistryEntryKey getKey();

}

