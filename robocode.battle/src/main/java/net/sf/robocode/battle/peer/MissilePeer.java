package net.sf.robocode.battle.peer;

import net.sf.robocode.battle.BoundingRectangle;
import net.sf.robocode.peer.MissileStatus;
import net.sf.robocode.security.HiddenAccess;
import robocode.*;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.MissileState;
import robocode.naval.NavalRules;
import robocode.util.Collision;

import static java.lang.Math.*;
import static net.sf.robocode.io.Logger.logMessage;


import java.awt.geom.Rectangle2D;

import java.util.List;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public class MissilePeer extends ProjectilePeer {

    private final int missileId;

    private final BoundingRectangle missileBox;

    protected MissileState state;

    private List<RobotPeer> robots;

    private double startX;
    private double startY;
    private double distance=0;

    MissilePeer(RobotPeer owner, BattleRules battleRules, int missileId) {
        super();
        missileBox = new BoundingRectangle();
        this.owner = owner;
        this.battleRules = battleRules;
        this.missileId = missileId;
        this.state = MissileState.FIRED;

    }

    public void update(List<RobotPeer> robots, List<MissilePeer> missiles, List<BulletPeer> bullets) {
        frame++;
        if (isActive()) {
            if (HiddenAccess.getNaval()) {
                    checkCollisionWithShip(robots);
                    this.robots = robots;
                if (getState() == MissileState.MOVING) {
                    updateMovement();
                    checkMissileCollisionWithBullet(bullets);
                    checkWallCollision();
                }
                if (distance > NavalRules.getMissileDistance(power)) {
                    setProjectileStateMissed();
                    addProjectileMissedEvent();
                }
                if (missiles != null) {
                    checkMissileCollisionWithMissile(missiles);
                }
            }
        }
        updateMissileState();
        owner.addMissileStatus(createStatus());
    }

    private void updateMovement() {
        if(frame ==2) {
            startX = x;
            startY = y;
        }
        lastX = x;
        lastY = y;
        double v = getVelocity();
        x += v * sin(heading);
        y += v * cos(heading);
        distance = x - startX  + y - startY;
        if(distance < 0){
            distance *=-1;
        }

        missileBox.setRect(getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, getY()-NavalRules.HALF_MISSILE_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT);
    }

    /**-Collision methods**/
    private void checkMissileCollisionWithMissile(List<MissilePeer> missiles){//check if missile hit another missile
        for (MissilePeer  missile : missiles) {
            if (!(missile == null) && missile != this) {
                if (Collision.doBoxesIntersect(missile.getMissileBox(), this.getMissileBox())) {
                    detonate();
                    missile.detonate();
                    addMissileHitMissileEvent(missile);

                }
            }
        }
    }

    private void checkCollisionBlastRadiusFromMissile(List<RobotPeer> ships, BoundingRectangle blastBox){//check if blast radius hit a ship

        if (ships !=null) {
            for (RobotPeer otherShip : ships) {
                if (!(otherShip == null || otherShip.isDead())) {
                    BoundingRectangle otherBoundingBox = new BoundingRectangle();
                    otherBoundingBox.setRect(otherShip.getX()-NavalRules.HALF_WIDTH_OFFSET, otherShip.getY()-NavalRules.HALF_HEIGHT_OFFSET, NavalRules.WIDTH, NavalRules.HEIGHT);
                    if (Collision.doBoxesIntersect(otherBoundingBox, blastBox)) {
                        victim = otherShip;
                        if(!otherShip.immuneForBlast) {
                            double damage  = computeBlastRadiusDamage(otherShip);

                            updateEnergyAndScoreBlastRadius(otherShip, damage);
                            addBlastRadiusCollisionEvent(otherShip);
                            setNewMissileCoordinatesBlastRadius(otherShip);
                        }
                        break;
                    }
                }
            }
        }
    }

    private double computeBlastRadiusDamage(RobotPeer otherRobot) {
        double distance = sqrt(Math.pow(getX() - otherRobot.getX(), 2) + Math.pow(getY() - otherRobot.getY(), 2));
        double damage = NavalRules.getMissileDamage(getPower()) - distance/3;
        if(damage < 5){
            damage = 5;
        }
        logMessage("Event: " +otherRobot.getName()+" Was hit for "+damage+" caused by the blast radius from "+getOwner().getName()+" his missile.");

        return damage;
    }

    void collideMissileWithShip(RobotPeer otherRobot){
        updateEnergyAndScoreMissileHitShip(otherRobot);
        addMissileHitShipEvent(otherRobot);
        otherRobot.setImmuneForBlast(true);
        detonate();
        otherRobot.setImmuneForBlast(false);
        setNewMissileCoordinatesShipCollision(otherRobot);
    }

    private void checkMissileCollisionWithBullet(List<BulletPeer> bullets) {//check if missile hit a bullet
        for (BulletPeer b : bullets) {
            if (b != null && b.getState() == BulletState.MOVING && Collision.doBoxesIntersect(b.getBoundingBox(), getMissileBox())) {
                detonate();
                b.setBulletStateMissileHitBullet();


                addMissileHitBulletEvent(b);

                break;
            }
        }
    }

    /**Event methods**/
    private void addBlastRadiusCollisionEvent(RobotPeer ship){

        ship.addEvent(
                new HitByMissileEvent(
                        robocode.util.Utils.normalRelativeAngle(heading + Math.PI - ship.getBodyHeading()),
                        createMissile(true)));

        getOwner().addEvent(
                new MissileHitEvent(getOwner().getNameForEvent(ship), ship.getEnergy(), createMissile(false)));

    }

    private void addMissileHitShipEvent(RobotPeer otherRobot) {
        logMessage(owner.getName()+"'s Missile hit "+otherRobot.getName()+" with Direct Impact! Victim is now immune for blast radius of this missile");
        otherRobot.addEvent(
                new HitByMissileEvent(
                        robocode.util.Utils.normalRelativeAngle(heading + Math.PI - otherRobot.getBodyHeading()),
                        createMissile(true)));
        getOwner().addEvent(
                new MissileHitEvent(getOwner().getNameForEvent(otherRobot), otherRobot.getEnergy(), createMissile(false)));
    }

    private void addMissileHitMissileEvent(MissilePeer otherMissile) {
        logMessage("Event: " +getOwner().getName()+" his Missile hit "+ otherMissile.getOwner().getName()+ " his Missile.");
        this.getOwner().addEvent(
                new MissileHitMissileEvent(createMissile(false), createMissile(false)));
        otherMissile.getOwner().addEvent(
                new MissileHitMissileEvent(createMissile(false), createMissile(false)));
    }

    private void addMissileHitBulletEvent(BulletPeer b) {
        getOwner().addEvent(new BulletHitMissileEvent(b.createBullet(false), createMissile(true)));
        logMessage("Event: " +b.getOwner().getName()+" his Bullet hit "+getOwner().getName()+ "his Missile.");
    }

    /**State methods**/
    Missile createMissile(boolean hideOwnerName) {
        String ownerName = (owner == null) ? null : (hideOwnerName ? getNameForEvent(owner) : owner.getName());
        String victimName = (victim == null) ? null : (hideOwnerName ? victim.getName() : getNameForEvent(victim));

        return new Missile(heading, x, y, power, ownerName, victimName, isActive(), missileId);
    }

    private MissileStatus createStatus() {
        return new MissileStatus(missileId, x, y, victim == null ? null : getNameForEvent(victim), isActive());
    }

    private void updateMissileState() {
        switch (state) {
            case FIRED:
                // Note that the missile must be in the FIRED state before it goes to the MOVING state
                if (frame > 0) {
                    state = MissileState.MOVING;
                }
                break;

            case ARRIVED:
            case EXPLODING:
            case EXPLODED:
                // Note that the missile explosion must be ended before it goes into the INACTIVE state
                if (frame >= getExplosionLength()) {
                    state = MissileState.INACTIVE;
                }
                break;

            default:
        }
    }

    /**Update Energy and Score methods**/
    private void updateEnergyAndScoreMissileHitShip(RobotPeer otherRobot) {
        double damage = NavalRules.getMissileDamage(power);

        double score = damage;
        if (score > otherRobot.getEnergy()) {
            score = otherRobot.getEnergy();
        }
        otherRobot.updateEnergy(-damage);
        if(otherRobot != this.getOwner()) {
            getOwner().getRobotStatistics().scoreMissileDamage(otherRobot.getName(), score);
            getOwner().updateEnergy(NavalRules.getMissileHitBonus(getPower()));
        }
        updateKillWithMissile(otherRobot);

    }

    private void updateEnergyAndScoreBlastRadius(RobotPeer otherRobot, double damage) {
        double score = damage;

        otherRobot.updateEnergy(-damage);
        if((otherRobot != getOwner())) {
            getOwner().updateEnergy(NavalRules.getMissileHitBonus(getPower()));
            if (score > otherRobot.getEnergy()) {
                score = otherRobot.getEnergy();
            }
            getOwner().getRobotStatistics().scoreMissileDamage(otherRobot.getName(), score);
        }
        updateKillWithBlastRadius(otherRobot);
    }

    private void updateKillWithMissile(RobotPeer otherRobot){
        if (otherRobot.getEnergy() <= 0.0 && otherRobot.isAlive()) {
            otherRobot.kill();
            double bonus = getOwner().getRobotStatistics().scoreMissileKill(otherRobot.getName());
            if (bonus > 0) {
                getOwner().println(
                        "SYSTEM: Bonus for killing "
                                + (getOwner().getNameForEvent(otherRobot) + ": " + (int) (bonus + .5)));
            }
        }
    }

    private void updateKillWithBlastRadius(RobotPeer otherRobot) {
        if (otherRobot.getEnergy() <= 0 && otherRobot.isAlive()) {
            otherRobot.kill();
            if (otherRobot != getOwner()) {
                double bonus = getOwner().getRobotStatistics().scoreMissileKill(otherRobot.getName());
                if (bonus > 0) {
                    getOwner().println(
                            "SYSTEM: Bonus for killing "
                                    + (getOwner().getNameForEvent(otherRobot) + ": " + (int) (bonus + .5)));
                }
            }
        }
    }


    boolean isActive() {
        return state.isActive();
    }

    void detonate() {//will trigger the missile to detonate
        state = MissileState.ARRIVED;//once the state has been set to arrived, the updatemissilestate method will
        frame = 0;                     //make update the state to exploding on the next turn
        createBlastRadius();
    }

    private void createBlastRadius() {
        BoundingRectangle blastBox = new BoundingRectangle();
        double radius = NavalRules.getMissileBlastRadius(power);
        blastBox.setRect((x - radius), (y - radius), radius * 2, radius * 2);
        checkCollisionBlastRadiusFromMissile(robots, blastBox);
    }

    /**----GETTERS & SETTERS----**/
    private void setNewMissileCoordinatesBlastRadius(RobotPeer otherRobot){
        double newX, newY;

        if (otherRobot.getBoundingBox().contains(lastX, lastY)) {
            newX = lastX;
            newY = lastY;

            setX(newX);
            setY(newY);
        } else {
            newX = x;
            newY = y;
        }

        deltaX = newX - otherRobot.getX();
        deltaY = newY - otherRobot.getY();

    }

    private void setNewMissileCoordinatesShipCollision(RobotPeer otherRobot) {
        double newX, newY;

        if (otherRobot.getBoundingBox().contains(lastX, lastY)) {
            newX = lastX;
            newY = lastY;

            setX(newX);
            setY(newY);
        } else {
            newX = x;
            newY = y;
        }

        deltaX = newX - otherRobot.getX();
        deltaY = newY - otherRobot.getY();
    }

    public int getMissileId() {
        return missileId;
    }

    public Rectangle2D getMissileBox(){
        return missileBox;
    }
    String getNameForEvent(RobotPeer otherRobot) {
        if (battleRules.getHideEnemyNames() && !owner.isTeamMate(otherRobot)) {
            return otherRobot.getAnnonymousName();
        }
        return otherRobot.getName();
    }

    public MissileState getState() {
        return state;
    }

    public double getPaintX() {
        return (state == MissileState.ARRIVED && victim != null) ? victim.getX() + deltaX : x;
    }

    public double getPaintY() {
        return (state == MissileState.ARRIVED && victim != null) ? victim.getY() + deltaY : y;
    }

    public double getVelocity() {
        return (NavalRules.getMissileSpeed(power));
    }
    /*----END OF GETTERS & SETTERS----*/


    @Override
    public String toString() {
        return getOwner().getName() + " V" + getVelocity() + " *" + (int) power + " X" + (int) x + " Y" + (int) y + " H"
                + heading + " " + state.toString();
    }
}
