
package first;

//imports needed for the Iterator
import java.util.Iterator;
import java.util.NoSuchElementException;


/*
 * <h1> LINKEDLIST<E> </h1>
 * 
 * This is my Linked List class where I made my own Linked List that has only
 * the most important methods necessary to the Stock Prediction Program.
 * There are numerous other methods that I could add to the Linked List but
 * these are the only ones necessary for the program. From this Linked List you
 * are able to instantiate the object of it, add to the end of the Linked List,
 * add to the beginning of the Linked List, get the last element, get the size,
 * and use the Iterator. This LinkedList is built through Nodes which make up the
 * mini classes that store information for the next and the current Nodes data.
 * This Linked List is also only able to be used in one direction so it's a 
 * singly linked list.
 * 
 * CS108
 * December 12th, 2017
 * @author Jacob Downey
 */
public class LinkedList<E> implements Iterable<E> {
	
	
	//instance variables of the head and tail Nodes and size
	private int size;
	private Node<E> head;
	private Node<E> tail;
	
	
	
	/**
	 * <h2> public LinkedList() </h2>
	 * This is the constructor of the LinkedList that sets the head and tail
	 * to null while also setting the size to 0.
	 * @param None
	 * @return None
	 */
	public LinkedList() {
		head = null;
		tail = null;
		size = 0;
	}
	
	
	
	/**
	 * <h2> private class Node<E> </h2>
	 * This is a private class of Node where the data for each segment of
	 * information the Node stores the data and the next Node.
	 * @param None
	 * @return None
	 * @author Jacob_Downey
	 */
	@SuppressWarnings("hiding")
	private class Node<E> {
		E data;
		Node<E> next;
		public Node(E var) {
			data = var;
			next = null;
		}
	}
	
	
	
	/**
	 * <h2> public boolean add(E var) </h2>
	 * Adds the parameter variable to the end of the Linked List
	 * @param E var - The variable that you will add to the LinkedList
	 * @return boolean - returns true
	 */
	public boolean add(E var) {
		Node<E> newNode = new Node<E>(var);
		if (size == 0) {
			head = newNode;
			tail = newNode;
		}
		else {
			tail.next = newNode;
			tail = newNode;
		}
		size++;
		return true;
	}
	
	
	/**
	 * <h2> public boolean addFirst(E var) </h2>
	 * Adds the parameter variable into the linked list at the beginning by
	 * taking advantage of the .next method and head setting
	 * @param var - The variable that you will add to the LinkedList
	 * @return boolean - returns true
	 */
	public boolean addFirst(E var) {
		Node<E> newNode = new Node<E>(var);
		if (size == 0) {
			head = newNode;
			tail = newNode;
		}
		else {
			newNode.next = head;
			head = newNode;
		}
		size++;
		return true;
	}
	
	
	/**
	 * <h2> public E getLast() </h2>
	 * Returns the data for the last Node
	 * @param None
	 * @return E - The Node of type E
	 */
	public E getLast() {
		return tail.data;
	}
	
	
	/**
	 * <h2> public int size() </h2>
	 * Returns the size of the Linked List, how many elements
	 * @param None
	 * @return int - The list size
	 */
	public int size() {
		return size;
	}
	
	
	
	/**
	 * <h2> public Iterator<E> iterator() </h1>
	 * Returns an iterator that can be used to iterate through all segments
	 * of the Linked List
	 * @param None
	 * @return Iterator<E> - the iterator
	 */
	public Iterator<E> iterator() {
		return new IteratorThing();
	}
	
	
	/**
	 * <h2> private class IteratorThing implements Iterator<E> </h2>
	 * This is the private IteratorThing class which implements the Iterator<E>.
	 * This class basically holds all the methods to go throughout the Linked
	 * List such as if there is a next node.
	 * @param None
	 * @return None
	 * @author Jacob_Downey
	 */
	private class IteratorThing implements Iterator<E> {
		
		private Node<E> currentNode;
		private int counter;

		private IteratorThing() {
			currentNode = head;
			counter = 1;
		}
		
		public boolean hasNext() {
			return counter <= size;
		}
		
		public E next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			E temp = currentNode.data;
			currentNode = currentNode.next;
			counter++;
			return temp;
		}
	}
	
}
