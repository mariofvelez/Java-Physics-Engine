package test.tests;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import debug.DebugDrawInfo;
import geometry.Circle;
import geometry.Polygon2d;
import geometry.Shape2d;
import math.Vec2d;
import physics.AABB;
import physics.AABBTree;
import physics.body.Body;
import physics.body.CollisionType;
import test.DebugInfo;
import test.Field;
import test.Test;

public class CollisionTest extends Test {

	Random rand;
	
	AABB test_aabb;
	
	ArrayList<Body> collide_list;
	
	public CollisionTest(Field field, DebugInfo info)
	{
		super(field, info);
		
		world.setGravity(new Vec2d(0.0f, 0.0f));
		world.iters = 1;
		
		rand = new Random();
		
		transform.data[2] = field.getWidth() / 2.0f;
		transform.data[5] = field.getHeight() / 2.0f;
		transform.data[0] = 30;
		transform.data[4] = -30;
		
		test_aabb = new AABB(-0.5f, 0.5f, -0.5f, 0.5f);
		
		collide_list = new ArrayList<>();
	}
	public void createRandomPolygon(Vec2d pos)
	{
		Body body = new Body(pos, CollisionType.DYNAMIC);
		
		Vec2d[] verts = new Vec2d[(int) (rand.nextFloat() * 6) + 3];
		for(int x = 0; x < verts.length; ++x)
		{
			verts[x] = Vec2d.fromPolar(((float) x / verts.length) * Math.PI * 2 + Math.PI/4, rand.nextFloat() * 0.2f + 0.6f);
		}
		Polygon2d p = new Polygon2d(verts);
		body.setShape(p);
		//body.vel.set(r.nextFloat() * 400 - 200, r.nextFloat() * 200);
		world.addBody(body);
//		body.setRotation(r.nextFloat() * (float) Math.PI * 2);
		body.restitution = 0.0f;
		body.friction = 0.5f;
//		body.group_filter = 1;
//		body.collide_filter = 2;
	}
	public void onMouseDown(Vec2d mouse)
	{
		createRandomPolygon(mouse);
	}
	public void onMouseMove(Vec2d mouse)
	{
		super.onMouseMove(mouse);
		test_aabb = new AABB(-0.5f, 0.5f, -0.5f, 0.5f);
		test_aabb.move(mouse.x, mouse.y);
	}
	public void step()
	{
		world.step();
		
		AABBTree tree = world.getTree();
		collide_list = tree.getCollideList(test_aabb);
	}
	
	public void draw(Graphics2D g2)
	{
		Color stat = new Color(82, 82, 82);
		
		DebugDrawInfo info = new DebugDrawInfo();
		world.forEachBody((body) -> {
			
			if(body.getCollisionType() == CollisionType.DYNAMIC)
			{
				Random r = new Random((int) (body.mass * 200));
				info.fill_col = new Color(r.nextFloat() * 0.5f + 0.5f, r.nextFloat() * 0.5f + 0.5f, r.nextFloat() * 0.5f + 0.5f);
				if(collide_list.contains(body))
					info.fill_col = Color.GREEN;
				float u = body.friction;
				int col = 255 - (int) (u * 255);
				info.outline_col = new Color(0, col, col);
			}
			else
			{
				info.fill_col = stat;
				info.outline_col = Color.BLACK;
			}
			Shape2d shape = body.getWorldShape().createCopy();
			body.getWorldShape().projectTo(transform, shape);
			shape.debugDraw(g2, info);
			
			if(body.shape instanceof Circle)
			{
				Circle circle = (Circle) body.shape;
				Vec2d rad = new Vec2d(circle.radius, 0);
				body.localToWorld(rad);
				transform.project2D(rad);
				Vec2d c = new Vec2d(circle.pos);
				body.localToWorld(c);
				transform.project2D(c);
				g2.setColor(Color.BLACK);
				g2.drawLine((int) c.x, (int) c.y, (int) rad.x, (int) rad.y);
			}
		});
		world.forEachConstraint((constraint) -> {
			constraint.debugDraw(g2, transform);
		});
		this.info.draw(g2, transform);
		g2.setColor(Color.BLACK);
		
		test_aabb.debugDraw(g2, transform);
	}
	
	

}
