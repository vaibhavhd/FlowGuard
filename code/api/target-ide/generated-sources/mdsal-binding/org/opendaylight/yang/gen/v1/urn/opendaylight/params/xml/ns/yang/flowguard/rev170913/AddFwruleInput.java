package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * container input {
 *     leaf ruleId {
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
 * <i>flowguard/add-fwrule/input</i>
 *
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.AddFwruleInputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.AddFwruleInputBuilder
 *
 */
public interface AddFwruleInput
    extends
    Fwrule,
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.AddFwruleInput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "input").intern();


}
