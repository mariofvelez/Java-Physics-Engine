package test.tests;

import java.awt.Graphics2D;

import geometry.Polygon2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import physics.constraint.SpringConstraint;
import test.DebugInfo;
import test.Field;
import test.Test;

public class SpringTest extends Test {

	public SpringTest(Field field, DebugInfo info)
	{
		super(field, info);
		
		transform.data[2] = 450;
		transform.data[5] = 300;
		transform.data[0] = 30;
		transform.data[4] = -30;
		
		world.setGravity(new Vec2d(0.0f, -9.80665f));
		world.iters = 16;
		
		float l = 0.3f;
		Body b1 = new Body(new Vec2d(0.0f, 5.0f), CollisionType.STATIC);
		b1.restitution = 0.5f;
		Polygon2d p1 = Polygon2d.createAsBox(new Vec2d(), new Vec2d(l, l));
		b1.setShape(p1);
		
		Body b2 = new Body(new Vec2d(5.0f, 5.0f), CollisionType.DYNAMIC);
		b2.restitution = 0.0f;
		Polygon2d p2 = Polygon2d.createAsBox(new Vec2d(), new Vec2d(l, l));
		b2.setShape(p2);
		
		Body b3 = new Body(new Vec2d(10.0f, 5.0f), CollisionType.DYNAMIC);
		b3.restitution= 0.5f;
		Polygon2d p3 = Polygon2d.createAsBox(new Vec2d(), new Vec2d(l * 3, l * 3));
		b3.setShape(p3);
		
		world.addBody(b1);
		world.addBody(b2);
		world.addBody(b3);
		
		SpringConstraint c1 = new SpringConstraint(1000f, 5.0f);
		c1.a = b1;
		c1.b = b2;
		c1.local_a = new Vec2d();
		c1.local_b = new Vec2d();
		
		SpringConstraint c2 = new SpringConstraint(1000f, 5.0f);
		c2.a = b2;
		c2.b = b3;
		c2.local_a = new Vec2d();
		c2.local_b = new Vec2d(/*0.5f, 0.0f*/);
		
		world.addConstraint(c1);
		world.addConstraint(c2);
		
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
