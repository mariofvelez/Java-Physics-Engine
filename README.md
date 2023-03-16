# Java-Physics-Engine
A fully-functional physics engine built from scratch in Java.

# Demo (Click to View)
[![Java Physics Engine Demo](http://img.youtube.com/vi/T8EzDo6zRXo/0.jpg)](http://www.youtube.com/watch?v=T8EzDo6zRXo "Java Physics Engine Demo")

# Features
- Polygon-polygon, circle-circle, and polygon-circle collision detection and response
- 2D Kinematics with forces and rotation
- Rigid body dynamics with restitution
- Static (immovable) objects like walls and ground
- Inertia, mass, and center of gravity
- Object pooling for fast creation of bodies

# Getting Started
<h3> Creating the World </h3>
Create a new World object and set the gravity vector (units per second squared)

```
World world = new World();
world.setGravity(new Vec2d(0.0f, 9.8f));
```

Additionally, you can set the time step of the world and iterations per time step.
The default value for dt is 1f/60, and
the default value for iters is 8

```
world.dt = 1.0f / 144.0f; // seconds elapsed per time step
world.iters = 16; // iterations per time step
```

To run the physics simulation, use the step() function. This should be done in a loop

```
world.step(); // calculates the next state of the world after dt seconds
```

<h3> Creating an Object </h3>
Bodies are all physical objects in the world that can collide with one another

To create one, you need a position (Vec2d) and a collision type (CollisionType)

```
Body b1 = new Body(new Vec2d(0.0f, 0.0f), CollisionType.DYNAMIC);
```

There are 2 collision types:
- STATIC: the body has infinite mass and momentum, unaffected by gravity, and does not interact with other static objects
- DYNAMIC: the body has a specified mass, affected by gravity, and interacts with both static and dynamic bodies

Next we can give the body some properties.

The default value for density is 1, and the default for restitution is 1

```
body.density = 0.8f;
body.restitution = 0.5f; // restitution is also referred to as bounciness or elasticity
```

Next, we need to attach a shape to our body, and give it geometry so it can interact with other bodies

There are two types of shapes we can add:
<h4> Circle </h4>
We create a circle with a position (relative to the body's position. It's best to keep it centered at (0, 0) to avoid complication), and a radius

```
Circle circle = new Circle(new Vec2d(0.0f, 0.0f), 3f);
body.setShape(circle); // attaches the circle to our body
```

<h4> Polygon </h4>
We create a polygon with an array of vectors relative to the body's position

```
// in this instance we are creating a triangle
Vec2d[] vertices = {
  new Vec2d(0.0f, 1.0f),
  new Vec2d(-1.0f, -1.0f),
  new Vec2d(1.0f, -1.0f)
};
Polygon2d polygon = new Polygon2d(vertices);
body.setShape(polygon);
```

Additionally, we can create a box shape quickly with the static function createAsBox(Vec2d, Vec2d)

The first parameter is the position, and the second parameter is half the dimensions of the box

```
// in this case, the box has a width of 3 and a height of 2, centered in the middle of the body
Polygon2d polygon = Polygon2d.createAsBox(new Vec2d(0.0f, 0.0f), new Vec2d(l.5f, l.0f));
```

Finally, we need to add our body to the world

```
world.addBody(body);
```

You can add as many bodies as you'd like, however the computation time scales quadratically (O(n^2) time) with dynamic bodies, and linearly (O(n) time) with static bodies. This will be improved later using a qaudtree

# What I learned
- Computational geometry - polygon and circle collision, as well as center of mass and moments of inertia
- Linear algebra - matrix transforms, rotations
- Implicit methods of integration - Euler, Runga-Kutta 4

# Things to Implement
- Voronoi partitioning using Fortune's Algorithm
- Collision optimization: impulse calculations and broad phase detection
- Fix friction impulse
- Ray and area querying
- RK4 integration with constraints
- Hinge, distance, prismatic, gear, and other constraint implementation
- Camera controls for testbed
- World saving and loading from file
- Level Editor
- FLIP fluids
- Continuous Collision Detection
- Soft bodies
