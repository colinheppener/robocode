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
public class HitByMissileEvent extends Event{
    private static final long serialVersionUID = 1L;
    private final static int DEFAULT_PRIORITY = 20;

    private final double bearing;
    private final Missile missile;

    public HitByMissileEvent(double bearing, Missile missile) {
        super();
        this.bearing = bearing;
        this.missile = missile;
    }
    public double getBearing() {
        return bearing * 180.0 / Math.PI;
    }

    /**
     * Returns the bearing to the bullet, relative to your robot's heading,
     * in radians (-Math.PI < getBearingRadians() <= Math.PI).
     * <p/>
     * If you were to turnRightRadians(event.getBearingRadians()), you would be
     * facing the direction the bullet came from. The calculation used here is:
     * (bullet's heading in radians + Math.PI) - (your heading in radians)
     *
     * @return the bearing to the bullet, in radians
     */
    public double getBearingRadians() {
        return bearing;
    }

    /**
     * Returns the bullet that hit your robot.
     *
     * @return the bullet that hit your robot
     */
    public Missile getMissile() {
        return missile;
    }

    /**
     * Returns the heading of the bullet when it hit you, in degrees
     * (0 <= getHeading() < 360).
     * <p/>
     * Note: This is not relative to the direction you are facing. The robot
     * that fired the bullet was in the opposite direction of getHeading() when
     * it fired the bullet.
     *
     * @return the heading of the bullet, in degrees
     */
    public double getHeading() {
        return missile.getHeading();
    }

    /**
     * @return the heading of the bullet, in degrees
     * @deprecated Use {@link #getHeading()} instead.
     */
    @Deprecated
    public double getHeadingDegrees() {
        return getHeading();
    }

    /**
     * Returns the heading of the bullet when it hit you, in radians
     * (0 <= getHeadingRadians() < 2 * PI).
     * <p/>
     * Note: This is not relative to the direction you are facing. The robot
     * that fired the bullet was in the opposite direction of
     * getHeadingRadians() when it fired the bullet.
     *
     * @return the heading of the bullet, in radians
     */
    public double getHeadingRadians() {
        return missile.getHeadingRadians();
    }

    /**
     * Returns the name of the robot that fired the bullet.
     *
     * @return the name of the robot that fired the bullet
     */
    public String getName() {
        return missile.getName();
    }

    /**
     * Returns the power of this bullet. The damage you take (in fact, already
     * took) is 4 * power, plus 2 * (power-1) if power > 1. The robot that fired
     * the bullet receives 3 * power back.
     *
     * @return the power of the bullet
     */
    public double getPower() {
        return missile.getPower();
    }

    /**
     * Returns the velocity of this bullet.
     *
     * @return the velocity of the bullet
     */
    public double getVelocity() {
        return missile.getVelocity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final int getDefaultPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
        IBasicEvents listener = robot.getBasicEventListener();

        if (listener != null) {
            listener.onHitByMissile(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte getSerializationType() {
        return RbSerializer.HitByBulletEvent_TYPE;
    }

    static ISerializableHelper createHiddenSerializer() {
        return new HitByMissileEvent.SerializableHelper();
    }

    private static class SerializableHelper implements ISerializableHelper {
        public int sizeOf(RbSerializer serializer, Object object) {
            HitByMissileEvent obj = (HitByMissileEvent) object;

            return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(RbSerializer.Bullet_TYPE, obj.missile)
                    + RbSerializer.SIZEOF_DOUBLE;
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            HitByMissileEvent obj = (HitByMissileEvent) object;

            serializer.serialize(buffer, RbSerializer.Missile_TYPE, obj.missile);
            serializer.serialize(buffer, obj.bearing);
        }

        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            Missile missile = (Missile) serializer.deserializeAny(buffer);
            double bearing = buffer.getDouble();

            return new HitByMissileEvent(bearing, missile);
        }
    }
}
