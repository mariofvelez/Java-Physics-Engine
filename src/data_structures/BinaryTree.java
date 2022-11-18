package data_structures;

import java.util.Comparator;
import java.util.function.Consumer;

public class BinaryTree<T> {
	
	public TreeNode<T> root;
	
	public BinaryTree()
	{
		
	}
	public TreeNode<T> getRoot()
	{
		return root;
	}
	public void setLeftChild(TreeNode<T> parent, TreeNode<T> left) throws Exception
	{
		if(parent == null)
			throw new Exception("Runtime error - parent " + parent + " is null!");
		if(parent.getLeft() != null)
			throw new Exception("Runtime error - left child of " + parent + " has already been set!");
		
		parent.setLeft(left);
	}
	public void setRightChild(TreeNode<T> parent, TreeNode<T> right) throws Exception
	{
		if(parent == null)
			throw new Exception("Runtime error - parent " + parent + " is null!");
		if(parent.getRight() != null)
			throw new Exception("Runtime error - right child of " + parent + " has already been set!");
		
		parent.setRight(right);
	}
	public void insertBST(T key, Comparator<T> comp) throws Exception
	{
		TreeNode<T> tree_node = new TreeNode<>(key);
		if(root == null)
		{
			root = tree_node;
			return;
		}
		TreeNode<T> p, q;
		
		p = q = root;
		
		while(q != null)
		{
			p = q;
			int compare = comp.compare(tree_node.getKey(), p.getKey());
			if(compare < 0)
				q = p.getLeft();
			else if(compare > 0)
				q = p.getRight();
			else
				return;
		}
		int compare = comp.compare(tree_node.getKey(), p.getKey());
		if(compare < 0)
			setLeftChild(p, tree_node);
		else if(compare > 0)
			setRightChild(p, tree_node);
	}
	public void insertBST(T[] keys, Comparator<T> comp) throws Exception
	{
		for(int i = 0; i < keys.length; ++i)
			insertBST(keys[i], comp);
	}
	public TreeNode<T> search(T key, Comparator<T> comp)
	{
		TreeNode<T> p;
		
		if(root == null)
			return null;
		
		p = root;
		while(p != null)
		{
			int compare = comp.compare(key, p.getKey());
			if(compare < 0)
				p = p.getLeft();
			else if(compare > 0)
				p = p.getRight();
			else
				return p;
		}
		return null;
	}

}
