package navalsample;

import robocode.*;

import java.awt.*;


public class MissileShip extends Ship {
    private int direction = 1;

    @Override
    public void run() {
        //Colors
        setBodyColor(Color.RED);
        setFrontCannonColor(Color.BLUE);
        setBackCannonColor(Color.BLUE);
        setRadarColor(Color.YELLOW);
        setScanColor(Color.YELLOW);

        this.setTurnRadarRightDegrees(Double.POSITIVE_INFINITY);
        direction = 1;
        this.setTurnRightDegrees(Double.POSITIVE_INFINITY);
        this.setAhead(Double.POSITIVE_INFINITY);

        while (true) {
            execute();
        }
    }


    @Override
    public void onScannedShip(ScannedShipEvent e) {
        // The easiest way to target the scanned ship is to use these functions
        // Turn your front cannon towards the ship
        setTurnFrontCannonRightRadians(e.getBearingFrontRadians());
        //At this point you've already told your ship to move 4000 ahead,
        //AND you've told it to turn its front cannon
        //All you gotta do now is wait for your cannon to reach its destination
        while(getFrontCannonTurnRemainingRadians() != 0){
            execute();
            if(getFrontCannonAtBlindSpot())
                break;
        }
        //If your front cannon is not at its blindspot
        if(!getFrontCannonAtBlindSpot()){
            //Then shoot!
            launchMissile(30);
        }
    }

    public void onHitWall(HitWallEvent wall){
        direction *= -1;
        setAhead(direction  * 4000);
    }

    public void onHitRobot(HitRobotEvent event)
    {
        direction *= -1;
        setAhead(direction  * 4000);
    }

    public void onMissileHitMissile(MissileHitMissileEvent e){
        System.out.println("our missile hit "+e.getHitMissile().getName()+"'s missile");
    }
}
