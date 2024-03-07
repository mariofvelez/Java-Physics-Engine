package physics;

import java.util.ArrayList;

import physics.body.Body;

public class BVHTree {
	
	ArrayList<Body> bodies;
	ArrayList<BVHNode> nodes;
	
	public BVHTree(World world)
	{
		bodies = world.d_bodies;
		nodes = new ArrayList<>();
	}

}
class BVHNode {
	
	AABB aabb;
	int left;
	int right;
	int body_index;
	
	
}
