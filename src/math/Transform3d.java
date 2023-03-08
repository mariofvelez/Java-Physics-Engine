package math;

/**
 * A transform in 3D space
 * @author Mario Velez
 *
 */
public class Transform3d {
	public Vec3d origin;
	public Vec3d tx;
	public Vec3d ty;
	public Vec3d tz;
	
	/**
	 * creates a new Transform3d identical to its reference Transform3d
	 */
	public Transform3d()
	{
		origin = new Vec3d();
		tx = new Vec3d(1f, 0f, 0f);
		ty = new Vec3d(0f, 1f, 0f);
		tz = new Vec3d(0f, 0f, 1f);
	}
	public Transform3d(Vec3d origin)
	{
		this.origin = origin;
		tx = new Vec3d(1f, 0f, 0f);
		ty = new Vec3d(0f, 1f, 0f);
		tz = new Vec3d(0f, 0f, 1f);
	}
	public Transform3d(Vec3d origin, Vec3d tx, Vec3d ty, Vec3d tz)
	{
		this.origin = origin;
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
	}
	public static Transform3d add(Transform3d a, Transform3d b)
	{
		Transform3d a1 = new Transform3d(new Vec3d(), a.tx, a.ty, a.tz);
		Transform3d c = new Transform3d(a.toReference(b.origin),
										a1.toReference(b.tx),
										a1.toReference(b.ty),
										a1.toReference(b.tz));
		return c;
	}
	public Vec3d toReference(Vec3d vec)
	{
		Vec3d point = new Vec3d();
		point.add(origin);
		point.add(Vec3d.mult(tx, vec.x));
		point.add(Vec3d.mult(ty, vec.y));
		point.add(Vec3d.mult(tz, vec.z));
		return point;
	}
	public Vec3d toSpace(Vec3d vec)
	{
		Vec3d point = new Vec3d(vec);
		//FIXME - transform the point from the reference to this space
		return point;
	}
	public void rotate(Vec3d normal, float angle)
	{
		//FIXME - rotate all axes along the normal vector
	}
	public void rotate(float rx, float ry, float rz)
	{
		//FIXME - rotate along all axes by rx, ry, and rz
	}

}
