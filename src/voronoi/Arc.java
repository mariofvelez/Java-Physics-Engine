package voronoi;

import data_structures.RedBlackNode;

public class Arc extends RedBlackNode<Diagram.Site> {
	
	enum Side{LEFT, RIGHT};
	
	Diagram.HalfEdge left_edge;
	Diagram.HalfEdge right_edge;
	Event event;
	
	Arc prev;
	Arc next;
	
	Color color;
	
	Side side;
	
	public Arc()
	{
		
	}

}
