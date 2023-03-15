package test.tests;

import java.awt.Graphics2D;

import geometry.Circle;
import geometry.Polygon2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import test.Field;
import test.Test;

public class CircleStackTest extends Test {

	public CircleStackTest(Field field)
	{
		super(field);
		
		world.setGravity(0.0f, -9.81f);
		world.iters = 32;
		
		transform.data[2] = 450;
		transform.data[5] = 300;
		transform.data[0] = 30;
		transform.data[4] = -30;
		
		float ground_width = 16.0f;
		
		Body ground = new Body(new Vec2d(0, -5), CollisionType.STATIC);
		ground.restitution = 0.0f;
		ground.friction = 0.1f;
		ground.setRotation(-0.1f);
		
		Polygon2d p = Polygon2d.createAsBox(new Vec2d(), new Vec2d(ground_width/2f, 0.5f));
		ground.setShape(p);
		
		Body left_wall = new Body(new Vec2d(-ground_width/2f, -2f), CollisionType.STATIC);
		
		Polygon2d p2 = Polygon2d.createAsBox(new Vec2d(), new Vec2d(0.5f, 4.0f));
		left_wall.setShape(p2);
		
		Body right_wall = new Body(new Vec2d(ground_width/2f, -3f), CollisionType.STATIC);
		
		Polygon2d p3 = Polygon2d.createAsBox(new Vec2d(), new Vec2d(0.5f, 4.0f));
		right_wall.setShape(p3);
		
		world.addBody(ground);
		world.addBody(left_wall);
		world.addBody(right_wall);
		
		float r = 1.0f;
		float x_offs = 0.1f;
		for(int i = 0; i < 5; ++i)
		{
			createCircle(new Vec2d(x_offs, -3.5f + r + i*r*2), r);
			x_offs *= -1f;
		}
	}
	private void createCircle(Vec2d pos, float r)
	{
		Body b = new Body(pos, CollisionType.DYNAMIC);
		b.restitution = 0.0f;
		b.friction = 0.1f;
		
		Circle c = new Circle(new Vec2d(), r);
		b.setShape(c);
		
		world.addBody(b);
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
