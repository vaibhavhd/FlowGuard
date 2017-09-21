package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry;
import org.opendaylight.yangtools.yang.binding.Identifier;
import java.util.Objects;


public class ConflictSwitchKey
 implements Identifier<ConflictSwitch> {
    private static final long serialVersionUID = -5448769402858479023L;
    private final java.lang.String _switchId;


    public ConflictSwitchKey(java.lang.String _switchId) {
    
    
        this._switchId = _switchId;
    }
    
    /**
     * Creates a copy from Source Object.
     *
     * @param source Source object
     */
    public ConflictSwitchKey(ConflictSwitchKey source) {
        this._switchId = source._switchId;
    }


    public java.lang.String getSwitchId() {
        return _switchId;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(_switchId);
        return result;
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ConflictSwitchKey other = (ConflictSwitchKey) obj;
        if (!Objects.equals(_switchId, other._switchId)) {
            return false;
        }
        return true;
    }

    @Override
    public java.lang.String toString() {
        java.lang.StringBuilder builder = new java.lang.StringBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.ConflictSwitchKey.class.getSimpleName()).append(" [");
        boolean first = true;
    
        if (_switchId != null) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append("_switchId=");
            builder.append(_switchId);
         }
        return builder.append(']').toString();
    }
}

