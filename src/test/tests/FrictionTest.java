package test.tests;

import java.awt.Graphics2D;

import geometry.Circle;
import geometry.Polygon2d;
import math.MathConstant;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import test.DebugInfo;
import test.Field;
import test.Test;

public class FrictionTest extends Test {
	
	private float r = 0.5f;
	private float r2 = 8.0f;

	public FrictionTest(Field field, DebugInfo info)
	{
		super(field, info);
		
		transform.data[2] = field.getWidth() / 2.0f;
		transform.data[5] = field.getHeight() / 2.0f;
		transform.data[0] = 30;
		transform.data[4] = -30;
		
		world.setGravity(new Vec2d(0.0f, -9.80665f));
		world.iters = 128;
		
		createPlatform(new Vec2d(0, 3), -0.1f, r2);
		
		for(int i = 0; i < 6; ++i)
		{
			createCube(new Vec2d(3 - i * 1.5f, 4), i * 0.05f);
		}
		
		createPlatform(new Vec2d(0, -1), -0.1f, r2);
		
		for(int i = 0; i < 6; ++i)
		{
			createCircle(new Vec2d(3 - i * 1.5f, 0), i * 0.2f);
		}
		
		createPlatform(new Vec2d(5, -5), 0.2f, r2);
		
		createPlatform(new Vec2d(-3, -5.5f), MathConstant.HALF_PI, 1);
		createPlatform(new Vec2d(13, -2.5f), MathConstant.HALF_PI, 1);
	}
	private void createCube(Vec2d pos, float friction)
	{
		Body body = new Body(pos, CollisionType.DYNAMIC);
		body.restitution = 0.5f;
		body.friction = friction;
		
		Polygon2d p = Polygon2d.createAsBox(new Vec2d(), new Vec2d(r, r));
		body.setShape(p);
		
		world.addBody(body);
	}
	private void createCircle(Vec2d pos, float friction)
	{
		Body body = new Body(pos, CollisionType.DYNAMIC);
		body.restitution = 0.5f;
		body.friction = friction;
		
		Circle c = new Circle(new Vec2d(), r);
		body.setShape(c);
		
		world.addBody(body);
	}
	private void createPlatform(Vec2d pos, float angle, float len)
	{
		Body body = new Body(pos, CollisionType.STATIC);
		body.restitution = 0.5f;
		body.friction = 1.0f;
		
		Polygon2d p = Polygon2d.createAsBox(new Vec2d(), new Vec2d(len, 0.1f));
		body.setShape(p);
		
		body.setRotation(angle);
		
		world.addBody(body);
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
