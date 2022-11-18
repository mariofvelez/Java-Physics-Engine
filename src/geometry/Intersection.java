package geometry;

import math.Vec2d;

public class Intersection {
	public LineSegment a;
	public LineSegment b;
	
	public Vec2d point;
	public boolean intersected;
	
	public Intersection(LineSegment a, LineSegment b, Vec2d point, boolean intersected)
	{
		this.a = a;
		this.b = b;
		this.point = point;
		this.intersected = intersected;
	}

}
