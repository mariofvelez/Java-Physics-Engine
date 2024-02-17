package voronoi;

import java.awt.Graphics2D;
import java.util.ArrayList;

import geometry.Polygon2d;
import math.Transform;
import math.Vec2d;

public class Diagram {
	
	class Site
	{
		int index;
		Vec2d point;
		Face face;
		
		public Site(int index, Vec2d point, Face face)
		{
			this.index = index;
			this.point = point;
			this.face = face;
		}
	}
	class HalfEdge
	{
		Vec2d origin;
		Vec2d destination;
		HalfEdge twin;
		Face face;
		HalfEdge prev;
		HalfEdge next;
	}
	class Face
	{
		Site site;
		HalfEdge outer;
		
		public Face(Site site, HalfEdge outer)
		{
			this.site = site;
			this.outer = outer;
		}
	}
	
	ArrayList<Site> sites;
	ArrayList<Face> faces;
	ArrayList<Vec2d> vertices;
	ArrayList<HalfEdge> half_edges;
	
	public Diagram(ArrayList<Vec2d> points)
	{
		sites = new ArrayList<>();
		faces = new ArrayList<>();
		vertices = new ArrayList<>();
		half_edges = new ArrayList<>();
		
		for(int i = 0; i < points.size(); ++i)
		{
			Site site = new Site(i, points.get(i), null);
			sites.add(site);
			Face face = new Face(site, null);
			faces.add(face);
			site.face = face;
		}
	}
	public void draw(Graphics2D g2)
	{
		for(int i = 0; i < vertices.size(); ++i)
			vertices.get(i).debugDraw(g2, 3);
		
		for(int i = 0; i < faces.size(); ++i)
		{
			Face face = faces.get(i);
			for(HalfEdge edge = face.outer; edge != null; edge = edge.next)
			{
				if(edge.origin != null && edge.destination != null)
				g2.drawLine((int) edge.origin.x, (int) edge.origin.y, (int) edge.destination.x, (int) edge.destination.y);
			}
		}
	}
	public void draw(Graphics2D g2, Transform transform)
	{
		for(int i = 0; i < vertices.size(); ++i)
		{
			Vec2d v = new Vec2d(vertices.get(i));
			transform.project2D(v);
			v.debugDraw(g2, 3);
		}
		
		for(int i = 0; i < faces.size(); ++i)
		{
			Face face = faces.get(i);
			for(HalfEdge edge = face.outer; edge != null; edge = edge.next)
			{
				if(edge.origin != null && edge.destination != null)
				{
					Vec2d origin = new Vec2d(edge.origin);
					Vec2d destination = new Vec2d(edge.destination);
					transform.project2D(origin);
					transform.project2D(destination);
					g2.drawLine((int) origin.x, (int) origin.y, (int) destination.x, (int) destination.y);
				}
			}
		}
	}
	public Site getSite(int index)
	{
		return sites.get(index);
	}
	public int getSiteCount()
	{
		return sites.size();
	}
	public Face getFace(int index)
	{
		return faces.get(index);
	}
	public ArrayList<Vec2d> getVertices()
	{
		return vertices;
	}
	public ArrayList<HalfEdge> getHalfEdges()
	{
		return half_edges;
	}
	public boolean intersect(Polygon2d polygon)
	{
		//FIXME - finish this
		return false;
	}
	public Vec2d createVertex(Vec2d point)
	{
		vertices.add(point);
		return vertices.get(vertices.size()-1);
	}
	public HalfEdge createHalfEdge(Face face)
	{
		HalfEdge edge = new HalfEdge();
		edge.face = face;
		if(face.outer == null)
			face.outer = edge;
		half_edges.add(edge);
		return edge;
	}

}
