package net.sf.robocode.test.naval;

import static java.lang.Math.PI;
import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.junit.Assert.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import net.sf.robocode.battle.BoundingRectangle;
import net.sf.robocode.battle.peer.MissilePeer;
import net.sf.robocode.io.Logger;
import net.sf.robocode.naval.shippeer.JMockShipPeer;

import org.junit.Ignore;
import org.junit.Test;

import robocode.BattleRules;
import robocode.Bullet;
import robocode.Missile;
import robocode.Ship;
import robocode.naval.NavalRules;
import robocode.util.Collision;

/**
 * This is a class used for unit testing the AdvancedCollision methods.
 * 
 * @author Thales B.V. / Jiri Waning
 */
@SuppressWarnings("unused")
public class CollisionTest extends Collision {
	private final String SUFFIX = "FAILED: robocode.tests::src/test/java::robocode.naval::CollisionTest::";

	//Tests whether the 2 given lines intersect, which they should, given they're in an X pattern.
	@Test
	public void test_intersect_a() { // boolean intersect(Line2D.Double, Line2D.Double)
		Line2D.Double lineA = new Line2D.Double(0.0d, 0.0d, 10.0d, 10.0d);
		Line2D.Double lineB = new Line2D.Double(0.0d, 10.0d, 10.0d, 0.0d);
		boolean ret = intersect(lineA, lineB);
		assertEquals(SUFFIX + "test_intersect_a",  ret, true);
	}
	
	//Tests whether two almost parallel lines intersect.
	@Test
	public void test_intersect_b() { // boolean intersect(Line2D.Double, Line2D.Double)
		Line2D.Double lineA = new Line2D.Double(0.0d, 0.0d, 10.0d, 10.0d);
		Line2D.Double lineB = new Line2D.Double(0.1d, 0.1d, 11.0d, 11.0d);
		boolean ret = intersect(lineA, lineB);
		assertEquals(SUFFIX + "test_intersect_b", ret, false);
	}
		
	@Test
	public void test_getPolygon_a() { // Polygon getPolygon(Rectangle2D, AffineTransform)
		Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
		AffineTransform at = new AffineTransform(); // Identity
		Polygon p = getPolygon(rect, at);
		
		final String SUFFIX = (this.SUFFIX + "test_getPolygon_a ==> ");
			
		// 5 vertices (NW, WE, SE, SW, NW)
		assertEquals(SUFFIX + "npoints", p.npoints, 0x05);
			
		// X-coordinates
		assertEquals(SUFFIX + "xpoints[0]", p.xpoints[0], 50); // NW
		assertEquals(SUFFIX + "xpoints[1]", p.xpoints[1], 100); // NE
		assertEquals(SUFFIX + "xpoints[2]", p.xpoints[2], 100); // SE
		assertEquals(SUFFIX + "xpoints[3]", p.xpoints[3], 50); // SW
		assertEquals(SUFFIX + "xpoints[4]", p.xpoints[4], p.xpoints[0]); // NW
			
		// Y-coordinates
		assertEquals(SUFFIX + "ypoints[0]", p.ypoints[0], 50); // NW
		assertEquals(SUFFIX + "ypoints[1]", p.ypoints[1], 50); // NE
		assertEquals(SUFFIX + "ypoints[2]", p.ypoints[2], 100); // SE
		assertEquals(SUFFIX + "ypoints[3]", p.ypoints[3], 100); // SW
		assertEquals(SUFFIX + "ypoints[4]", p.ypoints[4], p.ypoints[0]); // NW
	}
		
	@Test
	public void test_getPolygon_b() { // Polygon getPolygon(Rectangle2D, AffineTransform)
		Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
		double
			centerX = rect.getCenterX(),
			centerY = rect.getCenterY();
		AffineTransform at = AffineTransform.getRotateInstance(PI * 0.25d, centerX, centerY);
		Polygon p = getPolygon(rect, at);
		
		final String SUFFIX = (this.SUFFIX + "test_getPolygon_b ==> ");
				
		// 5 vertices (NW, WE, SE, SW, NW)
		assertEquals(SUFFIX + "npoints", p.npoints, 0x05);
				
		// Determines the length of half the diagonal.
		double extra = (sqrt(pow(rect.getWidth(), 2) + pow(rect.getHeight(), 2)) / 2);

		// X-coordinates
		assertEquals(SUFFIX + "xpoints[0]", p.xpoints[0] == floor(centerX), true); // Top
		assertEquals(SUFFIX + "xpoints[1]", p.xpoints[2] == floor(centerX), true); // Bottom
		assertEquals(SUFFIX + "xpoints[2]", p.xpoints[1] == floor(centerX + extra), true); // Right
		assertEquals(SUFFIX + "xpoints[3]", p.xpoints[3] == floor(centerX - extra), true); // Left
		assertEquals(SUFFIX + "xpoints[4]", p.xpoints[4], p.xpoints[0]); // Top
				
		// Y-coordinates
		assertEquals(SUFFIX + "ypoints[0]", p.ypoints[0] == floor(centerY - extra), true); // NW
		assertEquals(SUFFIX + "ypoints[1]", p.ypoints[2] == floor(centerY + extra), true); // SE
		assertEquals(SUFFIX + "ypoints[2]", p.ypoints[1] == floor(centerY), true); // NE
		assertEquals(SUFFIX + "ypoints[3]", p.ypoints[3] == floor(centerY), true); // SW
		assertEquals(SUFFIX + "ypoints[4]", p.ypoints[4], p.ypoints[0]); // NW
	}
		
	@Test
	public void test_distinct_a() { // ArrayList<Point> distinct(Polygon p) 
		Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
		AffineTransform at = new AffineTransform(); // Identity
		Polygon p = getPolygon(rect, at); // Succeeded, look at the above tests!
		ArrayList<Point> vertices = distinct(p);
		
		final String SUFFIX = (this.SUFFIX + "test_distinct_a ==> ");
			
		// 4 vertices (NW, NE, SE, SW)
		assertEquals(SUFFIX + "vertices.size()", vertices.size(), 4);
		
		// Verify the points too!
		assertEquals(SUFFIX + "vertices.get(0)", vertices.get(0), new Point2D.Double(50.0d, 50.0d)); // NW
		assertEquals(SUFFIX + "vertices.get(1)", vertices.get(1), new Point2D.Double(100.0d, 50.0d)); // NE
		assertEquals(SUFFIX + "vertices.get(2)", vertices.get(2), new Point2D.Double(100.0d, 100.0d)); // SE
		assertEquals(SUFFIX + "vertices.get(3)", vertices.get(3), new Point2D.Double(50.0d, 100.0d)); // SW
	}



	@Test
	public void test_intersectLine_a() { // boolean intersectLine(Polygon, Line2D.Double)
		Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
		AffineTransform at = new AffineTransform(); // Identity
		Polygon p = getPolygon(rect, at); // Succeeded, look at the above tests!
		Line2D.Double line = new Line2D.Double(0.0d, 0.0d, 150.0d, 150.0d);
			
		boolean intersects = intersectLine(p, line);
		assertEquals(SUFFIX + "test_intersectLine_a", intersects, true);
	}
		
	@Test
	public void test_intersectLine_b() { // boolean intersectLine(Polygon, Line2D.Double)
		Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
		AffineTransform at = new AffineTransform(); // Identity
		Polygon p = getPolygon(rect, at); // Succeeded, look at the above tests!
		Line2D.Double line = new Line2D.Double(99.0d, 99.0d, 101.0d, 101.0d);
			
		boolean intersects = intersectLine(p, line);
		assertEquals(SUFFIX + "test_intersectLine_b", intersects, true);
	}
	
	@Test
	public void test_intersectLine_c() { // boolean intersectLine(Polygon, Line2D.Double)
		Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
		AffineTransform at = new AffineTransform(); // Identity
		Polygon p = getPolygon(rect, at); // Succeeded, look at the above tests!
		Line2D.Double line = new Line2D.Double(48.9d, 99.0d, 51.1d, 101.1d);
			
		boolean intersects = intersectLine(p, line);
		assertEquals(SUFFIX + "test_intersectLine_c", intersects, false);
	}
		
	@Test
	public void test_intersectPolygon_a() { // intersectPolygon(Polygon, Polygon)
		Polygon p0;
		{
			Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
			AffineTransform at = new AffineTransform(); // Identity
			p0 = getPolygon(rect, at); // Succeeded, look at the above tests!
		}
			
		Polygon p1;
		{
			Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
			AffineTransform at = new AffineTransform(); // Identity
			p1 = getPolygon(rect, at); // Succeeded, look at the above tests!
		}
			
		boolean intersects = intersectPolygon(p0, p1);
		assertEquals(SUFFIX + "test_intersectPolygon_a", intersects, true);
	}
		
	@Test
	public void test_intersectPolygon_b() { // intersectPolygon(Polygon, Polygon)
		Polygon p0;
		{
			Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
			AffineTransform at = new AffineTransform(); // Identity
			p0 = getPolygon(rect, at); // Succeeded, look at the above tests!
		}
			
		Polygon p1;
		{
			Rectangle2D rect = new Rectangle2D.Double(75.0d, 75.0d, 50.0d, 50.0d);
			AffineTransform at = new AffineTransform(); // Identity
			p1 = getPolygon(rect, at); // Succeeded, look at the above tests!
		}
			
		boolean intersects = intersectPolygon(p0, p1);
		assertEquals(SUFFIX + "test_intersectPolygon_b", intersects, true);
	}
		
	@Test
	public void test_intersectPolygon_c() { // intersectPolygon(Polygon, Polygon)
		Polygon p0;
		{
			Rectangle2D rect = new Rectangle2D.Double(50.0d, 50.0d, 50.0d, 50.0d);
			AffineTransform at = new AffineTransform(); // Identity
			p0 = getPolygon(rect, at); // Succeeded, look at the above tests!
		}
			
		Polygon p1;
		{
			Rectangle2D rect = new Rectangle2D.Double(101.0d, 101.0d, 50.0d, 50.0d);
			AffineTransform at = new AffineTransform(); // Identity
			p1 = getPolygon(rect, at); // Succeeded, look at the above tests!
		}
			
		boolean intersects = intersectPolygon(p0, p1);
		assertEquals(SUFFIX + "test_intersectPolygon_c", intersects, false);
	}
	
	@Test
	public void test_mine_collision(){
		JMockShipPeer ship = new JMockShipPeer(300, 300, Math.toRadians(90));
		BoundingRectangle rect = new BoundingRectangle(310,310,5,5);
		assertTrue("test_mine_collision failed 1 ", collide(ship,rect));
		ship = new JMockShipPeer(300,300,Math.toRadians(180));
		assertTrue("test_mine_collision failed 2 ", collide(ship,rect));
	}

	@Test
	public void testMissileCollisionWithShipTrue(){
		JMockShipPeer ship = new JMockShipPeer(300, 300, Math.toRadians(90));
		BoundingRectangle shipBox = new BoundingRectangle(ship.getX()-NavalRules.HALF_WIDTH_OFFSET, ship.getY()-NavalRules.HALF_HEIGHT_OFFSET, NavalRules.WIDTH, NavalRules.HEIGHT);

		Missile missile = new Missile(Math.toRadians(90)+Math.PI, 300, 300, 30, "othership", null, true, 1);
		BoundingRectangle missileBox = new BoundingRectangle(missile.getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, missile.getY()-NavalRules.HALF_MISSILE_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT);
		assertTrue(SUFFIX+" Missiles do not collide while they should",Collision.doBoxesIntersect(shipBox, missileBox));
	}

	@Test
	public void testMissileCollisionWithShipFalse(){
		JMockShipPeer ship = new JMockShipPeer(300, 300, Math.toRadians(90));
		BoundingRectangle shipBox = new BoundingRectangle(ship.getX()-NavalRules.HALF_WIDTH_OFFSET, ship.getY()-NavalRules.HALF_HEIGHT_OFFSET, NavalRules.WIDTH, NavalRules.HEIGHT);

		Missile missile = new Missile(Math.toRadians(90)+Math.PI, 100, 100, 30, "othership", null, true, 1);
		BoundingRectangle missileBox = new BoundingRectangle(missile.getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, missile.getY()-NavalRules.HALF_MISSILE_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT);

		assertFalse(SUFFIX+" Missiles do collide while they should not", Collision.doBoxesIntersect(shipBox, missileBox));
	}

	@Test
	public void testMissileCollisionWithMissileTrue(){
		Missile missile1 = new Missile(Math.toRadians(90), 300, 300, 30, "firstship", null,true, 0);
		Missile missile2 = new Missile(Math.toRadians(90)+Math.PI, 300, 300, 30, "secondship", null, true, 1);
		BoundingRectangle missile1Box = new BoundingRectangle(
				missile1.getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, missile1.getY()-NavalRules.HALF_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT );

		BoundingRectangle missile2Box = new BoundingRectangle(
				missile2.getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, missile2.getY()-NavalRules.HALF_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT );
		assertTrue(SUFFIX+" Missiles do not collide while they should", doBoxesIntersect(missile1Box, missile2Box));
	}

	@Test
	public void testMissileCollisionWithMissileFalse(){
		Missile missile1 = new Missile(Math.toRadians(90), 300, 300, 30, "firstship", null,true, 0);
		Missile missile2 = new Missile(Math.toRadians(90)+Math.PI, 100, 100, 30, "secondship", null, true, 1);
		BoundingRectangle missile1Box = new BoundingRectangle(
				missile1.getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, missile1.getY()-NavalRules.HALF_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT );

		BoundingRectangle missile2Box = new BoundingRectangle(
				missile2.getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, missile2.getY()-NavalRules.HALF_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT );
		assertFalse(SUFFIX+" Missiles do collide while they should not", doBoxesIntersect(missile1Box, missile2Box));
	}

	@Test
	public void testMissileCollisionWithBulletTrue(){
		Missile missile = new Missile(Math.toRadians(90), 300, 300, 30, "firstship", null, true, 0);
		Bullet bullet = new Bullet(Math.toRadians(90), 300, 300, 3, "secondship", null, true, 0);
		BoundingRectangle missileBox = new BoundingRectangle(
				missile.getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, missile.getY()-NavalRules.HALF_MISSILE_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT );
		BoundingRectangle bulletBox = new BoundingRectangle(bullet.getX()-(bullet.getPower()), bullet.getY()-(bullet.getPower()), bullet.getPower()*2, bullet.getPower()*2);

		assertTrue(SUFFIX+" Bullet and missile do not collide while they should", doBoxesIntersect(missileBox, bulletBox));
	}

	@Test
	public void testMissileCollisionWithBulletFalse(){
		Missile missile = new Missile(Math.toRadians(90), 300, 300, 30, "firstship", null, true, 0);
		Bullet bullet = new Bullet(Math.toRadians(90)+Math.PI, 100, 100, 3, "secondship", null, true, 0);
		BoundingRectangle missileBox = new BoundingRectangle(
				missile.getX()-NavalRules.HALF_MISSILE_WIDTH_OFFSET, missile.getY()-NavalRules.HALF_HEIGHT_OFFSET, NavalRules.MISSILE_WIDTH, NavalRules.MISSILE_HEIGHT );
		BoundingRectangle bulletBox = new BoundingRectangle(
				bullet.getX()-10.5, bullet.getY()-10.5, 21, 21);

		assertFalse(SUFFIX+" Bullet and missile do collide while they should not", doBoxesIntersect(missileBox, bulletBox));
	}

	@Test
	public void testBulletCollisionWithBulletTrue(){
		Bullet bullet = new Bullet(Math.toRadians(90), 300, 300, 3, "firstship", null, true, 0);
		BoundingRectangle bulletBox = new BoundingRectangle(
				bullet.getX()-10.5, bullet.getY()-10.5, 21, 21);
		Bullet bullet2 = new Bullet(Math.toRadians(90)+Math.PI, 300, 300, 3, "secondship", null, true, 1);
		BoundingRectangle bulletBox2 = new BoundingRectangle(
				bullet2.getX()-10.5, bullet2.getY()-10.5, 21, 21);

		assertTrue(SUFFIX+" Bullets do not collide while they should", doBoxesIntersect(bulletBox, bulletBox2));
	}

	@Test
	public void testBulletCollisionWithBulletFalse(){
		Bullet bullet = new Bullet(Math.toRadians(90), 300, 300, 3, "firstship", null, true, 0);
		BoundingRectangle bulletBox = new BoundingRectangle(
				bullet.getX()-10.5, bullet.getY()-10.5, 21, 21);
		Bullet bullet2 = new Bullet(Math.toRadians(90)+Math.PI, 100, 100, 3, "secondship", null, true, 1);
		BoundingRectangle bulletBox2 = new BoundingRectangle(
				bullet2.getX()-10.5, bullet2.getY()-10.5, 21, 21);

		assertFalse(SUFFIX+" Bullets do  collide while they should not", doBoxesIntersect(bulletBox, bulletBox2));
	}
}
