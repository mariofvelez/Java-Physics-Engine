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

import debug.DebugDrawInfo;
import geometry.Circle;
import geometry.Polygon2d;
import math.Vec2d;
import physics.CollisionInfo;
import physics.CollisionListener;
import physics.World;
import physics.body.Body;
import physics.body.CollisionType;

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
	
	World world;
	
	Vec2d poc = null;
	
	Random r = new Random();
	
	int collisions = 0;
	
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
		
		world = new World();
		world.setGravity(new Vec2d(0, 550.0f));
		
		v = new ArrayList<>();;
		for(int i = 0; i < 20; ++i)
		{
			Vec2d vec = new Vec2d(r.nextFloat() * 300 + 50, r.nextFloat() * 300 + 50);
			v.add(vec);
		}
		
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
		
		createGround(size);
		
		float speed = 3;
		
		createPaddle(new Vec2d(700, 400), -speed);
		createPaddle(new Vec2d(900, 400), speed);
		createPaddle(new Vec2d(1000, 600), -speed);
		createPaddle(new Vec2d(600, 600), speed);
		
		for(int i = 0; i < 40; ++i)
		{
			Body body = new Body(new Vec2d(r.nextFloat() * 200 + 300, r.nextFloat() * 200 + 300), CollisionType.DYNAMIC);
			
			Vec2d[] verts = new Vec2d[(int) (r.nextFloat() * 6) + 3];
			for(int x = 0; x < verts.length; ++x)
			{
				verts[x] = Vec2d.fromPolar(((float) x / verts.length) * Math.PI * 2 + Math.PI/4, r.nextFloat() * 10 + 20);
			}
			Polygon2d p = new Polygon2d(verts);
			body.setShape(p);
			world.addBody(body);
//			body.setRotation(r.nextFloat() * (float) Math.PI * 2);
			body.restitution = 0.7f;
			body.friction = 0.5f;
		}
		
//		Body body = new Body(new Vec2d(size.width/2, size.height / 3));
//		body.density = 5f;
//		body.restitution = 0.7f;
//		body.friction = 0.5f;
//		
//		Circle shape = new Circle(new Vec2d(), 30);
//		body.setShape(shape);
//		
//		world.addBody(body);
		
		world.setCollisionListener(new CollisionListener() {
			
			@Override
			public void beforeSolve(CollisionInfo info) {
				poc = info.poc;
				
			}
			
			@Override
			public void afterSolve(CollisionInfo info) {
				// TODO Auto-generated method stub
				collisions++;
			}
		});
		
	}
	
	public void createGround(Dimension size)
	{
//		Body b = new Body(new Vec2d(size.width / 2, size.height - 60), CollisionType.STATIC);
		Body br = new Body(new Vec2d(size.width - size.width / 4 + 40, size.height - 160), CollisionType.STATIC);
		Body bl = new Body(new Vec2d(size.width / 4 - 40, size.height - 160), CollisionType.STATIC);
//		Body t = new Body(new Vec2d(size.width / 2, size.height / 2), CollisionType.STATIC);
		Body l = new Body(new Vec2d(20, size.height / 2), CollisionType.STATIC);
		Body r = new Body(new Vec2d(size.width - 40, size.height / 2), CollisionType.STATIC);
		
		Polygon2d pbr = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(size.width / 4 - 40, 20));
		Polygon2d pbl = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(size.width / 4 - 40, 20));
		Polygon2d pt = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(50, 8));
		Polygon2d pl = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(20, size.height / 2));
		Polygon2d pr = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(20, size.height / 2));
		
		br.setRotation(-0.2f);
		bl.setRotation(0.2f);
//		b.setShape(pb);
		br.setShape(pbr);
		bl.setShape(pbl);
//		t.setShape(pt);
		l.setShape(pl);
		r.setShape(pr);
		
//		setGroundParameters(b);
		setGroundParameters(br);
		setGroundParameters(bl);
//		setGroundParameters(t);
		setGroundParameters(l);
		setGroundParameters(r);
		
		
//		world.addBody(b);
		world.addBody(br);
		world.addBody(bl);
//		world.addBody(t);
		world.addBody(l);
		world.addBody(r);
		
//		t.setRotationSpeed(2f);
		
		for(int i = 0; i < 38; ++i)
		{
			Body body = new Body(new Vec2d(size.width/2, size.height / 3 - i * 50), CollisionType.DYNAMIC);
			body.restitution = 0.3f;
			body.friction = 0.5f;
			
			Circle shape = new Circle(new Vec2d(), 20);
			body.setShape(shape);
			
			world.addBody(body);
		}
	}
	private void setGroundParameters(Body b)
	{
		b.restitution = 0.9f;
		b.friction = 0.5f;
	}
	private void createPaddle(Vec2d pos, float speed)
	{
		Body a = new Body(pos, CollisionType.STATIC);
		Body b = new Body(pos, CollisionType.STATIC);
		
		Polygon2d pa = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(100, 10));
		Polygon2d pb = Polygon2d.createAsBox(Vec2d.ZERO, new Vec2d(10, 100));
		
		a.setShape(pa);
		b.setShape(pb);
		
		setGroundParameters(a);
		setGroundParameters(b);
		
		world.addBody(a);
		world.addBody(b);
		
		a.setRotationSpeed(speed);
		b.setRotationSpeed(speed);
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
	
	Vec2d local = new Vec2d(50, 10);
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
		
		world.forEachBody((body) -> {
			if(body.getPositionUnmodifiable().y > 1000)
				world.removeBody(body);
		});
		
		if(runTime % 1 == 0 && world.getBodySize() < 300)
		{
			Body body = new Body(new Vec2d(r.nextFloat() * 400 + 600, r.nextFloat() * 100), CollisionType.DYNAMIC);
			
			Vec2d[] verts = new Vec2d[(int) (r.nextFloat() * 6) + 3];
			for(int x = 0; x < verts.length; ++x)
			{
				verts[x] = Vec2d.fromPolar(((float) x / verts.length) * Math.PI * 2 + Math.PI/4, r.nextFloat() * 10 + 20);
			}
			Polygon2d p = new Polygon2d(verts);
			body.setShape(p);
			body.vel.set(r.nextFloat() * 400 - 200, r.nextFloat() * 200);
			world.addBody(body);
//			body.setRotation(r.nextFloat() * (float) Math.PI * 2);
			body.restitution = 0.3f;
			body.friction = 0.5f;
		}
		
		if(mouseDown != null)
		{
			Vec2d pos = new Vec2d(mouseDown);
			body.localToWorld(pos);
			Vec2d force = Vec2d.subtract(new Vec2d(mousex, mousey), pos);
			force.mult(5.0f);
			body.applyForceWorld(pos, force);
		}
		
		long t1 = System.currentTimeMillis();
		
		collisions = 0;
		world.step();
		
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
			
			Color stat = new Color(82, 82, 82);
			
			DebugDrawInfo info = new DebugDrawInfo();
			world.forEachBody((body) -> {
				
				if(body.getCollisionType() == CollisionType.DYNAMIC)
				{
					Random r = new Random((int) (body.mass * 200));
					info.col = new Color(r.nextFloat() * 0.5f + 0.5f, r.nextFloat() * 0.5f + 0.5f, r.nextFloat() * 0.5f + 0.5f);
				}
				else
					info.col = stat;
				body.getWorldShape().debugDraw(g2, info);
				
				if(body.shape instanceof Circle)
				{
					Circle circle = (Circle) body.shape;
					Vec2d rad = new Vec2d(circle.radius, 0);
					body.localToWorld(rad);
					Vec2d c = new Vec2d(circle.pos);
					body.localToWorld(c);
					g2.setColor(Color.BLACK);
					g2.drawLine((int) c.x, (int) c.y, (int) rad.x, (int) rad.y);
				}
			});
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
				g2.setColor(Color.BLUE);
				g2.drawLine((int) pos.x, (int) pos.y, mousex, mousey);
			}
			
			g2.drawString("time: " + ms_avg + "ms", 20, 20);
			g2.drawString("objects: " + world.getBodySize(), 20, 40);
			g2.drawString("collisions/frame: " + collisions, 20, 60);

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
			for(int i = 0; i < world.d_bodies.size(); ++i)
			{
				Body body_b = world.d_bodies.get(i);
				Vec2d mouse = new Vec2d(mousex, mousey);
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
				world.addBody(body);
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