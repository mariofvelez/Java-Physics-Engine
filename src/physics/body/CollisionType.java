package physics.body;

public enum CollisionType {
	
	/**
	 * Does not change velocity or angular velocity when colliding. Acts as infinite mass and infinite angular momentum
	 */
	STATIC,
	/**
	 * Changes velocity and angular velocity appropriately when colliding.
	 */
	DYNAMIC

}
