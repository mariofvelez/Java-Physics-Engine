package debug;

import java.awt.Color;

public class DebugDrawInfo {
	
	public Color outline_col;
	public Color fill_col;
	public boolean wireframe;
	public boolean normal;
	public float normal_len;
	
	public DebugDrawInfo()
	{
		outline_col = new Color(0, 0, 0);
		fill_col = new Color(255, 255, 255);
		wireframe = false;
		normal = true;
		normal_len = 1;
	}

}
