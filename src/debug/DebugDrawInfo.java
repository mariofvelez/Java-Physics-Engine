package debug;

import java.awt.Color;

public class DebugDrawInfo {
	
	public Color col;
	public boolean wireframe;
	public boolean normal;
	public float normal_len;
	
	public DebugDrawInfo()
	{
		col = new Color(255, 255, 255);
		wireframe = false;
		normal = false;
		normal_len = 1;
	}

}
