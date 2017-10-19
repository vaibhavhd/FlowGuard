package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule.Action;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.common.QName;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * grouping fwrule {
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
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>flowguard/fwrule</i>
 *
 */
public interface Fwrule
    extends
    DataObject
{


    public enum Action {
        /**
         * Deny = 1
         *
         */
        Deny(1, "deny"),
        
        /**
         * Allow = 2
         *
         */
        Allow(2, "allow")
        ;
    
    
        java.lang.String name;
        int value;
        private static final java.util.Map<java.lang.Integer, Action> VALUE_MAP;
    
        static {
            final com.google.common.collect.ImmutableMap.Builder<java.lang.Integer, Action> b = com.google.common.collect.ImmutableMap.builder();
            for (Action enumItem : Action.values())
            {
                b.put(enumItem.value, enumItem);
            }
    
            VALUE_MAP = b.build();
        }
    
        private Action(int value, java.lang.String name) {
            this.value = value;
            this.name = name;
        }
    
        /**
         * Returns the name of the enumeration item as it is specified in the input yang.
         *
         * @return the name of the enumeration item as it is specified in the input yang
         */
        public java.lang.String getName() {
            return name;
        }
    
        /**
         * @return integer value
         */
        public int getIntValue() {
            return value;
        }
    
        /**
         * @param valueArg
         * @return corresponding Action item
         */
        public static Action forValue(int valueArg) {
            return VALUE_MAP.get(valueArg);
        }
    }

    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "fwrule").intern();

    /**
     * Name of FW rule
     *
     *
     *
     * @return <code>java.lang.Integer</code> <code>ruleId</code>, or <code>null</code> if not present
     */
    java.lang.Integer getRuleId();
    
    /**
     * Priority of the FW rule
     *
     *
     *
     * @return <code>java.lang.Integer</code> <code>priority</code>, or <code>null</code> if not present
     */
    java.lang.Integer getPriority();
    
    /**
     * The IP address source
     *
     *
     *
     * @return <code>java.lang.String</code> <code>sourceIpAddress</code>, or <code>null</code> if not present
     */
    java.lang.String getSourceIpAddress();
    
    /**
     * The IP address destination
     *
     *
     *
     * @return <code>java.lang.String</code> <code>destinationIpAddress</code>, or <code>null</code> if not present
     */
    java.lang.String getDestinationIpAddress();
    
    /**
     * The source port
     *
     *
     *
     * @return <code>java.lang.String</code> <code>sourcePort</code>, or <code>null</code> if not present
     */
    java.lang.String getSourcePort();
    
    /**
     * The destination port
     *
     *
     *
     * @return <code>java.lang.String</code> <code>destinationPort</code>, or <code>null</code> if not present
     */
    java.lang.String getDestinationPort();
    
    /**
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule.Action</code> <code>action</code>, or <code>null</code> if not present
     */
    Action getAction();

}

