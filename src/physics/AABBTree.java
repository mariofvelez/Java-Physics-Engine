package physics;

import java.awt.Color;
import java.awt.Graphics2D;

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
	public void debugDraw(Graphics2D g2, Transform transform)
	{
		if(root != null)
			root.debugDraw(g2, transform, 20);
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
	void debugDraw(Graphics2D g2, Transform transform, float color)
	{
		if(color > 255)
			color = 255;
		g2.setColor(new Color(255, 0, 0, (int)(color)));
		aabb.debugDraw(g2, transform);
		if(!is_child)
		{
			left.debugDraw(g2, transform, color + 20);
			right.debugDraw(g2, transform, color + 20);
		}
	}
}