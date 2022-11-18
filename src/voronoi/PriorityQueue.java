package voronoi;

import java.util.ArrayList;

public class PriorityQueue<T extends IndexListener<T>> {
	
	private ArrayList<T> elements;
	
	public PriorityQueue()
	{
		elements = new ArrayList<>();
	}
	public boolean isEmpty()
	{
		return elements.isEmpty();
	}
	public T pop()
	{
		swap(0, elements.size()-1);
		T top = elements.get(elements.size()-1);
		elements.remove(elements.size()-1);
		siftDown(0);
		return top;
	}
	public void push(T element)
	{
		element.setIndex(elements.size());
		elements.add(element);
		siftUp(elements.size()-1);
	}
	public void update(int i)
	{
		int parent = getParent(i);
		if(parent >= 0 && elements.get(parent).compareTo(elements.get(i)) < 0)
			siftUp(i);
		else
			siftDown(i);
	}
	public void remove(int i)
	{
		swap(i, elements.size() - 1);
		elements.remove(elements.size()-1);
		if(i < elements.size())
			update(i);
	}
	private int getParent(int i)
	{
		return (i + 1) / 2 - 1;
	}
	private int getLeftChild(int i)
	{
		return 2 * (i + 1) - 1;
	}
	private int getRightChild(int i)
	{
		return 2 * (i + 1);
	}
	private void siftDown(int i)
	{
		int left = getLeftChild(i);
		int right = getRightChild(i);
		int j = i;
		if(left < elements.size() && elements.get(j).compareTo(elements.get(left)) < 0)
			j = left;
		if(right < elements.size() && elements.get(j).compareTo(elements.get(right)) < 0)
			j = right;
		if(j != i)
		{
			swap(i, j);
			siftDown(j);
		}
	}
	private void siftUp(int i)
	{
		int parent = getParent(i);
		if(parent >= 0 && elements.get(parent).compareTo(elements.get(i)) < 0)
		{
			swap(i, parent);
			siftUp(parent);
		}
	}
	private void swap(int i, int j)
	{
		T temp = elements.get(i);
		elements.set(i, elements.get(j));
		elements.set(j, temp);
		elements.get(i).setIndex(i);
		elements.get(j).setIndex(j);
	}

}
