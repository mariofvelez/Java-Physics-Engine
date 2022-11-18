package voronoi;

import math.Vec2d;

public class Event implements IndexListener<Event> {
	
	enum Type{SITE, CIRCLE};
	
	final Type type;
	float y;
	int index;
	Diagram.Site site;
	Vec2d point;
	Arc arc;
	
	public Event(Diagram.Site site)
	{
		type = Type.SITE;
		y = site.point.y;
		index = -1;
		this.site = site;
	}
	public Event(float y, Vec2d point, Arc arc)
	{
		type = Type.CIRCLE;
		this.y = y;
		index = -1;
		this.point = point;
		this.arc = arc;
	}
	public int compareTo(Event e)
	{
		if(y < e.y)
			return -1;
		if(y > e.y)
			return 1;
		return 0;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}

}
