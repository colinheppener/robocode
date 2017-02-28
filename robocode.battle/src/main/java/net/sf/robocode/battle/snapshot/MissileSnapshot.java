/**
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.peer.MissilePeer;
import net.sf.robocode.battle.peer.ExplosionPeer;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.MissileState;
import robocode.control.snapshot.IMissileSnapshot;

import java.awt.geom.Rectangle2D;
import java.io.IOException;


final class MissileSnapshot implements java.io.Serializable, IXmlSerializable, IMissileSnapshot {

    private static final long serialVersionUID = 2L;

    /** The missile state */
    private MissileState state;

    /** The missile power */
    private double power;

    /** The x position */
    private double x;

    /** The y position */
    private double y;

    /** The x painting position (due to offset on robot when missile hits a robot) */
    private double paintX;

    /** The y painting position (due to offset on robot when missile hits a robot) */
    private double paintY;

    /** The ARGB color of the missile */
    private int color = ExecCommands.defaultMissileColor;

    /** The current frame number to display, i.e. when the missile explodes */
    private int frame;

    /** Flag specifying if this missile has turned into an explosion */
    private boolean isExplosion;

    /** Index to which explosion image that must be rendered */
    private int explosionImageIndex;

    private int missileId;

    private int victimIndex = -1;

    private int ownerIndex;

    private double heading;

    private Rectangle2D boundingBox;

    /**
     * Creates a snapshot of a missile that must be filled out with data later.
     */
    public MissileSnapshot() {
        state = MissileState.INACTIVE;
        ownerIndex = -1;
        victimIndex = -1;
        explosionImageIndex = -1;
        heading = Double.NaN;
        power = Double.NaN;
    }

    /**
     * Creates a snapshot of a missile.
     *
     * @param missile the missile to make a snapshot of.
     */
    MissileSnapshot(MissilePeer missile) {
        state = missile.getState();

        power = missile.getPower();
        boundingBox = missile.getMissileBox();

        x = missile.getX();
        y = missile.getY();

        paintX = missile.getPaintX();
        paintY = missile.getPaintY();

        color = missile.getColor();

        frame = missile.getFrame();
        isExplosion = false;
        explosionImageIndex = missile.getExplosionImageIndex();

        missileId = missile.getMissileId();

        final RobotPeer victim = missile.getVictim();

        if (victim != null) {
            victimIndex = victim.getRobotIndex();
        }
        ownerIndex = missile.getOwner().getRobotIndex();

        heading = missile.getHeading();
    }

    @Override
    public String toString() {
        return ownerIndex + "-" + missileId + " (" + (int) power + ") X" + (int) x + " Y" + (int) y + " "
                + state.toString();
    }

    /**
     * {@inheritDoc}
     */
    public int getMissileId() {
        return missileId;
    }

    /**
     * {@inheritDoc}
     */
    public MissileState getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    public double getPower() {
        return power;
    }

    /**
     * {@inheritDoc}
     */
    public double getX() {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    public double getY() {
        return y;
    }

    @Override
    public double getBodyHeading() {
        return 0;
    }

    @Override
    public double getBattleFieldWidth() {
        return 0;
    }

    @Override
    public double getBattleFieldHeight() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public double getPaintX() {
        return paintX;
    }

    /**
     * {@inheritDoc}
     */
    public double getPaintY() {
        return paintY;
    }

    /**
     * {@inheritDoc}
     */
    public int getColor() {
        return color;
    }

    /**
     * {@inheritDoc}
     */
    public int getFrame() {
        return frame;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExplosion() {
        return isExplosion;
    }

    /**
     * {@inheritDoc}
     */
    public int getExplosionImageIndex() {
        return explosionImageIndex;
    }

    /**
     * {@inheritDoc}
     */
    public double getHeading() {
        return heading;
    }

    public double getHeadingDegrees(){
        return Math.toDegrees(heading);
    }

    /**
     * {@inheritDoc}
     */
    public int getVictimIndex() {
        return victimIndex;
    }

    /**
     * {@inheritDoc}
     */
    public int getOwnerIndex() {
        return ownerIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
        writer.startElement(options.shortAttributes ? "b" : "missile"); {
            writer.writeAttribute("id", ownerIndex + "-" + missileId);
            if (!options.skipExploded || state != MissileState.MOVING) {
                writer.writeAttribute(options.shortAttributes ? "s" : "state", state.toString());
                writer.writeAttribute(options.shortAttributes ? "p" : "power", power, options.trimPrecision);
            }
            if (state == MissileState.ARRIVED) {
                writer.writeAttribute(options.shortAttributes ? "v" : "victim", victimIndex);
            }
            if (state == MissileState.FIRED) {
                writer.writeAttribute(options.shortAttributes ? "o" : "owner", ownerIndex);
                writer.writeAttribute(options.shortAttributes ? "h" : "heading", heading, options.trimPrecision);
            }
            writer.writeAttribute("x", paintX, options.trimPrecision);
            writer.writeAttribute("y", paintY, options.trimPrecision);
            if (!options.skipNames) {
                if (color != ExecCommands.defaultMissileColor) {
                    writer.writeAttribute(options.shortAttributes ? "c" : "color",
                            Integer.toHexString(color).toUpperCase());
                }
            }
            if (!options.skipExploded) {
                if (frame != 0) {
                    writer.writeAttribute("frame", frame);
                }
                if (isExplosion) {
                    writer.writeAttribute("isExplosion", true);
                    writer.writeAttribute("explosion", explosionImageIndex);
                }
            }
            if (!options.skipVersion) {
                writer.writeAttribute("ver", serialVersionUID);
            }
        }
        writer.endElement();
    }

    /**
     * {@inheritDoc}
     */
    public XmlReader.Element readXml(XmlReader reader) {
        return reader.expect("missile", "b", new XmlReader.Element() {
            public IXmlSerializable read(XmlReader reader) {
                final MissileSnapshot snapshot = new MissileSnapshot();

                reader.expect("id", new XmlReader.Attribute() {
                    public void read(String value) {
                        String[] parts = value.split("-");

                        snapshot.ownerIndex = Integer.parseInt(parts[0]);
                        snapshot.missileId = Integer.parseInt(parts[1]);
                    }
                });

                reader.expect("state", "s", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.state = MissileState.valueOf(value);
                    }
                });

                reader.expect("power", "p", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.power = Double.parseDouble(value);
                    }
                });

                reader.expect("heading", "h", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.heading = Double.parseDouble(value);
                    }
                });

                reader.expect("victim", "v", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.victimIndex = Integer.parseInt(value);
                    }
                });

                reader.expect("owner", "o", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.ownerIndex = Integer.parseInt(value);
                    }
                });

                reader.expect("x", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.x = Double.parseDouble(value);
                        snapshot.paintX = snapshot.x;
                    }
                });

                reader.expect("y", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.y = Double.parseDouble(value);
                        snapshot.paintY = snapshot.y;
                    }
                });

                reader.expect("color", "c", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.color = Long.valueOf(value.toUpperCase(), 16).intValue();
                    }
                });

                reader.expect("isExplosion", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.isExplosion = Boolean.parseBoolean(value);
                        if (snapshot.isExplosion && snapshot.state == null) {
                            snapshot.state = MissileState.EXPLODED;
                        }
                    }
                });

                reader.expect("explosion", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.explosionImageIndex = Integer.parseInt(value);
                    }
                });

                reader.expect("frame", new XmlReader.Attribute() {
                    public void read(String value) {
                        snapshot.frame = Integer.parseInt(value);
                    }
                });
                return snapshot;
            }
        });
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
    public boolean isProjectile() {
        return true;
    }
}
