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
public class MissileHitMissileEvent extends Event {
    private static final long serialVersionUID = 1L;
    private final static int DEFAULT_PRIORITY = 55;

    private Missile missile;
    private final Missile hitMissile;

    public MissileHitMissileEvent(Missile missile, Missile hitMissile) {
        super();
        this.missile = missile;
        this.hitMissile = hitMissile;
    }

    public Missile getMissile()
    {
        return missile;
    }

    public Missile getHitMissile()
    {
        return hitMissile;
    }

    @Override
    final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
        IBasicEvents listener = robot.getBasicEventListener();

        if (listener != null) {
            listener.onMissileHitMissile(this);
        }
    }

    @Override
    byte getSerializationType() {
        return RbSerializer.MissileHitMissileEvent_TYPE;
    }

    static ISerializableHelper createHiddenSerializer() {
        return new SerializableHelper();
    }

    private static class SerializableHelper implements ISerializableHelper {
        public int sizeOf(RbSerializer serializer, Object object) {
            MissileHitMissileEvent obj = (MissileHitMissileEvent) object;

            return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT
                    + serializer.sizeOf(RbSerializer.Missile_TYPE, obj.hitMissile);
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            MissileHitMissileEvent obj = (MissileHitMissileEvent) object;

            // no need to transmit whole bullet, rest of it is already known to proxy side
            serializer.serialize(buffer, obj.missile.getMissileId());
            serializer.serialize(buffer, RbSerializer.Bullet_TYPE, obj.hitMissile);
        }

        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            Missile missile = new Missile(0, 0, 0, 0, null, null, false, buffer.getInt());
            Missile hitMissile = (Missile) serializer.deserializeAny(buffer);

            return new MissileHitMissileEvent(missile, hitMissile);
        }
    }

}
