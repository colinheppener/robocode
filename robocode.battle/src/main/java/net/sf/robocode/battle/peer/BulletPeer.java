/*******************************************************************************
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.battle.peer;

import net.sf.robocode.peer.BulletStatus;
import net.sf.robocode.security.HiddenAccess;
import robocode.*;
import robocode.control.snapshot.BulletState;
import robocode.naval.NavalRules;
import robocode.util.Collision;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static net.sf.robocode.io.Logger.logMessage;

import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Titus Chen (constributor)
 * @author Pavel Savara (constributor)
 */
public class BulletPeer extends ProjectilePeer{

	BulletState state;

	private int bulletId;

	BulletPeer(RobotPeer owner, BattleRules battleRules, int bulletId) {
		super();
		this.owner = owner;
		this.battleRules = battleRules;
		this.bulletId = bulletId;
		state = BulletState.FIRED;
		color = owner.getBulletColor(); // Store current bullet color set on robot
	}
	
	/**
	 * An extra constructor for the ShipPeer.
	 * This is done because the BulletColor is held within the WeaponComponents, not the ShipPeer
	 * @param owner	The owner of the bullet
	 * @param battleRules The rules of this battle
	 * @param bulletId The id of the bullet
	 * @param color The color of the bullet. This parameter is why this constructor was made.
	 */
	BulletPeer(ShipPeer owner, BattleRules battleRules, int bulletId, int color){
		this(owner, battleRules, bulletId);
		this.color = color;
	}

	public void update(List<RobotPeer> robots, List<BulletPeer> bullets, List<MissilePeer> missiles) {
		frame++;
		if (isActive()) {
			updateMovement();
			checkWallCollision();
			if (isActive()) {
				if(HiddenAccess.getNaval()){
					checkCollisionWithShip(robots);
					if (bullets != null) {
						checkBulletCollisionWithBullet(bullets);
					}
					if(isActive() && missiles != null){
						checkBulletCollisionWithMissile(missiles);
					}
				}
				else{
					checkRobotCollision(robots);
				}



			}
		}
		updateBulletState();
		owner.addBulletStatus(createStatus());
	}

	private void updateMovement() {
		lastX = x;
		lastY = y;

		double v = getVelocity();

		x += v * sin(heading);
		y += v * cos(heading);

		boundingBox.setRect(x-(getPower()), y-(getPower()), getPower()*2, getPower()*2);
		boundingLine.setLine(lastX, lastY, x, y);
	}

	/**This checkRobotCollision method is used for the regular robocode version. NOT NAVAL.**/
	private void checkRobotCollision(List<RobotPeer> robots) {
		for (RobotPeer otherRobot : robots) {
			if (!(otherRobot == null || otherRobot == owner || otherRobot.isDead())
					&& otherRobot.getBoundingBox().intersectsLine(boundingLine)) {

				state = BulletState.HIT_VICTIM;
				frame = 0;
				victim = otherRobot;

				double damage = NavalRules.getBulletDamage(power);

				if (owner.isSentryRobot()) {
					if (victim.isSentryRobot()) {
						damage = 0;
					} else {
						int range = battleRules.getSentryBorderSize();
						if (x > range && x < (battleRules.getBattlefieldWidth() - range) && y > range
								&& y < (battleRules.getBattlefieldHeight() - range)) {
							damage = 0;
						}
					}
				}

				double score = damage;
				if (score > otherRobot.getEnergy()) {
					score = otherRobot.getEnergy();
				}
				otherRobot.updateEnergy(-damage);

				boolean teamFire = (owner.getTeamPeer() != null && owner.getTeamPeer() == otherRobot.getTeamPeer());

				if (!teamFire && !otherRobot.isSentryRobot()) {
					owner.getRobotStatistics().scoreBulletDamage(otherRobot.getName(), score);
				}

				if (otherRobot.getEnergy() <= 0 && otherRobot.isAlive()) {
					otherRobot.kill();
					if (!teamFire && !otherRobot.isSentryRobot()) {
						double bonus = owner.getRobotStatistics().scoreBulletKill(otherRobot.getName());
						if (bonus > 0) {
							owner.println(
									"SYSTEM: Bonus for killing "
											+ (owner.getNameForEvent(otherRobot) + ": " + (int) (bonus + .5)));
						}
					}
				}

				if (!victim.isSentryRobot()) {
					owner.updateEnergy(NavalRules.getBulletHitBonus(power));
				}

				otherRobot.addEvent(
						new HitByBulletEvent(
								robocode.util.Utils.normalRelativeAngle(heading + Math.PI - otherRobot.getBodyHeading()),
								createBullet(true))); // Bugfix #366

				owner.addEvent(
						new BulletHitEvent(owner.getNameForEvent(otherRobot), otherRobot.getEnergy(), createBullet(false))); // Bugfix #366

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

				break;
			}
		}
	}

	/**Collision methods**/
	void collideBulletWithShip(RobotPeer otherRobot){
		setBulletStateBulletHitShip();

		updateEnergyAndScoreBulletHitShip(otherRobot);
		updateKillWithBullet(otherRobot);

		addBulletHitShipEvent(otherRobot);

		setNewBulletCoordinatesShipCollision(otherRobot);
	}

	private void checkBulletCollisionWithBullet(List<BulletPeer> otherBullets) {//check if bullet hit another bullet
		for (BulletPeer b : otherBullets) {
			if (b != null && b != this && b.isActive() && Collision.doBoxesIntersect(getBoundingBox(),b.getBoundingBox())) {
				if(getOwner() != b.getOwner()) {
					setBulletStateBulletHitBullet(b);
					addBulletHitBulletEvent(b);
				}
				break;
			}
		}
	}

	private void checkBulletCollisionWithMissile(List<MissilePeer> missiles) {//check if bullet hit a missile
		for (MissilePeer missile : missiles) {
			if (missile != null && missile.isActive() && Collision.doBoxesIntersect(getBoundingBox(), missile.getBoundingBox())) {
				setBulletStateBulletHitMissile();
				x = lastX;
				y = lastY;

				missile.detonate();
				addBulletHitMissileEvent(missile);
				break;
			}
		}
	}

	/**State methods**/
	void updateBulletState() {
		switch (state) {
			case FIRED:
				// Note that the bullet must be in the FIRED state before it goes to the MOVING state
				if (frame > 0) {
					state = BulletState.MOVING;
				}
				break;

			case HIT_BULLET:
			case HIT_VICTIM:
			case HIT_WALL:
			case EXPLODED:
				// Note that the bullet explosion must be ended before it goes into the INACTIVE state
				if (frame >= getExplosionLength()) {
					state = BulletState.INACTIVE;
				}
				break;

			default:
		}
	}

	Bullet createBullet(boolean hideOwnerName) {
		String ownerName = (owner == null) ? null : (hideOwnerName ? getNameForEvent(owner) : owner.getName());
		String victimName = (victim == null) ? null : (hideOwnerName ? victim.getName() : getNameForEvent(victim));

		return new Bullet(heading, x, y, power, ownerName, victimName, isActive(), bulletId);
	}

	private BulletStatus createStatus() {
		return new BulletStatus(bulletId, x, y, victim == null ? null : getNameForEvent(victim), isActive());
	}

	void setState(BulletState newState) {
		state = newState;
	}

	private void setBulletStateBulletHitBullet(BulletPeer b) {
		setState(BulletState.HIT_BULLET);
		frame = 0;
		x = lastX;
		y = lastY;

		b.setState(BulletState.HIT_BULLET);
		b.frame = 0;
		b.x = b.lastX;
		b.y = b.lastY;
	}

	private void setBulletStateBulletHitShip() {
		setState(BulletState.HIT_VICTIM);
		frame = 0;
	}

	private void setBulletStateBulletHitMissile() {
		setState(BulletState.HIT_BULLET);
		frame = 0;
	}

	void setBulletStateMissileHitBullet() {
		setState(BulletState.HIT_BULLET);
		frame = 0;
		x = getLastX();
		y = getLastY();
	}

	/**Event methods**/
	private void addBulletHitBulletEvent(BulletPeer otherBullet){
		getOwner().addEvent(new BulletHitBulletEvent(createBullet(false), otherBullet.createBullet(true)));
		otherBullet.getOwner().addEvent(new BulletHitBulletEvent(otherBullet.createBullet(false), createBullet(true)));
		logMessage("Event: " +getOwner().getName()+" his Bullet hit "+otherBullet.getOwner().getName()+ "his Bullet.");

	}

	private void addBulletHitShipEvent(RobotPeer otherRobot) {
		otherRobot.addEvent(
				new HitByBulletEvent(
						robocode.util.Utils.normalRelativeAngle(heading + Math.PI - otherRobot.getBodyHeading()),
						createBullet(true)));
		getOwner().addEvent(
				new BulletHitEvent(getOwner().getNameForEvent(otherRobot), otherRobot.getEnergy(), createBullet(false)));
	}

	private void addBulletHitMissileEvent(MissilePeer missile){
		getOwner().addEvent(new BulletHitMissileEvent(createBullet(false), missile.createMissile(true)));
		logMessage("Event: " +getOwner().getName()+" his Bullet hit "+missile.getOwner().getName()+ "his Missile.");
	}

	/**Update Energy and Score methods**/
	private void updateEnergyAndScoreBulletHitShip(RobotPeer otherRobot) {
		double damage = NavalRules.getBulletDamage(power);

		double score = damage;
		if (score > otherRobot.getEnergy()) {
			score = otherRobot.getEnergy();
		}

		otherRobot.updateEnergy(-damage);
		getOwner().getRobotStatistics().scoreBulletDamage(otherRobot.getName(), score);
		getOwner().updateEnergy(NavalRules.getBulletHitBonus(power));

	}

	private void updateKillWithBullet(RobotPeer otherRobot){
		if (otherRobot.getEnergy() <= 0 && otherRobot.isAlive()) {
			otherRobot.kill();
			double bonus = getOwner().getRobotStatistics().scoreBulletKill(otherRobot.getName());
			if (bonus > 0) {
				getOwner().println(
						"SYSTEM: Bonus for killing "
								+ (getOwner().getNameForEvent(otherRobot) + ": " + (int) (bonus + .5)));
			}
		}
	}


	boolean isActive() {
		return state.isActive();
	}

	/**----GETTERS & SETTERS----**/
	private void setNewBulletCoordinatesShipCollision(RobotPeer otherRobot) {
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

	public int getBulletId() {
		return bulletId;
	}

	String getNameForEvent(RobotPeer otherRobot) {
		if (battleRules.getHideEnemyNames() && !owner.isTeamMate(otherRobot)) {
			return otherRobot.getAnnonymousName();
		}
		return otherRobot.getName();
	}

	public BulletState getState() {
		return state;
	}

	public double getPaintX() {
		return (state == BulletState.HIT_VICTIM && victim != null) ? victim.getX() + deltaX : x;
	}

	public double getPaintY() {
		return (state == BulletState.HIT_VICTIM && victim != null) ? victim.getY() + deltaY : y;
	}

	public double getVelocity() {
		return NavalRules.getBulletSpeed(power);
	}

	@Override
	public String toString() {
		return getOwner().getName() + " V" + getVelocity() + " *" + (int) power + " X" + (int) x + " Y" + (int) y + " H"
				+ heading + " " + state.toString();
	}
}
