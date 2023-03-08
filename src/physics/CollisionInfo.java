package physics;

import math.Vec2d;
import physics.body.Body;

public class CollisionInfo {
	
	/**
	 * The first body of the collision
	 */
	public Body a;
	/**
	 * The second body of the collision
	 */
	public Body b;
	/**
	 * The normal vector of the collision
	 */
	public Vec2d normal;
	/**
	 * The point of collision
	 */
	public Vec2d poc;

}
