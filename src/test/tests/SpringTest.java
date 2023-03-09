package test.tests;

import java.awt.Graphics2D;

import geometry.Circle;
import geometry.Polygon2d;
import math.Vec2d;
import physics.World;
import physics.body.Body;
import physics.body.CollisionType;
import test.Field;
import test.Test;

public class SpringTest extends Test {

	public SpringTest(Field field)
	{
		super(field);
		
		transform.data[2] = 450;
		transform.data[5] = 300;
		transform.data[0] = 30;
		transform.data[4] = -30;
		
		world = new World();
		world.setGravity(new Vec2d(0.0f, 9.80665f));
		world.iters = 32;
		
		float l = 0.3f;
		Body b1 = new Body(new Vec2d(-5.0f, 3.0f), CollisionType.STATIC);
		Polygon2d p1 = Polygon2d.createAsBox(new Vec2d(), new Vec2d(l, l));
		b1.setShape(p1);
		
		Body b2 = new Body(new Vec2d(0.0f, 3.0f), CollisionType.STATIC);
		Polygon2d p2 = Polygon2d.createAsBox(new Vec2d(), new Vec2d(l, l));
		b2.setShape(p2);
		
		Body b3 = new Body(new Vec2d(5.0f, 3.0f), CollisionType.STATIC);
		Polygon2d p3 = Polygon2d.createAsBox(new Vec2d(), new Vec2d(l, l));
		b3.setShape(p3);
		
		world.addBody(b1);
		world.addBody(b2);
		world.addBody(b3);
	}
	
	public void step()
	{
		world.step();
	}
	
	public void draw(Graphics2D g2)
	{
		super.debugDraw(g2);
	}

}
