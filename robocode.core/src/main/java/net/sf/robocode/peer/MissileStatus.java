package net.sf.robocode.peer;

import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public class MissileStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int missileId;
    public final String victimName;
    public final boolean isActive;
    public final double x;
    public final double y;

    public MissileStatus(int missileId, double x, double y, String victimName, boolean isActive)
    {
        this.missileId = missileId;
        this.x = x;
        this.y = y;
        this.isActive = isActive;
        this.victimName = victimName;
    }

    static ISerializableHelper createHiddenSerializer() {
        return new SerializableHelper();
    }

    private static class SerializableHelper implements ISerializableHelper {
        public int sizeOf(RbSerializer serializer, Object object) {
            MissileStatus obj = (MissileStatus) object;

            return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.victimName)
                    + RbSerializer.SIZEOF_BOOL + 2 * RbSerializer.SIZEOF_DOUBLE;
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            MissileStatus obj = (MissileStatus) object;

            serializer.serialize(buffer, obj.missileId);
            serializer.serialize(buffer, obj.victimName);
            serializer.serialize(buffer, obj.isActive);
            serializer.serialize(buffer, obj.x);
            serializer.serialize(buffer, obj.y);
        }

        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            int missileId = buffer.getInt();
            String victimName = serializer.deserializeString(buffer);
            boolean isActive = serializer.deserializeBoolean(buffer);
            double x = buffer.getDouble();
            double y = buffer.getDouble();

            return new MissileStatus(missileId, x, y, victimName, isActive);
        }
    }
}
