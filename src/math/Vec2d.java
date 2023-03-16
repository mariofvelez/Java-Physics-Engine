package math;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 2 dimensional vector
 * @author Mario Velez
 *
 */
public class Vec2d {
	public static final Vec2d ZERO = new Vec2d(0, 0);
	public static final Vec2d NORTH = new Vec2d(0, 1);
	public static final Vec2d SOUTH = new Vec2d(0, -1);
	public static final Vec2d EAST = new Vec2d(1, 0);
	public static final Vec2d WEST = new Vec2d(-1, 0);
	
	public float x;
	public float y;
	
	public Vec2d()
	{
		x = 0;
		y = 0;
	}
	public Vec2d(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	/**
	 * creates a copy of a Vec2d
	 * @param copy - the Vec2d to copy
	 */
	public Vec2d(Vec2d copy)
	{
		this(copy.x, copy.y);
	}
	/**
	 * (x, y)
	 */
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	public static Vec2d fromPolar(double r, double d)
	{
		double x = Math.cos(r)*d;
		double y = Math.sin(r)*d;
		return new Vec2d((float) x, (float) y);
	}
	public static Vec2d fromString(String str)
	{
		String[] vec = str.split("[(, )]+");
		return new Vec2d(Float.parseFloat(vec[1]), Float.parseFloat(vec[2]));
	}
	/**
	 * 
	 * @param vec - other Vec2d to be compared
	 * @returns true if both x and y values are the same
	 */
	public boolean equals(Vec2d vec)
	{
		return x == vec.x && y == vec.y;
	}
	public boolean equals(float x, float y)
	{
		return this.x == x && this.y == y;
	}
	public void print()
	{
		System.out.println(toString());
	}
	public void setZero()
	{
		x = 0;
		y = 0;
	}
	public void set(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	public void set(Vec2d vec)
	{
		this.x = vec.x;
		this.y = vec.y;
	}
	public void setPolar(float r, float d)
	{
		x = (float) Math.cos(r) * d;
		y = (float) Math.sin(r) * d;
	}
	public void negate()
	{
		x = -x;
		y = -y;
	}
	/**
	 * sets the x and y to the first integer smaller
	 */
	public void floor()
	{
		if(x < 0)
			x = (int) x-1;
		else
			x = (int) x;
		if(y < 0)
			y = (int) y-1;
		else
			y = (int) y;
	}
	public float length()
	{
		return (float) Math.sqrt(x*x + y*y);
	}
	/**
	 * @returns the length squared
	 */
	public float length2()
	{
		return x*x + y*y;
	}
	public void add(float x, float y)
	{
		this.x += x;
		this.y += y;
	}
	public void add(Vec2d vec)
	{
		this.x += vec.x;
		this.y += vec.y;
	}
	public static Vec2d add(Vec2d a, Vec2d b)
	{
		return new Vec2d(a.x + b.x, a.y + b.y);
	}
	public void subtract(float x, float y)
	{
		this.x -= x;
		this.y -= y;
	}
	public void subtract(Vec2d vec)
	{
		this.x -= vec.x;
		this.y -= vec.y;
	}
	public static Vec2d subtract(Vec2d a, Vec2d b)
	{
		return new Vec2d(a.x - b.x, a.y - b.y);
	}
	
	public void mult(float x, float y)
	{
		this.x *= x;
		this.y *= y;
	}
	public void mult(Vec2d vec)
	{
		this.x *= vec.x;
		this.y *= vec.y;
	}
	public static Vec2d mult(Vec2d a, Vec2d b)
	{
		return new Vec2d(a.x * b.x, a.y * b.y);
	}
	
	public void mult(float scale)
	{
		this.x *= scale;
		this.y *= scale;
	}
	public static Vec2d mult(Vec2d a, float scale)
	{
		return new Vec2d(a.x * scale, a.y * scale);
	}
	public float dist(Vec2d vec)
	{
		return (float) (Math.sqrt((x-vec.x)*(x-vec.x) + (y-vec.y)*(y-vec.y)));
	}
	/**
	 * 
	 * @param a - Vector a
	 * @param b - Vector b
	 * @returns the distance between the vectors
	 */
	public static float dist(Vec2d a, Vec2d b)
	{
		return (float) (Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y)));
	}
	public static float dist2(Vec2d a, Vec2d b)
	{
		return (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y);
	}
	/**
	 * 
	 * @param vecs - set of vectors
	 * @returns an array of length 2 containing the smallest and largest x and y. 
	 * In other words it returns a bounding box of the set of points
	 */
	public static Vec2d[] minAndMax(Vec2d... vecs)
	{
		Vec2d min = new Vec2d(vecs[0]);
		Vec2d max = new Vec2d(vecs[0]);
		
		for(int i = 1; i < vecs.length; i++)
		{
			if(vecs[i].x < min.x)
				min.x = vecs[i].x;
			if(vecs[i].y < min.y)
				min.y = vecs[i].y;
			if(vecs[i].x > max.x)
				max.x = vecs[i].x;
			if(vecs[i].y > max.y)
				max.y = vecs[i].y;
		}
		
		return new Vec2d[] {min, max};
	}
	public float dotProduct(Vec2d vec)
	{
		return x*vec.x + y*vec.y;
	}
	public static float dotProduct(Vec2d a, Vec2d b)
	{
		return a.x*b.x + a.y*b.y;
	}
	public float crossProduct(Vec2d vec)
	{
		return x*vec.y - y*vec.x;
	}
	public static float crossProduct(Vec2d a, Vec2d b)
	{
		return a.x*b.y - a.y*b.x;
	}
	/**
	 * sets the length to 1
	 */
	public float normalize()
	{
		float length = length();
		x /= length;
		y /= length;
		return length;
	}
	/**
	 * divides by the length squared. Usually used for projection
	 */
	public void normalize2()
	{
		float length = length2();
		x /= length;
		y /= length;
	}
	public static Vec2d normalize(Vec2d vec)
	{
		float length = vec.length();
		return new Vec2d(vec.x / length, vec.y / length);
	}
	public static Vec2d avg(Vec2d... vecs)
	{
		Vec2d avg = new Vec2d(0, 0);
		for(int i = 0; i < vecs.length; i++)
			avg.add(vecs[i]);
		avg.mult(1f/vecs.length);
		return avg;
	}
	public static Vec2d sum(Vec2d... vecs)
	{
		Vec2d sum = new Vec2d(vecs[0]);
		for(int i = 1; i < vecs.length; i++)
			sum.add(vecs[i]);
		return sum;
	}
	public static void sort(Comparator<Vec2d> c, Vec2d... vecs)
	{
		Arrays.parallelSort(vecs, c);
	}
	
	/**
	 * reflects a vector off a surface with a normal
	 * @param a - the Vec2d to be reflected
	 * @param normal - the normal of the surface
	 * @returns the reflected vector normalized
	 */
	public static Vec2d reflect(Vec2d ri, Vec2d normal)
	{
		Vec2d normalized = normalize(normal);
		float product = dotProduct(ri, normalized); // 
		Vec2d n = mult(normalized, product);
		n.mult(2);
		Vec2d rf = subtract(ri, n);
		return rf;
	}
	/**
	 * bounces a vector off a surface defined by the given normal, restitution, and friction
	 * @param ri - the Vec2d to bounce
	 * @param normal - the normal of the surface
	 * @param restitution - vector between 0.0f and 1.0f that determines the bounciness between the vector and the surface
	 * @param friction - value between 0.0f and 1.0f that determines the friction between the vector and the surface
	 * @return
	 */
	public static Vec2d bounce(Vec2d ri, Vec2d normal, float restitution, float friction)
	{
		friction = 1 - friction;
		Vec2d normalized = normalize(normal);
		Vec2d axis = normalized.rightNormal();
		float rest = dotProduct(ri, normalized); // how hard it hit the surface
		float fric = dotProduct(ri, axis); // how hard it grazed the surface
		Vec2d rf = new Vec2d(0, 0);
		rf.subtract(normalized.x*rest*restitution, normalized.y*rest*restitution);
		rf.add(axis.x*fric*friction, axis.y*fric*friction);
		return rf;
	}
	/**
	 * the left hand normal of this Vec2d
	 * @return - the normal vector
	 */
	public Vec2d leftNormal()
	{
		return new Vec2d(y, -x);
	}
	/**
	 * the right hand normal of this Vec2d
	 * @return - the normal vector
	 */
	public Vec2d rightNormal()
	{
		return new Vec2d(-y, x);
	}
	/**
	 * rotates left by 90 degrees
	 */
	public void rotateLeft()
	{
		float temp = x;
		this.x = y;
		this.y = -temp;
	}
	/**
	 * rotates right by 90 degrees
	 */
	public void rotateRight()
	{
		float temp = x;
		this.x = -y;
		this.y = temp;
	}
	/**
	 * 
	 * @param a - the first vector
	 * @param b - the second vector
	 * @param dist - a value between 0 and 1 which represents the distance from a to b
	 * @returns the interpolated value between the two vectors
	 */
	public static Vec2d lerp(Vec2d a, Vec2d b, float dist)
	{
		Vec2d lerped = new Vec2d(a.x + (b.x-a.x)*dist, a.y + (b.y-a.y)*dist);
		return lerped;
	}
	/**
	 * Interpolates through a Bezier curve constrained to vector points
	 * @param points - the points that define the curve
	 * @param dist - a value between 0 and 1 which represents the distance along the curve to interpolate
	 * @returns the interpolated value along the Bezier curve
	 */
	public static Vec2d bezierLerp(float dist, Vec2d... points)
	{
		if(points.length < 2)
			return points[0];
		Vec2d[] next = new Vec2d[points.length-1];
		for(int i = 0; i < next.length; i++)
			next[i] = lerp(points[i], points[i+1], dist);
		return bezierLerp(dist, next);
	}
	/**
	 * creates a set of points along the Bezier curve
	 * @param count - the amount of points, including start and finish
	 * @param points - the points that define the curve
	 * @returns the list of interpolated values along the curve
	 */
	public static Vec2d[] getBezierPoints(int count, Vec2d... points)
	{
		Vec2d[] p1 = new Vec2d[count];
//		float dist = 1f / (points.length-1);
		p1[0] = new Vec2d(points[0]);
		for(int i = 1; i < count-1; i++)
			p1[i] = bezierLerp((float)i/count, points);
		p1[count-1] = new Vec2d(points[points.length-1]);
		return p1;
	}
	/**
	 * Usually used for drawing to a Canvas
	 * @param vecs - the set of vectors
	 * @returns an array of each x point
	 */
	public static int[] xPoints(Vec2d... vecs)
	{
		int[] x_points = new int[vecs.length];
		for (int i = 0; i < x_points.length; i++)
		{
			x_points[i] = (int) vecs[i].x;
		}
		return x_points;
	}
	/**
	 * Usually used for drawing to a Canvas
	 * @param vecs - the set of vectors
	 * @returns an array of each y point
	 */
	public static int[] yPoints(Vec2d... vecs)
	{
		int[] y_points = new int[vecs.length];
		for (int i = 0; i < y_points.length; i++)
		{
			y_points[i] = (int) vecs[i].y;
		}
		return y_points;
	}
	/**
	 * Draws a dot where it is
	 * @param g2 - graphics
	 */
	public void debugDraw(Graphics2D g2)
	{
		g2.fillOval((int)x-4, (int)y-4, 8, 8);
	}
	public void debugDraw(Graphics2D g2, int r)
	{
		g2.fillOval((int)x-r, (int)y-r, r*2, r*2);
	}
	public void debugDraw(Graphics2D g2, Color c)
	{
		g2.setColor(c);
		g2.fillOval((int)x-4, (int)y-4, 8, 8);
		g2.setColor(Color.BLACK);
		g2.drawOval((int)x-4, (int)y-4, 8, 8);
	}

}
/**
 * preset vectors
 * @author Mario Velez
 *
 */
enum Vec2dConstant {;
	public static final Vec2d ZERO = new Vec2d(0, 0);
	public static final Vec2d NORTH = new Vec2d(1, 0);
	public static final Vec2d SOUTH = new Vec2d(-1, 0);
	public static final Vec2d EAST = new Vec2d(0, 1);
	public static final Vec2d WEST = new Vec2d(0, -1);
}
