package physics;

import java.util.ArrayList;
import java.util.function.Consumer;

import geometry.Geometry;
import geometry.Shape2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;

public class World {
	
	public float dt = 1f/60;
	public int iters = 8;
	
	public Vec2d gravity = new Vec2d(0, 0);
	
	public ArrayList<Body> d_bodies;
	public ArrayList<Body> s_bodies;
	
	private CollisionListener listener;
	private CollisionInfo collision_info;
	
	private ArrayList<Body> remove_list;
	
	public World()
	{
		d_bodies = new ArrayList<>();
		s_bodies = new ArrayList<>();
		collision_info = new CollisionInfo();
		
		remove_list = new ArrayList<>();
	}
	public int getBodySize()
	{
		return d_bodies.size() + s_bodies.size();
	}
	public void setCollisionListener(CollisionListener listener)
	{
		this.listener = listener;
	}
	public void setGravity(Vec2d gravity)
	{
		this.gravity.x = gravity.x * dt;
		this.gravity.y = gravity.y * dt;
	}
	public void setGravity(float gx, float gy)
	{
		gravity.x = gx * dt;
		gravity.y = gy * dt;
	}
	public void addBody(Body body)
	{
		body.world = this;
		
		if(body.getCollisionType() == CollisionType.DYNAMIC)
			d_bodies.add(body);
		else if(body.getCollisionType() == CollisionType.STATIC)
			s_bodies.add(body);
	}
	/**
	 * Removes a body from the world, does not actually remove it until the next step() is called
	 * @param body - the body to remove
	 */
	public void removeBody(Body body)
	{
		remove_list.add(body);
	}
	public void step()
	{
		for(int i = 0; i < remove_list.size(); ++i)
		{
			Body body = remove_list.get(i);
			if(body.getCollisionType() == CollisionType.DYNAMIC)
				d_bodies.remove(remove_list.get(i));
			else if(body.getCollisionType() == CollisionType.STATIC)
				s_bodies.remove(remove_list.get(i));
		}
		remove_list.clear();
		
		for(int x = 0; x < iters; ++x)
		{
			for(int i = 0; i < s_bodies.size(); ++i)
			{
				Body body = s_bodies.get(i);
				body.move(dt * (1f / iters));
				body.rotate();
				body.updateProject();
				
				body.updateAABB();
			}
			
			for(int i = 0; i < d_bodies.size(); ++i)
			{
				Body body = d_bodies.get(i);
				body.applyGravity(Vec2d.mult(gravity, (1f / iters)));
				body.move(dt * (1f / iters));
				body.rotate();
				body.updateProject();
				
				body.updateAABB();
			}
			
			for(int i = 0; i < d_bodies.size(); ++i)
			{
				//dynamic
				Body body = d_bodies.get(i);
				for(int j = 0; j < i; ++j)
				{
					Body other = d_bodies.get(j);
					
					Vec2d poi = new Vec2d(0, 0);
					
					if(!body.aabb.intersects(other.aabb))
						continue;
					
					Vec2d move = Geometry.intersectionNormal(body.shape_proj, other.shape_proj, poi);
					
					if(!move.equals(Vec2d.ZERO))
					{
						collision_info.poc = poi;
						collision_info.a = body;
						collision_info.b = other;
						
						if(listener != null)
							listener.beforeSolve(collision_info);
						//resolve overlap
						move.mult(0.5f);
						other.move(move);
						move.negate();
						body.move(move);
						
						body.updateAABB();
						other.updateAABB();
						
						move.normalize();
						collision_info.normal = move;
						
						solveDDCollision(collision_info);
						
						
						if(listener != null)
							listener.afterSolve(collision_info);
					}
					
				}
				//static
				for(int j = 0; j < s_bodies.size(); ++j)
				{
					Body other = s_bodies.get(j);
					
					Vec2d poi = new Vec2d(0, 0);
					
					
					if(!body.aabb.intersects(other.aabb))
						continue;
					
					Vec2d move = Geometry.intersectionNormal(other.shape_proj, body.shape_proj, poi);
					
					if(!move.equals(Vec2d.ZERO))
					{
						collision_info.poc = poi;
						collision_info.a = body;
						collision_info.b = other;
						
						if(listener != null)
							listener.beforeSolve(collision_info);
						//resolve overlap
						body.move(move);
						
						body.updateAABB();
						
						move.normalize();
						collision_info.normal = move;
						
						solveDSCollision(collision_info);
						
						if(listener != null)
							listener.afterSolve(collision_info);
					}
					
				}
			}
		}
	}
	public void forEachShape(Consumer<Shape2d> f)
	{
		for(int i = 0; i < d_bodies.size(); ++i)
			f.accept(d_bodies.get(i).getWorldShape());
		for(int i = 0; i < s_bodies.size(); ++i)
			f.accept(s_bodies.get(i).getWorldShape());
	}
	public void forEachBody(Consumer<Body> f)
	{
		for(int i = 0; i < d_bodies.size(); ++i)
			f.accept(d_bodies.get(i));
		for(int i = 0; i < s_bodies.size(); ++i)
			f.accept(s_bodies.get(i));
	}
	Vec2d ac = new Vec2d();
	Vec2d bc = new Vec2d();
	Vec2d rap = new Vec2d();
	Vec2d rbp = new Vec2d();
	Vec2d vap1 = new Vec2d();
	Vec2d vbp1 = new Vec2d();
	Vec2d na = new Vec2d();
	Vec2d nb = new Vec2d();
	Vec2d vab1 = new Vec2d();
	Vec2d jn = new Vec2d();
	Vec2d ia = new Vec2d();
	Vec2d ja = new Vec2d();
	Vec2d ib = new Vec2d();
	Vec2d jb = new Vec2d();
	
	private void solveDSCollision(CollisionInfo info)
	{
		float ma = info.a.mass;
		float mb = info.b.mass;
		
		Vec2d p = info.poc;
		ac.set(info.a.centroid);
		info.a.localToWorld(ac); //TODO - store world coords of these in body
		bc.set(info.b.centroid);
		info.b.localToWorld(bc);
		rap.set(p.x - ac.x, p.y - ac.y);
		rbp.set(p.x - bc.x, p.y - bc.y);
		
		float wa1 = info.a.getrotationSpeed();
		float wb1 = info.b.getrotationSpeed();
		
		vap1.set(info.a.vel.x + (-wa1 * rap.y), info.a.vel.y + (wa1 * rap.x));
		vbp1.set(info.b.vel.x + (-wb1 * rbp.y), info.b.vel.y + (wb1 * rbp.x));
		
		Vec2d n = info.normal;
//		Vec2d lat = n.rightNormal();
		
		//must be negative
		float na = Vec2d.dotProduct(n, vap1);
		//must be positive
		float nb = Vec2d.dotProduct(n, vbp1);
		
		//moving away from each other
		if(na > nb)
			return;
		
		float e = Math.min(info.a.restitution, info.b.restitution);
		float f = Math.min(info.a.friction, info.b.friction);
		
		vab1.set(vap1.x - vbp1.x, vap1.y - vbp1.y);
//		Vec2d vab1 = Vec2d.subtract(vap1, vbp1);
		
		
		//impulse restitution
		jn.set(vab1.x * -(1+e), vab1.y * -(1+e));
		float j = Vec2d.dotProduct(jn, n);//Vec2d.mult(vab1, -(1 + e)), n);
		
		float temp = Vec2d.crossProduct(rap, n);
		float temp2 = Vec2d.crossProduct(rbp, n);
		
		j /= (1 / ma) + (temp*temp / info.a.I);
		
		//impulse friction
//		
//		float jf = Vec2d.dotProduct(Vec2d.mult(vab1, (1 - f)), lat);
//		
//		float temp3 = Vec2d.crossProduct(rap, lat);
//		float temp4 = Vec2d.crossProduct(rbp, lat);
//		
//		float divf = 0;
//		if(info.a.collision_type == CollisionType.DYNAMIC)
//			divf += (1 / ma) + (temp3*temp3 / info.a.I);
//		if(info.b.collision_type == CollisionType.DYNAMIC)
//			divf += (1 / mb) + (temp4*temp4 / info.b.I);
//		jf /= divf;
		
		//final velocity
		ia.set(n.x * j/ma, n.y * j/ma);
		info.a.vel.add(ia);//Vec2d.mult(n, j / ma));
//			info.a.vel.subtract(Vec2d.mult(lat, jf / ma));
		info.a.setRotationSpeed(wa1 + Vec2d.crossProduct(rap, Vec2d.mult(n, j))/info.a.I);
	}
	private void solveDDCollision(CollisionInfo info)
	{
//		info.normal.normalize();
//		Vec2d lat = info.normal.leftNormal();
//		
//		float m1m2 = info.a.mass + info.b.mass;
//		
//		float u1 = info.a.vel.dotProduct(info.normal);
//		float u2 = info.b.vel.dotProduct(info.normal);
//		float l1 = info.a.vel.dotProduct(lat);
//		float l2 = info.b.vel.dotProduct(lat);
//		
//		float v_1 = ((info.a.mass - info.b.mass) / m1m2)*u1 + ((info.b.mass*2) / m1m2)*u2;
//		float v_2 = ((info.a.mass*2) / m1m2)*u1 + ((info.b.mass - info.a.mass) / m1m2)*u2;
//		
//		info.a.vel.set(Vec2d.add(Vec2d.mult(info.normal, v_1), Vec2d.mult(lat, l1)));
//		info.b.vel.set(Vec2d.add(Vec2d.mult(info.normal, v_2), Vec2d.mult(lat, l2)));
		
		
		float ma = info.a.mass;
		float mb = info.b.mass;
		
		Vec2d p = info.poc;
		ac.set(info.a.centroid);
		info.a.localToWorld(ac); //TODO - store world coords of these in body
		bc.set(info.b.centroid);
		info.b.localToWorld(bc);
		rap.set(p.x - ac.x, p.y - ac.y);
		rbp.set(p.x - bc.x, p.y - bc.y);
		
		float wa1 = info.a.getrotationSpeed();
		float wb1 = info.b.getrotationSpeed();
		
		vap1.set(info.a.vel.x + (-wa1 * rap.y), info.a.vel.y + (wa1 * rap.x));
		vbp1.set(info.b.vel.x + (-wb1 * rbp.y), info.b.vel.y + (wb1 * rbp.x));
//		Vec2d vap1 = Vec2d.add(info.a.vel, new Vec2d(-wa1 * rap.y, wa1 * rap.x));
//		Vec2d vbp1 = Vec2d.add(info.b.vel, new Vec2d(-wb1 * rbp.y, wb1 * rbp.x));
		
//		if(Vec2d.dotProduct(vap1, vbp1) > 0f)
//			return;
		
//		float d1 = Vec2d.dist(rap, rbp);
//		Vec2d va = Vec2d.mult(vap1, 1f / 10000);
//		Vec2d vb = Vec2d.mult(vbp1, 1f / 10000);
//		Vec2d pa = Vec2d.add(rap, va);
//		Vec2d pb = Vec2d.add(rbp, vb);
//		float d2 = Vec2d.dist(pa, pb);
//		if(d1 - d2 > 0)
//			return;
		
		Vec2d n = info.normal;
//		Vec2d lat = n.rightNormal();
		
		//must be negative
		float na = Vec2d.dotProduct(n, vap1);
		//must be positive
		float nb = Vec2d.dotProduct(n, vbp1);
		
		//moving away from each other
		if(na > nb)
			return;
		
		float e = Math.min(info.a.restitution, info.b.restitution);
		float f = Math.min(info.a.friction, info.b.friction);
		
		vab1.set(vap1.x - vbp1.x, vap1.y - vbp1.y);
//		Vec2d vab1 = Vec2d.subtract(vap1, vbp1);
		
		
		//impulse restitution
		jn.set(vab1.x * -(1+e), vab1.y * -(1+e));
		float j = Vec2d.dotProduct(jn, n);//Vec2d.mult(vab1, -(1 + e)), n);
		
		float temp = Vec2d.crossProduct(rap, n);
		float temp2 = Vec2d.crossProduct(rbp, n);
		
		j /= (1 / ma) + (temp*temp / info.a.I) + (1 / mb) + (temp2*temp2 / info.b.I);
		
		//impulse friction
//		
//		float jf = Vec2d.dotProduct(Vec2d.mult(vab1, (1 - f)), lat);
//		
//		float temp3 = Vec2d.crossProduct(rap, lat);
//		float temp4 = Vec2d.crossProduct(rbp, lat);
//		
//		float divf = 0;
//		if(info.a.collision_type == CollisionType.DYNAMIC)
//			divf += (1 / ma) + (temp3*temp3 / info.a.I);
//		if(info.b.collision_type == CollisionType.DYNAMIC)
//			divf += (1 / mb) + (temp4*temp4 / info.b.I);
//		jf /= divf;
		
		//final velocity
		ia.set(n.x * j/ma, n.y * j/ma);
		info.a.vel.add(ia);//Vec2d.mult(n, j / ma));
//			info.a.vel.subtract(Vec2d.mult(lat, jf / ma));
		info.a.setRotationSpeed(wa1 + Vec2d.crossProduct(rap, Vec2d.mult(n, j))/info.a.I);		
		
		ib.set(n.x * j/mb, n.y * j/mb);
		info.b.vel.subtract(ib);//Vec2d.mult(n, j / mb));
//			info.b.vel.add(Vec2d.mult(lat, jf / mb));
		info.b.setRotationSpeed(wb1 - Vec2d.crossProduct(rbp, Vec2d.mult(n, j))/info.b.I);
		
//		System.out.println("here");
//		
//		Vec2d v1 = new Vec2d(info.poc);
//		Vec2d v2 = new Vec2d(info.poc);
//		
//		info.a.pointVelocityFromWorld(v1, dt);
//		info.b.pointVelocityFromWorld(v2, dt);
//		
//		v1.mult(info.b.mass * 0.001f);
//		v2.mult(info.a.mass * -0.001f);
//				
//		info.a.applyForceWorld(info.poc, v2);
//		info.b.applyForceWorld(info.poc, v1);
	}
//	private Vec2d cross(Vec2d a, Vec2d b)
//	{
//		a.y*b.z - a.z*b.y,
//		a.z*b.x - a.x*b.z,
//		a.x*b.y - a.y*b.x
//		return new Vec2d(-);
//	}
}
