package data_structures;

import java.util.Comparator;

import data_structures.RedBlackNode.Color;

public class RedBlackTree<T> {
	
	public RedBlackNode<T> root;
	public RedBlackNode<T> nil;
	
	public RedBlackTree()
	{	
		nil = new RedBlackNode<T>();
		nil.setBlack();
		root = nil;
	}
	public RedBlackNode<T> createNode(T key)
	{
		RedBlackNode<T> node = new RedBlackNode<>();
		node.p = nil;
		node.left = nil;
		node.right = nil;
		node.setRed();
		return node;
	}
	public boolean isEmpty()
	{
		return root == nil;
	}
	public void setRoot(RedBlackNode<T> node)
	{
		root = node;
		root.p = nil;
	}
	/**
	 * finds the smallest value of this tree
	 * @param x - where to start
	 * @returns the node with the smallest value
	 */
	public RedBlackNode<T> min(RedBlackNode<T> x)
	{
		while(x.left != nil)
			x = x.left;
		return x;
	}
	/**
	 * finds the largest value of this tree
	 * @param x - where to start
	 * @returns the node with the largest value
	 */
	public RedBlackNode<T> max(RedBlackNode<T> x)
	{
		while(x.right != nil)
			x = x.right;
		return x;
	}
	public void rotateLeft(RedBlackNode<T> x)
	{
		RedBlackNode<T> y = x.right;
		x.right = y.left;
		
		if(y.left != nil)
			y.left.p = x;
		
		y.p = x.p;
		
		if(x.p == nil)
			root = y;
		else if(x == x.p.left)
			x.p.left = y;
		else
			x.p.right = y;
		
		y.left = x;
		x.p = y;
	}
	public void rotateRight(RedBlackNode<T> y)
	{
		RedBlackNode<T> x = y.left;
		y.left = x.right;
		
		if(x.right != nil)
			x.right.p = y;
		
		x.p = y.p;
		
		if(y.p == nil)
			root = x;
		else if(y == y.p.left)
			y.p.left = x;
		else
			y.p.right = y;
		
		x.right = y;
		y.p = x;
	}
	public void insertRB(T key, Comparator<T> comp)
	{
		RedBlackNode<T> z = new RedBlackNode<>(key);
		RedBlackNode<T> y = nil;
		RedBlackNode<T> x = root;
		
		while(x != nil)
		{
			y = x;
			if(comp.compare(z.key, x.key) < 0)
				x = x.left;
			else
				x = x.right;
		}
		z.p = y;
		if(y == nil)
			root = z;
		else if(comp.compare(z.key, y.key) < 0)
			y.left = z;
		else
			y.right = z;
		
		z.left = nil;
		z.right = nil;
		z.setRed();
	}
	public void insertFixup(RedBlackNode<T> z)
	{
		while(z.p.isRed())
		{
			if(z.p == z.p.p.left)
			{
				RedBlackNode<T> y = z.p.p.right;
				if(y.isRed())
				{
					z.p.setBlack();
					y.setBlack();
					z.p.p.setRed();
					z = z.p.p;
				}
				else
				{
					if(z == z.p.right)
					{
						z = z.p;
						rotateLeft(z);
					}
					z.p.setBlack();
					z.p.p.setRed();
					rotateRight(z.p.p);
				}
			}
			else
			{
				RedBlackNode<T> y = z.p.p.left;
				if(y.isRed())
				{
					z.p.setBlack();
					y.setBlack();
					z.p.p.setRed();
					z = z.p.p;
				}
				else
				{
					if(z == z.p.left)
					{
						z = z.p;
						rotateRight(z);
					}
					z.p.setBlack();
					z.p.p.setRed();
					rotateLeft(z.p.p);
				}
			}
		}
		root.setBlack();
	}
	protected void transplant(RedBlackNode<T> u, RedBlackNode<T> v)
	{
		if(u.p == nil)
			root = v;
		else if(u == u.p.left)
			u.p.left = v;
		else
			u.p.right = v;
		v.p = u.p;
	}
	public void deleteRB(RedBlackNode<T> z)
	{
		RedBlackNode<T> y = z;
		RedBlackNode<T> x;
		Color y_original = y.color;
		
		if(z.left == nil)
		{
			x = z.right;
			transplant(z, z.right);
		}
		else if(z.right == nil)
		{
			x = z.left;
			transplant(z, z.left);
		}
		else
		{
			y = min(z.right);
			y_original = y.color;
			x = y.right;
			
			if(y.p == z)
				x.p = y;
			else
			{
				transplant(y, y.right);
				y.right = z.right;
				y.right.p = y;
			}
			transplant(z, y);
			y.left = z.left;
			y.left.p = y;
			y.color = z.color;
		}
		if(y_original == Color.BLACK)
			deleteFixup(x);
	}
	public void deleteFixup(RedBlackNode<T> x)
	{
		while(x != root && x.isBlack())
		{
			if(x == x.p.left)
			{
				RedBlackNode<T> w = x.p.right;
				if(w.isRed())
				{
					w.setBlack();
					x.p.setRed();
					rotateLeft(x.p);
					w = x.p.right;
				}
				if(w.left.isBlack() && w.right.isBlack())
				{
					w.setRed();
					x = x.p;
				}
				else if(w.right.isBlack())
				{
					w.left.setBlack();
					w.setRed();
					rotateRight(w);
					w = x.p.right;
				}
				w.color = x.p.color;
				x.p.setBlack();
				w.right.setBlack();
				rotateLeft(x.p);
				x = root;
			}
			else
			{
				RedBlackNode<T> w = x.p.left;
				if(w.isRed())
				{
					w.setBlack();
					x.p.setRed();
					rotateRight(x.p);
					w = x.p.left;
				}
				if(w.right.isBlack() && w.left.isBlack())
				{
					w.setRed();
					x = x.p;
				}
				else if(w.left.isBlack())
				{
					w.right.setBlack();
					w.setRed();
					rotateLeft(w);
					w = x.p.left;
				}
				w.color = x.p.color;
				x.p.setBlack();
				w.left.setBlack();
				rotateRight(x.p);
				x = root;
			}
		}
		x.setBlack();
	}
	public void replace(RedBlackNode<T> x, RedBlackNode<T> y)
	{
		transplant(x, y);
		y.left = x.left;
		y.right = x.right;
		if(!isNil(y.left))
			y.left.p = y;
		if(!isNil(y.right))
			y.right.p = y;
		y.color = x.color;
	}
	public boolean isNil(RedBlackNode<T> x)
	{
		return x == nil;
	}

}
