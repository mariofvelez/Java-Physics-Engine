package graphics;

import math.Vec2d;
import physics.AABB;
import physics.body.Body;

public class CameraTracker {
	
	Camera camera;
	
	AABB safe_bounds;
	Body tracked_body;
	
	float look_ahead_scale = 0;
	float inside_strength = 1;
	float outside_strength = 1;
	
	boolean can_rotate = false;
	float rotate_scale = 1;
	
	Vec2d offset;
	
	public CameraTracker(Camera camera)
	{
		
	}
	public void setCamera(Camera camera)
	{
		this.camera = camera;
	}
	public void setTrackedBody(Body body)
	{
		tracked_body = body;
	}
	public void setLookAheadScale(float scale)
	{
		look_ahead_scale = scale;
	}
	public void setInsideStrength(float strength)
	{
		inside_strength = strength;
	}
	public void setOutsideStrength(float strength)
	{
		outside_strength = strength;
	}
	public void updateCamera()
	{
		if(tracked_body != null)
		{
			Vec2d location = camera.getCameraCoord(tracked_body.getPositionUnmodifiable());
			location.add(tracked_body.vel.x * look_ahead_scale, tracked_body.vel.y * look_ahead_scale);
			location.add(offset);
			boolean on_camera = camera.bounds.intersects(location);
			boolean in_safe = safe_bounds.intersects(location);
			
			Vec2d move = Vec2d.subtract(camera.getCameraCoord(Vec2d.ZERO), location);
			if(on_camera && !in_safe)
				move.mult(inside_strength);
			else if(in_safe)
				move.mult(outside_strength);
			camera.move(move);
		}
	}
	

}
