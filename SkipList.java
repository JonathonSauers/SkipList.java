// Jonathon Sauers
// jo046326
// COP 3503, Fall 2017
// SkipList.java

import java.util.ArrayList;
import java.util.HashMap;

class Node<T>
{
	private T data;
	private int height;

	// Holds the references the node is pointing to.
	ArrayList<Node<T>> next = new ArrayList<>();

	// Used for the head node that holds no data.
	Node(int height)
	{
		this.height = height;
		for(int i = 0; i < height; i++)
			this.next.add(null);
	}

	// Creates a node with a height and some data.
	Node(T data, int height)
	{
		this.height = height;
		for(int i = 0; i < height; i++)
			this.next.add(null);

		this.data = data;
	}

	public T value()
	{
		return this.data;
	}

	public int height()
	{
		return this.height;
	}

	// Gets the next node at a certain level.
	public Node<T> next(int level)
	{
		if(level < 0 || level > this.height - 1)
			return null;

		return this.next.get(level);
	}

	// Sets the next reference of a node.
	public void setNext(int level, Node<T> node)
	{
		this.next.set(level, node);
	}

	public void grow()
	{
		this.height = this.height + 1;
		this.next.add(height - 1, null);
	}

	// Gives a 50% chance of growing.
	public void maybeGrow()
	{
		if(Math.random() < 0.5)
			grow();
	}

	// Can trim the height if necessary.
	public void trim(int height)
	{
		for(int i = height; i < this.height; i++)
			this.next.set(i, null);

		this.height = height;
	}
}

// A Generic skip list class.
public class SkipList<T extends Comparable<T>>
{
	private Node<T> head;
	private int height, size;

	// Sets the height to 1 and instantiates the head node.
	SkipList()
	{
		this.height = 1;
		this.head = new Node<T>(height);
	}

	// Sets the height to "height" and instantiates the head node.
	SkipList(int height)
	{
		this.height = height;
		this.head = new Node<T>(height);
	}

	public int size()
	{
		return this.size;
	}

	public int height()
	{
		return this.height;
	}

	public Node<T> head()
	{
		return this.head;
	}

	public void insert(T data)
	{
		// Increment the current size.
		this.size = this.size + 1;

		// Determine whether the skip list needs to grow.
    if(getMaxHeight(this.size) > this.height)
    {
      this.height = getMaxHeight(this.size);
      this.growSkipList();
    }

		// Preparing to traverse through, and put the nodes into a hashmap.
		Node<T> temp = this.head;
		int level = this.height - 1;
		HashMap<Integer, Node<T>> nodeMap = new HashMap<>();

		// Goes throught the skip list to find where to insert the new node.
		while(level >= 0 && temp != null)
		{
			// If a node points to a null reference, put it in the hashmap
      // and drop down a level to continue traversing.
			if(temp.next(level) == null)
			{
				nodeMap.put(level, temp);
				level = level - 1;
			}

			// If the next node is still bigger, continue through the skip list.
			else if(temp.next(level).value().compareTo(data) < 0)
				temp = temp.next(level);

			// If the next node is smaller or equal, map the node and drop a level.
			else if(temp.next(level).value().compareTo(data) >= 0)
			{
				nodeMap.put(level, temp);
				level = level - 1;
			}
		}

		// We found where to insert the new node, so let's do it.
		int newHeight = generateRandomHeight(this.height);
		Node<T> newNode = new Node<T>(data,newHeight);

		// Attach the old references to the newly inserted node.
		for(int i = newHeight - 1; i >= 0; i--)
		{
			newNode.setNext(i, nodeMap.get(i).next(i));
			nodeMap.get(i).setNext(i, newNode);
		}
	}

	public void insert(T data, int height)
	{
		// Increment the current size.
		this.size = this.size + 1;

		// Determine whether the skip list needs to grow.
    if(getMaxHeight(this.size) > this.height)
    {
      this.height = getMaxHeight(this.size);
      this.growSkipList();
    }

		// Preparing to traverse through, and put the nodes into a hashmap.
		Node<T> temp = this.head;
		int level = this.height - 1;
		HashMap<Integer, Node<T>> nodeMap = new HashMap<>();

		// Goes throught the skip list to find where to insert the new node.
		while(level >= 0 && temp != null)
		{
			// If a node points to a null reference, put it in the hashmap
      // and drop down a level to continue traversing.
			if(temp.next(level) == null)
			{
				nodeMap.put(level, temp);
				level = level - 1;
			}

			// If the next node is still bigger, continue through the skip list.
			else if(temp.next(level).value().compareTo(data) < 0)
				temp = temp.next(level);

			// If the next node is smaller or equal, map the node and drop a level.
			else if(temp.next(level).value().compareTo(data) >= 0)
			{
				nodeMap.put(level, temp);
				level = level - 1;
			}
		}

		// We found where to insert the new node, so let's do it.
		int newHeight = height;
		Node<T> newNode = new Node<T>(data, newHeight);

		// Attach the old references to the newly inserted node.
		for(int i = newHeight - 1; i >= 0; i--)
		{
			newNode.setNext(i, nodeMap.get(i).next(i));
			nodeMap.get(i).setNext(i, newNode);
		}
	}

	// Delete a node from a skip list.
	public void delete(T data)
	{
		// Preparing to traverse through, and put the nodes into a hashmap.
		int level = this.height - 1;
		Node<T> temp = this.head;
		HashMap<Integer, Node<T>> nodeMap = new HashMap<>();

		// Goes throught the skip list to find where to delete the node.
		while(level >= 0 && temp != null)
		{
			// If a node points to a null reference, put it in the hashmap
      // and drop down a level to continue traversing.
			if(temp.next(level) == null)
				level = level - 1;

			// If the next node is still bigger, continue through the skip list.
			else if(temp.next(level).value().compareTo(data) < 0)
				temp = temp.next(level);

			// If the next node is smaller or equal, map the node and drop a level.
			else if(temp.next(level).value().compareTo(data) > 0)
				level = level - 1;

			// We found it.
			else
			{
				nodeMap.put(level, temp);
				level = level - 1;
			}
		}

		// Point temp to what we are deleting.
		temp = temp.next(level + 1);

		// Check temp isn't the last node and has the correct value.
		if(temp != null && temp.value().compareTo(data) == 0)
		{
			int newlevel = temp.height();

			for(int i = newlevel - 1; i >= 0; i--)
				nodeMap.get(i).setNext(i, temp.next(i));

			// Decrement the size.
			this.size = this.size - 1;

			// Find the new max height for the skip list.
			int newHeight;

			if(this.size <= 1)
				newHeight = 1;
			else
				newHeight = getMaxHeight(this.size);

			// Check if we need to update the height.
			if(newHeight < this.height)
			{
				this.height = newHeight;
				this.trimSkipList();
			}
		}
	}

	// Check if an element is in a skip list.
	public boolean contains(T data)
	{
		Node<T> temp = this.head;
		int level = this.height - 1;

		// Goes throught the skip list to find the node.
		while(level >= 0 && temp != null)
		{
			// If a node puts to a null reference, drop a level and keep traversing.
			if(temp.next(level) == null)
				level = level - 1;

			// If the next node is still bigger, continue through the skip list.
			else if(temp.next(level).value().compareTo(data) < 0)
				temp = temp.next(level);

			// If the next node is smaller or equal, map the node and drop a level.
			else if(temp.next(level).value().compareTo(data) > 0)
				level = level - 1;

			// We found it.
			else
				return true;
		}
		return false;
	}

	// Finds the max height the skip list can have.
	private static int getMaxHeight(int n)
	{
		return (int) Math.ceil((Math.log(n) / Math.log(2)));
	}

	// Generates a random height based on a "coin flip".
	private static int generateRandomHeight(int maxHeight)
	{
		// i is the height.
		int i;

		for(i = 1; i < maxHeight; i++)
			if(Math.random() < 0.5)
				break;

		// Return the height determined.
		return i;
	}

	// Grows the skip list.
	private void growSkipList()
	{
		this.head.grow();

    // tempHead is the head, tempNext is the next node.
    Node<T> tempHead = this.head;
		Node<T> tempNext = this.head.next(this.height - 2);

    while(tempNext != null)
    {
      tempNext.maybeGrow();

			// Connect everything if tempNext grew.
      if(tempNext.height() == this.height)
      {
        tempHead.setNext(this.height - 1, tempNext);
        tempHead = tempHead.next(this.height - 1);
      }

			// Keep traversing.
      tempNext = tempNext.next(this.height - 2);
    }
	}

	// Calls trimSkipListHelper to recursively go through a skip list.
	private void trimSkipList()
	{
		trimSkipListHelper(this.head);
	}

	// A recursive method to traverse through a skip list.
	private void trimSkipListHelper(Node<T> temp)
	{
		// We are at the end of the level.
		if(temp == null)
			return;

		int level = this.height - 1;

		// Goes through the list until the end and trims it.
		trimSkipListHelper(temp.next(level));
		temp.trim(this.height);
	}

	// How difficult I found this assignment.
	public static double difficultyRating()
	{
		return 4.0;
	}

	// How long it took me to complete this assignment.
	public static double hoursSpent()
	{
		return 15.0;
	}
}
