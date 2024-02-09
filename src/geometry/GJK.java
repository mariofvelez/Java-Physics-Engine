package geometry;

import java.awt.Color;
import java.awt.Graphics2D;

import math.Vec2d;

public class GJK {
	
	static Simplex s = new Simplex();
	static Vec2d D = new Vec2d(1.0f, 0.0f);
	
	public static boolean intersect(Shape2d a, Shape2d b) throws InterruptedException
	{
		
		Vec2d A = support(a, b, new Vec2d(1.0f, 0.0f));
		
		s = new Simplex();
		s.addVertex(A);
		
		D = Vec2d.mult(A, -1.0f);
		
		for(int i = 0; i < 32; ++i)
		{
			A = support(a, b, D);
			
			if(Vec2d.dotProduct(A, D) < 0.0f)
				return false;
			
			s.addVertex(A);
			
			if(doSimplex(s, D))
				return true;
		}
		return false;
	}
	
	private static Vec2d support(Shape2d a, Shape2d b, Vec2d d)
	{
		Vec2d s = new Vec2d();
		a.support(d, s);
		
		Vec2d d1 = Vec2d.mult(d, -1.0f);
		
		Vec2d s2 = new Vec2d();
		b.support(d1, s2);
		
		return Vec2d.subtract(s, s2);
	}
	
	private static boolean doSimplex(Simplex s, Vec2d d) throws InterruptedException
	{
		switch(s.length)
		{
		case 1:
			return false;
		case 2:
		{
			Vec2d ab = Vec2d.subtract(s.verts[1], s.verts[0]);
			Vec2d ao = Vec2d.mult(s.verts[1], -1.0f);
			d.set(triple_cross(ab, ao));
			
			return false;
		}
		case 3:
		{
			Vec2d ab = Vec2d.subtract(s.verts[2], s.verts[1]);
			Vec2d ac = Vec2d.subtract(s.verts[2], s.verts[0]);
			Vec2d ao = Vec2d.mult(s.verts[2], -1.0f);
			Vec2d ac_normal = ac.leftNormal();
			ac_normal.mult(Vec2d.dotProduct(ac_normal, ab)); // points away from simplex
			Vec2d ab_normal = ab.rightNormal();
			ab_normal.mult(Vec2d.dotProduct(ab_normal, ac)); // points away from simplex
			
			if(Vec2d.dotProduct(ac_normal, ao) > 0.0f)
			{
				// [C, A]
				s.verts[1] = s.verts[2];
				s.length = 2;
				d.set(ac_normal);
				
				return false;
			}
			else if(Vec2d.dotProduct(ab_normal, ao) > 0.0f)
			{
				// [B, A]
				s.verts[0] = s.verts[1];
				s.verts[1] = s.verts[2];
				s.length = 2;
				d.set(ab_normal);
				
				return false;
			}
			else
			{
				return true;
			}
		}
		}
		return false;
	}
	
	private static Vec2d triple_cross(Vec2d a, Vec2d b)
	{
		Vec2d normal = a.leftNormal();
		normal.mult(Vec2d.dotProduct(normal, b));
		return normal;
	}
	
	public static void debugDraw(Graphics2D g2)
	{
		s.debugDraw(g2);
		Vec2d dir = new Vec2d(D);
		dir.normalize();
		dir.mult(20.0f);
		dir.add(200.0f, 200.0f);
		
		g2.setColor(Color.CYAN);
		dir.debugDraw(g2, 4);
		g2.drawLine(200, 200, (int) dir.x, (int) dir.y);
	}

}

class Simplex
{
	public Vec2d[] verts;
	public int length = 0;
	
	private static Color[] colors = {
		Color.BLUE,
		Color.GREEN,
		Color.RED
	};
	
	Simplex()
	{
		verts = new Vec2d[3];
	}
	void addVertex(Vec2d v)
	{
		verts[length] = new Vec2d(v);
		length++;
	}
	void debugDraw(Graphics2D g2)
	{
		for(int i = 0; i < length; ++i)
		{
			g2.setColor(colors[i]);
			Vec2d v = new Vec2d(verts[i]);
			v.add(200.0f, 200.0f);
			v.debugDraw(g2, 4);
		}
	}
}
