package robocode;

import robocode.naval.NavalRules;

import java.io.Serializable;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 * Created this class to reduce the amount of duplicate code, the serialization classes are still in their original classes
 */
public abstract class Projectile implements Serializable {
    private static final long serialVersionUID = 1L;

    double headingRadians;
    double x;
    double y;
    double power;
    String ownerName;
    String victimName;
    boolean isActive;

    /**
     * Returns the direction the projectile is/was heading, in degrees
     * (0 <= getHeading() < 360). This is not relative to the direction you are
     * facing.
     *
     * @return the direction the projectile is/was heading, in degrees
     */
    public double getHeading() {
        return Math.toDegrees(headingRadians);
    }

    /**
     * Returns the direction the projectile is/was heading, in radians
     * (0 <= getHeadingRadians() < 2 * Math.PI). This is not relative to the
     * direction you are facing.
     *
     * @return the direction the projectile is/was heading, in radians
     */
    public double getHeadingRadians() {
        return headingRadians;
    }

    /**
     * Returns the name of the robot that fired this projectile.
     *
     * @return the name of the robot that fired this projectile
     */
    public String getName() {
        return ownerName;
    }

    /**
     * Returns the power of this projectile.
     * 
     *
     * @return the power of the projectile
     */
    public double getPower() {
        return power;
    }

    /**
     * Returns the velocity of this projectile. The velocity of the projectile is
     * constant once it has been fired.
     *
     * @return the velocity of the projectile
     */
    public double getVelocity() {
        return NavalRules.getBulletSpeed(power);
    }

    /**
     * Returns the name of the robot that this projectile hit, or {@code null} if
     * the projectile has not hit a robot.
     *
     * @return the name of the robot that this projectile hit, or {@code null} if
     *         the projectile has not hit a robot.
     */
    public String getVictim() {
        return victimName;
    }

    /**
     * Returns the X position of the projectile.
     *
     * @return the X position of the projectile
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y position of the projectile.
     *
     * @return the Y position of the projectile
     */
    public double getY() {
        return y;
    }

    /**
     * Checks if this projectile is still active on the battlefield.
     *
     * @return {@code true} if the projectile is still active on the battlefield;
     *         {@code false} otherwise
     */
    public boolean isActive() {
        return isActive;
    }


    /**
     * Updates this projectile based on the specified projectile status.
     *
     * @param x the new X position of the projectile .
     * @param y the new Y position of the projectile.
     * @param victimName the name if the victim that has been hit by this projectile.
     * @param isActive {@code true} if the projectile still moves; {@code false} otherwise.
     */
    // this method is invisible on RobotAPI
    void update(double x, double y, String victimName, boolean isActive) {
        this.x = x;
        this.y = y;
        this.victimName = victimName;
        this.isActive = isActive;
    }
}
