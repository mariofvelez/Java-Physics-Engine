package integration;

import java.util.function.BiFunction;

public class EulerIntegrator implements Integrator {
	
	public float y(float t, float h, float yn, BiFunction<Float, Float, Float> f)
	{
		return yn + f.apply(t, yn) * h;
	}

}
