package robocode;

import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.nio.ByteBuffer;
/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public class MissileMissedEvent extends Event{
    private static final long serialVersionUID = 1L;
    private final static int DEFAULT_PRIORITY = 60;

    private Missile missile;

    public MissileMissedEvent(Missile missile) {
        this.missile = missile;
    }

    public Missile getMissile()
    {
        return missile;
    }

    @Override
    final int getDefaultPriority() {
        return DEFAULT_PRIORITY;
    }

    @Override
    final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
        IBasicEvents listener = robot.getBasicEventListener();

        if (listener != null) {
            listener.onMissileMissed(this);
        }
    }

    @Override
    byte getSerializationType() {
        return RbSerializer.BulletMissedEvent_TYPE;
    }

    static ISerializableHelper createHiddenSerializer() {
        return new SerializableHelper();
    }

    private static class SerializableHelper implements ISerializableHelper {
        public int sizeOf(RbSerializer serializer, Object object) {
            return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT;
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            MissileMissedEvent obj = (MissileMissedEvent) object;

            serializer.serialize(buffer, obj.missile.getMissileId());
        }

        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            Missile missile = new Missile(0, 0, 0, 0, null, null, false, buffer.getInt());

            return new MissileMissedEvent(missile);
        }
    }
}
