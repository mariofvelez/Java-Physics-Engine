package graphics;

import java.awt.image.BufferedImage;

import math.Transform;
import math.Vec2d;
import physics.AABB;

public class Camera {
	
	BufferedImage img;
	int[] img_px;
	float[] z_buffer;
	Transform transform;
	Transform inv_transform;
	
	AABB bounds;
	
	public Camera(Vec2d position)
	{
		transform = new Transform(3, true);
		transform.data[2] = position.x;
		transform.data[5] = position.y;
	}
	public void move(float x, float y)
	{
		transform.data[2] += x;
		transform.data[5] += y;
	}
	public void move(Vec2d move)
	{
		transform.data[2] += move.x;
		transform.data[5] += move.y;
	}
	public Vec2d getCameraCoord(Vec2d point)
	{
		Vec2d coord = new Vec2d(point);
		inv_transform.project2D(coord);
		return coord;
	}

}
