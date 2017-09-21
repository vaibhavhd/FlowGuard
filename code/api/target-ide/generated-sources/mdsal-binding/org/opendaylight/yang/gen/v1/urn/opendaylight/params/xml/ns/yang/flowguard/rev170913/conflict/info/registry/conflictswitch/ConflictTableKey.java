package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch;
import org.opendaylight.yangtools.yang.binding.Identifier;
import java.util.Objects;


public class ConflictTableKey
 implements Identifier<ConflictTable> {
    private static final long serialVersionUID = 6161078578386239685L;
    private final java.lang.Integer _tableId;


    public ConflictTableKey(java.lang.Integer _tableId) {
    
    
        this._tableId = _tableId;
    }
    
    /**
     * Creates a copy from Source Object.
     *
     * @param source Source object
     */
    public ConflictTableKey(ConflictTableKey source) {
        this._tableId = source._tableId;
    }


    public java.lang.Integer getTableId() {
        return _tableId;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(_tableId);
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
        ConflictTableKey other = (ConflictTableKey) obj;
        if (!Objects.equals(_tableId, other._tableId)) {
            return false;
        }
        return true;
    }

    @Override
    public java.lang.String toString() {
        java.lang.StringBuilder builder = new java.lang.StringBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.ConflictTableKey.class.getSimpleName()).append(" [");
        boolean first = true;
    
        if (_tableId != null) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append("_tableId=");
            builder.append(_tableId);
         }
        return builder.append(']').toString();
    }
}

