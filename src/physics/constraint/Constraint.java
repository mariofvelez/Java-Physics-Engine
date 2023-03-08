package physics.constraint;

import physics.World;

public abstract class Constraint {
	
	protected World world;
	
	public abstract void solve(float dt);
	
	public void setWorld(World world)
	{
		this.world = world;
	}

}
