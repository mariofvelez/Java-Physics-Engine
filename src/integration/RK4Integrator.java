package integration;

import java.util.function.BiFunction;

public class RK4Integrator implements Integrator {
	
	public float y(float t, float h, float yn, BiFunction<Float, Float, Float> f)
	{
		float k1 = f.apply(t, yn);
		float k2 = f.apply(t + h / 2.0f, yn + h * k1 / 2.0f);
		float k3 = f.apply(t + h / 2.0f, yn + h * k2 / 2.0f);
		float k4 = f.apply(t + h, yn + h*k3);
		return yn + (h / 6.0f) * (k1 + 2.0f*k2 + 2.0f*k3 + k4);
	}

}
