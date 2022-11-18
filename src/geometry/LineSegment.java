package geometry;

import java.awt.Graphics2D;

import math.Vec2d;

public class LineSegment {
	public Vec2d a;
	public Vec2d b;
	
	public LineSegment(Vec2d a, Vec2d b)
	{
		this.a = a;
		this.b = b;
	}
		
	public Intersection intersection(LineSegment other)
	{
		Vec2d poi = new Vec2d(0, 0);
		
		Vec2d c = other.a;
		Vec2d d = other.b;
		
		float bxax = (b.x-a.x);
		float byay = (b.y-a.y);
		float dycy = (d.y-c.y);
		float dxcx = (d.x-c.x);
		
		float denom = dycy*bxax - dxcx*byay;
		
		if(denom == 0) //parallel lines, do not intersect
			return new Intersection(this, other, null, false);
		
		float axcx = (a.x-c.x);
		float aycy = (a.y-c.y);
		
		float ua = dxcx*aycy - dycy*axcx;
			  ua /= denom;
		float ub = bxax*aycy - byay*axcx;
			  ub /= denom;
			  
		//point of intersection for lines
		poi.x = a.x + ua*bxax;
		poi.y = a.y + ua*byay;
		
		if(ua >= 0 && ua < 1 && ub >= 0 && ub < 1) //if line segments are within bounds
			return new Intersection(this, other, poi, true);
		return new Intersection(this, other, null, false); //do not intersect
	}
	public boolean intersects(Vec2d c, Vec2d d)
	{
		Vec2d poi = new Vec2d(0, 0);
				
		float bxax = (b.x-a.x);
		float byay = (b.y-a.y);
		float dycy = (d.y-c.y);
		float dxcx = (d.x-c.x);
		
		float denom = dycy*bxax - dxcx*byay;
		
		if(denom == 0) //parallel lines, do not intersect
			return false;
		
		float axcx = (a.x-c.x);
		float aycy = (a.y-c.y);
		
		float ua = dxcx*aycy - dycy*axcx;
			  ua /= denom;
		float ub = bxax*aycy - byay*axcx;
			  ub /= denom;
			  
		//point of intersection for lines
		poi.x = a.x + ua*bxax;
		poi.y = a.y + ua*byay;
		
		if(ua >= 0 && ua < 1 && ub >= 0 && ub < 1) //if line segments are within bounds
			return true;
		return false; //do not intersect
	}
	public void debugDraw(Graphics2D g2)
	{
		g2.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
	}

}
