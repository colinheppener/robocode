package navalsample;
import java.awt.Color;

import robocode.*;
import robocode.naval.NavalRules;
import robocode.util.Utils;


/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */


public class MineShip extends Ship {
    private TargetShip targetShip = new TargetShip();

    private boolean tracking;

    private double direction = 1;

    private int rounds = 0;

    @Override
    public void run() {

        setBodyColor(Color.MAGENTA);
        setBackCannonColor(Color.CYAN);
        setFrontCannonColor(Color.CYAN);
        setRadarColor(Color.PINK);
        setBulletColorBack(Color.ORANGE);
        setBulletColorFront(Color.YELLOW);

        while(true) {



            if (!tracking && getRadarTurnRemainingRadians() == 0.0) {
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
            if (getVelocity() == 0.0) {
                setAhead(direction * 4000);
            }

            if (rounds > 10) {
                tracking = false;
                targetShip.setNone();
            }
            rounds++;

            execute();

        }
    }


    @Override
    public void onScannedShip(ScannedShipEvent event) {
        updatePotentialThreat(event);
        updateTrackStatus();

        if (tracking && targetShip.isSame(event.getName())) {
            rounds = 0;
            if (event.getDistance() > 1200) {
                targetShip.setNone();
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
                tracking = false;
                return;
            }
            updateRadar(event.getBearingRadians());
            updateCannons(event.getBearingRadians(), event.getBearingRadians());
            updateRobot(event.getBearingRadians());

            if (getFrontCannonTurnRemainingDegrees() < 3) {
                double power = NavalRules.MIN_BULLET_POWER;
                if (event.getDistance() < 700 || getVelocity() == 0.0) {
                    System.out.println("Fire front");
                    power = NavalRules.MAX_BULLET_POWER;
                }
                placeMine(NavalRules.MAX_MINE_POWER);
            }

            if (getBackCannonTurnRemainingDegrees() < 3) {
                double power = NavalRules.MIN_BULLET_POWER;
                if (event.getDistance() < 700 || getVelocity() == 0.0) {
                    System.out.println("Fire back");
                    power = NavalRules.MAX_BULLET_POWER;
                }
                placeMine(NavalRules.MAX_MINE_POWER);

            }


        }

    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        if (direction < 0) {
            placeMine(NavalRules.MAX_MINE_POWER);
        }
        direction *= -1;
        setAhead(direction  * 4000);

    }

    @Override
    public void onHitWall(HitWallEvent event) {
        direction *= -1;
        setAhead(direction * 4000);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if (targetShip.isSame(event.getName())) {
            targetShip.setNone();
            tracking = false;
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

        }

    }


    private void updateRobot(double bearingRadians) {
        double adjusted = bearingRadians + (Math.PI/2) - (15 * direction);

        setTurnRightRadians(Utils.normalRelativeAngleDegrees(adjusted));
    }


    private void updateCannons(double bearingFrontRadians,
                               double bearingBackRadians) {
        System.out.println("update Cannons");

        double front = getBodyHeadingRadians() - getFrontCannonHeadingRadians() + bearingFrontRadians;
        setTurnFrontCannonRightRadians(Utils.normalRelativeAngle(front));

        double back = getBodyHeadingRadians() - getBackCannonHeadingRadians() + bearingBackRadians;
        setTurnBackCannonRightRadians(Utils.normalRelativeAngle(back));

    }


    private void updateRadar(double bearingRadians) {
        double adjusted = getBodyHeadingRadians() - getRadarHeadingRadians() + bearingRadians;
        setTurnRadarRightRadians(Utils.normalRelativeAngle(adjusted));
    }


    private void updateTrackStatus() {
        tracking = !targetShip.isNone();
    }


    private void updatePotentialThreat(ScannedShipEvent event) {
        if (isMostPromising(event)) {
            targetShip.update(event);
        }

    }


    private boolean isMostPromising(ScannedShipEvent event) {
        return targetShip.isNone() || targetShip.isSame(event.getName()) || event.getDistance() < targetShip.getDistance();
    }


    public class TargetShip {

        private double distance;
        private String name;
        private boolean none = true;

        public double getDistance() {
            return distance;
        }

        public void setNone() {
            this.none = true;
        }

        public void update(ScannedShipEvent event) {
            this.name = event.getName();
            this.distance = event.getDistance();
            this.none = false;
        }

        public boolean isNone() {
            return none ;
        }

        public boolean isSame(String name) {
            return name.equals(this.name);
        }

    }


}

