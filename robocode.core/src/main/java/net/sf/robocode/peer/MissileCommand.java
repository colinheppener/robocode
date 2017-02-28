package net.sf.robocode.peer;

import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public class MissileCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    public MissileCommand(double power, boolean fireAssistValid, double fireAssistAngle, int missileId) {
        this.fireAssistValid = fireAssistValid;
        this.fireAssistAngle = fireAssistAngle;
        this.missileId = missileId;
        this.power = power;
    }

    private final double power;
    private final boolean fireAssistValid;
    private final double fireAssistAngle;
    private final int missileId;

    public boolean isFireAssistValid() {
        return fireAssistValid;
    }

    public int getMissileId() {
        return missileId;
    }

    public double getPower() {
        return power;
    }

    public double getFireAssistAngle() {
        return fireAssistAngle;
    }

    static ISerializableHelper createHiddenSerializer() {
        return new SerializableHelper();
    }

    private static class SerializableHelper implements ISerializableHelper {
        public int sizeOf(RbSerializer serializer, Object object) {
            return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_BOOL
                    + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_INT;
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            MissileCommand obj = (MissileCommand) object;

            serializer.serialize(buffer, obj.power);
            serializer.serialize(buffer, obj.fireAssistValid);
            serializer.serialize(buffer, obj.fireAssistAngle);
            serializer.serialize(buffer, obj.missileId);
        }

        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            double power = buffer.getDouble();
            boolean fireAssistValid = serializer.deserializeBoolean(buffer);
            double fireAssistAngle = buffer.getDouble();
            int missileId = buffer.getInt();

            return new MissileCommand(power, fireAssistValid, fireAssistAngle, missileId);
        }
    }
}
