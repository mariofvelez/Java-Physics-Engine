package geometry;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import math.Vec2d;

/**
 * 
 * @author Mario Velez
 *
 */
public class Field extends Canvas
		implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener,
				   Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -796167392411348854L;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Graphics bufferGraphics; // graphics for backbuffer
	private BufferStrategy bufferStrategy;
	
	public static int mousex = 0; // mouse values
	public static int mousey = 0;

	Font font = new Font("Guardians Regular", 0, 10); // font
	Font font1 = new Font("Airstrike Regular", 0, 80);
	Font font2 = new Font("Airstrike Regular", 0, 60);
	Font font3 = new Font("Airstrike Regular", 0, 30);
	Font font4 = new Font("Airstrike Regular", 0, 15);
	Color color = new Color(50, 50, 50);
	Color color1 = new Color(200, 200, 200);

	public static ArrayList<Integer> keysDown; // holds all the keys being held down
	boolean leftClick;

	private Thread thread;

	private boolean running;
	private int refreshTime;
	
	public float timeStep = 1;
	public int velIters = 8;
	public int posIters = 3;
	
	public static int[] anchor = new  int[2];
	public static boolean dragging;
	
	Shape2d[] shapes;
	
	Circle s;
	Vec2d vel;
	//NeuralNetwork nn;
	
	BufferedImage img;
	int[] img_data;
	BufferedImage buffer;
	int[] buffer_data;
	private final int width;
	private final int height;
	
	public boolean show_ray_march = false;
	
	public float restitution = 0.5f;
	public float friction = 0.01f;
	
	public float intensity = 250; //light level of the light source
	
	Thread gjk_thread;
	boolean gjk_running = true;
	
	public Field(Dimension size) throws Exception {
		this.setPreferredSize(size);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);

		this.thread = new Thread(this);
		running = true;
		refreshTime = 16;//(int) (1f/50 * 1000);

		keysDown = new ArrayList<Integer>();
		
		shapes = new Shape2d[4];
		
		Vec2d[] vecs = {
				new Vec2d(100f, 180f),
				new Vec2d(340f, 80f),
				new Vec2d(300f, 200f),
				new Vec2d(150f, 250f),
				new Vec2d(80f, 200f)
		};
		shapes[0] = new Polygon2d(vecs);
		
		Vec2d[] vecs2 = {
				new Vec2d(600, 335),
				new Vec2d(653, 371),
				new Vec2d(597, 455),
				new Vec2d(510, 437),
				new Vec2d(532, 375)
		};
		shapes[1] = new Polygon2d(vecs2);
		
		shapes[2] = new Circle(new Vec2d(200f, 300f), 40);
		shapes[3] = new Circle(new Vec2d(500f, 200f), 55f);
		
		s = new Circle(new Vec2d(450f, 100f), 30f);
		vel = new Vec2d(0, 0);
		
		img = new BufferedImage(Window1.WIDTH, Window1.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		width = img.getWidth();
		height = img.getHeight();
		img_data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buffer_data = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();
		
		
//		Graphics2D g2 = img.createGraphics();
//		
//		g2.setColor(Color.BLACK);
//		
//		double strength = 0.3;
//		
//		Vec2d[] v = new Vec2d[10000];
//		v[0] = new Vec2d(450, 300);
//		double r = Utils.rand(-strength, strength);
//		
//		for(int i = 1; i < v.length; i++)
//		{
//			r += Utils.rand(-strength, strength);
//			v[i] = Vec2d.add(v[i-1], Vec2d.fromPolar(r, 1));
//		}
//		
//		g2.drawPolyline(Vec2d.xPoints(v), Vec2d.yPoints(v), v.length);
//		g2.dispose();
		
		gjk_thread = new Thread(new Runnable() {
			public void run() {
				synchronized (gjk_thread) {
				while(gjk_running)
				{
					try {
						System.out.println("new intersection test");
						GJK.intersect(shapes[0], shapes[1]);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}
			}
		});
		//gjk_thread.start();
	}
	public void stepGJK()
	{
		synchronized (gjk_thread) {
			gjk_thread.notify();			
		}
	}
	private static final int BLACK = 0b11111111000000000000000000000000;
	public void computeRayMarching()
	{
		long t1 = System.currentTimeMillis();
		Vec2d pos = new Vec2d(0, 0);
		for(int y = 0; y < height; y++)
		{
			pos.y = y;
			for(int x = 0; x < width; x++)
			{
				pos.x = x;
				
				Vec2d dir = Vec2d.subtract(pos, s.pos); //start
				dir.normalize();
				Vec2d p = new Vec2d(s.pos);
				boolean done = false;
				boolean exposed = false;
				int iters = 0;
				
				int min_step = 255;
				
				while(!done)
				{
					float min_dist = shapes[0].distance(p, false).length();
					for(int i = 1; i < shapes.length; i++) // finding shortest distance
					{
						Vec2d dist = shapes[i].distance(p, false);
						float dist_length = dist.length();
						if(dist_length < min_dist)
							min_dist = dist_length;
					}
					
					p.add(dir.x*min_dist, dir.y*min_dist);
					
					if(min_dist < min_step)
						min_step = (int) min_dist;
					
					if(min_dist < 0.5f)
					{
						done = true;
//						exposed = false;
					}
					if(min_dist > 1000)
					{
						done = true;
//						exposed = true;
					}
					iters++;
				}
				if(iters > 255)
					iters = 255;
				
				if(Vec2d.dist(s.pos, pos) > Vec2d.dist(p, s.pos))
					exposed = true;
				else
					exposed = false;
				
				
				if(exposed)
					img_data[y*width + x] = BLACK;//(255 - min_step) << 24;//img_data[y*width + x] = 0;
				else
					img_data[y*width + x] = 0;
				
//				img_data[y*width + x] = new Color(iters, iters, iters).getRGB();
			}
		}
		for(int i = 0; i < img_data.length; i++)
			buffer_data[i] = img_data[i];
		System.out.println("computed lighting in: " + (System.currentTimeMillis() - t1) + " milliseconds");
	}
	int partitions = 4;
	int dist = 1000;
	public void computeRaycasting(int index)
	{
		long t1 = System.currentTimeMillis();
		LineSegment ls = new LineSegment(s.pos, new Vec2d(0, 0));
		int begin = (height / partitions) * index;
		int end = begin + (height / partitions);
		
//		System.out.println("begin: " + begin + ", end: " + end);
		
		for(int y = begin; y < end; y++)
		{
			ls.b.y = y;
			for(int x = 0; x < width; x++)
			{
				ls.b.x = x;
				
				boolean exposed = true;
				
				if(Math.abs(s.pos.x - x) > dist || Math.abs(s.pos.y - y) > dist)
					continue;
				
				for(int i = 0; i < shapes.length; i++)
				{
					if(shapes[i].intersects(ls))
					{
						exposed = false;
						break;
					}
				}
				
				if(exposed)
				{
//					System.out.println(x + ", " + y);
					img_data[y*width + x] = 0;
				}
				else
				{
					int dist = Math.min((int) (Vec2d.dist2(ls.a, ls.b) / this.dist), 255);
					img_data[y*width + x] = (255 - dist) << 24;
				}
				
//				img_data[y*width + x] = new Color(iters, iters, iters).getRGB();
			}
		}
//		System.out.println("computed lighting in: " + (System.currentTimeMillis() - t1) + " milliseconds");
//		for(int i = 0; i < img_data.length; i++)
//			buffer_data[i] = img_data[i];
	}
	public void computeSoftShadows()
	{
		long t1 = System.currentTimeMillis();
		Vec2d pos = new Vec2d(0, 0);
		for(int y = 0; y < height; y++)
		{
			pos.y = y;
			for(int x = 0; x < width; x++)
			{
				pos.x = x;
				
				float exposure = 1.0f;
				
				//FIXME - compute the amount of exposure to the pixel
				
				float light_level = intensity - s.distance(pos, false).length();
				light_level *= exposure;
				
				if(light_level > 255)
					light_level = 255;
				else if(light_level < 0)
					light_level = 0;
								
				int grayscale = (int) light_level;
				
				img_data[y*width + x] = (255-grayscale << 24);// + (grayscale << 8) + (grayscale);
			}
		}
		System.out.println("computed lighting in: " + (System.currentTimeMillis() - t1) + " milliseconds");
		for(int i = 0; i < img_data.length; i++)
			buffer_data[i] = img_data[i];		
	}
//	public void computeLighting()
//	{
//		System.out.println("started");
//		long t1 = System.currentTimeMillis();
//		Vec2d pos = new Vec2d(0, 0);
//		for(int y = 0; y < height; y++)
//		{
//			pos.y = y;
//			for(int x = 0; x < width; x++)
//			{
//				pos.x = x;
//				Vec2d dir = Vec2d.subtract(pos, s.pos);
//				dir.normalize();
//				Vec2d axis = dir.rightNormal();
//				
//				boolean exposed = true;
//				
//				for(int i = 2; i < 3; i++)
//				{
//					Vec2d bounds = shapes[i].projectedBounds(axis, s.pos);
//					if(bounds.x < 0 && bounds.y > 0)
//					{
//						exposed = false;
//						break;
//					}
//				}
//				
//				if(exposed)
//					img_data[y*width + x] = 0;
//				else
//					img_data[y*width + x] = BLACK;
//			}
//		}
//		System.out.println("computed lighting in: " + (System.currentTimeMillis() - t1) + " milliseconds");
//	}
	private int index = 0;
	public void paint(Graphics g) {


		if (bufferStrategy == null) {
			this.createBufferStrategy(2);
			bufferStrategy = this.getBufferStrategy();
			bufferGraphics = bufferStrategy.getDrawGraphics();

			this.thread.start();
			
			for(; index < partitions; index++)
			{
//				Thread thread2 = new Thread(() -> {
//					while(true)
//					{
//						int ind = index;
//						computeRaycasting(ind);
//					}
//				});
//				Thread thread2 = new Thread(new Runnable()
//				{
//					int ind = index;
//					public void run()
//					{
//						while(running)
//							computeRaycasting(ind);
//					}
//					
//				});
//				thread2.start();
			}
//			index = 0;
			
		}
	}
	@Override
	public void run() {
		// what runs when editor is running
		
		while (running) {
			long t1 = System.currentTimeMillis();
			
			DoLogic();
			Draw();

			DrawBackbufferToScreen();

			Thread.currentThread();
			try {
				Thread.sleep(refreshTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long t2 = System.currentTimeMillis();
			
			if(t2 - t1 > 1)
			{
				if(refreshTime > 1)
					refreshTime --;
			}
			else
				refreshTime ++;
		}
		gjk_running = false;
	}

	public void DrawBackbufferToScreen() {
		bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();
	}

	public void DoLogic()
	{
		if(collision && selected != s)
		{
			vel.add(0, 0.01f);
			s.move(vel);
			
			for(int i = 0; i < shapes.length; i++)
			{
				Vec2d move = Geometry.intersectionNormal(s, shapes[i]);
				move.negate();
				s.move(move);
				if(move.length() > 0)
				{
					vel = Vec2d.bounce(vel, move, restitution, friction);
					s.move(vel);
				}
			}
		}
	}

	public void Draw() // titleScreen
	{
		// clears the backbuffer
		bufferGraphics = bufferStrategy.getDrawGraphics();
		try {
			bufferGraphics.clearRect(0, 0, this.getSize().width, this.getSize().height);
			// where everything will be drawn to the backbuffer
			Graphics2D g2 = (Graphics2D) bufferGraphics;
			
			//g2.setFont(font);
			g2.setColor(new Color(255, 127, 0, 100));
			
			g2.drawImage(img, 0, 0, null);
			
			float push = 0.5f;
			
			boolean[] gjk_intersected = new boolean[shapes.length];
			for(int i = 0; i < shapes.length; ++i)
			{
				for(int j = 0; j < i; j++)
				{
					if(GJK.intersect(shapes[i],  shapes[j]))
					{
						gjk_intersected[i] = true;
						gjk_intersected[j] = true;
					}
				}
			}
			
			boolean[] is_intersected = new boolean[4];
			for(int i = 0; i < shapes.length; i++)
			{
				for(int j = 0; j < shapes.length; j++)
				{
					if(i==j)
						continue;
					Vec2d poi = new Vec2d(0, 0);
					Vec2d normal = Geometry.intersectionNormal(shapes[i], shapes[j], poi);
					if(!poi.equals(Vec2d.ZERO))
					{
						g2.setColor(Color.GREEN);
						poi.debugDraw(g2, 4);
					}
					g2.setColor(Color.BLUE);
					int center_x = 0;
					int center_y = 0;
					if(shapes[j].getClass().equals(Circle.class))
					{
						Circle shape = (Circle) shapes[j];
						center_x = (int) shape.pos.x;
						center_y = (int) shape.pos.y;
						if(collision)
						{
							shapes[i].move(Vec2d.mult(normal, -push));
							shapes[j].move(Vec2d.mult(normal, push));
						}
					}
					else
					{
						Polygon2d shape = (Polygon2d) shapes[j];
						Vec2d avg = new Vec2d(0, 0);
						for(int k = 0; k < shape.vertices.length; k++)
							avg.add(shape.vertices[k]);
						avg.mult(1f/shape.vertices.length);
						center_x = (int) avg.x;
						center_y = (int) avg.y;
						if(collision)
						{
							shapes[i].move(Vec2d.mult(normal, -push));
							shapes[j].move(Vec2d.mult(normal, push));
						}
					}
					g2.fillOval(center_x-2, center_y-2, 4, 4);
					g2.drawLine(center_x, center_y, center_x + (int) normal.x, center_y + (int) normal.y);
					if(Geometry.intersected(shapes[i], shapes[j]))
						is_intersected[i] = true;
				}
				shapes[i].debugDraw(g2, gjk_intersected[i]);
			}
			s.debugDraw(g2, true);
			g2.setColor(Color.RED);
			
			float min_dist = Float.MAX_VALUE;
			Vec2d dist_vector = new Vec2d(0, 0);
			for(int i = 0; i < shapes.length; i++)
			{
				Vec2d dist = shapes[i].distance(new Vec2d(mousex, mousey), false);
				float dist_length = dist.length();
				if(dist_length < min_dist)
				{
					min_dist = dist_length;
					dist_vector = dist;
				}
			}
//			
//			g2.drawOval(mousex - (int) min_dist, mousey - (int) min_dist, (int) (min_dist*2), (int) (min_dist*2));
			g2.drawLine(mousex, mousey, mousex - (int) dist_vector.x, mousey - (int) dist_vector.y);
			
			g2.setColor(Color.BLACK);
			g2.drawString("Drag shapes", 20, 15);
			g2.drawString("shortest distance to shape", 20, 30);
			g2.drawString("in collision", 20, 45);
			g2.drawString("direction to out", 20, 60);
			g2.drawString("press \'k\'  to " + (collision? "disable":"enable") + " collision", 20, 75);
			g2.drawString("simplex size: " + GJK.s.length, 20, 90);
			
			g2.setColor(Color.RED);
			g2.drawLine(5, 25, 15, 25);
			
			g2.setColor(new Color(255, 127, 0, 100));
			g2.fillRect(5, 35, 10, 10);
			
			g2.setColor(Color.BLUE);
			g2.drawLine(5, 55, 15, 55);
			
			
			if(show_ray_march)
			{
				Vec2d pos = new Vec2d(mousex, mousey);
				Vec2d dir = Vec2d.subtract(pos, s.pos); //start
				dir.normalize();
				Vec2d p = new Vec2d(s.pos);
				boolean done = false;
				while(!done)
				{
					float md = shapes[0].distance(p, false).length();
					for(int i = 1; i < shapes.length; i++) // finding shortest distance
					{
						Vec2d dist = shapes[i].distance(p, false);
						float dist_length = dist.length();
						if(dist_length < md)
							md = dist_length;
					}
					
					g2.drawOval((int) (p.x - md), (int) (p.y - md), (int) (md*2), (int) (md*2));
					g2.fillOval((int) (p.x - 3), (int) (p.y - 3), (int) (6), (int) (6));
					
					p.add(dir.x*md, dir.y*md);
					
					if(md < 1)
					{
						done = true;
					}
					if(md > 500)
					{
						done = true;
					}
				}
			}
			
			
			
			
//			float[][] transform = {
//					{100, 100},
//					{300, 200},
//					{200, 400},
//					{50, 300}
//			};
//			g2.drawPolygon(Utils.xPoints(transform), Utils.yPoints(transform), transform.length);
//			
//			float[] point = {0, 0};
//			for(int x = 0; x <= 10; x++)
//			{
//				for(int y = 0; y <= 10; y++)
//				{
//					float[] pointa = DrawUtils.getPoint(transform, point);
//					int[] xy = {(int) pointa[0], (int) pointa[1]};
//					g2.fillOval(xy[0], xy[1], 4, 4);
//					point[1] += 0.1f;
//				}
//				point[0] += 0.1f;
//				point[1] = 0;
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bufferGraphics.dispose();
		}
	}

	boolean collision = false;
	@Override
	public void keyPressed(KeyEvent e) {
		if (!keysDown.contains(e.getKeyCode()) && e.getKeyCode() != 86)
			keysDown.add(new Integer(e.getKeyCode()));
		if(keysDown.contains(KeyEvent.VK_K))
			collision = !collision;
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown.remove(new Integer(e.getKeyCode()));
		
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//addShape = true;
//		float[] pos = {e.getX(), e.getY()};
//		pos = sm.toMap(pos[0], pos[1]);
//		Material mat = new Material(1.0f, 0.5f, 0.1f);
//		Body cube = new Body(pos[0], pos[1], true, mat);
//		cube.createBoxShape(new Vec2d(0.5f, 0.5f));
//		cm.addBody(cube);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
	Shape2d selected = null;
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1)
		{
			leftClick = true;
			for(int i = 0; i < shapes.length; i++)
				if(shapes[i].intersects(new Vec2d(mousex, mousey)))
					selected = shapes[i];
			if(s.intersects(new Vec2d(mousex, mousey)))
				selected = s;
		}
		else if(e.getButton() == 2)
		{
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1)
		{
			leftClick = false;
			selected = null;
		}
		if(e.getButton() == 2)
			dragging = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(leftClick)
		{
			leftClick = true;
			if(selected != null)
			{
				if(selected.getClass().equals(Circle.class))
				{
					Circle circle = (Circle) selected;
					circle.pos.add(e.getX()-mousex, e.getY()-mousey);
				}
				else if(selected.getClass().equals(Polygon2d.class))
				{
					Polygon2d polygon = (Polygon2d) selected;
					for(int i = 0; i < polygon.vertices.length; i++)
						polygon.vertices[i].add(e.getX()-mousex, e.getY()-mousey);
				}
			}
		}
		mousex = e.getX();
		mousey = e.getY();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
	}

}
class Compiler implements JavaCompiler {

	@Override
	public Set<SourceVersion> getSourceVersions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int run(InputStream arg0, OutputStream arg1, OutputStream arg2, String... arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int isSupportedOption(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener,
			Locale locale, Charset charset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompilationTask getTask(Writer out, JavaFileManager fileManager,
			DiagnosticListener<? super JavaFileObject> diagnosticListener, Iterable<String> options,
			Iterable<String> classes, Iterable<? extends JavaFileObject> compilationUnits) {
		// TODO Auto-generated method stub
		return null;
	}
	
}