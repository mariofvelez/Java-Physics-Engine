package test.tests;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import math.Vec2d;
import test.DebugInfo;
import test.Field;
import test.Test;
import voronoi.Diagram;
import voronoi.FortuneAlgorithm;

public class VoronoiTest extends Test {
	
	Diagram diagram;
	
	ArrayList<Vec2d> v;

	public VoronoiTest(Field field, DebugInfo info)
	{
		super(field, info);
		
		transform.data[2] = 450;
		transform.data[5] = 300;
		transform.data[0] = 30;
		transform.data[4] = -30;
		
		v = new ArrayList<Vec2d>();
		
		Random r = new Random();
		for(int i = 0; i < 10; ++i)
		{
			v.add(new Vec2d(rand(r), rand(r)));
		}
		
		FortuneAlgorithm algo = new FortuneAlgorithm(v);
		algo.construct();
		diagram = algo.getDiagram();
	}
	private float rand(Random r)
	{
		return r.nextFloat() * 8 - 4;
	}
	public void step()
	{
		
	}
	public void draw(Graphics2D g2)
	{
		diagram.draw(g2, transform);
	}

}
