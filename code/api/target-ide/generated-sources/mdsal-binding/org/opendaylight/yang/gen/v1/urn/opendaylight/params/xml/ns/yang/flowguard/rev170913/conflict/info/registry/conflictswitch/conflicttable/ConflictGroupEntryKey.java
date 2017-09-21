package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable;
import org.opendaylight.yangtools.yang.binding.Identifier;
import java.util.Objects;


public class ConflictGroupEntryKey
 implements Identifier<ConflictGroupEntry> {
    private static final long serialVersionUID = 5548245560405322848L;
    private final java.lang.Integer _id;


    public ConflictGroupEntryKey(java.lang.Integer _id) {
    
    
        this._id = _id;
    }
    
    /**
     * Creates a copy from Source Object.
     *
     * @param source Source object
     */
    public ConflictGroupEntryKey(ConflictGroupEntryKey source) {
        this._id = source._id;
    }


    public java.lang.Integer getId() {
        return _id;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(_id);
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
        ConflictGroupEntryKey other = (ConflictGroupEntryKey) obj;
        if (!Objects.equals(_id, other._id)) {
            return false;
        }
        return true;
    }

    @Override
    public java.lang.String toString() {
        java.lang.StringBuilder builder = new java.lang.StringBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.flowguard.rev170913.conflict.info.registry.conflictswitch.conflicttable.ConflictGroupEntryKey.class.getSimpleName()).append(" [");
        boolean first = true;
    
        if (_id != null) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append("_id=");
            builder.append(_id);
         }
        return builder.append(']').toString();
    }
}

