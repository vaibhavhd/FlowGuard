package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.AugmentationHolder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo.Action;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.concepts.Builder;
import java.util.Objects;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo.Protocol;
import java.util.Collections;
import java.util.Map;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList} instances.
 *
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList
 *
 */
public class ConflictGroupListBuilder implements Builder <org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList> {

    private Action _action;
    private java.lang.Integer _conflictGroupNumber;
    private java.lang.String _conflictType;
    private java.lang.Integer _corCount;
    private java.lang.String _dlDst;
    private java.lang.String _dlSrc;
    private java.lang.Integer _genCount;
    private java.lang.Integer _id;
    private java.lang.String _inPort;
    private ConflictGroupListKey _key;
    private java.lang.Integer _l4Dst;
    private java.lang.Integer _l4Src;
    private java.lang.String _mechanism;
    private java.lang.String _nwDst;
    private java.lang.String _nwSrc;
    private java.lang.Integer _overCount;
    private java.lang.Integer _priority;
    private Protocol _protocol;
    private java.lang.Integer _redCount;
    private java.lang.Integer _shCount;
    private java.lang.Long _vlanId;
    private java.lang.Boolean _resolution;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>> augmentation = Collections.emptyMap();

    public ConflictGroupListBuilder() {
    }
    public ConflictGroupListBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo arg) {
        this._priority = arg.getPriority();
        this._id = arg.getId();
        this._vlanId = arg.getVlanId();
        this._inPort = arg.getInPort();
        this._dlSrc = arg.getDlSrc();
        this._dlDst = arg.getDlDst();
        this._nwSrc = arg.getNwSrc();
        this._nwDst = arg.getNwDst();
        this._l4Src = arg.getL4Src();
        this._l4Dst = arg.getL4Dst();
        this._action = arg.getAction();
        this._protocol = arg.getProtocol();
        this._genCount = arg.getGenCount();
        this._shCount = arg.getShCount();
        this._redCount = arg.getRedCount();
        this._corCount = arg.getCorCount();
        this._overCount = arg.getOverCount();
        this._conflictType = arg.getConflictType();
        this._conflictGroupNumber = arg.getConflictGroupNumber();
        this._resolution = arg.isResolution();
        this._mechanism = arg.getMechanism();
    }

    public ConflictGroupListBuilder(ConflictGroupList base) {
        if (base.getKey() == null) {
            this._key = new ConflictGroupListKey(
                base.getId()
            );
            this._id = base.getId();
        } else {
            this._key = base.getKey();
            this._id = _key.getId();
        }
        this._action = base.getAction();
        this._conflictGroupNumber = base.getConflictGroupNumber();
        this._conflictType = base.getConflictType();
        this._corCount = base.getCorCount();
        this._dlDst = base.getDlDst();
        this._dlSrc = base.getDlSrc();
        this._genCount = base.getGenCount();
        this._inPort = base.getInPort();
        this._l4Dst = base.getL4Dst();
        this._l4Src = base.getL4Src();
        this._mechanism = base.getMechanism();
        this._nwDst = base.getNwDst();
        this._nwSrc = base.getNwSrc();
        this._overCount = base.getOverCount();
        this._priority = base.getPriority();
        this._protocol = base.getProtocol();
        this._redCount = base.getRedCount();
        this._shCount = base.getShCount();
        this._vlanId = base.getVlanId();
        this._resolution = base.isResolution();
        if (base instanceof ConflictGroupListImpl) {
            ConflictGroupListImpl impl = (ConflictGroupListImpl) base;
            if (!impl.augmentation.isEmpty()) {
                this.augmentation = new HashMap<>(impl.augmentation);
            }
        } else if (base instanceof AugmentationHolder) {
            @SuppressWarnings("unchecked")
            AugmentationHolder<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList> casted =(AugmentationHolder<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>) base;
            if (!casted.augmentations().isEmpty()) {
                this.augmentation = new HashMap<>(casted.augmentations());
            }
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo) {
            this._priority = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getPriority();
            this._id = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getId();
            this._vlanId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getVlanId();
            this._inPort = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getInPort();
            this._dlSrc = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getDlSrc();
            this._dlDst = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getDlDst();
            this._nwSrc = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getNwSrc();
            this._nwDst = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getNwDst();
            this._l4Src = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getL4Src();
            this._l4Dst = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getL4Dst();
            this._action = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getAction();
            this._protocol = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getProtocol();
            this._genCount = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getGenCount();
            this._shCount = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getShCount();
            this._redCount = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getRedCount();
            this._corCount = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getCorCount();
            this._overCount = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getOverCount();
            this._conflictType = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getConflictType();
            this._conflictGroupNumber = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getConflictGroupNumber();
            this._resolution = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).isResolution();
            this._mechanism = ((org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo)arg).getMechanism();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.ConflictInfo] \n" +
              "but was: " + arg
            );
        }
    }

    public Action getAction() {
        return _action;
    }
    
    public java.lang.Integer getConflictGroupNumber() {
        return _conflictGroupNumber;
    }
    
    public java.lang.String getConflictType() {
        return _conflictType;
    }
    
    public java.lang.Integer getCorCount() {
        return _corCount;
    }
    
    public java.lang.String getDlDst() {
        return _dlDst;
    }
    
    public java.lang.String getDlSrc() {
        return _dlSrc;
    }
    
    public java.lang.Integer getGenCount() {
        return _genCount;
    }
    
    public java.lang.Integer getId() {
        return _id;
    }
    
    public java.lang.String getInPort() {
        return _inPort;
    }
    
    public ConflictGroupListKey getKey() {
        return _key;
    }
    
    public java.lang.Integer getL4Dst() {
        return _l4Dst;
    }
    
    public java.lang.Integer getL4Src() {
        return _l4Src;
    }
    
    public java.lang.String getMechanism() {
        return _mechanism;
    }
    
    public java.lang.String getNwDst() {
        return _nwDst;
    }
    
    public java.lang.String getNwSrc() {
        return _nwSrc;
    }
    
    public java.lang.Integer getOverCount() {
        return _overCount;
    }
    
    public java.lang.Integer getPriority() {
        return _priority;
    }
    
    public Protocol getProtocol() {
        return _protocol;
    }
    
    public java.lang.Integer getRedCount() {
        return _redCount;
    }
    
    public java.lang.Integer getShCount() {
        return _shCount;
    }
    
    public java.lang.Long getVlanId() {
        return _vlanId;
    }
    
    public java.lang.Boolean isResolution() {
        return _resolution;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

     
    public ConflictGroupListBuilder setAction(final Action value) {
        this._action = value;
        return this;
    }
    
     
     private static void checkConflictGroupNumberRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setConflictGroupNumber(final java.lang.Integer value) {
    if (value != null) {
        checkConflictGroupNumberRange(value);
    }
        this._conflictGroupNumber = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setConflictType(final java.lang.String value) {
        this._conflictType = value;
        return this;
    }
    
     
     private static void checkCorCountRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setCorCount(final java.lang.Integer value) {
    if (value != null) {
        checkCorCountRange(value);
    }
        this._corCount = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setDlDst(final java.lang.String value) {
        this._dlDst = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setDlSrc(final java.lang.String value) {
        this._dlSrc = value;
        return this;
    }
    
     
     private static void checkGenCountRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setGenCount(final java.lang.Integer value) {
    if (value != null) {
        checkGenCountRange(value);
    }
        this._genCount = value;
        return this;
    }
    
     
     private static void checkIdRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setId(final java.lang.Integer value) {
    if (value != null) {
        checkIdRange(value);
    }
        this._id = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setInPort(final java.lang.String value) {
        this._inPort = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setKey(final ConflictGroupListKey value) {
        this._key = value;
        return this;
    }
    
     
     private static void checkL4DstRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setL4Dst(final java.lang.Integer value) {
    if (value != null) {
        checkL4DstRange(value);
    }
        this._l4Dst = value;
        return this;
    }
    
     
     private static void checkL4SrcRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setL4Src(final java.lang.Integer value) {
    if (value != null) {
        checkL4SrcRange(value);
    }
        this._l4Src = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setMechanism(final java.lang.String value) {
        this._mechanism = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setNwDst(final java.lang.String value) {
        this._nwDst = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setNwSrc(final java.lang.String value) {
        this._nwSrc = value;
        return this;
    }
    
     
     private static void checkOverCountRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setOverCount(final java.lang.Integer value) {
    if (value != null) {
        checkOverCountRange(value);
    }
        this._overCount = value;
        return this;
    }
    
     
     private static void checkPriorityRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setPriority(final java.lang.Integer value) {
    if (value != null) {
        checkPriorityRange(value);
    }
        this._priority = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setProtocol(final Protocol value) {
        this._protocol = value;
        return this;
    }
    
     
     private static void checkRedCountRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setRedCount(final java.lang.Integer value) {
    if (value != null) {
        checkRedCountRange(value);
    }
        this._redCount = value;
        return this;
    }
    
     
     private static void checkShCountRange(final int value) {
         if (value >= 0 && value <= 65535) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥65535]].", value));
     }
    
    public ConflictGroupListBuilder setShCount(final java.lang.Integer value) {
    if (value != null) {
        checkShCountRange(value);
    }
        this._shCount = value;
        return this;
    }
    
     
     private static void checkVlanIdRange(final long value) {
         if (value >= 0L && value <= 4294967295L) {
             return;
         }
         throw new IllegalArgumentException(String.format("Invalid range: %s, expected: [[0‥4294967295]].", value));
     }
    
    public ConflictGroupListBuilder setVlanId(final java.lang.Long value) {
    if (value != null) {
        checkVlanIdRange(value);
    }
        this._vlanId = value;
        return this;
    }
    
     
    public ConflictGroupListBuilder setResolution(final java.lang.Boolean value) {
        this._resolution = value;
        return this;
    }
    
    public ConflictGroupListBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList> augmentation) {
        if (augmentation == null) {
            return removeAugmentation(augmentationType);
        }
    
        if (!(this.augmentation instanceof HashMap)) {
            this.augmentation = new HashMap<>();
        }
    
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }
    
    public ConflictGroupListBuilder removeAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>> augmentationType) {
        if (this.augmentation instanceof HashMap) {
            this.augmentation.remove(augmentationType);
        }
        return this;
    }

    @Override
    public ConflictGroupList build() {
        return new ConflictGroupListImpl(this);
    }

    private static final class ConflictGroupListImpl implements ConflictGroupList {

        @Override
        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList.class;
        }

        private final Action _action;
        private final java.lang.Integer _conflictGroupNumber;
        private final java.lang.String _conflictType;
        private final java.lang.Integer _corCount;
        private final java.lang.String _dlDst;
        private final java.lang.String _dlSrc;
        private final java.lang.Integer _genCount;
        private final java.lang.Integer _id;
        private final java.lang.String _inPort;
        private final ConflictGroupListKey _key;
        private final java.lang.Integer _l4Dst;
        private final java.lang.Integer _l4Src;
        private final java.lang.String _mechanism;
        private final java.lang.String _nwDst;
        private final java.lang.String _nwSrc;
        private final java.lang.Integer _overCount;
        private final java.lang.Integer _priority;
        private final Protocol _protocol;
        private final java.lang.Integer _redCount;
        private final java.lang.Integer _shCount;
        private final java.lang.Long _vlanId;
        private final java.lang.Boolean _resolution;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>> augmentation = Collections.emptyMap();

        private ConflictGroupListImpl(ConflictGroupListBuilder base) {
            if (base.getKey() == null) {
                this._key = new ConflictGroupListKey(
                    base.getId()
                );
                this._id = base.getId();
            } else {
                this._key = base.getKey();
                this._id = _key.getId();
            }
            this._action = base.getAction();
            this._conflictGroupNumber = base.getConflictGroupNumber();
            this._conflictType = base.getConflictType();
            this._corCount = base.getCorCount();
            this._dlDst = base.getDlDst();
            this._dlSrc = base.getDlSrc();
            this._genCount = base.getGenCount();
            this._inPort = base.getInPort();
            this._l4Dst = base.getL4Dst();
            this._l4Src = base.getL4Src();
            this._mechanism = base.getMechanism();
            this._nwDst = base.getNwDst();
            this._nwSrc = base.getNwSrc();
            this._overCount = base.getOverCount();
            this._priority = base.getPriority();
            this._protocol = base.getProtocol();
            this._redCount = base.getRedCount();
            this._shCount = base.getShCount();
            this._vlanId = base.getVlanId();
            this._resolution = base.isResolution();
            switch (base.augmentation.size()) {
            case 0:
                this.augmentation = Collections.emptyMap();
                break;
            case 1:
                final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>> e = base.augmentation.entrySet().iterator().next();
                this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>>singletonMap(e.getKey(), e.getValue());
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
        public java.lang.Integer getConflictGroupNumber() {
            return _conflictGroupNumber;
        }
        
        @Override
        public java.lang.String getConflictType() {
            return _conflictType;
        }
        
        @Override
        public java.lang.Integer getCorCount() {
            return _corCount;
        }
        
        @Override
        public java.lang.String getDlDst() {
            return _dlDst;
        }
        
        @Override
        public java.lang.String getDlSrc() {
            return _dlSrc;
        }
        
        @Override
        public java.lang.Integer getGenCount() {
            return _genCount;
        }
        
        @Override
        public java.lang.Integer getId() {
            return _id;
        }
        
        @Override
        public java.lang.String getInPort() {
            return _inPort;
        }
        
        @Override
        public ConflictGroupListKey getKey() {
            return _key;
        }
        
        @Override
        public java.lang.Integer getL4Dst() {
            return _l4Dst;
        }
        
        @Override
        public java.lang.Integer getL4Src() {
            return _l4Src;
        }
        
        @Override
        public java.lang.String getMechanism() {
            return _mechanism;
        }
        
        @Override
        public java.lang.String getNwDst() {
            return _nwDst;
        }
        
        @Override
        public java.lang.String getNwSrc() {
            return _nwSrc;
        }
        
        @Override
        public java.lang.Integer getOverCount() {
            return _overCount;
        }
        
        @Override
        public java.lang.Integer getPriority() {
            return _priority;
        }
        
        @Override
        public Protocol getProtocol() {
            return _protocol;
        }
        
        @Override
        public java.lang.Integer getRedCount() {
            return _redCount;
        }
        
        @Override
        public java.lang.Integer getShCount() {
            return _shCount;
        }
        
        @Override
        public java.lang.Long getVlanId() {
            return _vlanId;
        }
        
        @Override
        public java.lang.Boolean isResolution() {
            return _resolution;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + Objects.hashCode(_conflictGroupNumber);
            result = prime * result + Objects.hashCode(_conflictType);
            result = prime * result + Objects.hashCode(_corCount);
            result = prime * result + Objects.hashCode(_dlDst);
            result = prime * result + Objects.hashCode(_dlSrc);
            result = prime * result + Objects.hashCode(_genCount);
            result = prime * result + Objects.hashCode(_id);
            result = prime * result + Objects.hashCode(_inPort);
            result = prime * result + Objects.hashCode(_key);
            result = prime * result + Objects.hashCode(_l4Dst);
            result = prime * result + Objects.hashCode(_l4Src);
            result = prime * result + Objects.hashCode(_mechanism);
            result = prime * result + Objects.hashCode(_nwDst);
            result = prime * result + Objects.hashCode(_nwSrc);
            result = prime * result + Objects.hashCode(_overCount);
            result = prime * result + Objects.hashCode(_priority);
            result = prime * result + Objects.hashCode(_protocol);
            result = prime * result + Objects.hashCode(_redCount);
            result = prime * result + Objects.hashCode(_shCount);
            result = prime * result + Objects.hashCode(_vlanId);
            result = prime * result + Objects.hashCode(_resolution);
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList other = (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList)obj;
            if (!Objects.equals(_action, other.getAction())) {
                return false;
            }
            if (!Objects.equals(_conflictGroupNumber, other.getConflictGroupNumber())) {
                return false;
            }
            if (!Objects.equals(_conflictType, other.getConflictType())) {
                return false;
            }
            if (!Objects.equals(_corCount, other.getCorCount())) {
                return false;
            }
            if (!Objects.equals(_dlDst, other.getDlDst())) {
                return false;
            }
            if (!Objects.equals(_dlSrc, other.getDlSrc())) {
                return false;
            }
            if (!Objects.equals(_genCount, other.getGenCount())) {
                return false;
            }
            if (!Objects.equals(_id, other.getId())) {
                return false;
            }
            if (!Objects.equals(_inPort, other.getInPort())) {
                return false;
            }
            if (!Objects.equals(_key, other.getKey())) {
                return false;
            }
            if (!Objects.equals(_l4Dst, other.getL4Dst())) {
                return false;
            }
            if (!Objects.equals(_l4Src, other.getL4Src())) {
                return false;
            }
            if (!Objects.equals(_mechanism, other.getMechanism())) {
                return false;
            }
            if (!Objects.equals(_nwDst, other.getNwDst())) {
                return false;
            }
            if (!Objects.equals(_nwSrc, other.getNwSrc())) {
                return false;
            }
            if (!Objects.equals(_overCount, other.getOverCount())) {
                return false;
            }
            if (!Objects.equals(_priority, other.getPriority())) {
                return false;
            }
            if (!Objects.equals(_protocol, other.getProtocol())) {
                return false;
            }
            if (!Objects.equals(_redCount, other.getRedCount())) {
                return false;
            }
            if (!Objects.equals(_shCount, other.getShCount())) {
                return false;
            }
            if (!Objects.equals(_vlanId, other.getVlanId())) {
                return false;
            }
            if (!Objects.equals(_resolution, other.isResolution())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                ConflictGroupListImpl otherImpl = (ConflictGroupListImpl) obj;
                if (!Objects.equals(augmentation, otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.get.conflicts.output.ConflictGroupList>> e : augmentation.entrySet()) {
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
            java.lang.String name = "ConflictGroupList [";
            java.lang.StringBuilder builder = new java.lang.StringBuilder (name);
            if (_action != null) {
                builder.append("_action=");
                builder.append(_action);
                builder.append(", ");
            }
            if (_conflictGroupNumber != null) {
                builder.append("_conflictGroupNumber=");
                builder.append(_conflictGroupNumber);
                builder.append(", ");
            }
            if (_conflictType != null) {
                builder.append("_conflictType=");
                builder.append(_conflictType);
                builder.append(", ");
            }
            if (_corCount != null) {
                builder.append("_corCount=");
                builder.append(_corCount);
                builder.append(", ");
            }
            if (_dlDst != null) {
                builder.append("_dlDst=");
                builder.append(_dlDst);
                builder.append(", ");
            }
            if (_dlSrc != null) {
                builder.append("_dlSrc=");
                builder.append(_dlSrc);
                builder.append(", ");
            }
            if (_genCount != null) {
                builder.append("_genCount=");
                builder.append(_genCount);
                builder.append(", ");
            }
            if (_id != null) {
                builder.append("_id=");
                builder.append(_id);
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
            if (_l4Dst != null) {
                builder.append("_l4Dst=");
                builder.append(_l4Dst);
                builder.append(", ");
            }
            if (_l4Src != null) {
                builder.append("_l4Src=");
                builder.append(_l4Src);
                builder.append(", ");
            }
            if (_mechanism != null) {
                builder.append("_mechanism=");
                builder.append(_mechanism);
                builder.append(", ");
            }
            if (_nwDst != null) {
                builder.append("_nwDst=");
                builder.append(_nwDst);
                builder.append(", ");
            }
            if (_nwSrc != null) {
                builder.append("_nwSrc=");
                builder.append(_nwSrc);
                builder.append(", ");
            }
            if (_overCount != null) {
                builder.append("_overCount=");
                builder.append(_overCount);
                builder.append(", ");
            }
            if (_priority != null) {
                builder.append("_priority=");
                builder.append(_priority);
                builder.append(", ");
            }
            if (_protocol != null) {
                builder.append("_protocol=");
                builder.append(_protocol);
                builder.append(", ");
            }
            if (_redCount != null) {
                builder.append("_redCount=");
                builder.append(_redCount);
                builder.append(", ");
            }
            if (_shCount != null) {
                builder.append("_shCount=");
                builder.append(_shCount);
                builder.append(", ");
            }
            if (_vlanId != null) {
                builder.append("_vlanId=");
                builder.append(_vlanId);
                builder.append(", ");
            }
            if (_resolution != null) {
                builder.append("_resolution=");
                builder.append(_resolution);
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
