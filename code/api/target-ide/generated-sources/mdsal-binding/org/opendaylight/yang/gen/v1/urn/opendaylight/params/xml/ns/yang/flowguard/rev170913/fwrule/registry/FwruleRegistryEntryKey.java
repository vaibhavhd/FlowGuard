package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry;
import org.opendaylight.yangtools.yang.binding.Identifier;
import java.util.Objects;


public class FwruleRegistryEntryKey
 implements Identifier<FwruleRegistryEntry> {
    private static final long serialVersionUID = -519698708885900316L;
    private final java.lang.Integer _ruleId;


    public FwruleRegistryEntryKey(java.lang.Integer _ruleId) {
    
    
        this._ruleId = _ruleId;
    }
    
    /**
     * Creates a copy from Source Object.
     *
     * @param source Source object
     */
    public FwruleRegistryEntryKey(FwruleRegistryEntryKey source) {
        this._ruleId = source._ruleId;
    }


    public java.lang.Integer getRuleId() {
        return _ruleId;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(_ruleId);
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
        FwruleRegistryEntryKey other = (FwruleRegistryEntryKey) obj;
        if (!Objects.equals(_ruleId, other._ruleId)) {
            return false;
        }
        return true;
    }

    @Override
    public java.lang.String toString() {
        java.lang.StringBuilder builder = new java.lang.StringBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.fwrule.registry.FwruleRegistryEntryKey.class.getSimpleName()).append(" [");
        boolean first = true;
    
        if (_ruleId != null) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append("_ruleId=");
            builder.append(_ruleId);
         }
        return builder.append(']').toString();
    }
}

