package geometry;

import java.util.ArrayList;

import math.Vec2d;

public abstract class Geometry {
	/**
	 * 
	 * @param a
	 * @param b
	 * @returns the vector to move b outside of a
	 */
	public static Vec2d intersectionNormal(Shape2d a, Shape2d b)
	{
		if(a.getClass().equals(Circle.class))
			if(b.getClass().equals(Circle.class))
				return intersectionNormal((Circle) a, (Circle) b);
			else
				return intersectionNormal((Circle) a, (Polygon2d) b);
		else
			if(b.getClass().equals(Circle.class))
			{
				Vec2d normal =  intersectionNormal((Circle) b, (Polygon2d) a);
				normal.negate();
				return normal;
			}
			else
				return intersectionNormal1((Polygon2d) a, (Polygon2d) b);
	}
	public static Vec2d intersectionNormal(Shape2d a, Shape2d b, Vec2d poi)
	{
		if(a.getClass().equals(Circle.class))
			if(b.getClass().equals(Circle.class))
				return intersectionNormal((Circle) a, (Circle) b, poi);
			else
				return intersectionNormal((Circle) a, (Polygon2d) b, poi);
		else
			if(b.getClass().equals(Circle.class))
			{
				Vec2d normal =  intersectionNormal((Circle) b, (Polygon2d) a, poi);
				normal.negate();
				return normal;
			}
			else
				return intersectionNormal1((Polygon2d) a, (Polygon2d) b, poi);
	}
	public static boolean intersected(Shape2d a, Shape2d b)
	{
		if(a.getClass().equals(Circle.class))
			if(b.getClass().equals(Circle.class))
				return intersected((Circle) a, (Circle) b);
			else
				return intersected((Circle) a, (Polygon2d) b);
		else
			if(b.getClass().equals(Circle.class))
				return intersected((Circle) b, (Polygon2d) a);
			else
				return intersected((Polygon2d) a, (Polygon2d) b);
	}
	/**
	 * 
	 * @param a - the circle
	 * @param b - the other circle
	 * @returns if the circles are in collision
	 */
	public static boolean intersected(Circle a, Circle b)
	{
//		if(Math.abs(a.pos.x - b.pos.x) > a.radius + b.radius)
//			return false;
		float dist_x = b.pos.x - a.pos.x;
		float dist_y = b.pos.y - a.pos.y;
		float r2 = a.radius + b.radius;
		return dist_x*dist_x + dist_y*dist_y < r2*r2;
	}
	/**
	 * 
	 * @param a - the circle
	 * @param b - the other circle
	 * @returns the vector to move b outside of a
	 */
	public static Vec2d intersectionNormal(Circle a, Circle b)
	{
		float dist_x = b.pos.x - a.pos.x;
		float dist_y = b.pos.y - a.pos.y;
		Vec2d normal = new Vec2d(dist_x, dist_y);
		normal.normalize();
		float dist2 = dist_x*dist_x + dist_y*dist_y;
		float r2 = a.radius + b.radius;
		if(dist2 < r2*r2 && !a.pos.equals(b.pos))
		{
			float len = a.radius+b.radius - (float) (Math.sqrt(dist2));
			normal.mult(len);
			return normal;
		}
		return new Vec2d(0f, 0f);
			
	}
	public static Vec2d intersectionNormal(Circle a, Circle b, Vec2d poi)
	{
		float dist_x = b.pos.x - a.pos.x;
		float dist_y = b.pos.y - a.pos.y;
		Vec2d normal = new Vec2d(dist_x, dist_y);
		normal.normalize();
		float dist2 = dist_x*dist_x + dist_y*dist_y;
		float r2 = a.radius + b.radius;
		if(dist2 < r2*r2 && !a.pos.equals(b.pos))
		{
			poi.set(a.pos.x + normal.x * a.radius, a.pos.y + normal.y * a.radius);
			Vec2d other = new Vec2d(b.pos.x - normal.x * b.radius, b.pos.y - normal.y * b.radius);
			poi.set(Vec2d.avg(poi, other));
			
			float len = a.radius+b.radius - (float) (Math.sqrt(dist2));
			normal.mult(len);
			return normal;
		}
		return new Vec2d(0f, 0f);
			
	}
	/**
	 * 
	 * @param a - the circle
	 * @param b - the polygon
	 * @returns if the circle and polygon are in collision
	 */
	public static boolean intersected(Circle a, Polygon2d b)
	{
		boolean outside = false;
		float minV = Float.MAX_VALUE;
		Vec2d minN = new Vec2d(0f, 0f);
		for(int i = 0; i < b.edges.length; i++) //if closest voronoi is an edge
		{
			Vec2d edge = Vec2d.subtract(b.edges[i][1], b.edges[i][0]);
			float len = edge.length();
			Vec2d normal = edge.leftNormal();
			edge.normalize();
			normal.normalize();
			Vec2d apos = Vec2d.subtract(a.pos, b.edges[i][0]);
			float dist = Vec2d.dotProduct(normal, apos);
			float edist = Vec2d.dotProduct(edge, apos);
			edge.normalize();
			if(Math.abs(dist) < Math.abs(minV) && edist > 0 && edist < len)
			{
				minV = dist;
				minN = normal;
				minN.mult(-dist+a.radius);
			}
			if(dist > 0)
				outside = true;
		}
		for(int i = 0; i < b.vertices.length; i++) //if closest voronoi is a vertex
		{
			float dist = Vec2d.dist(a.pos, b.vertices[i]);
			if(outside && dist < Math.abs(minV))
			{
				minV = dist;
				minN = Vec2d.subtract(a.pos, b.vertices[i]);
				minN.normalize();
				minN.mult(-dist+a.radius);
			}
			
		}
		
		if(!outside || minV < a.radius)
		{
//			a.pos.add(Vec2d.mult(minN, 0.5f));
//			for(int i = 0; i < b.vertices.length; i++)
//				b.vertices[i].subtract(Vec2d.mult(minN, 0.5f));
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @param a - the circle
	 * @param b - the polygon
	 * @returns the vector to move b outside of a
	 */
	public static Vec2d intersectionNormal(Circle a, Polygon2d b)
	{
		boolean outside = false;
		float minV = Float.MAX_VALUE;
		Vec2d minN = new Vec2d(0f, 0f);
		for(int i = 0; i < b.edges.length; i++) //if closest voronoi is an edge
		{
			Vec2d edge = Vec2d.subtract(b.edges[i][1], b.edges[i][0]);
			float len = edge.length();
			Vec2d normal = edge.leftNormal();
			edge.normalize();
			normal.normalize();
			Vec2d apos = Vec2d.subtract(a.pos, b.edges[i][0]);
			float dist = Vec2d.dotProduct(normal, apos);
			float edist = Vec2d.dotProduct(edge, apos);
			edge.normalize();
			if(Math.abs(dist) < Math.abs(minV) && edist > 0 && edist < len)
			{
				minV = dist;
				minN = normal;
				minN.mult(-dist+a.radius);
			}
			if(dist > 0)
				outside = true;
		}
		for(int i = 0; i < b.vertices.length; i++) //if closest voronoi is a vertex
		{
			float dist = Vec2d.dist(a.pos, b.vertices[i]);
			if(outside && dist < Math.abs(minV))
			{
				minV = dist;
				minN = Vec2d.subtract(a.pos, b.vertices[i]);
				minN.normalize();
				minN.mult(-dist+a.radius);
			}
			
		}
		
		if(!outside || minV < a.radius)
		{
			minN.negate();
			return minN;
		}
		return new Vec2d(0f, 0f);
	}
	public static Vec2d intersectionNormal(Circle a, Polygon2d b, Vec2d poi)
	{
		boolean outside = false;
		float minV = Float.MAX_VALUE;
		Vec2d minN = new Vec2d(0f, 0f);
		for(int i = 0; i < b.edges.length; i++) //if closest voronoi is an edge
		{
			Vec2d edge = Vec2d.subtract(b.edges[i][1], b.edges[i][0]);
			float len = edge.length();
			Vec2d normal = edge.leftNormal();
			edge.normalize();
			normal.normalize();
			Vec2d apos = Vec2d.subtract(a.pos, b.edges[i][0]);
			float dist = Vec2d.dotProduct(normal, apos);
			float edist = Vec2d.dotProduct(edge, apos);
			edge.normalize();
			if(Math.abs(dist) < Math.abs(minV) && edist > 0 && edist < len)
			{
				minV = dist;
				minN = normal;
				minN.mult(-dist+a.radius);
			}
			if(dist > 0)
				outside = true;
		}
		for(int i = 0; i < b.vertices.length; i++) //if closest voronoi is a vertex
		{
			float dist = Vec2d.dist(a.pos, b.vertices[i]);
			if(outside && dist < Math.abs(minV))
			{
				minV = dist;
				minN = Vec2d.subtract(a.pos, b.vertices[i]);
				minN.normalize();
				minN.mult(-dist+a.radius);
				
			}
			
		}
		
		if(!outside || minV < a.radius)
		{
			minN.negate();
			Vec2d dtorad = new Vec2d(minN);
			dtorad.normalize();
			dtorad.mult(a.radius);
			poi.set(a.pos);
			poi.subtract(minN);
			poi.add(dtorad);
			return minN;
		}
		return new Vec2d(0, 0);
	}
	/**
	 * 
	 * @param a - the polygon
	 * @param b - the other polygon
	 * @returns if the polygons are in collision
	 */
	public static boolean intersected(Polygon2d a, Polygon2d b)
	{
		for(int i = 0; i < b.vertices.length; i++)
			if(a.intersects(b.vertices[i]))
				return true;
		for(int i = 0; i < a.vertices.length; i++)
			if(b.intersects(a.vertices[i]))
				return true;
		return false;
	}
	public static Vec2d intersectionNormal(Polygon2d a, Polygon2d b)
	{
		float max_dist = 0;
		Vec2d max_dist_vector = new Vec2d(0, 0);
		for(int i = 0; i < b.vertices.length; i++)
		{
			Vec2d normal = a.distance(b.vertices[i], true);
			float dist = normal.length();
			if(dist >= max_dist)
			{
				max_dist = dist;
				normal.negate();
				max_dist_vector = normal;
			}
		}
		for(int i = 0; i < a.vertices.length; i++)
		{
			Vec2d normal = b.distance(a.vertices[i], true);
			float dist = normal.length();
			if(dist >= max_dist)
			{
				max_dist = dist;
				max_dist_vector = normal;
			}
		}
		return max_dist_vector;
	}
	public static Vec2d intersectionNormal2(Polygon2d a, Polygon2d b)
	{
		float min_dist = Float.MAX_VALUE;
		Vec2d min_vec = new Vec2d(0, 0);
		for(int i = 0; i < a.edges.length; ++i)
		{
			Vec2d norm = Vec2d.subtract(a.edges[i][1], a.edges[i][0]).leftNormal();
			norm.normalize();
			for(int j = 0; j < b.vertices.length; ++j)
			{
				Vec2d dist = Vec2d.subtract(b.vertices[j], a.edges[i][0]);
				float d = norm.dotProduct(dist);
				if(d > 0) // do not intersect
				{
					return new Vec2d(0, 0);
				}
				else if(d < min_dist)
				{
					min_dist = d;
					min_vec = dist;
					min_vec.negate();
				}
			}
		}
		for(int i = 0; i < b.edges.length; ++i)
		{
			Vec2d norm = Vec2d.subtract(b.edges[i][1], b.edges[i][0]).leftNormal();
			norm.normalize();
			for(int j = 0; j < a.vertices.length; ++j)
			{
				Vec2d dist = Vec2d.subtract(a.vertices[j], b.edges[i][0]);
				float d = norm.dotProduct(dist);
				if(d > 0)
					return new Vec2d(0, 0);
				else if(d < min_dist)
				{
					min_dist = d;
					min_vec = dist;
				}
			}
		}
		return min_vec;
	}
	/**
	 * 
	 * @param a - the polygon
	 * @param b - the other polygon
	 * @returns the vector to move b outside of a
	 */
	public static Vec2d intersectionNormal1(Polygon2d a, Polygon2d b)
	{
		if(!intersected(a, b))
			return Vec2d.ZERO;
		
		Polygon2d shape_a = a;
		Polygon2d shape_b = b;
		
		float overlap = Float.POSITIVE_INFINITY;
		Vec2d overlap_vec = new Vec2d(0, 0);
		
		for(int shape = 0; shape < 2; ++shape)
		{
			if(shape==1)
			{
				shape_a = b;
				shape_b = a;
			}
			for(int i = 0; i < shape_a.edges.length; i++) // every axis
			{
				Vec2d axis = Vec2d.subtract(shape_a.edges[i][1], shape_a.edges[i][0]).leftNormal();
//				float len = axis.length();
				axis.normalize();
				
				float min1 = axis.dotProduct(Vec2d.subtract(shape_a.vertices[0], shape_a.edges[i][0]));
				float max1 = min1;
				for(int j = 1; j < shape_a.vertices.length; j++)
				{
					float dp = axis.dotProduct(Vec2d.subtract(shape_a.vertices[j], shape_a.edges[i][0]));
					min1 = Math.min(min1, dp);
					max1 = Math.max(max1, dp);
				}
				float min2 = axis.dotProduct(Vec2d.subtract(shape_b.vertices[0], shape_a.edges[i][0]));
				float max2 = min2;
				for(int j = 1; j < shape_b.vertices.length; j++)
				{
					float dp = axis.dotProduct(Vec2d.subtract(shape_b.vertices[j], shape_a.edges[i][0]));
					min2 = Math.min(min2, dp);
					max2 = Math.max(max2, dp);
				}
				float curr_overlap = max1-min2;//Math.min(max1, max2)-Math.max(min1, min2);
				if(curr_overlap < overlap)
				{
					overlap = curr_overlap;
					overlap_vec = axis;
					if(shape==1)
						overlap_vec.negate();
				}
//				if(max2-len < min1 || max1 < min2-len)
//					return Vec2d.ZERO;
			}
		}
		overlap_vec.normalize();
		overlap_vec.mult(overlap);
//		a.move(overlap_vec);
		return overlap_vec;
	}
	public static Vec2d intersectionNormal1(Polygon2d a, Polygon2d b, Vec2d poi)
	{
		if(!intersected(a, b))
			return Vec2d.ZERO;
		
		Polygon2d shape_a = a;
		Polygon2d shape_b = b;
		
		float overlap = Float.POSITIVE_INFINITY;
		Vec2d overlap_vec = new Vec2d(0, 0);
		
		for(int shape = 0; shape < 2; ++shape)
		{
			if(shape==1)
			{
				shape_a = b;
				shape_b = a;
			}
			for(int i = 0; i < shape_a.edges.length; i++) // every axis
			{
				Vec2d axis = Vec2d.subtract(shape_a.edges[i][1], shape_a.edges[i][0]).leftNormal();
//				float len = axis.length();
				axis.normalize();
				
				float max1 = axis.dotProduct(Vec2d.subtract(shape_a.vertices[0], shape_a.edges[i][0]));
				int intersect_index = 0;
				for(int j = 1; j < shape_a.vertices.length; j++)
				{
					float dp = axis.dotProduct(Vec2d.subtract(shape_a.vertices[j], shape_a.edges[i][0]));
					max1 = Math.max(max1, dp);
				}
				
				float min2 = axis.dotProduct(Vec2d.subtract(shape_b.vertices[0], shape_a.edges[i][0]));
				for(int j = 1; j < shape_b.vertices.length; j++)
				{
					float dp = axis.dotProduct(Vec2d.subtract(shape_b.vertices[j], shape_a.edges[i][0]));
					if(dp < min2)
					{
						min2 = dp;
						intersect_index = j;
					}
					min2 = Math.min(min2, dp);
				}
				float curr_overlap = max1-min2;//Math.min(max1, max2)-Math.max(min1, min2);
				if(curr_overlap < overlap)
				{
					overlap = curr_overlap;
					overlap_vec = axis;
					poi.set(shape_b.vertices[intersect_index]);
					if(shape==1)
						overlap_vec.negate();
				}
//				if(max2-len < min1 || max1 < min2-len)
//					return Vec2d.ZERO;
			}
		}
		overlap_vec.normalize();
		overlap_vec.mult(overlap);
//		a.move(overlap_vec);
		return overlap_vec;
	}
	/**
	 * 
	 * @param a - the circle
	 * @param b - the other circle
	 * @returns the point(s) that the circles intersect at
	 */
	public Vec2d[] intersection(Circle a, Circle b)
	{
		//FIXME - finish the code
		/*
		 * 1: find the distance between the centers and call it d
		 * 2: if d is greater than both the radii, return an empty set
		 * 3: if d + the radius of the smaller one is less than the larger one, the smaller one is inside the larger one
		 * 	  return an empty set
		 * 4: if d is double the radius, interpolate between the centers 
		 */
		return new Vec2d[0];
	}
	/**
	 * 
	 * @param a - the circle
	 * @param b - the polygon
	 * @returns the point(s) that the circle and polygon intersect at
	 */
	public Vec2d[] intersection(Circle a, Polygon2d b)
	{
		//FIXME - finish the code
		/*
		 * 1: test all points of b for intersection with a
		 * 2: test all edges of b for intersection with a
		 */
		return new Vec2d[0];
	}
//	public Vec2d[] intersection(Polygon2d a, Polygon2d b)
//	{
//		int intersections = 0;
//		for(int i = 0; i < a.edges.length; i++)
//		{
//			for(int j = 0; j < b.edges.length; j++)
//			{
//				Vec2d poi = new Vec2d(0, 0);
//				
//				float bxax = b.edges[j][1].x - b.edges[j][0].x;
//				float byay = b.edges[j][1].y - b.edges[j][0].y;
//				float dycy = a.edges[i][1].x - a.edges[i][0].x;
//				float dxcx = a.edges[i][1].x - a.edges[i][0].x;
//				
//				float denom = dycy*bxax - dxcx*byay;
//				if(denom == 0)
//					continue;
//				
//				float axcx = b.edges[j][0].x - a.edges[i][0].x;
//				float aycy = b.edges[j][0].y - a.edges[i][0].x;
//			}
//		}
//		return new Vec2d[0];
//	}
	public static Vec2d circumcenter(Vec2d a, Vec2d b, Vec2d c)
	{
		Vec2d v1 = Vec2d.subtract(b, a);
		Vec2d v2 = Vec2d.subtract(c, a);
		float P = v1.length2();
		float Q = v2.length2();
		float R = v1.crossProduct(v2);
		return new Vec2d((v2.y*P - v1.y*Q) / (R*2) + a.x, (v2.x*P - v1.x*Q) / (R*-2) + a.y);
	}
	private static int i = 0;
	private static Vec2d[] box = {
			new Vec2d(0, 0),
			new Vec2d(1, 0),
			new Vec2d(1, 1),
			new Vec2d(0, 1)
	};
	/**
	 * top left at (0, 0)
	 * @param r - side length of cell
	 * @param values - Must be in clockwise order, starting from top left
	 * @returns a polygon in the shape of the cell
	 */
	public static Polygon2d squareMarch(float r, float... values)
	{
		i = 0;
		ArrayList<Vec2d> points = new ArrayList<>();
		for(int i = 0; i < values.length; ++i)
		{
			Vec2d a = Vec2d.mult(box[i], r);
			Vec2d b = Vec2d.mult(box[(i+1) % box.length], r);
			
			float fa = values[i];
			float fb = values[(i+1) % values.length];
			if(fa > 0.5f)
				points.add(a);
			
			if((fa - 0.5f) * (fb - 0.5f) < 0) // one above and one below 0.5
			{
				float da = Math.abs(fa - 0.5f);
				float db = Math.abs(fb - 0.5f);
				float total = da + db;
				Vec2d dir = Vec2d.subtract(a, b);
				dir.mult(da / total);
				points.add(Vec2d.subtract(a, dir));
			}
			
		}
		Vec2d[] verts = new Vec2d[points.size()];
		points.forEach((e) -> {verts[i++] = e;});
		if(verts.length == 0)
			return null;
		return new Polygon2d(verts);
	}
	public static void solveCollision(float m1, float m2, Vec2d v1, Vec2d v2, Vec2d normal)
	{
		normal.normalize();
		Vec2d lat = normal.leftNormal();
		
		float m1m2 = m1 + m2;
		
		float u1 = v1.dotProduct(normal);
		float u2 = v2.dotProduct(normal);
		float l1 = v1.dotProduct(lat);
		float l2 = v2.dotProduct(lat);
		
		float v_1 = ((m1 - m2) / m1m2)*u1 + ((m2*2) / m1m2)*u2;
		float v_2 = ((m1*2) / m1m2)*u1 + ((m2 - m1) / m1m2)*u2;
		
		v1.set(Vec2d.add(Vec2d.mult(normal, v_1), Vec2d.mult(lat, l1)));
		v2.set(Vec2d.add(Vec2d.mult(normal, v_2), Vec2d.mult(lat, l2)));
	}
//	public static Polygon2d[] voronoiDiagram(int width, int height, Vec2d... S)
//	{
//		ArrayList<LineSegment> E = new ArrayList<>();
//		ArrayList<Cell> C = new ArrayList<>();
//		
//		Cell c1 = new Cell(new Vec2d(-width, -height));
//		Cell c2 = new Cell(new Vec2d(2*width, -height));
//		Cell c3 = new Cell(new Vec2d(2*width, 2*height));
//		Cell c4 = new Cell(new Vec2d(-width, 2*height));
//		
//		C.add(c1);
//		C.add(c2);
//		C.add(c3);
//		C.add(c4);
//		
//		c1.addVertex(new Vec2d(width/2, height/2));
//		c1.addVertex(new Vec2d(width/2, -10*height));
//		c1.addVertex(new Vec2d(-10*width, height/2));
//		
//		c2.addVertex(new Vec2d(width/2, height/2));
//		c2.addVertex(new Vec2d(width/2, -10*height));
//		c2.addVertex(new Vec2d(10*width, height/2));
//		
//		c3.addVertex(new Vec2d(width/2, height/2));
//		c3.addVertex(new Vec2d(width/2, 10*height));
//		c3.addVertex(new Vec2d(10*width, height/2));
//		
//		c4.addVertex(new Vec2d(width/2, height/2));
//		c4.addVertex(new Vec2d(width/2, 10*height));
//		c4.addVertex(new Vec2d(-10*width, height/2));
//		
//		for(int i = 0; i < S.length; ++i)
//		{
//			Cell cell = new Cell(S[i]);
//			
//			for(int j = 0; j < C.size(); ++j)
//			{
//				Vec2d midpoint = Vec2d.avg(cell.site, C.get(j).site);
//				
//				for(int e = 0; e < cell.vertices.size(); ++e)
//				{
//					Vec2d a = cell.vertices.get(e);
//					Vec2d b = cell.vertices.get((e+1) % cell.vertices.size());
//				}
//			}
//		}
//		
//		return null;
//	}
	
}