package integration;

import java.util.function.BiFunction;

import math.Vec2d;

public class RK4Integrator implements Integrator {
	
	public float y(float t, float h, float yn, BiFunction<Float, Float, Float> f)
	{
		float k1 = f.apply(t, yn);
		float k2 = f.apply(t + h / 2.0f, yn + h * k1 / 2.0f);
		float k3 = f.apply(t + h / 2.0f, yn + h * k2 / 2.0f);
		float k4 = f.apply(t + h, yn + h*k3);
		return yn + (h / 6.0f) * (k1 + 2.0f*k2 + 2.0f*k3 + k4);
	}
	public Vec2d y(float t, float h, Vec2d yn, BiFunction<Float, Vec2d, Vec2d> f)
	{
		Vec2d k1 = f.apply(t, yn);
		Vec2d k2 = f.apply(t + h / 2.0f, Vec2d.add(yn, Vec2d.mult(k1, h / 2.0f)));
		Vec2d k3 = f.apply(t + h / 2.0f, Vec2d.add(yn, Vec2d.mult(k2, h / 2.0f)));
		Vec2d k4 = f.apply(t + h, Vec2d.add(yn, Vec2d.mult(k3, h)));
		Vec2d result = new Vec2d(k1);
		result.add(k2.x * 2, k2.y * 2);
		result.add(k3.x * 2, k3.y * 2);
		result.add(k4);
		result.mult(h / 6.0f);
		result.add(yn);
		return result;
	}

}
