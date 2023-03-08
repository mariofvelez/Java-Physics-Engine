package physics;

import java.awt.Graphics2D;
import java.util.Random;

import math.Transform2d;
import math.Vec2d;

/**
 * Axis-Aligned Bounding Box, 
 * A rectangle defined by its edges, usually used for outlining a region
 * @author Mario Velez
 *
 */
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
	/**
	 * Creates a copy of an AABB
	 * @param other - the AABB to copy
	 */
	public AABB(AABB other)
	{
		this.min_x = other.min_x;
		this.max_x = other.max_x;
		this.min_y = other.min_y;
		this.max_y = other.max_y;
	}
	/**
	 * Draws to the Graphics2D buffer
	 * @param g2 - the graphics to draw on
	 * @param transform - A linear transformation from world to pixel coordinates
	 */
	public void debugDraw(Graphics2D g2, Transform2d transform)
	{
		Vec2d[] vecs = getBoxCoordinates();
		for(int i = 0; i < vecs.length; i++)
			vecs[i] = transform.projectToTransform(vecs[i]);
		g2.drawPolygon(Vec2d.xPoints(vecs), Vec2d.yPoints(vecs), vecs.length);
	}
	/**
	 * Set each of the values individually
	 */
	public void set(float min_x, float max_x, float min_y, float max_y)
	{
		this.min_x = min_x;
		this.max_x = max_x;
		this.min_y = min_y;
		this.max_y = max_y;
	}
	/**
	 * Copies all the values from another AABB to this one
	 * @param other - the AABB to copy from
	 */
	public void set(AABB other)
	{
		this.min_x = other.min_x;
		this.max_x = other.max_x;
		this.min_y = other.min_y;
		this.max_y = other.max_y;
	}
	/**
	 * Moves the AABB by (x, y)
	 * @param x - the x distance to move
	 * @param y - the y distance to move
	 */
	public void move(float x, float y)
	{
		min_x += x;
		max_x += x;
		min_y += y;
		max_y += y;
	}
	/**
	 * Tests if this AABB contains the point (x, y)
	 * @param x - x value of the point
	 * @param y - y value of the point
	 * @returns true if it contains the point, false if not
	 */
	public boolean intersects(float x, float y)
	{
		return x > min_x && x < max_x && y > min_y && y < max_y;
	}
	/**
	 * Tests if this AABB contains a point
	 * @param pos - the position of the point
	 * @returns true if it contains the point, false if not
	 */
	public boolean intersects(Vec2d pos)
	{
		return pos.x > min_x && pos.x < max_x && pos.y > min_y && pos.y < max_y;
	}
	/**
	 * Tests if this AABB intersects with another AABB
	 * @param other - the other AABB to test
	 * @returns true if they intersect, false if not
	 */
	public boolean intersects(AABB other)
	{
		return max_x > other.min_x && min_x < other.max_x &&
				max_y > other.min_y && min_y < other.max_y;
	}
	/**
	 * Moves this AABB the shortest distance so that it is not intersecting with the other AABB
	 * @param other - the AABB to move away from
	 * @returns true if they intersected, false if not
	 */
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
	/**
	 * Creates an array of Vec2d storing all four corners, starting from top left, going clockwise
	 * @returns the array of Vec2d
	 */
	public Vec2d[] getBoxCoordinates()
	{
		return new Vec2d[] {new Vec2d(min_x, max_y), new Vec2d(max_x, max_y), new Vec2d(max_x, min_y), new Vec2d(min_x, min_y)};
	}
	/**
	 * Computes the area of the bounding box
	 * @returns the area
	 */
	public float computeArea()
	{
		return (max_x-min_x)*(max_y-min_y);
	}
	/**
	 * checks if the AABB can fit inside this AABB. Does not matter where they are.
	 * @param other - the other AABB
	 * @returns true if the other AABB can fit inside this AABB, false if not
	 */
	public boolean fits(AABB other)
	{
		return max_x - min_x >= other.max_x - other.min_x && max_y - min_y >= other.max_y - other.min_y;
	}
	/**
	 * Splits this AABB into 4 AABB containing the space inside this AABB excluding the excluded AABB, 
	 * assuming the excluded AABB fits completely inside this one
	 * @param aabb - the AABB to exclude
	 * @param r - a Random object used to determine if the left and right or top and bottom AABBs take up the corners
	 * @returns an array of the AABBs
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
	/**
	 * Sets this AABB to the top left quadrant of another AABB
	 * @param other - The other AABB
	 */
	public void setTopLeft(AABB other)
	{
		min_x = other.min_x;
		max_x = (other.min_x + other.max_x) / 2f;
		min_y = other.min_y;
		max_y = (other.min_y + other.max_y) / 2f;
	}
	/**
	 * Sets this AABB to the top right quadrant of another AABB
	 * @param other - The other AABB
	 */
	public void setTopRight(AABB other)
	{
		min_x = (other.min_x + other.max_x) / 2f;
		max_x = other.max_x;
		min_y = other.min_y;
		max_y = (other.min_y + other.max_y) / 2f;
	}
	/**
	 * Sets this AABB to the bottom left quadrant of another AABB
	 * @param other - The other AABB
	 */
	public void setBottomLeft(AABB other)
	{
		min_x = other.min_x;
		max_x = (other.min_x + other.max_x) / 2f;
		min_y = (other.min_y + other.max_y) / 2f;
		max_y = other.max_y;
	}
	/**
	 * Sets this AABB to the bottom right quadrant of another AABB
	 * @param other - The other AABB
	 */
	public void setBottomRight(AABB other)
	{
		min_x = (other.min_x + other.max_x) / 2f;
		max_x = other.max_x;
		min_y = (other.min_y + other.max_y) / 2f;
		max_y = other.max_y;
	}
}
