package net.sf.robocode.security;

import robocode.Missile;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public interface IHiddenMissileHelper {
    void update(Missile missile, double x, double y, String victimName, boolean isActive);

}
