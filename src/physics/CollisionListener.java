package physics;

public interface CollisionListener {
	
	/**
	 * The function to call before a collision is resolved
	 * @param info - stores the collision data
	 */
	public void beforeSolve(CollisionInfo info);
	/**
	 * The function to call after a collision is resolved
	 * @param info - stores the collision data
	 */
	public void afterSolve(CollisionInfo info);

}
