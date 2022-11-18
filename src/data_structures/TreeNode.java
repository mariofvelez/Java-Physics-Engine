package data_structures;

public class TreeNode<T> {
	
	public T key;
	public TreeNode<T> left;
	public TreeNode<T> right;
	
	public TreeNode()
	{
		
	}
	public TreeNode(T key)
	{
		this.key = key;
	}
	public void setKey(T key)
	{
		this.key = key;
	}
	public T getKey()
	{
		return key;
	}
	public void setRight(TreeNode<T> right)
	{
		this.right = right;
	}
	public TreeNode<T> getRight()
	{
		return right;
	}
	public void setLeft(TreeNode<T> left)
	{
		this.left = left;
	}
	public TreeNode<T> getLeft()
	{
		return left;
	}
	

}
