package geometry;

import math.Vec2d;

public class Ray {
	public Vec2d origin;
	public Vec2d direction;
	public static final float MAX_DISTANCE = 100f;

	public Ray(Vec2d origin, Vec2d direction)
	{
		this.origin = origin;
		this.direction = direction;
	}
	public float intersect(LineSegment segment)
	{
//		Vec2d pointA = Vec2d.subtract(segment.a, origin);
//		Vec2d segVec = Vec2d.subtract(segment.b, segment.a);
		return MAX_DISTANCE;
	}
	public float intersect(LineSegment[] segments)
	{
		float min = MAX_DISTANCE;
		for(int i = 0; i < segments.length; i++)
		{
			float dist = intersect(segments[i]);
			if(dist < min)
				min = dist;
		}
		return min;
	}
}
