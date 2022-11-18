package math;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import geometry.Circle;
import geometry.Polygon2d;
import geometry.Shape2d;

/**
 * a Cartesian plane that has a transform in scale, rotation, and translation. 
 * Defined by an origin vector, a vector in the x direction, and a vector in the y direction. 
 * TIP: You can stack Transform2ds to make complex structures that cascade
 * @author Mario Velez
 *
 */
public class Transform2d {
	public static final Transform2d ORIGINAL = new Transform2d();
	
	public Vec2d origin;
	/**
	 * Unit vector in the x direction
	 */
	public Vec2d tx;
	/**
	 * Unit vector in the y direction
	 */
	public Vec2d ty;
	
	/**
	 * Creates a Transform2d of
	 * @origin (0, 0)
	 * @dimensions x(1, 0) and y(0, 1)
	 */
	public Transform2d()
	{
		origin = new Vec2d(0f, 0f);
		tx = new Vec2d(1f, 0f);
		ty = new Vec2d(0f, 1f);
	}
	/**
	 * This constructor stores a reference to each Vec2d
	 * @param origin - origin
	 * @param tx - x direction
	 * @param ty - y direction
	 */
	public Transform2d(Vec2d origin, Vec2d tx, Vec2d ty)
	{
		this.origin = origin;
		this.tx = tx;
		this.ty = ty;
	}
	/**
	 * projects b onto a (order does matter)
	 * @param a - the original transform
	 * @param b - the transform to be projected
	 * @returns - the product of the 2 transforms
	 */
	public static Transform2d add(Transform2d a, Transform2d b)
	{
		Transform2d c = new Transform2d(a.projectToTransform(b.origin),
										a.projectToTransform(b.tx),
										a.projectToTransform(b.ty));
		return c;
	}
	public String toString()
	{
		return "origin: "+origin + ", tx: "+tx + ", ty: "+ty;
	}
	/**
	 * makes a new transform with the same parameters but none of the same references
	 */
	public Transform2d clone()
	{
		Transform2d tf2d = new Transform2d();
		tf2d.origin = new Vec2d(origin);
		tf2d.tx = new Vec2d(tx);
		tf2d.ty = new Vec2d(ty);
		return tf2d;
	}
	/**
	 * Takes a vector and returns its coordinates projected from this to Transform2d.ORIGINAL
	 * @param vec - the vector
	 * @return a vector transformed by this definition
	 */
	public Vec2d projectToTransform(Vec2d vec)
	{
		Vec2d point = new Vec2d(0, 0);
		point.add(Vec2d.mult(tx, vec.x));
		point.add(Vec2d.mult(ty, vec.y));
		point.add(origin);
		return point;
	}
	/**
	 * Projects but without shifting by the origin
	 * @param vec - the vector
	 * @returns a vector transformed by a linear transformation defined by this.tx and this.ty
	 */
	public Vec2d pTo(Vec2d vec)
	{
		Vec2d point = new Vec2d(0, 0);
		point.add(Vec2d.mult(tx, vec.x));
		point.add(Vec2d.mult(ty, vec.y));
		return point;
	}
	public Shape2d projectToTransform(Shape2d shape)
	{
		if(shape.getClass().equals(Polygon2d.class))
		{
			Polygon2d polygon = (Polygon2d) shape;
			Vec2d[] vecs = new Vec2d[polygon.vertices.length];
			for(int i = 0; i < vecs.length; i++)
				vecs[i] = projectToTransform(polygon.vertices[i]);
			return new Polygon2d(vecs);
		}
		else
		{
			Circle circle = (Circle) shape;
			return new Circle(projectToTransform(circle.pos), tx.length() * circle.radius);
		}
	}
	/**
	 * Takes a transform and returns a transform of transform * this
	 * @param transform - the transform to project
	 * @returns the resulting transform of the transform on top of this
	 */
	public Transform2d projectToTransform(Transform2d transform)
	{
		return new Transform2d(projectToTransform(transform.origin),
							   pTo(transform.tx),
							   pTo(transform.ty));
	}
	/**
	 * Takes a vector and returns its coordinates projected from Transform2d.ORIGINAL to this
	 * @param vec - the transformed vector
	 * @return the vector on this transform
	 */
	public Vec2d projectFromTransform(Vec2d vec)
	{
		Vec2d point = new Vec2d(Vec2d.subtract(vec, origin));
		Vec2d txn = new Vec2d(tx);
		txn.normalize2();
		Vec2d tyn = new Vec2d(ty);
		tyn.normalize2();
		float x = Vec2d.dotProduct(txn, point);
		float y = Vec2d.dotProduct(tyn, point);
		return new Vec2d(x, y);
	}
	public Shape2d projectFromTransform(Shape2d shape)
	{
		if(shape.getClass().equals(Polygon2d.class))
		{
			Polygon2d polygon = (Polygon2d) shape;
			Vec2d[] vecs = new Vec2d[polygon.vertices.length];
			for(int i = 0; i < vecs.length; i++)
				vecs[i] = projectFromTransform(polygon.vertices[i]);
			return new Polygon2d(vecs);
		}
		return null;
	}
	/**
	 * @param scale - tx is multiplied by scale.x and ty is multiplied by scale.y
	 * @param anchor - the anchor point does not change when scaled
	 */
	public void scale(Vec2d scale, Vec2d anchor)
	{
		Vec2d original = projectToTransform(anchor);
		tx.mult(scale.x);
		ty.mult(scale.y);
		Vec2d changed = projectToTransform(anchor);
		origin.subtract(Vec2d.subtract(changed, original));
	}
	/**
	 * Rotates this by the specified parameters
	 * @param angle - measured in radians
	 * @param anchor - the anchor point does not change when rotated
	 */
	public void rotate(float angle, Vec2d anchor)
	{
		Vec2d original = projectToTransform(anchor);
		
		float lengthx = tx.length();
		float lengthy = ty.length();
		double rx = Math.atan2(tx.y, tx.x);
		double ry = Math.atan2(ty.y, ty.x);
		rx += angle;
		ry += angle;
		tx.set((float) (Math.cos(rx)*lengthx), (float) (Math.sin(rx)*lengthx));
		ty.set((float) (Math.cos(ry)*lengthy), (float) (Math.sin(ry)*lengthy));
		
		Vec2d changed = projectToTransform(anchor);
		origin.subtract(Vec2d.subtract(changed, original));
	}
	/**
	 * does not alter origin
	 */
	public void setIdentity()
	{
		tx.x = tx.x > 0? 1 : -1;
		tx.y = 0;
		ty.x = 0;
		ty.y = 1;
	}
	/**
	 * sets this to a rotation
	 * @param angle - the angle of the rotation
	 * @param scale_x - how much to scale tx
	 * @param scale_y - how much to scale ty
	 */
	public void setRotationInstance(float angle, float scale_x, float scale_y)
	{
//		float sin = (float) Math.sin(angle);
//		float cos = (float) Math.cos(angle);
//		
//		if(tx.x > 0)
//			tx.x = cos * scale_x;
//		else
//			tx.x = -cos * scale_x;
//		tx.y = sin * scale_x;
//		ty.x = -sin * scale_y;
//		ty.y = cos * scale_y;
		setIdentity();
		if(Vec2d.dotProduct(tx, ty.rightNormal()) < 0)
			rotate(angle, Vec2d.ZERO);
		else
			rotate(-angle, Vec2d.ZERO);
		tx.mult(scale_x);
		ty.mult(scale_y);
	}
	private static AffineTransform atx = AffineTransform.getTranslateInstance(0, 0);
	/**
	 * Draws an image with this transform in a specified position
	 * @param g2 - graphics to draw on
	 * @param img - image to draw
	 * @param position - position of the image on this transform
	 * @param dimensions - dimensions to draw the image
	 */
	public void drawImage(Graphics2D g2, BufferedImage img, Vec2d position, Vec2d dimensions)
	{
		dimensions.x /= img.getWidth();
		dimensions.y /= img.getHeight();
		Vec2d pos = projectToTransform(position);
		Vec2d vx = projectToTransform(new Vec2d(position.x - dimensions.x, position.y));
		Vec2d vy = projectToTransform(new Vec2d(position.x, position.y - dimensions.y));
				
		atx.setTransform(vx.x - pos.x, vx.y - pos.y, vy.x - pos.x, vy.y - pos.y, pos.x, pos.y);
		g2.drawImage(img, atx, null);
	}
	/**
	 * Draws an image with this transformation
	 * @param g2 - graphics to draw on
	 * @param img - image to draw
	 * @param center - center of the image relative to this transform
	 * @param dim - dimensions of the image relative to this transform
	 */
//	public void drawImage(Graphics2D g2, BufferedImage img, Vec2d center, Vec2d dim)
//	{
//		Vec2d pos = projectToTransform(center);
//		double r1 = dim.x * tx.length() / img.getWidth();
//		double r2 = dim.y * ty.length() / img.getHeight();
//		if(Vec2d.dotProduct(tx, ty.rightNormal()) > 0)
//			r2 *= -1;
//		
//		AffineTransform tf = AffineTransform.getTranslateInstance(pos.x - (r1 * img.getWidth() / 2), pos.y - (r2 * img.getHeight() / 2));
//		tf.rotate(-tx.x, -tx.y, r1 * img.getWidth() / 2, r2 * img.getHeight() / 2);
//		tf.scale(r1, r2);
//		
//		g2.drawImage(img, tf, null);
//	}
	
}
