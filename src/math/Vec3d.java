package math;

/**
 * 
 * @author Mario Velez
 *
 */
public class Vec3d extends Vec2d {
	public float z;
	
	public static final Vec3d ZERO = new Vec3d(0f, 0f, 0f);
	public static final Vec3d X_POS = new Vec3d(1f, 0f, 0f);
	public static final Vec3d X_NEG = new Vec3d(-1f, 0f, 0f);
	public static final Vec3d Y_POS = new Vec3d(0f, 1f, 0f);
	public static final Vec3d Y_NEG = new Vec3d(0f, -1f, 0f);
	public static final Vec3d Z_POS = new Vec3d(0f, 0f, 1f);
	public static final Vec3d Z_NEG = new Vec3d(0f, 0f, -1f);
	
	public Vec3d()
	{
		super(0, 0);
		z = 0f;
	}
	public Vec3d(float x, float y, float z)
	{
		super(x, y);
		this.z = z;
	}
	public Vec3d(Vec3d copy)
	{
		super(copy.x, copy.y);
		this.z = copy.z;
	}
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}
	public void print()
	{
		System.out.println(toString());
	}
	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void set(Vec3d vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}
	public void negate()
	{
		x = -x;
		y = -y;
		z = -z;
	}
	public float length()
	{
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	public void add(float x, float y, float z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
	}
	public void add(Vec3d vec)
	{
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}
	public static Vec3d add(Vec3d a, Vec3d b)
	{
		return new Vec3d(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	public void subtract(float x, float y, float z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}
	public void subtract(Vec3d vec)
	{
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
	}
	public static Vec3d subtract(Vec3d a, Vec3d b)
	{
		return new Vec3d(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	public void mult(float x, float y, float z)
	{
		this.x *= x;
		this.y *= y;
		this.z *= z;
	}
	public void mult(Vec3d vec)
	{
		x *= vec.x;
		y *= vec.y;
		z *= vec.z;
	}
	public static Vec3d mult(Vec3d a, Vec3d b)
	{
		return new Vec3d(a.x * b.x, a.y * b.y, a.z * b.z);
	}
	public void mult(float scale)
	{
		x *= scale;
		y *= scale;
		z *= scale;
	}
	public static Vec3d mult(Vec3d a, float scale)
	{
		return new Vec3d(a.x * scale, a.y * scale, a.z * scale);
	}
	public float dist(Vec3d vec)
	{
		return (float) (Math.sqrt((x-vec.x)*(x-vec.x) + (y-vec.y)*(y-vec.y) + (z-vec.z)*(z-vec.z)));
	}
	public static float dist(Vec3d a, Vec3d b)
	{
		return (float) (Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y) + (a.z-b.z)*(a.z-b.z)));
	}
	public static float dist2(Vec3d a, Vec3d b)
	{
		return (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y) + (a.z-b.z)*(a.z-b.z);
	}
	public float dotProduct(Vec3d vec)
	{
		return x*vec.x + y*vec.y + z*vec.z;
	}
	public static float dotProduct(Vec3d a, Vec3d b)
	{
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}
	public Vec3d crossProduct(Vec3d vec)
	{
		System.out.println(" = <" + y + "*" + vec.z + " - " + z + "*" + vec.y + ", " + z + "*" + vec.x + " - " + x + "*" + vec.z + ", " + 
				x + "*" + vec.y + " - " + y + "*" + vec.x + ">");
		Vec3d cross = new Vec3d(
				y*vec.z - z*vec.y,
				z*vec.x - x*vec.z,
				x*vec.y - y*vec.x
				);
		return cross;
	}
	public static Vec3d crossProduct(Vec3d a, Vec3d b)
	{
		Vec3d cross = new Vec3d(
				a.y*b.z - a.z*b.y,
				a.z*b.x - a.x*b.z,
				a.x*b.y - a.y*b.x
				);
		return cross;
	}
	public float normalize()
	{
		float length = length();
		x /= length;
		y /= length;
		z /= length;
		return length;
	}
	public static Vec3d normalize(Vec3d vec)
	{
		float length = vec.length();
		return new Vec3d(vec.x / length, vec.y / length, vec.z / length);
	}
	public static Vec3d avg(Vec3d... vecs)
	{
		Vec3d avg = new Vec3d(0, 0, 0);
		for(int i = 0; i < vecs.length; i++)
			avg.add(vecs[i]);
		avg.mult(1f/vecs.length);
		return avg;
	}
	/**
	 * 
	 * @param a - the first vector
	 * @param b - the second vector
	 * @param dist - a value between 0 and 1 which represents the distance from a to b
	 * @returns the interpolated value between the two vectors
	 */
	public static Vec3d lerp(Vec3d a, Vec3d b, float dist)
	{
		Vec3d lerped = new Vec3d(a.x + (b.x-a.x)*dist, a.y + (b.y-a.y)*dist, a.z + (b.z-a.z)*dist);
		return lerped;
	}
	public static float areaOfTriangle(Vec3d a, Vec3d b, Vec3d c)
	{
		Vec3d cross = new Vec3d(
				(b.y - a.y)*(c.z - a.z) - (b.z - a.z)*(c.y - a.y),
				(b.z - a.z)*(c.x - a.x) - (b.x - a.x)*(c.z - a.z),
				(b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x)
				);
		return cross.length() / 2f;
	}
	public static void main(String[] args) {
		Vec3d a = new Vec3d(1, 1, 0);
		Vec3d b = new Vec3d(1, 0, 1);
		System.out.println(normalize(Vec3d.crossProduct(a, b)));
	}
}
