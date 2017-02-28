/**
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.security.IHiddenBulletHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

import java.nio.ByteBuffer;


/**
 * Represents a bullet. This is returned from {@link Robot#fireBullet(double)}
 * and {@link AdvancedRobot#setFireBullet(double)}, and all the bullet-related
 * events.
 *
 * @see Robot#fireBullet(double)
 * @see AdvancedRobot#setFireBullet(double)
 * @see BulletHitEvent
 * @see BulletMissedEvent
 * @see BulletHitBulletEvent
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Bullet extends Projectile {

	private final int bulletId;

	/**
	 * Called by the game to create a new {@code Bullet} object
	 *
	 * @param heading   the heading of the bullet, in radians.
	 * @param x		 the starting X position of the bullet.
	 * @param y		 the starting Y position of the bullet.
	 * @param power	 the power of the bullet.
	 * @param ownerName the name of the owner robot that owns the bullet.
	 * @param victimName the name of the robot hit by the bullet.
	 * @param isActive {@code true} if the bullet still moves; {@code false} otherwise.
	 * @param bulletId unique id of bullet for owner robot.
	 */
	public Bullet(double heading, double x, double y, double power, String ownerName, String victimName, boolean isActive, int bulletId) {
		this.headingRadians = heading;
		this.x = x;
		this.y = y;
		this.power = power;
		this.ownerName = ownerName;
		this.victimName = victimName;
		this.isActive = isActive; 
		this.bulletId = bulletId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return bulletId == ((Bullet) obj).bulletId;
	}

	@Override
	public int hashCode() {
		return bulletId;
	}


	/**
	 * @return unique id of bullet for owner robot
	 */
	int getBulletId() {
		return bulletId;
	}

	/**
	 * Creates a hidden bullet helper for accessing hidden methods on this object.
	 * 
	 * @return a hidden bullet helper.
	 */
	// this method is invisible on RobotAPI
	static IHiddenBulletHelper createHiddenHelper() {
		return new HiddenBulletHelper();
	}

	/**
	 * Creates a hidden bullet helper for accessing hidden methods on this object.
	 *
	 * @return a hidden bullet helper.
	 */
	// this class is invisible on RobotAPI
	static ISerializableHelper createHiddenSerializer() {
		return new HiddenBulletHelper();
	}

	// this class is invisible on RobotAPI
	private static class HiddenBulletHelper implements IHiddenBulletHelper, ISerializableHelper {

		public void update(Bullet bullet, double x, double y, String victimName, boolean isActive) {
			bullet.update(x, y, victimName, isActive);
		}

		public int sizeOf(RbSerializer serializer, Object object) {
			Bullet obj = (Bullet) object;

			return RbSerializer.SIZEOF_TYPEINFO + 4 * RbSerializer.SIZEOF_DOUBLE + serializer.sizeOf(obj.ownerName)
					+ serializer.sizeOf(obj.victimName) + RbSerializer.SIZEOF_BOOL + RbSerializer.SIZEOF_INT;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			Bullet obj = (Bullet) object;

			serializer.serialize(buffer, obj.headingRadians);
			serializer.serialize(buffer, obj.x);
			serializer.serialize(buffer, obj.y);
			serializer.serialize(buffer, obj.power);
			serializer.serialize(buffer, obj.ownerName);
			serializer.serialize(buffer, obj.victimName);
			serializer.serialize(buffer, obj.isActive);
			serializer.serialize(buffer, obj.bulletId);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double headingRadians = buffer.getDouble();
			double x = buffer.getDouble();
			double y = buffer.getDouble();
			double power = buffer.getDouble();
			String ownerName = serializer.deserializeString(buffer);
			String victimName = serializer.deserializeString(buffer);
			boolean isActive = serializer.deserializeBoolean(buffer);
			int bulletId = serializer.deserializeInt(buffer);

			return new Bullet(headingRadians, x, y, power, ownerName, victimName, isActive, bulletId);
		}
	}
}
