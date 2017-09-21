package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo.Action;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo.Protocol;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>flowguard</b>
 * <pre>
 * grouping conflict-info {
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
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>flowguard/conflict-info</i>
 *
 */
public interface ConflictInfo
    extends
    DataObject
{


    public enum Action {
        /**
         * drop = 1
         *
         */
        BLOCK(1, "BLOCK"),
        
        /**
         * forward = 2
         *
         */
        ALLOW(2, "ALLOW")
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
    
    public enum Protocol {
        TCP(1, "TCP"),
        
        UDP(2, "UDP"),
        
        ANY(3, "ANY")
        ;
    
    
        java.lang.String name;
        int value;
        private static final java.util.Map<java.lang.Integer, Protocol> VALUE_MAP;
    
        static {
            final com.google.common.collect.ImmutableMap.Builder<java.lang.Integer, Protocol> b = com.google.common.collect.ImmutableMap.builder();
            for (Protocol enumItem : Protocol.values())
            {
                b.put(enumItem.value, enumItem);
            }
    
            VALUE_MAP = b.build();
        }
    
        private Protocol(int value, java.lang.String name) {
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
         * @return corresponding Protocol item
         */
        public static Protocol forValue(int valueArg) {
            return VALUE_MAP.get(valueArg);
        }
    }

    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:params:xml:ns:yang:flowguard",
        "2017-09-13", "conflict-info").intern();

    /**
     * @return <code>java.lang.Integer</code> <code>priority</code>, or <code>null</code> if not present
     */
    java.lang.Integer getPriority();
    
    /**
     * @return <code>java.lang.Integer</code> <code>id</code>, or <code>null</code> if not present
     */
    java.lang.Integer getId();
    
    /**
     * @return <code>java.lang.Long</code> <code>vlanId</code>, or <code>null</code> if not present
     */
    java.lang.Long getVlanId();
    
    /**
     * @return <code>java.lang.String</code> <code>inPort</code>, or <code>null</code> if not present
     */
    java.lang.String getInPort();
    
    /**
     * @return <code>java.lang.String</code> <code>dlSrc</code>, or <code>null</code> if not present
     */
    java.lang.String getDlSrc();
    
    /**
     * @return <code>java.lang.String</code> <code>dlDst</code>, or <code>null</code> if not present
     */
    java.lang.String getDlDst();
    
    /**
     * @return <code>java.lang.String</code> <code>nwSrc</code>, or <code>null</code> if not present
     */
    java.lang.String getNwSrc();
    
    /**
     * @return <code>java.lang.String</code> <code>nwDst</code>, or <code>null</code> if not present
     */
    java.lang.String getNwDst();
    
    /**
     * @return <code>java.lang.Integer</code> <code>l4Src</code>, or <code>null</code> if not present
     */
    java.lang.Integer getL4Src();
    
    /**
     * @return <code>java.lang.Integer</code> <code>l4Dst</code>, or <code>null</code> if not present
     */
    java.lang.Integer getL4Dst();
    
    /**
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo.Action</code> <code>action</code>, or <code>null</code> if not present
     */
    Action getAction();
    
    /**
     * @return <code>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo.Protocol</code> <code>protocol</code>, or <code>null</code> if not present
     */
    Protocol getProtocol();
    
    /**
     * @return <code>java.lang.Integer</code> <code>genCount</code>, or <code>null</code> if not present
     */
    java.lang.Integer getGenCount();
    
    /**
     * @return <code>java.lang.Integer</code> <code>shCount</code>, or <code>null</code> if not present
     */
    java.lang.Integer getShCount();
    
    /**
     * @return <code>java.lang.Integer</code> <code>redCount</code>, or <code>null</code> if not present
     */
    java.lang.Integer getRedCount();
    
    /**
     * @return <code>java.lang.Integer</code> <code>corCount</code>, or <code>null</code> if not present
     */
    java.lang.Integer getCorCount();
    
    /**
     * @return <code>java.lang.Integer</code> <code>overCount</code>, or <code>null</code> if not present
     */
    java.lang.Integer getOverCount();
    
    /**
     * @return <code>java.lang.String</code> <code>conflictType</code>, or <code>null</code> if not present
     */
    java.lang.String getConflictType();
    
    /**
     * @return <code>java.lang.Integer</code> <code>conflictGroupNumber</code>, or <code>null</code> if not present
     */
    java.lang.Integer getConflictGroupNumber();
    
    /**
     * @return <code>java.lang.Boolean</code> <code>resolution</code>, or <code>null</code> if not present
     */
    java.lang.Boolean isResolution();
    
    /**
     * @return <code>java.lang.String</code> <code>mechanism</code>, or <code>null</code> if not present
     */
    java.lang.String getMechanism();

}

