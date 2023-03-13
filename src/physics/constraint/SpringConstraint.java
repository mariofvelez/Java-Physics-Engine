package physics.constraint;

import java.awt.Graphics2D;

import math.Transform;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;

public class SpringConstraint extends Constraint {
	
	public float k;
	public float x;
	
	public Body a;
	public Body b;
	
	public Vec2d local_a;
	public Vec2d local_b;
	
//	private BiFunction<Float, Float, Float> dy = (t, y) -> {
//			return -k * (y - x);
//	};
	
//	private Integrator integrator;
	
	public SpringConstraint(float k, float x)
	{
		this.k = k;
		this.x = x;
//		integrator = new RK4Integrator();
	}
	
	public void solve(float dt)
	{
		Vec2d world_a = new Vec2d(local_a);
		a.localToWorld(world_a);
		
		
		Vec2d world_b = new Vec2d(local_b);
		b.localToWorld(world_b);
		
//		System.out.println(world_b);
		
		float len = Vec2d.dist(world_a, world_b);
		
		float diff = len - x;
		
		Vec2d dir = Vec2d.subtract(world_b, world_a);
		dir.normalize();
		dir.mult(k * diff / world.iters);
		
//		System.out.println(dir);
		if(a.getCollisionType() == CollisionType.DYNAMIC)
			a.applyForceWorld(world_a, dir);
		
		dir.negate();
		
		if(b.getCollisionType() == CollisionType.DYNAMIC)
			b.applyForceWorld(world_b, dir);
		
		
//		
//		float v = integrator.y(t, h, yn, f)
	}
	public void debugDraw(Graphics2D g2, Transform transform)
	{
		Vec2d v1 = new Vec2d(local_a);
		Vec2d v2 = new Vec2d(local_b);
		
		a.localToWorld(v1);
		b.localToWorld(v2);
		
		transform.project2D(v1);
		transform.project2D(v2);
		
		g2.drawLine((int) v1.x, (int) v1.y, (int) v2.x, (int) v2.y);
	}

}
