package physics;

import java.util.ArrayList;
import java.util.function.Consumer;

import data_structures.BinaryTree;
import geometry.Geometry;
import geometry.Shape2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import physics.constraint.Constraint;

/**
 * Contains all the data for the physics simulation, and is where all the simulation happens
 * @author Mario Velez
 *
 */
public class World {
	
	/**
	 * The amount of time to step (in seconds) each time the step function is called
	 */
	public float dt = 1f/60;
	/**
	 * How many movement iterations for each time step
	 */
	public int iters = 8;
	/**
	 * Acceleration due to gravity, acted on each dynamic object
	 */
	public Vec2d gravity = new Vec2d(0, 0);
	
	public ArrayList<Body> d_bodies;
	public ArrayList<Body> s_bodies;
	
	public ArrayList<Constraint> constraints;
	/**
	 * An interface for collisions
	 */
	private CollisionListener listener;
	private CollisionInfo collision_info;
	
	private ArrayList<Body> remove_list;
	private ArrayList<Constraint> constraint_remove_list;
	
	private AABBTree aabb_tree;
	
	public World()
	{
		d_bodies = new ArrayList<>();
		s_bodies = new ArrayList<>();
		
		constraints = new ArrayList<>();
		
		collision_info = new CollisionInfo();
		
		remove_list = new ArrayList<>();
		constraint_remove_list = new ArrayList<>();
		
		aabb_tree = new AABBTree();
	}
	/**
	 * 
	 * @returns the total amount of bodies in this world (dynamic + static)
	 */
	public int getBodySize()
	{
		return d_bodies.size() + s_bodies.size();
	}
	public int getDynamicBodySize()
	{
		return d_bodies.size();
	}
	public int getStaticBodySize()
	{
		return s_bodies.size();
	}
	/**
	 * Sets the collision listener. The collision listener is an interface for checking when collisions happen.
	 * @param listener - the listener to attach to this world
	 */
	public void setCollisionListener(CollisionListener listener)
	{
		this.listener = listener;
	}
	/**
	 * Updates the gravity of this world
	 * @param gravity - the acceleration vector due to gravity
	 */
	public void setGravity(Vec2d gravity)
	{
		this.gravity.x = gravity.x * dt;
		this.gravity.y = gravity.y * dt;
	}
	/**
	 * Updates the gravity of this world
	 * @param gx - the x component of acceleration due to gravity
	 * @param gy - the y component of the acceleration due to gravity
	 */
	public void setGravity(float gx, float gy)
	{
		gravity.x = gx * dt;
		gravity.y = gy * dt;
	}
	/**
	 * Adds a body to this world
	 * @param body - the body to add
	 */
	public void addBody(Body body)
	{
		body.world = this;
		
		if(body.getCollisionType() == CollisionType.DYNAMIC)
			d_bodies.add(body);
		else if(body.getCollisionType() == CollisionType.STATIC)
			s_bodies.add(body);
	}
	/**
	 * Adds a constraint to this world
	 * @param constraint - the constraint to add
	 */
	public void addConstraint(Constraint constraint)
	{
		constraint.setWorld(this);
		
		constraints.add(constraint);
	}
	/**
	 * Removes a body from the world, does not actually remove it until the next step() function is called
	 * @param body - the body to remove
	 */
	public void removeBody(Body body)
	{
		remove_list.add(body);
	}
	/**
	 * Removes a constraint from the world, does not actually remove it until the next step() function is called
	 * @param constraint - the constraint to remove
	 */
	public void removeConstraint(Constraint constraint)
	{
		constraint_remove_list.add(constraint);
	}
	/**
	 * Steps the simulation forward in time by dt
	 */
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
		for(int i = 0; i < constraint_remove_list.size(); ++i)
		{
			constraints.remove(constraint_remove_list.get(i));
		}
		remove_list.clear();
		constraint_remove_list.clear();
		
		for(int x = 0; x < iters; ++x)
		{
			for(int i = 0; i < constraints.size(); ++i)
			{
				constraints.get(i).solve(dt / iters);
			}
			
			aabb_tree = new AABBTree();
			
			for(int i = 0; i < s_bodies.size(); ++i)
			{
				Body body = s_bodies.get(i);
				body.move(dt * (1f / iters));
				body.rotate();
				body.updateProject();
				
				body.updateAABB();
				
				// insert into aabb tree
				aabb_tree.addAABB(body.aabb, body);
			}
			
			for(int i = 0; i < d_bodies.size(); ++i)
			{
				Body body = d_bodies.get(i);
				body.applyGravity(Vec2d.mult(gravity, (1f / iters)));
				body.move(dt * (1f / iters));
				body.rotate();
				body.updateProject();
				
				body.updateAABB();
				
				// insert into aabb tree
				aabb_tree.addAABB(body.aabb, body);
			}
			
			for(int i = 0; i < d_bodies.size(); ++i)
			{
				//dynamic
				Body body = d_bodies.get(i);
				ArrayList<Body> others = aabb_tree.getCollideList(body.aabb);
				for(int j = 0; j < others.size(); ++j)
				{
					Body other = d_bodies.get(j);
					if(body == other)
						continue;
					if(other.getCollisionType() == CollisionType.STATIC)
						continue;
				
					boolean collide = (body.collide_filter & other.group_filter) != 0 &&
									  (body.group_filter & other.collide_filter) != 0;
					if(!collide)
						continue;
					
					Vec2d poi = new Vec2d(0, 0);
					
					//if(!body.aabb.intersects(other.aabb))
					//	continue;
					
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
					
					boolean collide = (body.collide_filter & other.group_filter) != 0 &&
							  (body.group_filter & other.collide_filter) != 0;
					if(!collide)
						continue;
					
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
	/**
	 * Runs a function for each shape in this world that is connected to a body
	 * @param f - the function to perform for each shape
	 */
	public void forEachShape(Consumer<Shape2d> f)
	{
		for(int i = 0; i < d_bodies.size(); ++i)
			f.accept(d_bodies.get(i).getWorldShape());
		for(int i = 0; i < s_bodies.size(); ++i)
			f.accept(s_bodies.get(i).getWorldShape());
	}
	/**
	 * Runs a function for each body in this world
	 * @param f - the function to perform for each body
	 */
	public void forEachBody(Consumer<Body> f)
	{
		for(int i = 0; i < d_bodies.size(); ++i)
			f.accept(d_bodies.get(i));
		for(int i = 0; i < s_bodies.size(); ++i)
			f.accept(s_bodies.get(i));
	}
	/**
	 * Runs a function for each constraint in this world
	 * @param f - the function to perform for each constraint
	 */
	public void forEachConstraint(Consumer<Constraint> f)
	{
		for(int i = 0; i < constraints.size(); ++i)
			f.accept(constraints.get(i));
	}
	/**
	 * Gets the acceleration structure of this world
	 * @returns the AABB BVH tree
	 */
	public AABBTree getTree()
	{
		return aabb_tree;
	}
	private Vec2d ac = new Vec2d();
	private Vec2d bc = new Vec2d();
	private Vec2d rap = new Vec2d();
	private Vec2d rbp = new Vec2d();
	private Vec2d vap1 = new Vec2d();
	private Vec2d vbp1 = new Vec2d();
	private Vec2d vab1 = new Vec2d();
	private Vec2d jn = new Vec2d();
	private Vec2d ia = new Vec2d();
	private Vec2d ib = new Vec2d();
	
	private float j;
	private float j2;
	
	private Vec2d n = new Vec2d();
	private Vec2d lat = new Vec2d();
	private float ma;
	private float mb;
	private float wa1;
	private float wb1;
	
	private void solveCollision(CollisionInfo info)
	{
		ma = info.a.mass;
		mb = info.b.mass;
		
		Vec2d p = info.poc;
		ac.set(info.a.centroid);
		info.a.localToWorld(ac); //TODO - store world coords of these in body
		bc.set(info.b.centroid);
		info.b.localToWorld(bc);
		rap.set(p.x - ac.x, p.y - ac.y);
		rbp.set(p.x - bc.x, p.y - bc.y);
		
		wa1 = info.a.getrotationSpeed();
		wb1 = info.b.getrotationSpeed();
		
		vap1.set(info.a.vel.x + (-wa1 * rap.y), info.a.vel.y + (wa1 * rap.x));
		vbp1.set(info.b.vel.x + (-wb1 * rbp.y), info.b.vel.y + (wb1 * rbp.x));
		
		n = info.normal; // collision normal
		lat = n.rightNormal(); // collision tangent
		
		//must be negative
		float na = Vec2d.dotProduct(n, vap1);
		//must be positive
		float nb = Vec2d.dotProduct(n, vbp1);
		
		//moving away from each other
		if(na > nb)
			return;
		
		float e = Math.min(info.a.restitution, info.b.restitution); // coefficient of restitution
		
		float u = Math.min(info.a.friction, info.b.friction); // coefficient of friction
		
		vab1.set(vap1.x - vbp1.x, vap1.y - vbp1.y);
		
		//impulse restitution
		jn.set(vab1.x * -(1+e), vab1.y * -(1+e));
		j = Vec2d.dotProduct(jn, n);//Vec2d.mult(vab1, -(1 + e)), n);
		
		Vec2d jf = new Vec2d(vab1.x * -(u), vab1.y * -(u));
		j2 = Vec2d.dotProduct(jf, lat);
	}
	private void updateBodyA(CollisionInfo info)
	{
		ia.set(n.x * j/ma, n.y * j/ma);
		info.a.vel.add(ia);//Vec2d.mult(n, j / ma));
		
		ia.set(lat.x * j2/ma, lat.y * j2/ma);
		info.a.vel.add(ia);
//			info.a.vel.subtract(Vec2d.mult(lat, jf / ma));
		info.a.setRotationSpeed(wa1 + (Vec2d.crossProduct(rap, Vec2d.mult(n, j)) + Vec2d.crossProduct(rap, Vec2d.mult(lat, j2)))/info.a.I);
	}
	private void updateBodyB(CollisionInfo info)
	{
		ib.set(n.x * j/mb, n.y * j/mb);
		info.b.vel.subtract(ib);//Vec2d.mult(n, j / mb));
		
		ib.set(lat.x * j2/mb, lat.y * j2/mb);
		info.b.vel.subtract(ib);
//			info.b.vel.add(Vec2d.mult(lat, jf / mb));
		info.b.setRotationSpeed(wb1 - (Vec2d.crossProduct(rbp, Vec2d.mult(n, j)) + Vec2d.crossProduct(rbp, Vec2d.mult(lat, j2)))/info.b.I);
	}
	private void solveDSCollision(CollisionInfo info)
	{
		solveCollision(info);
		
		float temp = Vec2d.crossProduct(rap, n);
		
		j /= (1 / ma) + (temp*temp / info.a.I);
		
		//impulse friction
		float temp2 = Vec2d.crossProduct(rap, lat);
		
		j2 /= (1 / ma) + (temp2*temp2 / info.a.I);
		
		//final velocity
		updateBodyA(info);
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
		
		solveCollision(info);
		
		float temp = Vec2d.crossProduct(rap, n);
		float temp2 = Vec2d.crossProduct(rbp, n);
		
		j /= (1 / ma) + (temp*temp / info.a.I) + (1 / mb) + (temp2*temp2 / info.b.I);
		
		//impulse friction
		float temp3 = Vec2d.crossProduct(rap, lat);
		float temp4 = Vec2d.crossProduct(rbp, lat);
		
		j2 /= (1 / ma) + (temp3*temp3 / info.a.I) + (1 / mb) + (temp4*temp4 / info.b.I);
		
		//final velocity
		updateBodyA(info);
		updateBodyB(info);
	}
}
