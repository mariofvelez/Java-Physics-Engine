package test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import geometry.Polygon2d;
import math.Transform;
import math.Vec2d;
import physics.CollisionInfo;
import physics.World;

public class DebugInfo {
	
	public World curr_world = null;
	
	private ArrayList<Vec2d> poc = new ArrayList<Vec2d>();
	private int collisions = 0;
	
	public boolean show_poc = false;
	public boolean show_centroid = false;
	public boolean show_edge_normals = false;
	public boolean show_vertex_velocities = false;
	
	public void beforeSolve(CollisionInfo info)
	{
		poc.add(info.poc);
	}
	public void afterSolve(CollisionInfo info)
	{
		collisions++;
	}
	public void draw(Graphics2D g2, Transform transform)
	{
		if(show_poc)
		{
			g2.setColor(new Color(26, 33, 38));
			for(int i = 0; i < poc.size(); ++i)
			{
				Vec2d p = new Vec2d(poc.get(i));
				transform.project2D(p);
				g2.fillOval((int)p.x-2, (int)p.y-2, 4, 4);
			}
		}
		if(show_centroid)
		{
			curr_world.forEachBody(b -> {
				Vec2d centroid = new Vec2d(b.centroid);
				Vec2d x = Vec2d.add(b.centroid, Vec2d.mult(Vec2d.EAST, 10));
				Vec2d y = Vec2d.add(b.centroid, Vec2d.mult(Vec2d.NORTH, 10));
				
				b.localToWorld(centroid);
				b.localToWorld(x);
				b.localToWorld(y);
				
				transform.project2D(centroid);
				transform.project2D(x);
				transform.project2D(y);
				
				g2.setColor(Color.BLUE);
				g2.fillOval((int)centroid.x-2, (int)centroid.y-2, 4, 4);
				g2.setColor(Color.RED);
				g2.drawLine((int)centroid.x, (int)centroid.y, (int) x.x, (int) x.y);
				g2.setColor(Color.GREEN);
				g2.drawLine((int)centroid.x, (int)centroid.y, (int) y.x, (int) y.y);
			});
			if(show_edge_normals)
			{
				curr_world.forEachBody(b -> {
					if(b.shape instanceof Polygon2d)
					{
						Polygon2d p = (Polygon2d) b.getWorldShape();
						for(int i = 0; i < p.edges.length; ++i)
						{
							Vec2d v = Vec2d.avg(p.edges[i]);
							
						}
					}
				});
			}
		}
	}
	public void restart()
	{
		poc.clear();
		collisions = 0;
	}
	public int getCollisions()
	{
		return collisions;
	}

}
