package test.tests;

import java.awt.Graphics2D;

import geometry.Polygon2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import test.DebugInfo;
import test.Field;
import test.Test;

public class PyramidTest extends Test {
	
	public PyramidTest(Field field, DebugInfo info)
	{
		super(field, info);
		
		world.setGravity(new Vec2d(0, -9.80665f));
		world.iters = 128;
		
		transform.data[2] = field.getWidth() / 2.0f;
		transform.data[5] = field.getHeight() / 2.0f;
		transform.data[0] = 30;
		transform.data[4] = -30;
		
		int num_layers = 8;
		float spacing = 1.0f;
		for(int layer = 0; layer < num_layers; ++layer)
		{
			for(int i = 0; i < layer + 1; ++i)
			{
				float x = i * spacing - layer * spacing * 0.5f;
				float y = num_layers * spacing - layer * spacing - 4.5f;
				addBlock(new Vec2d(x, y));
			}
		}
		
		Body ground = new Body(new Vec2d(0.0f, -5.0f), CollisionType.STATIC);
		Polygon2d pground = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(10.0f, 0.5f));
		
		ground.setShape(pground);
		
		ground.restitution = 0.0f;
		ground.friction = 1.0f;
		
		world.addBody(ground);
	}
	
	public void step()
	{
		world.step();
	}
	
	private void addBlock(Vec2d pos)
	{
		Body body = new Body(pos, CollisionType.DYNAMIC);
		Polygon2d p = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(0.5f, 0.5f));
		
		body.setShape(p);
		
		body.friction = 0.9f;
		body.restitution = 0.1f;
		
		world.addBody(body);
	}
	
	public void draw(Graphics2D g2)
	{
		super.debugDraw(g2);
	}

}
