package physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import math.Transform;
import physics.body.Body;

public class AABBTree {
	
	AABBNode root;
	
	public AABBTree()
	{
		root = null;
	}
	public void addAABB(AABB aabb, Body body)
	{
		if(root == null)
		{
			root = new AABBNode(aabb);
			root.body = body;
		}
		else
		{
			root.addNode(aabb, body);
		}
	}
	public void debugDraw(Graphics2D g2, Transform transform, int level)
	{
		if(root != null)
			root.debugDraw(g2, transform, 20, level - 1);
	}
	//TODO - make creating list of bodies more efficient
	public ArrayList<Body> getCollideList(AABB aabb)
	{
		ArrayList<Body> bodies = new ArrayList<>();
		if(root != null)
			root.checkCollision(bodies, aabb);
		return bodies;
	}

}
class AABBNode {
	AABB aabb;
	Body body;
	boolean is_child = true;
	
	AABBNode left;
	AABBNode right;
	
	public AABBNode(AABB aabb)
	{
		this.aabb = aabb;
		body = null;
	}
	void addNode(AABB aabb, Body body)
	{
		if(is_child)
		{
			AABB combined = AABB.combine(this.aabb, aabb);
			
			// left = old this
			left = new AABBNode(this.aabb);
			left.body = this.body;
			
			// right = new aabb
			right = new AABBNode(aabb);
			right.body = body;
			
			// set this to combined
			is_child = false;
			this.aabb = combined;
			this.body = null;
		}
		else
		{
			AABB left_combined = AABB.combine(aabb, left.aabb);
			AABB right_combined = AABB.combine(aabb, right.aabb);
			
			float left_area = left_combined.computeArea();
			float right_area = right_combined.computeArea();
			
			if(left_area < right_area)
			{
				this.aabb = AABB.combine(left_combined, this.aabb);
				left.addNode(aabb, body);
			}
			else
			{
				this.aabb = AABB.combine(right_combined, this.aabb);
				right.addNode(aabb, body);
			}
		}
	}
	void checkCollision(ArrayList<Body> bodies, AABB aabb)
	{
		if(this.aabb.intersects(aabb))
		{
			if(is_child)
				bodies.add(body);
			else
			{
				right.checkCollision(bodies, aabb);
				left.checkCollision(bodies, aabb);
			}
		}
	}
	void debugDraw(Graphics2D g2, Transform transform, float color, int level)
	{
		if(level < 0)
			return;
		if(color > 255)
			color = 255;
		g2.setColor(new Color(255, 0, 0, (int)(color)));
		aabb.debugDraw(g2, transform);
		if(!is_child)
		{
			left.debugDraw(g2, transform, color + 20, level - 1);
			right.debugDraw(g2, transform, color + 20, level - 1);
		}
	}
}