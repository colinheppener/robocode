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
public class MissileHitEvent extends Event{
    private static final long serialVersionUID = 1L;
    private final static int DEFAULT_PRIORITY = 50;

    private final String name;
    private final double energy;
    private Missile missile;

    public MissileHitEvent(String name, double energy, Missile missile) {
        super();
        this.name = name;
        this.energy = energy;
        this.missile = missile;
    }

    public Missile getMissile(){return missile;}

    public double getEnergy() {
        return energy;
    }

    @Deprecated
    public double getLife() {
        return energy;
    }

    public String getName() {
        return name;
    }

    @Override
    final int getDefaultPriority() {
        return DEFAULT_PRIORITY;
    }

    @Override
    final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
        IBasicEvents listener = robot.getBasicEventListener();

        if (listener != null) {
            listener.onMissileHit(this);
        }
    }

    @Override
    byte getSerializationType() {
        return RbSerializer.MissileHitEvent_TYPE;
    }

    static ISerializableHelper createHiddenSerializer() {
        return new SerializableHelper();
    }

    private static class SerializableHelper implements ISerializableHelper {
        public int sizeOf(RbSerializer serializer, Object object) {
            MissileHitEvent obj = (MissileHitEvent) object;

            return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.name)
                    + RbSerializer.SIZEOF_DOUBLE;
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            MissileHitEvent obj = (MissileHitEvent) object;

            serializer.serialize(buffer, obj.missile.getMissileId());
            serializer.serialize(buffer, obj.name);
            serializer.serialize(buffer, obj.energy);
        }

        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            Missile missile= new Missile(0, 0, 0, 0, null, null, false, buffer.getInt());
            String name = serializer.deserializeString(buffer);
            double energy = buffer.getDouble();

            return new MissileHitEvent(name, energy, missile);
        }
    }
}
