package test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

import geometry.Circle;
import geometry.Polygon2d;
import math.Transform;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import test.tests.*;

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
	boolean leftClick;
	public static int mousey = 0;

	public static ArrayList<Integer> keysDown; // holds all the keys being held down
	private Thread thread;

	private boolean running;
	private int runTime;
	private float seconds;
	private int refreshTime;
	
	public static int[] anchor = new  int[2];
	public static boolean dragging;
	
	Test test;
	
	Random r = new Random();
	
	ArrayList<Vec2d> v;
	
//	Diagram diagram;
	
	public Field(Dimension size) throws Exception {
		this.setPreferredSize(size);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);

		this.thread = new Thread(this);
		running = true;
		runTime = 0;
		seconds = 0;
		refreshTime = (int) (1f/50 * 1000);

		keysDown = new ArrayList<Integer>();
		
//		FortuneAlgorithm algo = new FortuneAlgorithm(v);
//		algo.construct();
//		diagram = algo.getDiagram();
		
//		body_a = new Body(new Vec2d(size.width / 2, size.height / 2));
//		body_b = new Body(new Vec2d(size.width / 2 + 5, size.height - 200));
		
//		
//		Vec2d[] verts = {
//				new Vec2d(600, 335),
//				new Vec2d(653, 371),
//				new Vec2d(597, 455),
//				new Vec2d(510, 437),
//				new Vec2d(532, 375)
//		};
//		Polygon2d polygon = new Polygon2d(verts);
//		polygon.move(-600, -400);
//		body_a.setShape(polygon);
//		Vec2d[] verts2 = {
//				new Vec2d(600, 335),
//				new Vec2d(653, 371),
//				new Vec2d(597, 455),
//				new Vec2d(510, 437),
//				new Vec2d(532, 375)
//		};
//		Polygon2d polygon2 = new Polygon2d(verts2);
//		polygon2.move(-600, -400);
//		body_b.setShape(polygon2);
//		
//		
////		body_a.gravity_scale = 0;
////		body_b.gravity_scale = 0;
////		body_b.setCollisionType(CollisionType.STATIC);
////		body_b.density = 0.1f;
//		
//		world.addBody(body_a);
//		world.addBody(body_b);
//		body_a.setRotationSpeed(-0.5f);
//		body_b.setRotationSpeed(1f);
//		body_a.restitution = 0.5f;
//		body_b.restitution = 0.5f;
		
		
//		Body body = new Body(new Vec2d(size.width/2, size.height / 3));
//		body.density = 5f;
//		body.restitution = 0.7f;
//		body.friction = 0.5f;
//		
//		Circle shape = new Circle(new Vec2d(), 30);
//		body.setShape(shape);
//		
//		world.addBody(body);
		
	}

	public void paint(Graphics g) {


		if (bufferStrategy == null) {
			this.createBufferStrategy(2);
			bufferStrategy = this.getBufferStrategy();
			bufferGraphics = bufferStrategy.getDrawGraphics();

			this.thread.start();
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
			
			if(t2 - t1 > 16)
			{
				if(refreshTime > 0)
					refreshTime --;
			}
			else
				refreshTime ++;
			
			seconds += refreshTime/1000f;
			//System.out.println(t2 - t1);
			

		}
	}

	public void DrawBackbufferToScreen() {
		bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();
	}
	
	int[] ms = new int[32];
	float ms_avg = 0;
	public void DoLogic() {
		
//		local = new Vec2d(10, 10);
//		body_b.localToWorld(local);
//		Vec2d force = Vec2d.subtract(new Vec2d(mousex, mousey), local);
//		force.mult(0.1f);
//		body_b.applyForceWorld(local, force);
////		body_b.vel.mult(0.98f);
////		body_b.vel.add(0, 9.8f);
//		body_b.setRotationSpeed(body_b.getrotationSpeed() * 0.98f);
		
		if(test == null)
			test = new FrictionTest(this);
		
		if(mouseDown != null)
		{
			Vec2d pos = new Vec2d(mouseDown);
			body.localToWorld(pos);
			Vec2d mouse = new Vec2d(mousex, mousey);
			Transform inverse = new Transform(test.transform);
			test.transform.invert3x3(inverse);
			inverse.project2D(mouse);
			Vec2d force = Vec2d.subtract(mouse, pos);
			force.mult(5.0f);
			body.applyForceWorld(pos, force);
		}
		
		long t1 = System.currentTimeMillis();
		
		test.step();
		
		long t2 = System.currentTimeMillis();
		
		ms[runTime % ms.length] = (int) (t2 - t1);
		ms_avg = 0;
		for(int i = 0; i < ms.length; ++i)
			ms_avg += ms[i];
		ms_avg /= ms.length;
		
		runTime++;
	}

	public void Draw() // titleScreen
	{
		// clears the backbuffer
		bufferGraphics = bufferStrategy.getDrawGraphics();
		try {
			bufferGraphics.clearRect(0, 0, this.getSize().width, this.getSize().height);
			// where everything will be drawn to the backbuffer
			Graphics2D g2 = (Graphics2D) bufferGraphics;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			test.draw(g2);
			
			g2.setColor(Color.BLACK);
//			Vec2d centroid = new Vec2d(body_b.centroid);
//			body_b.transform.project2D(centroid);
//			centroid.debugDraw(g2);
			
//			g2.setColor(Color.RED);
//			local.debugDraw(g2);
			
//			Polygon2d p = (Polygon2d) body_b.shape;
//			for(int i = 0; i < p.vertices.length; ++i)
//			{
//				Vec2d a = new Vec2d(p.vertices[i]); // world coordinates 
//				body_b.transform.project2D(a);
//				Vec2d b = new Vec2d(a);
//				body_b.pointVelocityFromWorld(b, 0.16f);
//				g2.drawLine((int) a.x, (int) a.y, (int) (a.x + b.x), (int) (a.y + b.y));
//			}
//			
//			g2.setColor(Color.BLUE);
//			if(poc != null)
//				poc.debugDraw(g2);
			
//			g2.setColor(Color.BLUE);
//			for(int i = 0; i < v.size(); ++i)
//			{
//				v.get(i).debugDraw(g2, 3);
//			}
//			g2.setColor(Color.RED);
//			diagram.draw(g2);
			
			
			if(mouseDown != null)
			{
				Vec2d pos = new Vec2d(mouseDown);
				body.localToWorld(pos);
				test.transform.project2D(pos);
				g2.setColor(Color.BLUE);
				g2.drawLine((int) pos.x, (int) pos.y, mousex, mousey);
				g2.setColor(Color.BLACK);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bufferGraphics.dispose();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!keysDown.contains(e.getKeyCode()) && e.getKeyCode() != 86)
			keysDown.add(new Integer(e.getKeyCode()));
		
		
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
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
	Vec2d mouseDown = null;
	Body body = null;
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1)
		{
			leftClick = true;
			
			boolean grabbed = false;
			for(int i = 0; i < test.world.d_bodies.size(); ++i)
			{
				Body body_b = test.world.d_bodies.get(i);
				Vec2d mouse = new Vec2d(mousex, mousey);
				Transform inverse = new Transform(test.transform);
				test.transform.invert3x3(inverse);
				inverse.project2D(mouse);
				if(body_b.getWorldShape().intersects(mouse))
				{
					mouseDown = new Vec2d(mouse);
					body_b.worldToLocal(mouseDown);
					body = body_b;
					grabbed = true;
					break;
				}
			}
			if(!grabbed)
			{
				Body body = new Body(new Vec2d(mousex, mousey), CollisionType.DYNAMIC);
				
				if(r.nextBoolean())
				{
					Vec2d[] verts = new Vec2d[(int) (r.nextFloat() * 6) + 3];
					for(int x = 0; x < verts.length; ++x)
					{
						verts[x] = Vec2d.fromPolar(((float) x / verts.length) * Math.PI * 2, r.nextFloat() * 10 + 20);
					}
					Polygon2d p = new Polygon2d(verts);
					body.setShape(p);
				}
				else
				{
					Circle c = new Circle(new Vec2d(), r.nextFloat() * 10 + 20);
					body.setShape(c);
				}
				test.world.addBody(body);
//				body.setRotation(r.nextFloat() * (float) Math.PI * 2);
				body.restitution = 0.5f;
			}
			
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
			
//			if(body.getWorldShape().intersects(mouseDown))
//			{
//				Vec2d force = Vec2d.subtract(new Vec2d(mousex, mousey), mouseDown);
//				force.mult(10);
//				body_b.applyForceWorld(mouseDown, force);
//			}
			
			mouseDown = null;
			body = null;
		}
		if(e.getButton() == 2)
			dragging = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(leftClick)
			leftClick = true;
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