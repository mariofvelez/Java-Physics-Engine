package test.tests;

import java.awt.Graphics2D;
import java.util.Random;

import geometry.Polygon2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import test.DebugInfo;
import test.Field;
import test.Test;

public class PolygonTest extends Test {
	
	Random r;
	
	public PolygonTest(Field field, DebugInfo info)
	{
		super(field, info);
		
		world.setGravity(new Vec2d(0, -9.80665f));
		world.iters = 128;
		
		r = new Random();
		
		transform.data[2] = field.getWidth() / 2.0f;
		transform.data[5] = field.getHeight() / 2.0f;
		transform.data[0] = 30;
		transform.data[4] = -30;
		
		float speed = -4;
		float len = 2.0f;
		float paddle_height = 2.0f;
		
		createPaddle(new Vec2d(-len * 1.5f, -len * 2 + paddle_height), -speed, len);
		createPaddle(new Vec2d(len * 1.5f, -len * 2 + paddle_height), speed, len);
		createPaddle(new Vec2d(-len * 2.0f, len / 2.0f + paddle_height), speed, len);
		createPaddle(new Vec2d(len * 2.0f, len / 2.0f + paddle_height), -speed, len);
		
		createGround();
	}
	public void step()
	{
		world.forEachBody((body) -> {
			if(body.getPositionUnmodifiable().y < -20)
				world.removeBody(body);
		});
		
		if(world.getBodySize() < 50)
		{
			createRandomPolygon();
		}
		
		world.step();
	}
	public void draw(Graphics2D g2)
	{
		super.debugDraw(g2);
	}
	public void createRandomPolygon()
	{
		Body body = new Body(new Vec2d(r.nextFloat() * 10.0f - 5.0f, 8.0f), CollisionType.DYNAMIC);
		
		Vec2d[] verts = new Vec2d[(int) (r.nextFloat() * 6) + 3];
		for(int x = 0; x < verts.length; ++x)
		{
			verts[x] = Vec2d.fromPolar(((float) x / verts.length) * Math.PI * 2 + Math.PI/4, r.nextFloat() * 0.2f + 0.6f);
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
	public void createGround()
	{
		Body ground = new Body(new Vec2d(0.0f, -5.0f), CollisionType.STATIC);
		Polygon2d pground = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(10.0f, 0.5f));
		
		ground.setShape(pground);
		
		setGroundParameters(ground);
		world.addBody(ground);
//		Body b = new Body(new Vec2d(size.width / 2, size.height - 60), CollisionType.STATIC);
//		Body br = new Body(new Vec2d(size.width - size.width / 4 + 20, size.height - 160), CollisionType.STATIC);
//		Body bl = new Body(new Vec2d(size.width / 4 - 40, size.height - 160), CollisionType.STATIC);
////		Body t = new Body(new Vec2d(size.width / 2, size.height / 2), CollisionType.STATIC);
//		Body l = new Body(new Vec2d(20, size.height / 2), CollisionType.STATIC);
//		Body r = new Body(new Vec2d(size.width - 20, size.height / 2), CollisionType.STATIC);
//		
////		br.group_filter = 2;
////		bl.group_filter = 2;
////		l.group_filter = 2;
////		r.group_filter = 2;
//		
//		Polygon2d pbr = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(size.width / 4 - 40, 20));
//		Polygon2d pbl = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(size.width / 4 - 40, 20));
//		Polygon2d pl = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(20, size.height / 2));
//		Polygon2d pr = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(20, size.height / 2));
//		
//		br.setRotation(-0.2f);
//		bl.setRotation(0.2f);
////		b.setShape(pb);
//		br.setShape(pbr);
//		bl.setShape(pbl);
////		t.setShape(pt);
//		l.setShape(pl);
//		r.setShape(pr);
//		
////		setGroundParameters(b);
//		setGroundParameters(br);
//		setGroundParameters(bl);
////		setGroundParameters(t);
//		setGroundParameters(l);
//		setGroundParameters(r);
//		
//		
////		world.addBody(b);
//		world.addBody(br);
//		world.addBody(bl);
////		world.addBody(t);
//		world.addBody(l);
//		world.addBody(r);
		
//		t.setRotationSpeed(2f);
		
//		for(int i = 0; i < 38; ++i)
//		{
//			Body body = new Body(new Vec2d(size.width/2, size.height / 3 - i * 50), CollisionType.DYNAMIC);
//			body.restitution = 0.3f;
//			body.friction = 0.5f;
//			
//			Circle shape = new Circle(new Vec2d(), 20);
//			body.setShape(shape);
//			
//			world.addBody(body);
//		}
	}
	private void setGroundParameters(Body b)
	{
		b.restitution = 0.5f;
		b.friction = 0.5f;
	}
	private void createPaddle(Vec2d pos, float speed, float len)
	{
		Body a = new Body(pos, CollisionType.STATIC);
		Body b = new Body(pos, CollisionType.STATIC);
		
//		a.group_filter = 2;
//		b.group_filter = 2;
		
		Polygon2d pa = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(len, len / 10.0f));
		Polygon2d pb = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(len / 10.0f, len));
		
		a.setShape(pa);
		b.setShape(pb);
		
		setGroundParameters(a);
		setGroundParameters(b);
		
		world.addBody(a);
		world.addBody(b);
		
		a.setRotationSpeed(speed);
		b.setRotationSpeed(speed);
	}

}
