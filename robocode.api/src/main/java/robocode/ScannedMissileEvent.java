/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package robocode;

import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicEvents4;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.nio.ByteBuffer;

import static java.lang.Math.PI;

/**
 * Extension on {@link robocode.ScannedShipEvent}
 * @author Thales B.V. / Jiri Waning / Thomas Hakkers / Colin Heppener
 * @since 1.8.3.0 Alpha 1
 * @version 0.1
 */
public class ScannedMissileEvent extends Event {
    private static final long serialVersionUID = 1L;
    private final static int DEFAULT_PRIORITY = 98;

    protected final String name;
    protected final double power;
    protected final double heading;
    protected final double distance;
    protected final double velocity;

    private final double bearingFront;
    private final double bearingBack;
    private final double bearingRadar;

    private final double x;
    private final double y;
    private String owner;

    /**
     * Called by the game to create a new ScannedMissileEvent.
     *
     * @param name         The name of the scanned missile.
     * @param power        The power of the scanned missile.
     * @param bearingFront The bearing of the scanned missile, in radians, for the weapon on the front end.
     * @param bearingBack  The bearing of the scanned missile, in radians, for the weapon on the back end.
     * @param bearingRadar The bearing of the scanned missile, in radians, for the radar.
     * @param distance     The distance from your ship to the scanned missile.
     * @param heading      The heading of the scanned missile.
     * @param velocity     The velocity of the scanned missile.
     */
    public ScannedMissileEvent(String name, double power, double bearingFront, double bearingBack, double bearingRadar, double distance, double heading, double velocity, double x, double y, String owner) {
        this.name = name;
        this.power = power;
        this.heading = heading;
        this.distance = distance;
        this.velocity = velocity;

        this.bearingFront = bearingFront;
        this.bearingBack = bearingBack;
        this.bearingRadar = bearingRadar;

        this.x = x;
        this.y = y;
        this.owner = owner;
    }

    /**
     * Returns the bearing to the missile you scanned, measured from the front
     * weapon; relative to your ship's front weapon zero (absolute north!),
     * in degrees (-180 <= getBearing() < 180)
     *
     * @return The bearing to the missile you scanned, in degrees, measured from
     * the front weapon.
     */
    public double getBearingFront() {
        return bearingFront * 180 / PI;
    }

    /**
     * Returns the bearing to the missile you scanned, measured from the front
     * weapon; relative to your ship's front weapon zero (absolute north!),
     * in radians (-PI <= getBearing() < PI)
     *
     * @return The bearing to the missile you scanned, in radians, measured from
     * the front weapon.
     */
    public double getBearingFrontRadians() {
        return bearingFront;
    }

    /**
     * Returns the bearing to the missile you scanned, measured from the back
     * weapon; relative to your ship's back weapon zero (absolute south!),
     * in degrees (-180 <= getBearing() < 180)
     *
     * @return The bearing to the missile you scanned, in degrees, measured from
     * the back weapon.
     */
    public double getBearingBack() {
        return bearingBack * 180.0d / PI;
    }

    /**
     * Returns the bearing to the missile you scanned, measured from the back
     * weapon; relative to your ship's back weapon zero (absolute south!),
     * in radians (-PI <= getBearing() < PI)
     *
     * @return The bearing to the missile you scanned, in radians, measured from
     * the back weapon.
     */
    public double getBearingBackRadians() {
        return bearingBack;
    }

    /**
     * Returns the distance to the missile's origin. (Your center to it's center.)
     *
     * @return The distance to the missile's origin.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the power of the missile.
     *
     * @return The power of the missile.
     */
    public double getPower() {
        return power;
    }

    /**
     * Returns the heading of the missile, in degrees. (0 <= getHeading() < 360)
     *
     * @return The heading of the missile, in degrees.
     */
    public double getHeading() {
        return heading * 180.0 / Math.PI;
    }

    /**
     * Returns the heading of the missile, in radians. (0 <= getHeading() < TWO_PI)
     *
     * @return The heading of the missile, in radians.
     */
    public double getHeadingRadians() {
        return heading;
    }

    public double getBearingRadians() {
        return bearingRadar;
    }

    public double getBearingDegrees() {
        return Math.toDegrees(bearingRadar);
    }

    /**
     * Returns the name of owner of the missile.
     *
     * @return The name of the owner of the missile.
     */
    public String getName() {
        return owner;
    }

    /**
     * Returns the velocity of the missile.
     *
     * @return The velocity of the missile.
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int compareTo(Event event) {
        final int res = super.compareTo(event);

        if (res != 0) {
            return res;
        }
        // Compare the distance, if the events are ScannedShipEvents
        if (event instanceof ScannedMissileEvent) {
            ScannedMissileEvent obj = (ScannedMissileEvent) event;
            return (this.name == obj.name &&
                    this.power == obj.power &&
                    this.heading == obj.heading &&
                    this.distance == obj.distance &&
                    this.velocity == obj.velocity &&
                    this.bearingFront == obj.bearingFront &&
                    this.bearingBack == obj.bearingBack) ? 1 : 0;
        }
        // No difference found
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final int getDefaultPriority() {
        return DEFAULT_PRIORITY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
        IBasicEvents listener = robot.getBasicEventListener();

        if (listener != null) {
            ((IBasicEvents4) listener).onScannedMissile(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte getSerializationType() {
        return RbSerializer.ScannedMissileEvent_TYPE;
    }

    static ISerializableHelper createHiddenSerializer() {
        return new SerializableHelper();
    }



    private static class SerializableHelper implements ISerializableHelper {
        public int sizeOf(RbSerializer serializer, Object object) {
            ScannedMissileEvent obj = (ScannedMissileEvent) object;

            return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.name) + 6 * RbSerializer.SIZEOF_DOUBLE;
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            ScannedMissileEvent obj = (ScannedMissileEvent) object;

            serializer.serialize(buffer, obj.name);
            serializer.serialize(buffer, obj.power);
            serializer.serialize(buffer, obj.heading);
            serializer.serialize(buffer, obj.distance);
            serializer.serialize(buffer, obj.velocity);

            serializer.serialize(buffer, obj.bearingFront);
            serializer.serialize(buffer, obj.bearingBack);
            serializer.serialize(buffer, obj.bearingRadar);
            serializer.serialize(buffer, obj.x);
            serializer.serialize(buffer, obj.y);
            serializer.serialize(buffer, obj.owner);
        }

        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            String name = serializer.deserializeString(buffer);
            double power = buffer.getDouble();
            double heading = buffer.getDouble();
            double distance = buffer.getDouble();
            double velocity = buffer.getDouble();

            double bearingFront = buffer.getDouble();
            double bearingBack = buffer.getDouble();
            double bearingRadar = buffer.getDouble();
            double x = buffer.getDouble();
            double y = buffer.getDouble();
            String owner = serializer.deserializeString(buffer);

            return new ScannedMissileEvent(name, power, bearingFront, bearingBack, bearingRadar, distance, heading, velocity,x ,y, owner);
        }
    }
}
