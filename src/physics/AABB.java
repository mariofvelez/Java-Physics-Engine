package physics;

import java.awt.Graphics2D;
import java.util.Random;

import math.Transform2d;
import math.Vec2d;

public class AABB {
	public float min_x;
	public float max_x;
	public float min_y;
	public float max_y;
	
	public AABB(float min_x, float max_x, float min_y, float max_y)
	{
		this.min_x = min_x;
		this.max_x = max_x;
		this.min_y = min_y;
		this.max_y = max_y;
	}
	public AABB(AABB other)
	{
		this.min_x = other.min_x;
		this.max_x = other.max_x;
		this.min_y = other.min_y;
		this.max_y = other.max_y;
	}
	public void debugDraw(Graphics2D g2, Transform2d transform)
	{
		Vec2d[] vecs = getBoxCoordinates();
		for(int i = 0; i < vecs.length; i++)
			vecs[i] = transform.projectToTransform(vecs[i]);
		g2.drawPolygon(Vec2d.xPoints(vecs), Vec2d.yPoints(vecs), vecs.length);
	}
	public void set(float min_x, float max_x, float min_y, float max_y)
	{
		this.min_x = min_x;
		this.max_x = max_x;
		this.min_y = min_y;
		this.max_y = max_y;
	}
	public void set(AABB other)
	{
		this.min_x = other.min_x;
		this.max_x = other.max_x;
		this.min_y = other.min_y;
		this.max_y = other.max_y;
	}
	public void move(float x, float y)
	{
		min_x += x;
		max_x += x;
		min_y += y;
		max_y += y;
	}
	public boolean intersects(float x, float y)
	{
		return x > min_x && x < max_x && y > min_y && y < max_y;
	}
	public boolean intersects(Vec2d pos)
	{
		return pos.x > min_x && pos.x < max_x && pos.y > min_y && pos.y < max_y;
	}
	public boolean intersects(AABB other)
	{
		return max_x > other.min_x && min_x < other.max_x &&
				max_y > other.min_y && min_y < other.max_y;
	}
	public boolean moveOut(AABB other)
	{
		/*
		 * 
		 */
		if(!intersects(other))
			return false;
		
		float right = other.min_x - max_x; // distance between each side
		float left = min_x - other.max_x;
		float up = other.min_y - max_y;
		float down = min_y - other.max_y;
		
		float lr = right > left? -right : left;
		float ud = up > down? up : -down;
		
		if(Math.abs(lr) < Math.abs(ud))
			move(lr, 0);
		else
			move(0, ud);
		return true;
	}
	public Vec2d[] getBoxCoordinates()
	{
		return new Vec2d[] {new Vec2d(min_x, max_y), new Vec2d(max_x, max_y), new Vec2d(max_x, min_y), new Vec2d(min_x, min_y)};
	}
	/**
	 * Computes the area of the bounding box
	 * @return
	 */
	public float computeArea()
	{
		return (max_x-min_x)*(max_y-min_y);
	}
	/**
	 * checks if the aabb can fit inside this aabb. Does not matter where they are.
	 * @param other - the other aabb
	 * @returns true if the other aabb can fit inside this aabb, false if not
	 */
	public boolean fits(AABB other)
	{
		return max_x - min_x >= other.max_x - other.min_x && max_y - min_y >= other.max_y - other.min_y;
	}
	/**
	 * Splits this aabb into 4 aabbs containing the space inside this aabb excluding the excluded aabb, 
	 * assuming the excluded aabb fits inside this one
	 * @param aabb - the aabb to exclude
	 * @returns an array of the aabbs
	 */
	public AABB[] splitExcluding(AABB aabb, Random r)
	{
		if(r.nextBoolean())
		{
			AABB[] arr = new AABB[4];
			arr[0] = new AABB(min_x, max_x, min_y, aabb.min_y);
			arr[1] = new AABB(aabb.max_x, max_x, aabb.min_y, aabb.max_y);
			arr[2] = new AABB(min_x, max_x, aabb.max_y, max_y);
			arr[3] = new AABB(min_x, aabb.min_x, aabb.min_y, aabb.max_y);
			return arr;
		}
		else
		{
			AABB[] arr = new AABB[4];
			arr[0] = new AABB(aabb.min_x, aabb.max_x, min_y, aabb.min_y);
			arr[1] = new AABB(aabb.max_x, max_x, min_y, max_y);
			arr[2] = new AABB(aabb.min_x, aabb.max_x, aabb.max_y, max_y);
			arr[3] = new AABB(min_x, aabb.min_x, min_y, max_y);
		return arr;
		}
	}
	public void setTopLeft(AABB other)
	{
		min_x = other.min_x;
		max_x = (other.min_x + other.max_x) / 2f;
		min_y = other.min_y;
		max_y = (other.min_y + other.max_y) / 2f;
	}
	public void setTopRight(AABB other)
	{
		min_x = (other.min_x + other.max_x) / 2f;
		max_x = other.max_x;
		min_y = other.min_y;
		max_y = (other.min_y + other.max_y) / 2f;
	}
	public void setBottomLeft(AABB other)
	{
		min_x = other.min_x;
		max_x = (other.min_x + other.max_x) / 2f;
		min_y = (other.min_y + other.max_y) / 2f;
		max_y = other.max_y;
	}
	public void setBottomRight(AABB other)
	{
		min_x = (other.min_x + other.max_x) / 2f;
		max_x = other.max_x;
		min_y = (other.min_y + other.max_y) / 2f;
		max_y = other.max_y;
	}
}
