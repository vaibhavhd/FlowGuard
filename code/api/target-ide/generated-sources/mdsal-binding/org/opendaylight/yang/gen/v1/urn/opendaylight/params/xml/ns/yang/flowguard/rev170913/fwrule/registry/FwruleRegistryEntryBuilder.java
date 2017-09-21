package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.AugmentationHolder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule.Action;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.concepts.Builder;
import java.util.Objects;
import java.util.Collections;
import java.util.Map;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry} instances.
 *
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry
 *
 */
public class FwruleRegistryEntryBuilder implements Builder <org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry> {

    private Action _action;
    private java.lang.String _destinationIpAddress;
    private java.lang.String _destinationPort;
    private java.lang.Integer _inPort;
    private FwruleRegistryEntryKey _key;
    private java.lang.String _node;
    private java.lang.Integer _priority;
    private java.lang.Integer _ruleId;
    private java.lang.String _sourceIpAddress;
    private java.lang.String _sourcePort;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>> augmentation = Collections.emptyMap();

    public FwruleRegistryEntryBuilder() {
    }
    public FwruleRegistryEntryBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule arg) {
        this._ruleId = arg.getRuleId();
        this._node = arg.getNode();
        this._inPort = arg.getInPort();
        this._priority = arg.getPriority();
        this._sourceIpAddress = arg.getSourceIpAddress();
        this._destinationIpAddress = arg.getDestinationIpAddress();
        this._sourcePort = arg.getSourcePort();
        this._destinationPort = arg.getDestinationPort();
        this._action = arg.getAction();
    }

    public FwruleRegistryEntryBuilder(FwruleRegistryEntry base) {
        if (base.getKey() == null) {
            this._key = new FwruleRegistryEntryKey(
                base.getRuleId()
            );
            this._ruleId = base.getRuleId();
        } else {
            this._key = base.getKey();
            this._ruleId = _key.getRuleId();
        }
        this._action = base.getAction();
        this._destinationIpAddress = base.getDestinationIpAddress();
        this._destinationPort = base.getDestinationPort();
        this._inPort = base.getInPort();
        this._node = base.getNode();
        this._priority = base.getPriority();
        this._sourceIpAddress = base.getSourceIpAddress();
        this._sourcePort = base.getSourcePort();
        if (base instanceof FwruleRegistryEntryImpl) {
            FwruleRegistryEntryImpl impl = (FwruleRegistryEntryImpl) base;
            if (!impl.augmentation.isEmpty()) {
                this.augmentation = new HashMap<>(impl.augmentation);
            }
        } else if (base instanceof AugmentationHolder) {
            @SuppressWarnings("unchecked")
            AugmentationHolder<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry> casted =(AugmentationHolder<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>) base;
            if (!casted.augmentations().isEmpty()) {
                this.augmentation = new HashMap<>(casted.augmentations());
            }
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule) {
            this._ruleId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getRuleId();
            this._node = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getNode();
            this._inPort = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getInPort();
            this._priority = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getPriority();
            this._sourceIpAddress = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getSourceIpAddress();
            this._destinationIpAddress = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getDestinationIpAddress();
            this._sourcePort = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getSourcePort();
            this._destinationPort = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getDestinationPort();
            this._action = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule)arg).getAction();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.Fwrule] \n" +
              "but was: " + arg
            );
        }
    }

    public Action getAction() {
        return _action;
    }
    
    public java.lang.String getDestinationIpAddress() {
        return _destinationIpAddress;
    }
    
    public java.lang.String getDestinationPort() {
        return _destinationPort;
    }
    
    public java.lang.Integer getInPort() {
        return _inPort;
    }
    
    public FwruleRegistryEntryKey getKey() {
        return _key;
    }
    
    public java.lang.String getNode() {
        return _node;
    }
    
    public java.lang.Integer getPriority() {
        return _priority;
    }
    
    public java.lang.Integer getRuleId() {
        return _ruleId;
    }
    
    public java.lang.String getSourceIpAddress() {
        return _sourceIpAddress;
    }
    
    public java.lang.String getSourcePort() {
        return _sourcePort;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

     
    public FwruleRegistryEntryBuilder setAction(final Action value) {
        this._action = value;
        return this;
    }
    
     
    public FwruleRegistryEntryBuilder setDestinationIpAddress(final java.lang.String value) {
        this._destinationIpAddress = value;
        return this;
    }
    
     
    public FwruleRegistryEntryBuilder setDestinationPort(final java.lang.String value) {
        this._destinationPort = value;
        return this;
    }
    
     
     private static void checkInPortRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public FwruleRegistryEntryBuilder setInPort(final java.lang.Integer value) {
    if (value != null) {
        checkInPortRange(value);
    }
        this._inPort = value;
        return this;
    }
    
     
    public FwruleRegistryEntryBuilder setKey(final FwruleRegistryEntryKey value) {
        this._key = value;
        return this;
    }
    
     
    public FwruleRegistryEntryBuilder setNode(final java.lang.String value) {
        this._node = value;
        return this;
    }
    
     
     private static void checkPriorityRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public FwruleRegistryEntryBuilder setPriority(final java.lang.Integer value) {
    if (value != null) {
        checkPriorityRange(value);
    }
        this._priority = value;
        return this;
    }
    
     
     private static void checkRuleIdRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public FwruleRegistryEntryBuilder setRuleId(final java.lang.Integer value) {
    if (value != null) {
        checkRuleIdRange(value);
    }
        this._ruleId = value;
        return this;
    }
    
     
    public FwruleRegistryEntryBuilder setSourceIpAddress(final java.lang.String value) {
        this._sourceIpAddress = value;
        return this;
    }
    
     
    public FwruleRegistryEntryBuilder setSourcePort(final java.lang.String value) {
        this._sourcePort = value;
        return this;
    }
    
    public FwruleRegistryEntryBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry> augmentation) {
        if (augmentation == null) {
            return removeAugmentation(augmentationType);
        }
    
        if (!(this.augmentation instanceof HashMap)) {
            this.augmentation = new HashMap<>();
        }
    
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }
    
    public FwruleRegistryEntryBuilder removeAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>> augmentationType) {
        if (this.augmentation instanceof HashMap) {
            this.augmentation.remove(augmentationType);
        }
        return this;
    }

    @Override
    public FwruleRegistryEntry build() {
        return new FwruleRegistryEntryImpl(this);
    }

    private static final class FwruleRegistryEntryImpl implements FwruleRegistryEntry {

        @Override
        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry.class;
        }

        private final Action _action;
        private final java.lang.String _destinationIpAddress;
        private final java.lang.String _destinationPort;
        private final java.lang.Integer _inPort;
        private final FwruleRegistryEntryKey _key;
        private final java.lang.String _node;
        private final java.lang.Integer _priority;
        private final java.lang.Integer _ruleId;
        private final java.lang.String _sourceIpAddress;
        private final java.lang.String _sourcePort;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>> augmentation = Collections.emptyMap();

        private FwruleRegistryEntryImpl(FwruleRegistryEntryBuilder base) {
            if (base.getKey() == null) {
                this._key = new FwruleRegistryEntryKey(
                    base.getRuleId()
                );
                this._ruleId = base.getRuleId();
            } else {
                this._key = base.getKey();
                this._ruleId = _key.getRuleId();
            }
            this._action = base.getAction();
            this._destinationIpAddress = base.getDestinationIpAddress();
            this._destinationPort = base.getDestinationPort();
            this._inPort = base.getInPort();
            this._node = base.getNode();
            this._priority = base.getPriority();
            this._sourceIpAddress = base.getSourceIpAddress();
            this._sourcePort = base.getSourcePort();
            switch (base.augmentation.size()) {
            case 0:
                this.augmentation = Collections.emptyMap();
                break;
            case 1:
                final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>> e = base.augmentation.entrySet().iterator().next();
                this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>>singletonMap(e.getKey(), e.getValue());
                break;
            default :
                this.augmentation = new HashMap<>(base.augmentation);
            }
        }

        @Override
        public Action getAction() {
            return _action;
        }
        
        @Override
        public java.lang.String getDestinationIpAddress() {
            return _destinationIpAddress;
        }
        
        @Override
        public java.lang.String getDestinationPort() {
            return _destinationPort;
        }
        
        @Override
        public java.lang.Integer getInPort() {
            return _inPort;
        }
        
        @Override
        public FwruleRegistryEntryKey getKey() {
            return _key;
        }
        
        @Override
        public java.lang.String getNode() {
            return _node;
        }
        
        @Override
        public java.lang.Integer getPriority() {
            return _priority;
        }
        
        @Override
        public java.lang.Integer getRuleId() {
            return _ruleId;
        }
        
        @Override
        public java.lang.String getSourceIpAddress() {
            return _sourceIpAddress;
        }
        
        @Override
        public java.lang.String getSourcePort() {
            return _sourcePort;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        private int hash = 0;
        private volatile boolean hashValid = false;
        
        @Override
        public int hashCode() {
            if (hashValid) {
                return hash;
            }
        
            final int prime = 31;
            int result = 1;
            result = prime * result + Objects.hashCode(_action);
            result = prime * result + Objects.hashCode(_destinationIpAddress);
            result = prime * result + Objects.hashCode(_destinationPort);
            result = prime * result + Objects.hashCode(_inPort);
            result = prime * result + Objects.hashCode(_key);
            result = prime * result + Objects.hashCode(_node);
            result = prime * result + Objects.hashCode(_priority);
            result = prime * result + Objects.hashCode(_ruleId);
            result = prime * result + Objects.hashCode(_sourceIpAddress);
            result = prime * result + Objects.hashCode(_sourcePort);
            result = prime * result + Objects.hashCode(augmentation);
        
            hash = result;
            hashValid = true;
            return result;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof DataObject)) {
                return false;
            }
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry other = (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry)obj;
            if (!Objects.equals(_action, other.getAction())) {
                return false;
            }
            if (!Objects.equals(_destinationIpAddress, other.getDestinationIpAddress())) {
                return false;
            }
            if (!Objects.equals(_destinationPort, other.getDestinationPort())) {
                return false;
            }
            if (!Objects.equals(_inPort, other.getInPort())) {
                return false;
            }
            if (!Objects.equals(_key, other.getKey())) {
                return false;
            }
            if (!Objects.equals(_node, other.getNode())) {
                return false;
            }
            if (!Objects.equals(_priority, other.getPriority())) {
                return false;
            }
            if (!Objects.equals(_ruleId, other.getRuleId())) {
                return false;
            }
            if (!Objects.equals(_sourceIpAddress, other.getSourceIpAddress())) {
                return false;
            }
            if (!Objects.equals(_sourcePort, other.getSourcePort())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                FwruleRegistryEntryImpl otherImpl = (FwruleRegistryEntryImpl) obj;
                if (!Objects.equals(augmentation, otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntry>> e : augmentation.entrySet()) {
                    if (!e.getValue().equals(other.getAugmentation(e.getKey()))) {
                        return false;
                    }
                }
                // .. and give the other one the chance to do the same
                if (!obj.equals(this)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public java.lang.String toString() {
            java.lang.String name = "FwruleRegistryEntry [";
            java.lang.StringBuilder builder = new java.lang.StringBuilder (name);
            if (_action != null) {
                builder.append("_action=");
                builder.append(_action);
                builder.append(", ");
            }
            if (_destinationIpAddress != null) {
                builder.append("_destinationIpAddress=");
                builder.append(_destinationIpAddress);
                builder.append(", ");
            }
            if (_destinationPort != null) {
                builder.append("_destinationPort=");
                builder.append(_destinationPort);
                builder.append(", ");
            }
            if (_inPort != null) {
                builder.append("_inPort=");
                builder.append(_inPort);
                builder.append(", ");
            }
            if (_key != null) {
                builder.append("_key=");
                builder.append(_key);
                builder.append(", ");
            }
            if (_node != null) {
                builder.append("_node=");
                builder.append(_node);
                builder.append(", ");
            }
            if (_priority != null) {
                builder.append("_priority=");
                builder.append(_priority);
                builder.append(", ");
            }
            if (_ruleId != null) {
                builder.append("_ruleId=");
                builder.append(_ruleId);
                builder.append(", ");
            }
            if (_sourceIpAddress != null) {
                builder.append("_sourceIpAddress=");
                builder.append(_sourceIpAddress);
                builder.append(", ");
            }
            if (_sourcePort != null) {
                builder.append("_sourcePort=");
                builder.append(_sourcePort);
            }
            final int builderLength = builder.length();
            final int builderAdditionalLength = builder.substring(name.length(), builderLength).length();
            if (builderAdditionalLength > 2 && !builder.substring(builderLength - 2, builderLength).equals(", ")) {
                builder.append(", ");
            }
            builder.append("augmentation=");
            builder.append(augmentation.values());
            return builder.append(']').toString();
        }
    }

}
