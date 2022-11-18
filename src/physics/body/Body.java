package physics.body;

import geometry.Circle;
import geometry.Polygon2d;
import geometry.Shape2d;
import math.Transform;
import math.Vec2d;
import physics.AABB;
import physics.World;

public class Body {
	
	public World world;
	
	private CollisionType collision_type;
	public Vec2d vel;
	private float rot_speed = 0;
	Transform rot;
	public Transform transform;
	Transform inv_transform;
	public Shape2d shape;
	public Shape2d shape_proj;
	
	public AABB aabb;
	
	public Vec2d centroid = new Vec2d(0, 0);
	public float I;
	
	public float density = 1;
	public float friction = 0;
	public float restitution = 1;
	public float mass = 1;
	
	public float gravity_scale = 1;
	public float move_scale = 1;
	
	public Object user_data;
	
	public Body(Vec2d pos, CollisionType type)
	{
		collision_type = type;
		
		transform = new Transform(3, true);
		transform.data[2] = pos.x;
		transform.data[5] = pos.y;
		
		inv_transform = new Transform(3, true);
		transform.invert3x3(inv_transform);
		
		rot = new Transform(3, true);
		
		vel = new Vec2d(0, 0);
		
		aabb = new AABB(0, 0, 0, 0);
	}
	public CollisionType getCollisionType()
	{
		return collision_type;
	}
	public void worldToLocal(Vec2d world_point)
	{
		inv_transform.project2D(world_point);
	}
	public void localToWorld(Vec2d local_point)
	{
		transform.project2D(local_point);
	}
	public void setProperties(float density, float friction, float restitution)
	{
		this.density = density;
		this.friction = friction;
		this.restitution = restitution;
	}
	public void setCollisionType(CollisionType type)
	{
		collision_type = type;
	}
	public float calculateMomentum()
	{
		return shape.area * vel.length();
	}
	public void setShape(Shape2d shape)
	{
		this.shape = shape;
		if(shape.getClass() == Circle.class)
			shape_proj = new Circle(new Vec2d(0, 0), 0);
		else
			shape_proj = new Polygon2d(((Polygon2d) shape).vertices);
		shape.computeCentroid(centroid);
		I = shape.computeInertia(centroid, density);
		
		mass = density * shape.area;
		
//		mass = density * shape.area;
		shape.projectTo(transform, shape_proj);
		
	}
	public void updateAABB()
	{
		shape_proj.setAABB(aabb);
	}
	public Shape2d getWorldShape()
	{
		return shape_proj;
	}
	public void setPosition(Vec2d pos)
	{
		transform.data[2] = pos.x;
		transform.data[5] = pos.y;
		transform.invert3x3(inv_transform);
	}
	public void setPosition(float x, float y)
	{
		transform.data[2] = x;
		transform.data[5] = y;
		transform.invert3x3(inv_transform);
	}
	public Vec2d getPositionUnmodifiable()
	{
		return new Vec2d(transform.data[2], transform.data[5]);
	}
	public float getrotationSpeed()
	{
		return rot_speed;
	}
	public void move(float dt)
	{
		transform.data[2] += vel.x * dt;
		transform.data[5] += vel.y * dt;
		transform.invert3x3(inv_transform);
	}
	public void move(Vec2d move)
	{
		transform.data[2] += move.x;
		transform.data[5] += move.y;
		transform.invert3x3(inv_transform);
	}
	public void move(float dx, float dy)
	{
		transform.data[2] += dx;
		transform.data[5] += dy;
		transform.invert3x3(inv_transform);
	}
	public void applyGravity(Vec2d gravity)
	{
		vel.x += gravity.x * gravity_scale;
		vel.y += gravity.y * gravity_scale;
	}
	public void applyGravity(float ax, float ay)
	{
		vel.x += ax * gravity_scale;
		vel.y += ay * gravity_scale;
	}
	public void setRotation(float angle)
	{
		transform.setRotationInstance(angle);
		transform.invert3x3(inv_transform);
	}
	public void setRotationSpeed(float angle)
	{
		rot_speed = angle;
		rot.setRotationInstance(centroid, rot_speed * world.dt * (1f / world.iters));
	}
	public void rotate()
	{
		transform.mult(rot);
		transform.invert3x3(inv_transform);
		updateProject();
	}
	public void applyForceWorld(Vec2d world_pos, Vec2d force)
	{
		Vec2d centroid = new Vec2d(this.centroid);
		transform.project2D(centroid);
		Vec2d pos = new Vec2d(world_pos);
		inv_transform.project2D(pos);
//		System.out.println(pos);
		
		Vec2d tangent = Vec2d.subtract(world_pos, centroid).rightNormal();
		tangent.normalize();
		float t = Vec2d.dist2(world_pos, centroid) * tangent.dotProduct(force);
		float a = t / I;
//		System.out.println("a: " + a);
		setRotationSpeed(a + rot_speed);
		vel.x += force.x * world.dt;
		vel.y += force.y * world.dt;
	}
	public void applyForceLocal(Vec2d local_pos, Vec2d force)
	{
//		
		
	}
	public void pointVelocityFromLocal(Vec2d v, float dt)
	{
		Vec2d v2 = new Vec2d(v);
		rot.project2D(v2);
		v.set(Vec2d.subtract(v2, v));
		v.mult(1f / dt);
//		v = v.leftNormal();
//		v.add(vel.x, vel.y);
	}
	public void pointVelocityFromWorld(Vec2d v, float dt)
	{
		inv_transform.project2D(v); //from world to local points
		pointVelocityFromLocal(v, dt);
		transform.projectVector(v);
		v.add(vel);
	}
	public void updateProject()
	{
		shape.projectTo(transform, shape_proj);
	}

}
