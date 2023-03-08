package geometry;

import java.awt.Color;
import java.awt.Graphics2D;

import debug.DebugDrawInfo;
import math.Transform;
import math.Transform2d;
import math.Vec2d;
import physics.AABB;

public abstract class Shape2d {
	public float area;
	
	/**
	 * draws the shape to the Graphics2D object
	 * @param g2 - the object the shape is drawn on
	 * @param fill - whether or not to fill the object
	 */
	public abstract void debugDraw(Graphics2D g2, boolean fill);
	/**
	 * draws the shape to the Graphics2D object
	 * @param g2 = the graphics to draw on
	 * @param fill - whether or not to fill the shape
	 * @param color - the color to fill
	 */
	public abstract void debugDraw(Graphics2D g2, boolean fill, Color color);
	/**
	 * draws the shape to the Graphics2D object
	 * @param g2 - the graphics to draw on
	 * @param info - information about how the shape should be displayed
	 */
	public abstract void debugDraw(Graphics2D g2, DebugDrawInfo info);
	/**
	 * 
	 * @param point - the point to test
	 * @returns true if the point is inside the shape, false it it's not
	 */
	public abstract boolean intersects(Vec2d point);
	/**
	 * 
	 * @param ls - the ray to test
	 * @returns true if the line segment intersects, false if not
	 */
	public abstract boolean intersects(LineSegment ls);
	/**
	 * Gets the first point of intersection if the line segment were a ray from A to B
	 * @param ls - the line segment to test
	 * @returns the point of intersection, null if no intersection
	 */
	public abstract Vec2d intersection(LineSegment ls);
	/**
	 * finds the bounds of the shape projected on a 1 dimensional axis
	 * @param axis - the axis that is projected on. It is inferred that the axis is normalized
	 * @param pos - the origin of the axis
	 * @returns the bounds of this shape on the axis, x being the lower bounds, and y being the upper bounds
	 */
	public abstract Vec2d projectedBounds(Vec2d axis, Vec2d pos);
	/**
	 * Creates a copy of this shape where no locations in memory are shared
	 * @return
	 */
	public abstract Shape2d createCopy();
	/**
	 * finds the point on the shape in the farthest direction of the axis
	 * @param axis - the axis to test
	 * @param point - adds the vector to this variable
	 */
	public abstract void support(Vec2d axis, Vec2d point);
	/**
	 * sets the aabb to the bounding box of this shape
	 * @param aabb
	 */
	public abstract void setAABB(AABB aabb);
	/**
	 * 
	 * @param point - the point to test the distance from
	 * @param inside - true to test if the point is inside, false if not
	 * @returns the vector from the point to the shape
	 */
	public abstract Vec2d distance(Vec2d point, boolean inside);
	/**
	 * Computes the normal vector of a point on the shape
	 * @param point - any point on the shape
	 * @returns the normal vector (normalized);
	 */
	public abstract Vec2d normal(Vec2d point);
	/**
	 * calculates the area of the shape
	 * @returns the area
	 */
	public abstract float computeArea();
	/**
	 * calculates the centroid of the shape
	 * @param centroid - where to store the centroid
	 */
	public abstract void computeCentroid(Vec2d centroid);
	/**
	 * calculates the rotational inertia of the shape, assuming an equal distribution of density
	 * @param density - the density of the shape
	 * @returns the rotational inertia
	 */
	public abstract float computeInertia(float density);
	/**
	 * calculates the rotational inertia of the shape about an arbitrary axis, assuming an equal distribution of density
	 * @param center - the axis of rotation
	 * @param density - the density of the shape
	 * @returns the rotational inertia
	 */
	public abstract float computeInertia(Vec2d center, float density);
	/**
	 * moves the shape by a certain amount
	 * @param dist - the vector to move it by
	 */
	public abstract void move(Vec2d dist);
	/**
	 * projects the shape on a given transform
	 * @param tf2d - the transform the shape is on
	 * @returns a new shape transformed by this shape
	 */
	public abstract Shape2d projectTo(Transform2d tf2d);
	/**
	 * transforms this shape, does not alter this shape
	 * @param transform - the transform
	 * @param shape - the destination shape
	 */
	public abstract void projectTo(Transform transform, Shape2d shape);
}
