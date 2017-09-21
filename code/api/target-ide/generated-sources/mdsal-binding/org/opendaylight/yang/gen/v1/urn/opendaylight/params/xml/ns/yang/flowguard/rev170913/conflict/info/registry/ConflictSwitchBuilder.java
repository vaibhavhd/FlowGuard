package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.AugmentationHolder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTable;
import java.util.Objects;
import java.util.List;
import java.util.Collections;
import java.util.Map;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch} instances.
 *
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch
 *
 */
public class ConflictSwitchBuilder implements Builder <org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch> {

    private List<ConflictTable> _conflictTable;
    private ConflictSwitchKey _key;
    private java.lang.String _switchId;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>> augmentation = Collections.emptyMap();

    public ConflictSwitchBuilder() {
    }

    public ConflictSwitchBuilder(ConflictSwitch base) {
        if (base.getKey() == null) {
            this._key = new ConflictSwitchKey(
                base.getSwitchId()
            );
            this._switchId = base.getSwitchId();
        } else {
            this._key = base.getKey();
            this._switchId = _key.getSwitchId();
        }
        this._conflictTable = base.getConflictTable();
        if (base instanceof ConflictSwitchImpl) {
            ConflictSwitchImpl impl = (ConflictSwitchImpl) base;
            if (!impl.augmentation.isEmpty()) {
                this.augmentation = new HashMap<>(impl.augmentation);
            }
        } else if (base instanceof AugmentationHolder) {
            @SuppressWarnings("unchecked")
            AugmentationHolder<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch> casted =(AugmentationHolder<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>) base;
            if (!casted.augmentations().isEmpty()) {
                this.augmentation = new HashMap<>(casted.augmentations());
            }
        }
    }


    public List<ConflictTable> getConflictTable() {
        return _conflictTable;
    }
    
    public ConflictSwitchKey getKey() {
        return _key;
    }
    
    public java.lang.String getSwitchId() {
        return _switchId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

     
    public ConflictSwitchBuilder setConflictTable(final List<ConflictTable> value) {
        this._conflictTable = value;
        return this;
    }
    
     
    public ConflictSwitchBuilder setKey(final ConflictSwitchKey value) {
        this._key = value;
        return this;
    }
    
     
    public ConflictSwitchBuilder setSwitchId(final java.lang.String value) {
        this._switchId = value;
        return this;
    }
    
    public ConflictSwitchBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch> augmentation) {
        if (augmentation == null) {
            return removeAugmentation(augmentationType);
        }
    
        if (!(this.augmentation instanceof HashMap)) {
            this.augmentation = new HashMap<>();
        }
    
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }
    
    public ConflictSwitchBuilder removeAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>> augmentationType) {
        if (this.augmentation instanceof HashMap) {
            this.augmentation.remove(augmentationType);
        }
        return this;
    }

    @Override
    public ConflictSwitch build() {
        return new ConflictSwitchImpl(this);
    }

    private static final class ConflictSwitchImpl implements ConflictSwitch {

        @Override
        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch.class;
        }

        private final List<ConflictTable> _conflictTable;
        private final ConflictSwitchKey _key;
        private final java.lang.String _switchId;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>> augmentation = Collections.emptyMap();

        private ConflictSwitchImpl(ConflictSwitchBuilder base) {
            if (base.getKey() == null) {
                this._key = new ConflictSwitchKey(
                    base.getSwitchId()
                );
                this._switchId = base.getSwitchId();
            } else {
                this._key = base.getKey();
                this._switchId = _key.getSwitchId();
            }
            this._conflictTable = base.getConflictTable();
            switch (base.augmentation.size()) {
            case 0:
                this.augmentation = Collections.emptyMap();
                break;
            case 1:
                final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>> e = base.augmentation.entrySet().iterator().next();
                this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>>singletonMap(e.getKey(), e.getValue());
                break;
            default :
                this.augmentation = new HashMap<>(base.augmentation);
            }
        }

        @Override
        public List<ConflictTable> getConflictTable() {
            return _conflictTable;
        }
        
        @Override
        public ConflictSwitchKey getKey() {
            return _key;
        }
        
        @Override
        public java.lang.String getSwitchId() {
            return _switchId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + Objects.hashCode(_conflictTable);
            result = prime * result + Objects.hashCode(_key);
            result = prime * result + Objects.hashCode(_switchId);
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch other = (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch)obj;
            if (!Objects.equals(_conflictTable, other.getConflictTable())) {
                return false;
            }
            if (!Objects.equals(_key, other.getKey())) {
                return false;
            }
            if (!Objects.equals(_switchId, other.getSwitchId())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                ConflictSwitchImpl otherImpl = (ConflictSwitchImpl) obj;
                if (!Objects.equals(augmentation, otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitch>> e : augmentation.entrySet()) {
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
            java.lang.String name = "ConflictSwitch [";
            java.lang.StringBuilder builder = new java.lang.StringBuilder (name);
            if (_conflictTable != null) {
                builder.append("_conflictTable=");
                builder.append(_conflictTable);
                builder.append(", ");
            }
            if (_key != null) {
                builder.append("_key=");
                builder.append(_key);
                builder.append(", ");
            }
            if (_switchId != null) {
                builder.append("_switchId=");
                builder.append(_switchId);
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
