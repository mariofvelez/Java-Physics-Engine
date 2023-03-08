package integration;

import java.util.function.BiFunction;

public interface Integrator {
	
	/**
	 * 
	 * @param t - current time
	 * @param h - time step
	 * @param yn - current position
	 * @param f - derivative functions
	 * @returns an implicit approximation
	 */
	public float y(float t, float h, float yn, BiFunction<Float, Float, Float> f);

}
