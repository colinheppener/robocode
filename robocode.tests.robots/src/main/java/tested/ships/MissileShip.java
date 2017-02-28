package tested.ships;

import robocode.Ship;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public class MissileShip extends Ship {
    public void run(){
        launchMissile(30);
        System.out.println("launched Missile");
    }
}
