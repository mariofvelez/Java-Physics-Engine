package geometry;

import math.Vec2d;

public final class Clip {
	
	private static VertexPolygon p_a;
	private static VertexPolygon p_b;
	
	public static Polygon2d intersection(Polygon2d a, Polygon2d b)
	{
		//PHASE 1
		p_a = new VertexPolygon(a);
		p_b = new VertexPolygon(a);
		for(int i = 0; i < a.vertices.length; ++i)
		{
			for(int j = 0; j < b.vertices.length; ++j)
			{
				Vertex intersect = intersect(a.vertices[i],
											 a.vertices[(i+1) % a.vertices.length],
											 b.vertices[j],
											 b.vertices[(j+1) % b.vertices.length]);
				if(intersect != null)
				{
					Vertex I1 = new Vertex(intersect);
					p_a.insertVertexAfter(i, I1);
					
					Vertex I2 = new Vertex(intersect);
					p_b.insertVertexAfter(j, I2);
					
					I1.neighbor = I2; //link neighboring vertices
					I2.neighbor = I1;
					
					I1.intersect = true;
					I2.intersect = true;
				}
			}
		}
		
		//PHASE 2
		boolean status = false;
		if(b.intersects(a.vertices[0]))
			status = true;
		
		for(int i = 0; i < p_a.vertices.length; ++i)
		{
			if(p_a.vertices[i].intersect)
			{
				p_a.vertices[i].entry_exit = status;
				status = !status;
			}
		}
		
		status = false;
		if(a.intersects(b.vertices[0]))
			status = true;
		
		for(int i = 0; i < p_b.vertices.length; ++i)
		{
			if(p_b.vertices[i].intersect)
			{
				p_b.vertices[i].entry_exit = status;
				status = !status;
			}
		}
		
		//PHASE 3
		Vertex current = p_a.vertices[0];
		while(!current.intersect)
			current = current.next;
		
		return null;
	}
	public static Polygon2d difference(Polygon2d a, Polygon2d b)
	{
		return null;
	}
	public static Polygon2d union(Polygon2d a, Polygon2d b)
	{
		return null;
	}
	public static Polygon2d xor(Polygon2d a, Polygon2d b)
	{
		return null;
	}
	private static Vertex intersect(Vec2d a, Vec2d b, Vec2d c, Vec2d d)
	{
		Vertex poi = new Vertex(0, 0);
		
		float bxax = (b.x-a.x);
		float byay = (b.y-a.y);
		float dycy = (d.y-c.y);
		float dxcx = (d.x-c.x);
		
		float denom = dycy*bxax - dxcx*byay;
		
		if(denom == 0) //parallel lines, do not intersect
			return null;
		
		float axcx = (a.x-c.x);
		float aycy = (a.y-c.y);
		
		float ua = dxcx*aycy - dycy*axcx;
			  ua /= denom;
		float ub = bxax*aycy - byay*axcx;
			  ub /= denom;
			  
		//point of intersection for lines
		poi.x = a.x + ua*bxax;
		poi.y = a.y + ua*byay;
		
		if(ua >= 0 && ua < 1 && ub >= 0 && ub < 1) //if line segments are within bounds
		{
			poi.alpha = ua;
			return poi;
		}
		return null; //do not intersect
	}

}
class Vertex extends Vec2d {
	Vertex prev, next;
	boolean intersect;
	boolean entry_exit;
	Vertex neighbor;
	float alpha;
	
	public Vertex(float x, float y)
	{
		super(x, y);
	}
	public Vertex(Vec2d v)
	{
		super(v.x, v.y);
	}
	public Vertex(Vertex vertex)
	{
		super(vertex);
	}
}
class VertexPolygon {
	Vertex[] vertices;
	
	public VertexPolygon(Polygon2d p)
	{
		vertices = new Vertex[p.vertices.length];
		for(int i = 0; i < vertices.length; ++i)
			vertices[i] = new Vertex(p.vertices[i]);
		for(int i = 0; i < vertices.length; ++i)
		{
			vertices[i].prev = vertices[(i-1) % vertices.length];
			vertices[i].next = vertices[(i+1) % vertices.length];
		}
	}
	public void insertVertexAfter(int index, Vertex v)
	{
		Vertex[] new_arr = new Vertex[vertices.length+1];
		
		int i = 0;
		for(; i <= index; ++i)
			new_arr[i] = vertices[i];
		
		new_arr[i] = v;
		v.prev = vertices[(i-1) % vertices.length];
		v.next = vertices[i];
		
		for(; i < new_arr.length; ++i)
			new_arr[i] = vertices[i-1];
	}
}