package robocode;

import net.sf.robocode.security.IHiddenMissileHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.naval.NavalRules;

import java.nio.ByteBuffer;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public class Missile extends Projectile {
    private int missileId;


    /**
     *  Called by the game to create a new {@code Missile} object
     *
     * @param heading   the heading of the missile, in radians.
     * @param x		 the starting X position of the missile.
     * @param y		 the starting Y position of the missile.
     * @param power	 the power of the missile.
     * @param ownerName the name of the owner robot that owns the missile.
     * @param victimName the name of the robot hit by the missile.
     * @param isActive {@code true} if the missile still moves; {@code false} otherwise.
     * @param missileId unique id of missile for owner robot.
     */
    public Missile(double heading, double x, double y, double power, String ownerName, String victimName, boolean isActive, int missileId) {
        this.headingRadians = heading;
        this.x = x;
        this.y = y;
        this.power = power;
        this.ownerName = ownerName;
        this.victimName = victimName;
        this.isActive = isActive;
        this.missileId = missileId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return missileId == ((Missile) obj).missileId;
    }

    @Override
    public int hashCode() {
        return missileId;
    }

    @Override
    public double getVelocity() {
        return NavalRules.getMissileSpeed(power);
    }

    /**
     * @return unique id of missile for owner robot
     */
    int getMissileId() {
        return missileId;
    }





    static IHiddenMissileHelper createHiddenHelper() {
        return new HiddenMissileHelper();
    }

    static ISerializableHelper createHiddenSerializer() {
        return new HiddenMissileHelper();
    }


    private static class HiddenMissileHelper implements IHiddenMissileHelper, ISerializableHelper {

        public void update(Missile missile, double x, double y, String victimName, boolean isActive) {
            missile.update(x, y, victimName, isActive);
        }

        public int sizeOf(RbSerializer serializer, Object object) {
            Missile obj = (Missile) object;

            return RbSerializer.SIZEOF_TYPEINFO + 4 * RbSerializer.SIZEOF_DOUBLE + serializer.sizeOf(obj.ownerName)
                    + serializer.sizeOf(obj.victimName) + RbSerializer.SIZEOF_BOOL + RbSerializer.SIZEOF_INT;
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            Missile obj = (Missile) object;

            serializer.serialize(buffer, obj.headingRadians);
        }
        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            double headingRadians = buffer.getDouble();
            double x = buffer.getDouble();
            double y = buffer.getDouble();
            double power = buffer.getDouble();
            String ownerName = serializer.deserializeString(buffer);
            String victimName = serializer.deserializeString(buffer);
            boolean isActive = serializer.deserializeBoolean(buffer);
            int missileId = serializer.deserializeInt(buffer);
            return new Missile(headingRadians, x, y, power, ownerName, victimName, isActive, missileId);
        }
    }
}
