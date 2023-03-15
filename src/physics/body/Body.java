package physics.body;

import geometry.Circle;
import geometry.Polygon2d;
import geometry.Shape2d;
import math.Transform;
import math.Vec2d;
import physics.AABB;
import physics.World;

public class Body {
	
	/**
	 * The world this body is in
	 */
	public World world;
	
	private CollisionType collision_type;
	/**
	 * The velocity of this body
	 */
	public Vec2d vel;
	private float rot_speed = 0;
	Transform rot;
	/**
	 * The linear transformation of the body's coordinate system
	 */
	public Transform transform;
	Transform inv_transform;
	/**
	 * The shape of this body in local coordinates
	 */
	public Shape2d shape;
	/**
	 * The shape of this body in world coordinates
	 */
	public Shape2d shape_proj;
	/**
	 * The bounding box of this body
	 */
	public AABB aabb;
	/**
	 * The center of mass for this body
	 */
	public Vec2d centroid = new Vec2d(0, 0);
	/**
	 * The moment of inertia for this body
	 */
	public float I;
	
	public float density = 1;
	/**
	 * Currently has no effect
	 */
	public float friction = 0;
	/**
	 * Bounciness
	 */
	public float restitution = 1;
	public float mass = 1;
	/**
	 * The scale of gravitational acceleration on this body. 0 is no gravity, 1 is normal gravity. Numbers outside the range [0, 1] 
	 * are also possible. You can use this to tune the jump of a player character for example
	 */
	public float gravity_scale = 1;
	/**
	 * The scale to which this body is affected by its velocity
	 */
	public float move_scale = 1;
	/**
	 * The bit groups that this body belongs to.
	 * The default group is 0b1
	 */
	public int group_filter = 1;
	/**
	 * The bit groups that this body will collide with.
	 * The default filter is collide with everything
	 */
	public int collide_filter = -1;
	/**
	 * Data stored with this body
	 */
	public Object user_data;
	
	/**
	 * Creates a Body
	 * @param pos - the world position of the origin of this body
	 * @param type - the collision type of this body
	 */
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
	/**
	 * 
	 * @returns the collision type of this body
	 */
	public CollisionType getCollisionType()
	{
		return collision_type;
	}
	/**
	 * Converts a Vec2d from world to local coordinates
	 * @param world_point - the vector to change, alters this vector
	 */
	public void worldToLocal(Vec2d world_point)
	{
		inv_transform.project2D(world_point);
	}
	/**
	 * Converts a Vec2d from local to world coordinates
	 * @param local_point - the vector to change, alters this vector
	 */
	public void localToWorld(Vec2d local_point)
	{
		transform.project2D(local_point);
	}
	/**
	 * Individually set some properties of this body
	 * @param density
	 * @param friction
	 * @param restitution
	 */
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
	/**
	 * Calculates the linear momentum of this body
	 * @returns the linear momentum
	 */
	public float calculateMomentum()
	{
		return shape.area * vel.length();
	}
	/**
	 * Sets the shape attached to this body
	 * @param shape - the shape to attach
	 */
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
	/**
	 * Updates the AABB
	 */
	public void updateAABB()
	{
		shape_proj.setAABB(aabb);
	}
	/**
	 * 
	 * @returns the shape in world coordinates attached to this body
	 */
	public Shape2d getWorldShape()
	{
		return shape_proj;
	}
	/**
	 * Sets the position of this body
	 * @param pos - the new position
	 */
	public void setPosition(Vec2d pos)
	{
		transform.data[2] = pos.x;
		transform.data[5] = pos.y;
		transform.invert3x3(inv_transform);
	}
	/**
	 * Sets the position of this body
	 * @param x - the new x position
	 * @param y - the new y position
	 */
	public void setPosition(float x, float y)
	{
		transform.data[2] = x;
		transform.data[5] = y;
		transform.invert3x3(inv_transform);
	}
	/**
	 * Get the current position of this body as a vector
	 * @returns the current position as a vector
	 */
	public Vec2d getPositionUnmodifiable()
	{
		return new Vec2d(transform.data[2], transform.data[5]);
	}
	/**
	 * Gets the rotational speed of this body, in radians per second
	 * @returns the rotational speed
	 */
	public float getrotationSpeed()
	{
		return rot_speed;
	}
	/**
	 * Moves this body by its velocity over a time period
	 * @param dt - the change in time
	 */
	public void move(float dt)
	{
		transform.data[2] += vel.x * dt;
		transform.data[5] += vel.y * dt;
		transform.invert3x3(inv_transform);
	}
	/**
	 * Moves this body
	 * @param move - the distance to move
	 */
	public void move(Vec2d move)
	{
		transform.data[2] += move.x;
		transform.data[5] += move.y;
		transform.invert3x3(inv_transform);
	}
	/**
	 * Moves this body
	 * @param dx - the x distance to move
	 * @param dy - the y distance to move
	 */
	public void move(float dx, float dy)
	{
		transform.data[2] += dx;
		transform.data[5] += dy;
		transform.invert3x3(inv_transform);
	}
	/**
	 * Applies gravity to the velocity of this body
	 * @param gravity - the gravity vector
	 */
	public void applyGravity(Vec2d gravity)
	{
		vel.x += gravity.x * gravity_scale;
		vel.y += gravity.y * gravity_scale;
	}
	/**
	 * Applies gravity to the velocity of this body
	 * @param ax - the x component of gravity
	 * @param ay - the y component of gravity
	 */
	public void applyGravity(float ax, float ay)
	{
		vel.x += ax * gravity_scale;
		vel.y += ay * gravity_scale;
	}
	/**
	 * Sets the rotation of this body
	 * @param angle - the angle to set in radians
	 */
	public void setRotation(float angle)
	{
		transform.setRotationInstance(angle);
		transform.invert3x3(inv_transform);
	}
	/**
	 * Sets the rotation speed of this body. Should only be done after adding this body to a world
	 * @param angle - the angle to set in radians per second
	 */
	public void setRotationSpeed(float angle)
	{
		rot_speed = angle;
		rot.setRotationInstance(centroid, rot_speed * world.dt * (1f / world.iters));
	}
	/**
	 * Rotates this body 1 time step / world.iters
	 */
	public void rotate()
	{
		transform.mult(rot);
		transform.invert3x3(inv_transform);
		updateProject();
	}
	/**
	 * Applies a force to this body at a world position for world.dt seconds
	 * @param world_pos - the position to apply the force
	 * @param force - the force vector
	 */
	public void applyForceWorld(Vec2d world_pos, Vec2d force)
	{
		Vec2d centroid = new Vec2d(this.centroid);
		transform.project2D(centroid);
		Vec2d pos = new Vec2d(world_pos);
		inv_transform.project2D(pos);
//		System.out.println(pos);
		
		Vec2d tangent = Vec2d.subtract(world_pos, centroid).rightNormal();
		if(tangent.length2() > 0.00001f)
		{
			tangent.normalize();
			float t = Vec2d.dist2(world_pos, centroid) * tangent.dotProduct(force);
			float a = t / I;
	//		System.out.println(tangent);
	//		System.out.println("a: " + a);
			setRotationSpeed(a + rot_speed);
		}
		vel.x += force.x * world.dt;
		vel.y += force.y * world.dt;
	}
	/**
	 * <h4> DOES NOT DO ANYTHING </h4>
	 * <p> Convert local to world coordinates instead <p>
	 * This function might be implemented in the future
	 */
	public void applyForceLocal(Vec2d local_pos, Vec2d force)
	{
//		
		
	}
	/**
	 * Gets the velocity vector at a local point
	 * @param v - the point to test in local coordinates
	 * @param dt - dt of the world
	 */
	public void pointVelocityFromLocal(Vec2d v, float dt)
	{
		Vec2d v2 = new Vec2d(v);
		rot.project2D(v2);
		v.set(Vec2d.subtract(v2, v));
		v.mult(1f / dt);
//		v = v.leftNormal();
	}
	/**
	 * Gets the velocity vector at a world point
	 * @param v - the point to test in world coordinates
	 * @param dt - dt of the world
	 */
	public void pointVelocityFromWorld(Vec2d v, float dt)
	{
		inv_transform.project2D(v); //from world to local points
		pointVelocityFromLocal(v, dt);
		transform.projectVector(v);
		v.add(vel);
	}
	/**
	 * Updates the projected shape
	 */
	public void updateProject()
	{
		shape.projectTo(transform, shape_proj);
	}

}
