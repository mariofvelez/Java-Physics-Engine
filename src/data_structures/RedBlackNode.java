package data_structures;

public class RedBlackNode<T> {
	
	public static enum Color {RED, BLACK};
	protected Color color;
	
	public T key;
	public RedBlackNode<T> left;
	public RedBlackNode<T> right;
	public RedBlackNode<T> p;
	
	public RedBlackNode(T key)
	{
		this.key = key;
	}
	public RedBlackNode()
	{
		this(null);
	}
	public void setBlack()
	{
		color = Color.BLACK;
	}
	public void setRed()
	{
		color = Color.RED;
	}
	public boolean isBlack()
	{
		return color == Color.BLACK;
	}
	public boolean isRed()
	{
		return color == Color.RED;
	}

}
