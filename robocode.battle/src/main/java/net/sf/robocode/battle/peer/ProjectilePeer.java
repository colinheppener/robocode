package net.sf.robocode.battle.peer;

import net.sf.robocode.battle.BoundingRectangle;
import robocode.*;
import robocode.control.snapshot.BulletState;
import robocode.naval.NavalRules;
import robocode.robotinterfaces.ITransformable;
import robocode.util.Collision;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import static net.sf.robocode.battle.peer.ShipPeer.*;
import static net.sf.robocode.io.Logger.logMessage;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public abstract class ProjectilePeer implements ITransformable {
    static final int EXPLOSION_LENGTH = 17;
    private final int RADIUS = 3;

    RobotPeer owner;
    RobotPeer victim;

    public BattleRules battleRules;

    public double heading;

    protected double x;
    protected double y;

    double lastX;
    double lastY;

    double deltaX;
    double deltaY;

    protected double power;

    public int color;

    final Line2D.Double boundingLine = new Line2D.Double();

    public BoundingRectangle boundingBox = new BoundingRectangle();

    protected int frame; // Do not set to -1

    int explosionImageIndex; // Do not set to -1

    /*----COLLISION METHODS START----*/

    /**
     * Used to detect collision between the walls of the battle arena and a projectile.
     * Called every turn
     */
    void checkWallCollision() {//check if projectile hit the wall
        if ((x - RADIUS <= 0) || (y - RADIUS <= 0) || (x + RADIUS >= getBattleRules().getBattlefieldWidth())
                || (y + RADIUS >= getBattleRules().getBattlefieldHeight())) {

            setProjectileStateMissed();
            addProjectileMissedEvent();

        }
    }

    /**
     * Used to detect collision between a ship and a projectile
     * Called every turn.
     *
     * @param ships The ships currently present in the game.
     */
    void checkCollisionWithShip(List<RobotPeer> ships) {//check if missile hit a ship
        for (RobotPeer otherRobot : ships) {
            if (!(otherRobot == null || otherRobot == getOwner() || otherRobot.isDead())) {
                if (isMissile()) {
                    BoundingRectangle otherBoundingBox = new BoundingRectangle();
                    otherBoundingBox.setRect(otherRobot.getX() - HALF_WIDTH_OFFSET, otherRobot.getY() - HALF_HEIGHT_OFFSET, WIDTH, HEIGHT);
                    MissilePeer missile = (MissilePeer) this;
                    if (Collision.doBoxesIntersect(missile.getMissileBox(), otherBoundingBox)) {
                        missile.collideMissileWithShip(otherRobot);
                        break;
                    }
                }
                if (isBullet()) {
                    boundingLine.setLine(lastX, lastY, x, y);
                    if (Collision.collide(otherRobot, boundingLine)) {
                        BulletPeer bullet = (BulletPeer) this;
                        bullet.collideBulletWithShip(otherRobot);
                        break;
                    }
                }
            }
        }
    }

    void setProjectileStateMissed(){
    if(isBullet()) {
        BulletPeer bullet = (BulletPeer) this;
        bullet.setState(BulletState.HIT_WALL);
        frame = 0;
    }
    if(isMissile()){
        MissilePeer missile = (MissilePeer) this;
        missile.detonate();
        }
    }


    /*----EVENT METHODS START----*/
    void addProjectileMissedEvent() {
        if(isBullet()) {
            getOwner().addEvent(new BulletMissedEvent((Bullet)createProjectile(false)));
        }
        if(isMissile()){
            getOwner().addEvent(new MissileMissedEvent((Missile)createProjectile(false)));
        }
    }

    private Projectile createProjectile(boolean hideOwnerName) {
        if(isBullet()) {
            BulletPeer bullet = (BulletPeer) this;
            String ownerName = (owner == null) ? null : (hideOwnerName ? bullet.getNameForEvent(owner) : owner.getName());
            String victimName = (victim == null) ? null : (hideOwnerName ? victim.getName() : bullet.getNameForEvent(victim));

            return new Bullet(heading, x, y, power, ownerName, victimName, bullet.isActive(), bullet.getBulletId());
        }
        if(isMissile())
        {
            MissilePeer missile = (MissilePeer) this;
            String ownerName = (owner == null) ? null : (hideOwnerName ? missile.getNameForEvent(owner) : owner.getName());
            String victimName = (victim == null) ? null : (hideOwnerName ? victim.getName() : missile.getNameForEvent(victim));

            return new Missile(heading, x, y, power, ownerName, victimName, missile.isActive(), missile.getMissileId());
        }
        return null;//shouldn't be reached
    }
    /*----EVENT METHODS END----*/


    public boolean isMissile(){return this instanceof MissilePeer;}

    public boolean isBullet(){return this instanceof BulletPeer;}


    /*----Getters & setters----*/
    public double getHeading(){
        return heading;
    }

    public int getFrame(){
        return frame;
    }

    public double getPower(){return power;}

    public RobotPeer getVictim() {
        return victim;
    }

    public RobotPeer getOwner() {
        return owner;
    }

    public int getColor(){return color;}

    double getLastX(){return lastX;}

    double getLastY(){return lastY;}

    public int getExplosionImageIndex() {
        return explosionImageIndex;
    }

    public int getExplosionLength() {
        return EXPLOSION_LENGTH;
    }

    public BattleRules getBattleRules() {
        return battleRules;
    }

    public void setHeading(double newHeading) {
        heading = newHeading;
    }

    public void setPower(double newPower) {
        power = newPower;
    }

    public void setX(double newX) {
        x = lastX = newX;
    }

    public void setY(double newY) {
        y = lastY = newY;
    }

    void setVictim(RobotPeer newVictim) {
        victim = newVictim;
    }
    /*----End of getters & setters that are not from ITransformable----*/


    /**----INTERFACE METHODS FROM ITransformable----
     * ITransformable is implemented because the missiles need to be detectable by radar
     * and in order to do that the MissilePeer class needs to implement ITransformable
     * */
    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getBodyHeading() {
        return 0;
    }
    @Override
    public double getBattleFieldWidth() {
        return battleRules.getBattlefieldWidth();
    }

    @Override
    public double getBattleFieldHeight() {
        return battleRules.getBattlefieldHeight();
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean isDroid() {
        return false;
    }

    @Override
    public boolean isJuniorRobot() {
        return false;
    }

    @Override
    public boolean isInteractiveRobot() {
        return false;
    }

    @Override
    public boolean isPaintRobot() {
        return false;
    }

    @Override
    public boolean isAdvancedRobot() {
        return false;
    }

    @Override
    public boolean isShip() {
        return false;
    }

    @Override
    public boolean isProjectile(){
        return true;
    }
}
