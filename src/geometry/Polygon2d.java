package geometry;

import java.awt.Color;
import java.awt.Graphics2D;

import debug.DebugDrawInfo;
import math.Transform;
import math.Transform2d;
import math.Vec2d;
import math.Vec3d;
import physics.AABB;

public class Polygon2d extends Shape2d {
	public Vec3d[] vertices;
	public Vec3d[][] edges;
	
	public Polygon2d(Vec2d... vertices)
	{
		if(vertices.length == 0)
			return;
		this.vertices = new Vec3d[vertices.length];
		for(int i = 0; i < vertices.length; ++i)
			this.vertices[i] = new Vec3d(vertices[i].x, vertices[i].y, 1);
		edges = new Vec3d[vertices.length][2];
		for(int i = 1; i < vertices.length; i++)
		{
			edges[i][0] = this.vertices[i-1];
			edges[i][1] = this.vertices[i];
		}
		edges[0][0] = this.vertices[vertices.length-1];
		edges[0][1] = this.vertices[0];
		computeArea();
		//System.out.println("area: " + area);
	}
	public void debugDraw(Graphics2D g2, boolean fill)
	{
		int[] px = new int[vertices.length];
		for(int i = 0; i < px.length; i++)
			px[i] = (int) vertices[i].x;
		
		int[] py = new int[vertices.length];
		for(int i = 0; i < py.length; i++)
			py[i] = (int) vertices[i].y;
		
		
		if(fill)
		{
//			g2.setColor(new Color(255, 127, 0, 100));
			g2.setColor(Color.WHITE);
			g2.fillPolygon(px, py, vertices.length);
		}
		g2.setColor(Color.BLACK);
		g2.drawPolygon(px, py, vertices.length);
		for(int i = 0; i < vertices.length; i++)
			g2.drawString("" + i, px[i], py[i]);
	}
	public void debugDraw(Graphics2D g2, boolean fill, Color color)
	{
		int[] px = new int[vertices.length];
		for(int i = 0; i < px.length; i++)
			px[i] = (int) vertices[i].x;
		
		int[] py = new int[vertices.length];
		for(int i = 0; i < py.length; i++)
			py[i] = (int) vertices[i].y;
		
		if(fill)
		{
			g2.setColor(color);
			g2.fillPolygon(px, py, vertices.length);
		}
		g2.setColor(Color.BLACK);
		g2.drawPolygon(px, py, vertices.length);
	}
	public void debugDraw(Graphics2D g2, DebugDrawInfo info)
	{
		int[] px = new int[vertices.length];
		for(int i = 0; i < px.length; i++)
			px[i] = (int) vertices[i].x;
		
		int[] py = new int[vertices.length];
		for(int i = 0; i < py.length; i++)
			py[i] = (int) vertices[i].y;
		
		if(info.normal)
		{
			g2.setColor(Color.BLUE);
			for(int i = 0; i < edges.length; ++i)
			{
				
				Vec2d normal = Vec2d.subtract(edges[i][1], edges[i][0]).leftNormal();
				normal.normalize();
				normal.mult(info.normal_len);
				
				Vec2d mid = Vec2d.avg(edges[i][0], edges[i][1]);
				g2.drawLine((int) mid.x, (int) mid.y, (int) (mid.x + normal.x), (int) (mid.y + normal.y));
			}
		}
		
		if(!info.wireframe)
		{
			g2.setColor(info.col);
			g2.fillPolygon(px, py, vertices.length);
		}
		g2.setColor(Color.BLACK);
		g2.drawPolygon(px, py, vertices.length);
	}
	public void debugDraw(Graphics2D g2, Transform2d transform, boolean fill, Color color)
	{
		Vec2d[] v = new Vec2d[vertices.length];
		for(int i = 0; i < v.length; ++i)
			v[i] = transform.projectToTransform(vertices[i]);
		
		if(fill)
		{
			g2.setColor(color);
			g2.fillPolygon(Vec2d.xPoints(v), Vec2d.yPoints(v), vertices.length);
		}
		g2.setColor(Color.BLACK);
		g2.drawPolygon(Vec2d.xPoints(v), Vec2d.yPoints(v), vertices.length);
	}
	public boolean intersects(Vec2d point)
	{
		for(int i = 1; i < vertices.length; i++)
		{
			Vec2d normal = Vec2d.subtract(vertices[i], vertices[i-1]).leftNormal();
			normal.normalize();
			Vec2d rel_point = Vec2d.subtract(point, vertices[i-1]);
			float dist = Vec2d.dotProduct(normal, rel_point);
			if(dist > 0)
				return false;
		}
		Vec2d normal = Vec2d.subtract(vertices[0], vertices[vertices.length-1]).leftNormal();
		Vec2d rel_point = Vec2d.subtract(point, vertices[vertices.length-1]);
		float dist = Vec2d.dotProduct(normal, rel_point);
		if(dist > 0)
			return false;
		return true;
	}
	public boolean intersects(LineSegment ls)
	{
		for(int i = 0; i < edges.length; i++)
			if(ls.intersects(edges[i][0], edges[i][1]))
				return true;
		return false;
	}
	public Vec2d intersection(LineSegment ls)
	{
		Vec2d dir = Vec2d.subtract(ls.b, ls.a);
		float min_dot = Float.POSITIVE_INFINITY;
		Vec2d point = null;
		LineSegment edge = new LineSegment(new Vec2d(), new Vec2d());
		for(int i = 0; i < edges.length; ++i)
		{
			edge.a = edges[i][0];
			edge.b = edges[i][1];
			
			Intersection in = edge.intersection(ls);
			if(in.intersected)
			{
				float dot = Vec2d.dotProduct(Vec2d.subtract(in.point, ls.a), dir);
				if(dot < min_dot)
				{
					min_dot = dot;
					point = in.point;
				}
			}
		}
		return point;
	}
	public float intersection(Vec2d point)
	{
		float min_dist = Float.MAX_VALUE;
		for(int i = 1; i < vertices.length; i++)
		{
			Vec2d normal = Vec2d.subtract(vertices[i], vertices[i-1]).leftNormal();
			normal.normalize();
			Vec2d rel_point = Vec2d.subtract(point, vertices[i-1]);
			float dist = Vec2d.dotProduct(normal, rel_point);
			if(dist > 0)
				return 1;
			if(dist < min_dist)
				min_dist = dist;
		}
		Vec2d normal = Vec2d.subtract(vertices[0], vertices[vertices.length-1]).leftNormal();
		Vec2d rel_point = Vec2d.subtract(point, vertices[vertices.length-1]);
		float dist = Vec2d.dotProduct(normal, rel_point);
		if(dist > 0)
			return 1;
		if(dist < min_dist)
			min_dist = dist;
		return min_dist;
	}
	public float computeArea()
	{
	    area = 0;
	    for(int i = 0; i < vertices.length-1; ++i)
	        area += vertices[i].x*vertices[i+1].y + 1 - vertices[i+1].x*vertices[i].y;
	    area +=  vertices[vertices.length-1].x*vertices[0].y + 1 - vertices[0].x*vertices[vertices.length-1].y;
	    return area = Math.abs(area/2f);
	}
	public void computeCentroid(Vec2d centroid)
	{
	    centroid.set(0, 0);
	    for(int i = 0; i < vertices.length-1; ++i)
	    {
	        float xy = vertices[i].x*vertices[i+1].y - vertices[i+1].x*vertices[i].y;
	        centroid.x += (vertices[i].x + vertices[i+1].x) * xy;
	        centroid.y += (vertices[i].y + vertices[i+1].y) * xy;
	    }
	    float xy = vertices[vertices.length-1].x*vertices[0].y - vertices[0].x*vertices[vertices.length-1].y;
	    centroid.x += (vertices[vertices.length-1].x + vertices[0].x) * xy;
	    centroid.y += (vertices[vertices.length-1].y + vertices[0].y) * xy;
	    centroid.mult(1 / (6 * area));
	}
	public float computeInertia(float density)
	{
	    float inertia = 0;
	    for(int i = 0; i < vertices.length-1; i++)
	    {
	    	float ai = vertices[i].x*vertices[i+1].y - vertices[i+1].x*vertices[i].y;
	        inertia += ((vertices[i].x*vertices[i+1].y) + (2*vertices[i].x*vertices[i].y) + (2*vertices[i+1].x*vertices[i+1].y)) * ai;
	    }
	    float ai = vertices[vertices.length-1].x*vertices[0].y - vertices[0].x*vertices[vertices.length-1].y;
        inertia += ((vertices[vertices.length-1].x*vertices[0].y) + (2*vertices[vertices.length-1].x*vertices[vertices.length-1].y) + (2*vertices[0].x*vertices[0].y)) * ai;
	    return Math.abs(inertia * density / 24);
	}
	public float computeInertia(Vec2d center, float density)
	{
		for(int i = 0; i < vertices.length; ++i)
			vertices[i].add(-center.x, -center.y);
		float inertia = 0;
		for(int i = 0; i < vertices.length-1; i++)
		{
			float ai = vertices[i].x*vertices[i+1].y - vertices[i+1].x*vertices[i].y;
			inertia += ((vertices[i].x*vertices[i+1].y) + (2*vertices[i].x*vertices[i].y) + (2*vertices[i+1].x*vertices[i+1].y)) * ai;
		}
		float ai = vertices[vertices.length-1].x*vertices[0].y - vertices[0].x*vertices[vertices.length-1].y;
		inertia += ((vertices[vertices.length-1].x*vertices[0].y) + (2*vertices[vertices.length-1].x*vertices[vertices.length-1].y) + (2*vertices[0].x*vertices[0].y)) * ai;
		for(int i = 0; i < vertices.length; ++i)
			vertices[i].add(center.x, center.y);
		return Math.abs(inertia * density / 24);
	}
	public Vec2d projectedBounds(Vec2d axis, Vec2d pos)
	{
		float[] p = new float[vertices.length];
		for(int i = 0; i < p.length; i++)
		{
			Vec2d dir = Vec2d.subtract(vertices[i], pos);
			p[i] = Vec2d.dotProduct(dir, axis);
		}
		return new Vec2d(min(p), max(p));
	}
	public Shape2d createCopy()
	{
		Vec2d[] v = new Vec2d[vertices.length];
		for(int i = 0; i < v.length; ++i)
			v[i] = new Vec2d(vertices[i]);
		Polygon2d p = new Polygon2d(v);
		return p;
	}
	public void support(Vec2d axis, Vec2d point)
	{
		float max = Vec2d.dotProduct(axis, vertices[0]);
		int ind = 0;
		for(int i = 1; i < vertices.length; ++i)
		{
			float dot = Vec2d.dotProduct(axis, vertices[i]);
			if(dot > max)
			{
				dot = max;
				ind = i;
			}
		}
		point.set(vertices[ind]);
	}
	public void setAABB(AABB aabb)
	{
		aabb.set(vertices[0].x, vertices[0].x, vertices[0].y, vertices[0].y);
		
		for(int i = 1; i < vertices.length; ++i)
		{
			aabb.min_x = Math.min(aabb.min_x, vertices[i].x);
			aabb.max_x = Math.max(aabb.max_x, vertices[i].x);
			
			aabb.min_y = Math.min(aabb.min_y, vertices[i].y);
			aabb.max_y = Math.max(aabb.max_y, vertices[i].y);
		}
	}
	private float min(float... a)
	{
		float min = a[0];
		for(int i = 1; i < a.length; ++i)
			min = Math.min(min, a[i]);
		return min;
	}
	private float max(float... a)
	{
		float min = a[0];
		for(int i = 1; i < a.length; ++i)
			min = Math.max(min, a[i]);
		return min;
	}
	/**
	 * @param point - the point to measure the distance
	 * @return the vector from the border of the polygon to the point
	 */
	public Vec2d distance(Vec2d point, boolean inside)
	{
		boolean outside = false;
		float minV = Float.MAX_VALUE;
		Vec2d minN = new Vec2d(0f, 0f);
		for(int i = 0; i < edges.length; i++) //if closest voronoi is an edge
		{
			Vec2d edge = Vec2d.subtract(edges[i][1], edges[i][0]);
			float len = edge.length();
			Vec2d normal = edge.leftNormal();
			edge.normalize();
			normal.normalize();
			Vec2d apos = Vec2d.subtract(point, edges[i][0]);
			float dist = Vec2d.dotProduct(normal, apos);
			float edist = Vec2d.dotProduct(edge, apos);
			edge.normalize();
			if(Math.abs(dist) < Math.abs(minV) && edist > 0 && edist < len)
			{
				minV = dist;
				minN = normal;
				minN.mult(dist);
			}
			if(dist > 0)
				outside = true;
		}
		for(int i = 0; i < vertices.length; i++) //if closest voronoi is a vertex
		{
			float dist = Vec2d.dist(point, vertices[i]);
			if(outside && dist < Math.abs(minV))
			{
				minV = dist;
				minN = Vec2d.subtract(point, vertices[i]);
				minN.normalize();
				minN.mult(dist);
			}
			
		}
		if(inside)
		{
			if(!outside || minV < 0)
			{
				return minN;
			}
			return new Vec2d(0, 0);
		}
		else if(!outside || minV < 0)
		{
			return new Vec2d(0, 0);
		}
		return minN;
	}
	public Vec2d normal(Vec2d point)
	{
		float min_dot = Float.POSITIVE_INFINITY;
		Vec2d normal = null;
		
		for(int i = 0; i < edges.length; i++) // every axis
		{
			Vec2d axis = Vec2d.subtract(edges[i][1], edges[i][0]).leftNormal();
//			
			float dot = Vec2d.dotProduct(axis, Vec2d.subtract(point, edges[i][0]));
			
			if(Math.abs(dot) < min_dot)
			{
				min_dot = Math.abs(dot);
				normal = axis;
			}
		}
		if(normal != null)
			normal.normalize();
		return normal;
	}
	public void move(Vec2d dist)
	{
		for(int i = 0; i < vertices.length; i++)
			vertices[i].add(dist);
	}
	public void move(float x, float y)
	{
		for(int i = 0; i < vertices.length; i++)
			vertices[i].add(x, y);
	}
	public Shape2d projectTo(Transform2d tf2d)
	{
		Vec2d[] verts = new Vec2d[vertices.length];
		for(int i = 0; i < verts.length; i++)
			verts[i] = tf2d.projectToTransform(vertices[i]);
		return new Polygon2d(verts);
	}
	/**
	 * Creates a polygon as an axis aligned rectangle
	 * @param center - the center of the box
	 * @param dimensions - half the width and half the height of the box
	 * @returns a Polygon2d as a box
	 */
	public static Polygon2d createAsBox(Vec2d center, Vec2d dimensions)
	{
		return new Polygon2d(new Vec2d(center.x - dimensions.x, center.y - dimensions.y),
							 new Vec2d(center.x + dimensions.x, center.y - dimensions.y),
							 new Vec2d(center.x + dimensions.x, center.y + dimensions.y),
							 new Vec2d(center.x - dimensions.x, center.y + dimensions.y));
	}
	public void projectTo(Transform transform, Shape2d shape)
	{
		Polygon2d polygon = (Polygon2d) shape;
		for(int i = 0; i < vertices.length; ++i)
		{
			polygon.vertices[i].set(vertices[i]);
			transform.project3D(polygon.vertices[i]);
		}
		for(int i = 1; i < vertices.length; i++)
		{
			polygon.edges[i][0] = polygon.vertices[i-1];
			polygon.edges[i][1] = polygon.vertices[i];
		}
		polygon.edges[0][0] = polygon.vertices[vertices.length-1];
		polygon.edges[0][1] = polygon.vertices[0];
		
	}

}
