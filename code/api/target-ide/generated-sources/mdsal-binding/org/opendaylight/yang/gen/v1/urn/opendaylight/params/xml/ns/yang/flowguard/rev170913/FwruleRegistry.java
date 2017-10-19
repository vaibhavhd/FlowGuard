package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * container fwrule-registry {
 *     list fwrule-registry-entry {
 *         key "ruleId"
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
 *         uses fwrule;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>flowguard/fwrule-registry</i>
 *
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FwruleRegistryBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FwruleRegistryBuilder
 *
 */
public interface FwruleRegistry
    extends
    ChildOf<FlowguardData>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.FwruleRegistry>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "fwrule-registry").intern();

    /**
     * @return <code>java.util.List</code> <code>fwruleRegistryEntry</code>, or <code>null</code> if not present
     */
    List<FwruleRegistryEntry> getFwruleRegistryEntry();

}

