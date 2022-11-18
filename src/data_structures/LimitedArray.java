package data_structures;

import java.util.function.Function;

public class LimitedArray<T> {
	
	/**
	 * size of the array. do not modify
	 */
	public int size;
	boolean[] alive;
	T[] data;
	
	public LimitedArray(int max_size)
	{
		alive = new boolean[max_size];
	}
	public void setArray(T[] arr) throws Exception
	{
		if(arr.length != alive.length)
			throw new Exception("Error: array length not equal to size");
		data = arr;
	}
	/**
	 * Will only add if there is enough room
	 * @param item - the item to add
	 */
	public void add(T item)
	{
		if(size < alive.length)
		{
			alive[size] = true;
			data[size++] = item;
		}
	}
	/**
	 * Removes all items that fit the function. Also recycles the array
	 * @param f - the function to test each item
	 */
	public void removeIf(Function<T, Boolean> f)
	{
		for(int i = 0; i < size; ++i)
		{
			if(f.apply(data[i]))
				alive[i] = false;
		}
		recycle();
	}
	/**
	 * removes the item at the specified index. Does not call recycle()
	 * @param index - the index to remove
	 */
	public void removeIndex(int index)
	{
		alive[index] = false;
	}
	/**
	 * removes every item
	 */
	public void clear()
	{
		for(int i = 0; i < size; ++i)
			alive[i] = false;
		size = 0;
	}
	/**
	 * puts the array back in order after items have been removed
	 */
	public void recycle()
	{
		int nf = 0; //next free spot
		for(int i = 0; i < alive.length; ++i)
		{
			if(alive[i])
			{
				boolean temp = alive[i];
				alive[i] = false;
				alive[nf] = temp;
				data[nf] = data[i];
				nf++;
			}
		}
		size = nf;
	}
	public T get(int index)
	{
		return data[index];
	}
}
