package test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import debug.DebugDrawInfo;
import geometry.Circle;
import geometry.Shape2d;
import math.Transform;
import math.Vec2d;
import physics.CollisionListener;
import physics.World;
import physics.body.CollisionType;

public abstract class Test {
	
	protected Field field;
	
	protected World world;
	protected CollisionListener listener;
	
	protected Transform transform;
	
	protected DebugInfo info;
	
	public Test(Field field)
	{
		this.field = field;
		
		transform = new Transform(3, true);
		
		info = new DebugInfo();
	}
	
	public abstract void step();
	
	public abstract void draw(Graphics2D g2);
	
	public void debugDraw(Graphics2D g2)
	{
		Color stat = new Color(82, 82, 82);
		
		DebugDrawInfo info = new DebugDrawInfo();
		world.forEachBody((body) -> {
			
			if(body.getCollisionType() == CollisionType.DYNAMIC)
			{
				Random r = new Random((int) (body.mass * 200));
				info.col = new Color(r.nextFloat() * 0.5f + 0.5f, r.nextFloat() * 0.5f + 0.5f, r.nextFloat() * 0.5f + 0.5f);
			}
			else
				info.col = stat;
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
	}

}
