package geometry;

import math.Vec2d;

public class GJK {
	
	public static Vec2d intersect(Shape2d a, Shape2d b)
	{
		Vec2d axis = new Vec2d(1, 0);
		
		Vec2d temp = new Vec2d();
		
		Vec2d A = new Vec2d(); //support
		a.support(axis, A);
		axis.negate();
		b.support(axis, temp);
		A.subtract(temp);
		
		Simplex s = new Simplex();
		s.addVertex(A);
		
		Vec2d D = new Vec2d(A);
		D.negate();
		
//		while(true)
		{
			a.support(D, A);
			b.support(D, temp);
			A.subtract(temp);
			
			if(Vec2d.dotProduct(A, D) < 0)
				return null;
			s.addVertex(A);
			
		}
		
		return null;
	}

}

class Simplex
{
	private int index = 0;
	Vec2d[] verts;
	
	Simplex()
	{
		verts = new Vec2d[3];
	}
	void addVertex(Vec2d v)
	{
		verts[index++] = new Vec2d(v);
	}
	boolean containsOrigin()
	{
		if(index < 3)
			return false;
		return false;
	}
}
