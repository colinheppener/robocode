package navalsample;

import java.awt.Color;

import robocode.*;
import robocode.naval.NavalRules;
import robocode.util.Utils;

public class MissileDetecter extends Ship {

    private Target target = new Target();

    private boolean tracking;

    private double direction = 1;

    private int rounds = 0;

    @Override
    public void run() {

        setBodyColor(Color.BLACK);
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
                target.setNone();
            }
            rounds++;

            execute();

        }
    }


    @Override
    public void onScannedMissile(ScannedMissileEvent event) {
        updatePotentialThreat(event);
        updateTrackStatus();

        if (tracking && target.isSame(event.getName())) {
            rounds = 0;
            if (event.getDistance() > 1200) {
                target.setNone();
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
                tracking = false;
                return;
            }
            updateRadar(event.getBearingRadians());
            updateCannons(event.getBearingRadians());
            updateRobot(event.getBearingRadians());
            if (getFrontCannonTurnRemainingDegrees() < 3) {
                double power = NavalRules.MIN_BULLET_POWER;
                fireFrontCannon(power );
            }

            if (getBackCannonTurnRemainingDegrees() < 3) {
                double power = NavalRules.MIN_BULLET_POWER;
                fireBackCannon(power);
            }


        }

    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
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
        if (target.isSame(event.getName())) {
            target.setNone();
            tracking = false;
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

        }

    }


    private void updateRobot(double bearingRadians) {
        double adjusted = bearingRadians + (Math.PI/2) - (15 * direction);

        setTurnRightRadians(Utils.normalRelativeAngleDegrees(adjusted));
    }


    private void updateCannons(double bearingFrontRadians) {

        double front = getBodyHeadingRadians() - getFrontCannonHeadingRadians() + bearingFrontRadians;
        setTurnFrontCannonRightRadians(Utils.normalRelativeAngle(front));

        double back = getBodyHeadingRadians() - getBackCannonHeadingRadians() + bearingFrontRadians;
        setTurnBackCannonRightRadians(Utils.normalRelativeAngle(back));

    }


    private void updateRadar(double bearingRadians) {
        double adjusted = getBodyHeadingRadians() - getRadarHeadingRadians() + bearingRadians;
        setTurnRadarRightRadians(Utils.normalRelativeAngle(adjusted));
    }


    private void updateTrackStatus() {
        tracking = !target.isNone();
    }


    private void updatePotentialThreat(ScannedMissileEvent event) {
        if (isMostPromising(event)) {
            target.update(event);
        }
    }


    private boolean isMostPromising(ScannedMissileEvent event) {
        return target.isNone() || target.isSame(event.getName()) || event.getDistance() < target.getDistance();
    }


    private class Target {

        private double distance;
        private String name;
        private boolean none = true;

        public double getDistance() {
            return distance;
        }

        public void setNone() {
            this.none = true;
        }

        public void update(ScannedMissileEvent event) {
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
