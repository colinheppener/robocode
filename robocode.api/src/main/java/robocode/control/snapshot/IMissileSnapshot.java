package robocode.control.snapshot;

import robocode.robotinterfaces.ITransformable;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public interface IMissileSnapshot extends ITransformable{

    /**
     * Returns the missile state.
     *
     * @return the missile state.
     */
    MissileState getState();

    /**
     * Returns the missile power.
     *
     * @return the missile power.
     */
    double getPower();

    /**
     * Returns the X position of the missile.
     *
     * @return the X position of the missile.
     */
    double getX();

    /**
     * Returns the Y position of the missile.
     *
     * @return the Y position of the missile.
     */
    double getY();

    /**
     * Returns the X painting position of the missile.
     * Note that this is not necessarily equal to the X position of the missile, even though
     * it will be in most cases. The painting position of the missile is needed as the missile
     * will "stick" to its victim when it has been hit, but only visually. 
     *
     * @return the X painting position of the missile.
     */
    double getPaintX();

    /**
     * Returns the Y painting position of the missile.
     * Note that this is not necessarily equal to the Y position of the missile, even though
     * it will be in most cases. The painting position of the missile is needed as the missile
     * will "stick" to its victim when it has been hit, but only visually. 
     *
     * @return the Y painting position of the missile.
     */
    double getPaintY();

    /**
     * Returns the color of the missile.
     *
     * @return an ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
     *
     * @see java.awt.Color#getRGB()
     */
    int getColor();

    /**
     * Returns the current frame number to display, i.e. when the missile explodes.
     *
     * @return the current frame number.
     *
     * @see #isExplosion()
     * @see #getExplosionImageIndex()
     */
    int getFrame();

    /**
     * Checks if the missile has become an explosion, i.e. when it a robot or missile has been hit.
     *
     * @return {@code true} if the missile is an explosion; {@code false} otherwise.
     *
     * @see #getFrame()
     * @see #getExplosionImageIndex()
     */
    boolean isExplosion();

    /**
     * Returns the explosion image index, which is different depending on the type of explosion.
     * E.g. if it is a small explosion on a robot that has been hit by this missile,
     * or a big explosion when a robot dies.
     *
     * @return the explosion image index.
     *
     * @see #isExplosion()
     * @see #getExplosionImageIndex()
     */
    int getExplosionImageIndex();

    /**
     * Returns the ID of the missile used for identifying the missile in a collection of missiles.
     *
     * @return the ID of the missile.
     */
    int getMissileId();

    /**
     * @return heading of the missile
     */
    double getHeading();

    double getHeadingDegrees();

    /**
     * @return contestantIndex of the victim, or -1 if still in air
     */
    int getVictimIndex();

    /**
     * @return contestantIndex of the owner
     */
    int getOwnerIndex();
}
