package physics;

public interface CollisionListener {
	
	public void beforeSolve(CollisionInfo info);
	public void afterSolve(CollisionInfo info);

}
