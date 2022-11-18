package geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import data_structures.RedBlackNode;
import data_structures.RedBlackTree;
import math.Vec2d;

public class Voronoi {
	
	private static Vec2d sweep = new Vec2d(0, 0);
	
	public static Polygon2d[] voronoiDiagramFortune(Vec2d... points)
	{
		Arrays.sort(points, (a, b) -> a.y > b.y? 1 : 0); // sorting points from top to bottom
		
		TempPoly[] polys = new TempPoly[points.length];
		for(int i = 0; i < polys.length; ++i)
			polys[i] = new TempPoly(points[i]);
		
		Queue<VoronoiEvent> Q = new LinkedList<>();
		for(int i = 0; i < points.length; ++i)
			Q.offer(new VoronoiEvent(i, null, points[i], VoronoiEventType.SITE));
				
		RedBlackTree<Vec2d> T = new RedBlackTree<>();
		T.nil = new Arc(T);
		
//		final Comparator<Vec2d> parabola_comp = (a, b) -> {
//			float a_y = getYFromDirectrix(a, sweep.y, sweep.x);
//			float b_y = getYFromDirectrix(b, sweep.y, sweep.x);
//			if(a_y == b_y)
//				return 0;
//			if(a_y < b_y)
//				return -1;
//			return 1;
//		};
		
		while(!Q.isEmpty())
		{
			VoronoiEvent p = Q.remove(); // the next event in the queue
			sweep.y = p.pos.y;
			
			if(p.type == VoronoiEventType.SITE)
			{
				System.out.println("new site event");
				// add parabola to T
				if(T.isEmpty())
				{
					Arc arc = new Arc(p.pos, T);
					arc.event = p;
					arc.event.arc = arc;
					T.setRoot(arc);
				}
				else
				{
					// find the closest arc in the beachline to p
					Arc to_break = locateArcAbove(T, p.pos, sweep.y);
					VoronoiEvent ev = to_break.event;
					deleteEvent(to_break, Q);
					to_break.event = ev;
					// split closest into 3 parabolas
					
					Arc middle = breakArc(T, to_break, p);
					Arc left = middle.prev;
					Arc right = middle.next;
					
					//add edge: left and middle
					
					//left triplet
					if(!T.isNil(left.prev))
						addEvent(Q, polys, left.prev, left, middle);
					//right triplet
					if(!T.isNil(right.next))
						addEvent(Q, polys, middle, right, right.next);
				}
			}
			else
			{
				System.out.println("new circle event");
				
				Vec2d point = p.pos;
				Arc arc = p.arc;
				
				//add vertex to diagram at point
				
				Arc left = arc.prev;
				Arc right = arc.next;
				
				deleteEvent(left, Q);
				deleteEvent(right, Q);
				
				removeArc(T, arc, point);
				
				if(!T.isNil(left.prev))
					addEvent(Q, polys, left.prev, left, right);
				if(!T.isNil(right.next))
					addEvent(Q, polys, left, right, right.next);
			}
		}
		
		Polygon2d[] polygons = new Polygon2d[polys.length];
		for(int i = 0; i < polys.length; ++i)
			polygons[i] = polys[i].create();
		
		return polygons;
	}
	private static void insertBefore(RedBlackTree<Vec2d> T, Arc x, Arc y)
	{
		if(T.isNil(x.left))
		{
			x.left = y;
			y.p = x;
		}
		else
		{
			x.prev.right = y;
			y.p = x.prev;
		}
		
		y.prev = x.prev;
		if(!T.isNil(y.prev))
			y.prev.next = y;
		y.next = x;
		x.prev = y;
		T.insertFixup(y);
	}
	private static void insertAfter(RedBlackTree<Vec2d> T, Arc x, Arc y)
	{
		if(T.isNil(x.right))
		{
			x.right = y;
			y.p = x;
		}
		else
		{
			x.next.left = y;
			y.p = x.next;
		}
		y.next = x.next;
		if(!T.isNil(y.next))
			y.next.prev = y;
		x.next = y;
		T.insertFixup(y);
	}
	private static void replace(RedBlackTree<Vec2d> T, Arc x, Arc y)
	{
		T.replace(x, y);
		
		y.prev = x.prev;
		y.next = x.next;
		if(!T.isNil(y.prev))
			y.prev.next = y;
		if(!T.isNil(y.next))
			y.next.prev = y;
	}
	private static Arc locateArcAbove(RedBlackTree<Vec2d> T, Vec2d point, float sweep_y)
	{
		Arc node = (Arc) T.root;
		boolean found = false;
		
		while(!found)
		{
			System.out.println("searching");
			float break_left = Float.NEGATIVE_INFINITY;
			float break_right = Float.POSITIVE_INFINITY;
			if(!T.isNil(node.prev))
				break_left = node.prev.computeBreakpoint(node, sweep_y);
			if(!T.isNil(node.next))
				break_right = node.computeBreakpoint(node.next, sweep_y);
			if(point.x < break_left)
				node = (Arc) node.right;
			else if(point.x > break_right)
				node = (Arc) node.left;
			else
				found = true;
		}
		return node;
	}
	private static void deleteEvent(Arc arc, Queue<VoronoiEvent> Q)
	{
		if(arc.event != null)
		{
			Q.remove(arc.event);
			arc.event = null;
		}
	}
	private static void addEvent(Queue<VoronoiEvent> Q, TempPoly[] polys, Arc left, Arc middle, Arc right)
	{
		System.out.println("added event");
		Vec2d convergence = Geometry.circumcenter(left.key, middle.key, right.key);
		float y = convergence.y - Vec2d.dist(convergence, left.key);
		
		boolean below = y <= sweep.y;
		boolean left_moving_right = left.key.y < right.key.y;
		boolean right_moving_right = middle.key.y < right.key.y;
		float left_init_x = left_moving_right? left.key.x : middle.key.x;
		float right_init_x = right_moving_right? middle.key.x : right.key.x;
		
		boolean is_valid = 
				((left_moving_right && left_init_x < convergence.x) ||
				(!left_moving_right && left_init_x > convergence.x)) &&
				((right_moving_right && right_init_x < convergence.x) ||
				(!right_moving_right && right_init_x > convergence.x));
		
		if(is_valid && below)
		{
			VoronoiEvent event = new VoronoiEvent(-1, middle, convergence, VoronoiEventType.CIRCLE);
			Q.offer(event);
		}
		
		polys[left.event.index].addVector(convergence);
		polys[middle.event.index].addVector(convergence);
		polys[right.event.index].addVector(convergence);
	}
	private static Arc breakArc(RedBlackTree<Vec2d> T, Arc arc, VoronoiEvent site)
	{
		Arc middle = new Arc(site.pos, T);
		Arc left = new Arc(arc.event.pos, T);
		Arc right = new Arc(arc.event.pos, T);
		
		System.out.println("site pos: " + site.pos);
		System.out.println("arc key: " + arc.key);
		
		replace(T, arc, middle);
		insertBefore(T, middle, left);
		insertAfter(T, middle, right);
		
		left.event = arc.event;
		right.event = arc.event;
		middle.event = site;
		System.out.println("arc event: " + arc.event);
		
		return middle;
	}
	private static void removeArc(RedBlackTree<Vec2d> T, Arc arc, Vec2d vertex)
	{
		
		
		T.deleteRB(arc);
		if(!T.isNil(arc.prev))
			arc.prev.next = arc.next;
		if(!T.isNil(arc.next))
			arc.next.prev = arc.prev;
	}
	
}
class VoronoiEvent {
	
	int index;
	Arc arc;
	Vec2d pos;
	VoronoiEventType type;
	
	public VoronoiEvent(int index, Arc arc, Vec2d pos, VoronoiEventType type)
	{
		this.arc = arc;
		this.pos = pos;
		this.type = type;
	}
}
enum VoronoiEventType {
	SITE,
	CIRCLE,
}
class Arc extends RedBlackNode<Vec2d> {
	
	VoronoiEvent event;
	Arc prev; //if in beachline
	Arc next;
	
	public Arc(Vec2d site, RedBlackTree<Vec2d> T)
	{
		p = T.nil;
		left = T.nil;
		right = T.nil;
		setRed();
		
		key = site;
		prev = (Arc) T.nil;
		next = (Arc) T.nil;
	}
	public Arc(RedBlackTree<Vec2d> T)
	{
		T.nil = this;
		setBlack();
		T.root = this;
		prev = this;
		next = this;
		key = null;
		p = this;
		left = this;
		right = this;
	}
	public float getY(float sweep_y, float x)
	{
		float a = 1 / (2*(key.y - sweep_y));
		float b = x - key.x;
		float c = (key.y + sweep_y) / 2;
		
		return a*b*b + c;
	}
	float computeBreakpoint(Arc other, float sweep_y)
	{
		float d1 = 1 / (2*(key.y - sweep_y));
		float d2 = 1 / (2*(other.key.y - sweep_y));
		float a = d1 - d2;
		float b = 2f * (other.key.x * d2 - key.x * d1);
		float c = (key.length2() - sweep_y * sweep_y) * d1 - (other.key.length2() - sweep_y * sweep_y) * d2;
		float delta = b * b - 4f * a * c;
		return (float) (-b + Math.sqrt(delta)) / (2 * a);
	}
}
class VoronoiDiagram
{
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
		Vec2d site;
		HalfEdge outer;
	}
	
	ArrayList<Vec2d> sites;
	ArrayList<Face> faces;
	ArrayList<Vec2d> vertices;
	ArrayList<HalfEdge> edges;
	
	public VoronoiDiagram()
	{
		sites = new ArrayList<>();
		faces = new ArrayList<>();
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
	}
}
class TempPoly
{
	ArrayList<Vec2d> vectors;
	Vec2d site;
	
	public TempPoly(Vec2d site)
	{
		vectors = new ArrayList<>();
		this.site = site;
	}
	/**
	 * Vectors must be added in ascending y order
	 * @param v
	 */
	public void addVector(Vec2d v)
	{
		if(v.x < site.x)
			vectors.add(v);
		else
			vectors.add(0, v);
	}
	public Polygon2d create()
	{
		Vec2d[] vecs = new Vec2d[vectors.size()];
		for(int i = 0; i < vecs.length; ++i)
			vecs[i] = vectors.get(i);
		Polygon2d poly = new Polygon2d(vecs);
		return poly;
	}
}



