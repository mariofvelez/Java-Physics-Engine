package test.tests;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Random;

import geometry.Circle;
import geometry.Polygon2d;
import math.Vec2d;
import physics.CollisionInfo;
import physics.CollisionListener;
import physics.body.Body;
import physics.body.CollisionType;
import test.Field;
import test.Test;

public class PolygonTest extends Test {
	
	Random r;
	
	public PolygonTest(Field field)
	{
		super(field);
		
		world.setGravity(new Vec2d(0, 550.0f));
		world.iters = 32;
		
		r = new Random();
		
		Vec2d size = new Vec2d(field.getSize().width, field.getSize().height);
		
		createGround(field.getSize());
		
		float speed = -7;
		float len = size.x / 18.0f;
		Vec2d center = Vec2d.mult(size, 0.5f);
		
		createPaddle(Vec2d.add(center, new Vec2d(-len * 1.5f, -len * 2)), -speed, len);
		createPaddle(Vec2d.add(center, new Vec2d(len * 1.5f, -len * 2)), speed, len);
		createPaddle(Vec2d.add(center, new Vec2d(-len * 2.0f, len / 2.0f)), speed, len);
		createPaddle(Vec2d.add(center, new Vec2d(len * 2.0f, len / 2.0f)), -speed, len);
		
		info.curr_world = world;
		info.show_poc = true;
		info.show_centroid = true;
	}
	public void step()
	{
		info.restart();
		world.forEachBody((body) -> {
			if(body.getPositionUnmodifiable().y > 1000)
				world.removeBody(body);
		});
		
		if(world.getBodySize() < 30)
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
		Body body = new Body(new Vec2d(r.nextFloat() * 400 + field.getSize().width/2 - 200, r.nextFloat() * 100), CollisionType.DYNAMIC);
		
		Vec2d[] verts = new Vec2d[(int) (r.nextFloat() * 6) + 3];
		for(int x = 0; x < verts.length; ++x)
		{
			verts[x] = Vec2d.fromPolar(((float) x / verts.length) * Math.PI * 2 + Math.PI/4, r.nextFloat() * 10 + 20);
		}
		Polygon2d p = new Polygon2d(verts);
		body.setShape(p);
		body.vel.set(r.nextFloat() * 400 - 200, r.nextFloat() * 200);
		world.addBody(body);
//		body.setRotation(r.nextFloat() * (float) Math.PI * 2);
		body.restitution = 0.3f;
		body.friction = 0.5f;
//		body.group_filter = 1;
//		body.collide_filter = 2;
	}
	public void createGround(Dimension size)
	{
//		Body b = new Body(new Vec2d(size.width / 2, size.height - 60), CollisionType.STATIC);
		Body br = new Body(new Vec2d(size.width - size.width / 4 + 20, size.height - 160), CollisionType.STATIC);
		Body bl = new Body(new Vec2d(size.width / 4 - 40, size.height - 160), CollisionType.STATIC);
//		Body t = new Body(new Vec2d(size.width / 2, size.height / 2), CollisionType.STATIC);
		Body l = new Body(new Vec2d(20, size.height / 2), CollisionType.STATIC);
		Body r = new Body(new Vec2d(size.width - 20, size.height / 2), CollisionType.STATIC);
		
//		br.group_filter = 2;
//		bl.group_filter = 2;
//		l.group_filter = 2;
//		r.group_filter = 2;
		
		Polygon2d pbr = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(size.width / 4 - 40, 20));
		Polygon2d pbl = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(size.width / 4 - 40, 20));
		Polygon2d pl = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(20, size.height / 2));
		Polygon2d pr = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(20, size.height / 2));
		
		br.setRotation(-0.2f);
		bl.setRotation(0.2f);
//		b.setShape(pb);
		br.setShape(pbr);
		bl.setShape(pbl);
//		t.setShape(pt);
		l.setShape(pl);
		r.setShape(pr);
		
//		setGroundParameters(b);
		setGroundParameters(br);
		setGroundParameters(bl);
//		setGroundParameters(t);
		setGroundParameters(l);
		setGroundParameters(r);
		
		
//		world.addBody(b);
		world.addBody(br);
		world.addBody(bl);
//		world.addBody(t);
		world.addBody(l);
		world.addBody(r);
		
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
		b.restitution = 0.9f;
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
