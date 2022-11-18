package voronoi;

import data_structures.RedBlackNode;
import data_structures.RedBlackTree;
import math.Vec2d;

public class Beachline extends RedBlackTree<Diagram.Site> {
	
	public Beachline()
	{
		super();
		nil = new Arc();
		nil.p = nil;
		nil.left = nil;
		nil.right = nil;
		Arc n = (Arc) nil;
		n.prev = (Arc) nil;
		n.next = (Arc) nil;
		nil.setBlack();
		root = nil;
	}
	public Arc createArc(Diagram.Site site)
	{
		return new Arc((Arc) nil, (Arc) nil, (Arc) nil, site, null, null, null, (Arc) nil, (Arc) nil, RedBlackNode.Color.RED);
	}
	public Arc getLeftMostArc()
	{
		Arc x = (Arc) root;
		while(!isNil(x.prev))
			x = x.prev;
		return x;
	}
	public Arc locateArcAbove(Vec2d point, float l)
	{
		Arc node = (Arc) root;
		boolean found = false;
		while(!found)
		{
			float break_left = Float.NEGATIVE_INFINITY;
			float break_right = Float.POSITIVE_INFINITY;
			
			if(!isNil(node.prev))
				break_left = computeBreakpoint(node.prev.key.point, node.key.point, l);
			if(!isNil(node.next))
				break_right = computeBreakpoint(node.key.point, node.next.key.point, l);
			if(point.x < break_left)
				node = (Arc) node.left;
			else if(point.x > break_right)
				node = (Arc) node.right;
			else
				found = true;
		}
		return node;
	}
	public void insertBefore(Arc x, Arc y)
	{
		if(isNil(x.left))
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
		if(!isNil(y.prev))
			y.prev.next = y;
		y.next = x;
		x.prev = y;
		
		insertFixup(y);
	}
	public void insertAfter(Arc x, Arc y)
	{
		if(isNil(x.right))
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
		if(!isNil(y.next))
			y.next.prev = y;
		y.prev = x;
		x.next = y;
		
		insertFixup(y);
	}
	public void replace(Arc x, Arc y)
	{
		super.replace(x, y);
		y.prev = x.prev;
		y.next = x.next;
		if(!isNil(y.prev))
			y.prev.next = y;
		if(!isNil(y.next))
			y.next.prev = y;
	}
	public void remove(Arc z)
	{
		super.deleteRB(z);
		if(!isNil(z.prev))
			z.prev.next = z;
		if(!isNil(z.next))
			z.next.prev = z;
	}
	float computeBreakpoint(Vec2d p1, Vec2d p2, float l)
	{
		float d1 = 1 / (2*(p1.y - l));
		float d2 = 1 / (2*(p2.y - l));
		float a = d1 - d2;
		float b = 2f * (p2.x * d2 - p1.x * d1);
		float c = (p1.length2() - l * l) * d1 - (p2.length2() - l * l) * d2;
		float delta = b * b - 4f * a * c;
		return (float) (-b + Math.sqrt(delta)) / (2 * a);
	}

}
