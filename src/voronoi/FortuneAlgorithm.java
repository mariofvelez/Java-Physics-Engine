package voronoi;

import java.util.ArrayList;

import geometry.Geometry;
import math.Vec2d;

public class FortuneAlgorithm {
	
	private Diagram diagram;
	private Beachline beachline;
	private PriorityQueue<Event> events;
	private float beachline_y;
	
	public FortuneAlgorithm(ArrayList<Vec2d> points)
	{
		points.sort((a, b) -> {
			if(a.y < b.y)
				return -1;
			if(a.y > b.y)
				return 1;
			if(a.x < b.x)
				return -1;
			if(a.x > b.x)
				return 1;
			return 0;
		});
		diagram = new Diagram(points);
		beachline = new Beachline();
		events = new PriorityQueue<>();
	}
	public Diagram getDiagram()
	{
		return diagram;
	}
	public void construct()
	{
		for(int i = 0; i < diagram.getSiteCount(); ++i)
			events.push(new Event(diagram.getSite(i)));
		
		while(!events.isEmpty())
		{
			Event event = events.pop();
			beachline_y = event.y;
			if(event.type == Event.Type.SITE)
				handleSite(event);
			else
				handleCircle(event);
		}
	}
	private void handleSite(Event event)
	{
		Diagram.Site site = event.site;
		
		if(beachline.isEmpty())
		{
			beachline.setRoot(beachline.createArc(site));
			return;
		}
		
		Arc to_break = beachline.locateArcAbove(site.point, beachline_y);
		System.out.println(beachline.isNil(to_break));
		deleteEvent(to_break);
		
		Arc middle = breakArc(to_break, site);
		Arc left = middle.prev;
		Arc right = middle.next;
		
		addEdge(left, middle);
		middle.right_edge = middle.left_edge;
		right.left_edge = left.right_edge;
		
		if(!beachline.isNil(left.prev))
			addEvent(left.prev, left, middle);
		if(!beachline.isNil(right.next))
			addEvent(middle, right, right.next);
	}
	private void handleCircle(Event event)
	{
		Vec2d point = event.point;
		Arc arc = event.arc;
		
		Vec2d vertex = diagram.createVertex(point);
		
		Arc left = arc.prev;
		Arc right = arc.next;
		deleteEvent(left);
		deleteEvent(right);
		
		removeArc(arc, vertex);
		
		if(!beachline.isNil(left.prev))
			addEvent(left.prev, left, right);
		if(!beachline.isNil(right.next))
			addEvent(left, right, right.next);
	}
	private Arc breakArc(Arc arc, Diagram.Site site)
	{
		Arc middle = beachline.createArc(site);
		Arc left = beachline.createArc(arc.key);
		left.left_edge = arc.left_edge;
		Arc right = beachline.createArc(arc.key);
		right.right_edge = arc.right_edge;
		
		beachline.replace(arc, middle);
		beachline.insertBefore(middle, left);
		beachline.insertAfter(middle, right);
		
		return middle;
	}
	private void removeArc(Arc arc, Vec2d vertex)
	{
		setDestination(arc.prev, arc, vertex);
		setDestination(arc, arc.next, vertex);
		
		arc.left_edge.next = arc.right_edge;
		arc.right_edge.prev = arc.left_edge;
		
		beachline.remove(arc);
		
		Diagram.HalfEdge prev_edge = arc.prev.right_edge;
		Diagram.HalfEdge next_edge = arc.next.left_edge;
		addEdge(arc.prev, arc.next);
		setOrigin(arc.prev, arc.next, vertex);
		setPrevHalfEdge(arc.prev.right_edge, prev_edge);
		setPrevHalfEdge(next_edge, arc.next.left_edge);
	}
	private boolean movingRight(Arc left, Arc right)
	{
		return left.key.point.y < right.key.point.y;
	}
	private float getInitialX(Arc left, Arc right, boolean moving_right)
	{
		return moving_right? left.key.point.x : right.key.point.x;
	}
	private void addEdge(Arc left, Arc right)
	{
		left.right_edge = diagram.createHalfEdge(left.key.face);
		right.left_edge = diagram.createHalfEdge(right.key.face);
		
		left.right_edge.twin = right.left_edge;
		right.left_edge.twin = left.right_edge;
	}
	private void setOrigin(Arc left, Arc right, Vec2d vertex)
	{
		left.right_edge.destination = vertex;
		right.left_edge.origin = vertex;
	}
	private void setDestination(Arc left, Arc right, Vec2d vertex)
	{
		left.right_edge.origin = vertex;
		right.left_edge.destination = vertex;
	}
	private void setPrevHalfEdge(Diagram.HalfEdge prev, Diagram.HalfEdge next)
	{
		prev.next = next;
		next.prev = prev;
	}
	private void addEvent(Arc left, Arc middle, Arc right)
	{
		float y;
		Vec2d convergence = Geometry.circumcenter(left.key.point, middle.key.point, right.key.point);
		y = convergence.y - Vec2d.dist(convergence, left.key.point);
		boolean below = y <= beachline_y;
		boolean left_moving_right = movingRight(left, middle);
		boolean right_moving_right = movingRight(middle, right);
		float left_initial_x = getInitialX(left, middle, left_moving_right);
		float right_initial_x = getInitialX(middle, right, right_moving_right);
		
		boolean is_valid = 
				((left_moving_right && left_initial_x < convergence.x) ||
				(!left_moving_right && left_initial_x > convergence.x)) &&
				((right_moving_right && right_initial_x < convergence.x) ||
				(!right_moving_right && right_initial_x > convergence.x));
		
		if(is_valid && below)
		{
			Event event = new Event(y, convergence, middle);
			middle.event = event;
			events.push(event);
		}
	}
	private void deleteEvent(Arc arc)
	{
		if(arc.event != null)
		{
//			((LinkedList<Event>) (events)).remove(arc.event.index);
			events.remove(arc.event.index);
			arc.event = null;
		}
	}
	
	class LinkedVertex
	{
		Diagram.HalfEdge prev_edge;
		Vec2d vertex;
		Diagram.HalfEdge next_edge;
	}

}
