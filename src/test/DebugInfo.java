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
	public Test curr_test = null;
	
	private ArrayList<Vec2d> poc = new ArrayList<Vec2d>();
	private int collisions = 0;
	
	public boolean show_centroid = false;
	public boolean show_edge_normals = false;
	public boolean show_poc = false;
	public boolean show_vertex_velocities = false;
	public boolean show_aabbs = false;
	
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
				Vec2d x = Vec2d.add(b.centroid, Vec2d.mult(Vec2d.EAST, 0.5f));
				Vec2d y = Vec2d.add(b.centroid, Vec2d.mult(Vec2d.NORTH, 0.5f));
				
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
		}
		if(show_edge_normals)
		{
			g2.setColor(Color.MAGENTA);
			curr_world.forEachBody(b -> {
				if(b.shape instanceof Polygon2d)
				{
					Polygon2d p = (Polygon2d) b.getWorldShape();
					for(int i = 0; i < p.edges.length; ++i)
					{
						Vec2d v = Vec2d.avg(p.edges[i]);
						Vec2d n = Vec2d.subtract(p.edges[i][1], p.edges[i][0]).leftNormal();
						n.normalize();
						n.mult(0.5f);
						n.add(v);
						
						transform.project2D(v);
						transform.project2D(n);
						
						g2.drawLine((int)v.x, (int)v.y, (int) n.x, (int) n.y);
					}
				}
			});
		}
		if(show_vertex_velocities)
		{
			curr_world.forEachBody(b -> {
				if(b.shape instanceof Polygon2d)
				{
					g2.setColor(Color.ORANGE);
					Polygon2d p = (Polygon2d) b.shape;
					for(int i = 0; i < p.vertices.length; ++i)
					{
						Vec2d v = new Vec2d(p.vertices[i]);
						b.localToWorld(v);
						Vec2d vel = new Vec2d(v);
						b.pointVelocityFromWorld(vel, curr_world.dt / curr_world.iters);
						vel.mult(0.1f);
						if(vel.length() > 3.0f)
						{
							vel.normalize();
							vel.mult(3.0f);
							g2.setColor(Color.RED);
						}
						vel.add(v);
						
						transform.project2D(v);
						transform.project2D(vel);
						
						g2.drawLine((int)v.x, (int)v.y, (int) vel.x, (int) vel.y);
					}
				}
			});
		}
		if(show_aabbs)
		{
			g2.setColor(Color.BLUE);
			curr_world.forEachBody(b -> {
				b.aabb.debugDraw(g2, transform);
			});
		}
		
		g2.setColor(Color.BLACK);
		int y = 20;
		g2.drawString("time: " + curr_test.field.ms_avg + "ms", 20, y += 20);
		g2.drawString("objects: " + curr_world.getBodySize(), 20, y += 20);
		g2.drawString("-dynamic: " + curr_world.getDynamicBodySize(), 35, y += 20);
		g2.drawString("-static: " + curr_world.getStaticBodySize(), 35, y += 20);
		g2.drawString("collisions/frame: " + collisions, 20, y += 20);
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
