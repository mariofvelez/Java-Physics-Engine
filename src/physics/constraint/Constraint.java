package physics.constraint;

import java.awt.Graphics2D;

import math.Transform;
import physics.World;

public abstract class Constraint {
	
	protected World world;
	
	public abstract void solve(float dt);
	
	public void setWorld(World world)
	{
		this.world = world;
	}
	public abstract void debugDraw(Graphics2D g2, Transform transform);

}
