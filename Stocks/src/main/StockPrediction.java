package main;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.jaunt.UserAgent;
import com.jaunt.component.Table;

//import java.util.Iterator;
//import java.util.NoSuchElementException;



/*
 * <h1> STOCK PREDICTION PROGRAM </h1>
 * 
 * Description
 * 
 * CS108
 * December 12th, 2017
 * @Jacob Downey
 * @Cole Browser
 */
public class StockPrediction {
	
	private static LinkedList<Point> pts;


	/**
	 * <h2> public static LinkedList<Point> loadFromScrape(String name) </h2>
	 * Description
	 * RUNTIME COMPLEXITY: O(n) - Iterating through the entire website for the points
	 * @param String name - the name of the stock to scrap from
	 * @return LinkedList<Point> - A list of Points written from the file
	 */
	public static void loadFromScrape(String name) {
		pts = new LinkedList<Point>();
		UserAgent agent = new UserAgent();
		Table t;
		int closeIndex = 4; //In the table, the value of the stock after closing is the fifth column
		
		try {
			agent.visit("https://finance.yahoo.com/quote/" + name + "/history?&interval=1d&filter=history&frequency=1d");
			t = agent.doc.getTable(0);
			
			/*
			 * Starts at index 1 to skip table headings
			 * Iterates to Integer.MAX_VALUE because the Table Object iterator does not function and
			 * the Table Object contains no methods for getting the number of rows or columns
			 * 
			 * We iterate through the data until we reach the text at the bottom of the table and
			 * trigger a NumberFormatException which tells us to stop filling the LinkedList
			 * 
			 * Along the way we add each element to the beginning of the LinkedList because the table
			 * that we are webscraping from appears in opposite order that we need from most recent
			 * to oldest so we basically flip the order
			 */
			for (int i = 1; i < Integer.MAX_VALUE; i++){
				double data = Double.parseDouble(t.getCell(closeIndex, i).getElement(0).getText().replaceAll(",", ""));
				pts.addFirst(new Point(i-1, data));
			}
		}
		catch (NumberFormatException e) {
			//dont do anything
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		int len = pts.size() - 1;
		
		/*
		 * The html table starts with the most recent price and descends in time
		 * We switch the x coordinates so that we can predict the future.
		 * 
		 * We can't read the table backwards or get the size early because the Table
		 * object does not include that functionality thus requireing another n iterations
		 */
		for (Point pt : pts) {
			pt.setX(len - pt.getX());
		}
	}
	
	
	
	/**
	 * <h2> public static LinkedList<Point> loadFromCSV(String filename) </h2>
	 * Description
	 * RUNTIME COMPLEXITY: O(n) - Iterating through the entire csv file
	 * @param String filename - The file name without the ending
	 * @return LinkedList<Point> - A list of Points written from the file
	 */
	public static void loadFromCSV(String filename) {
		Scanner scan = null;
		pts = null;
		try {
			scan = new Scanner(new File(filename + ".csv"));
			scan.nextLine();
			pts = new LinkedList<Point>();
			double counter = 0;
			while (scan.hasNextLine()) {
				String[] strArray = scan.nextLine().split(",");
				pts.add(new Point(counter++, Double.parseDouble(strArray[4])));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (scan != null) {
				scan.close();
			}
		}
	}
	
	
	
	
	
	/**
	 * <h2> public static double calcAvgSlope() </h2>
	 * Description
	 * RUNTIME COMPLEXITY: O(n) - Iterating through the entire pts LinkedList
	 * @param LinkedList<Point> pts - Contains all the points of the graph
	 * @return double - Returns the average slope of all points
	 */
	public static double calcAvgSlope() {
		double num = 0;
		double den = 0;
		Point avgPoint = calcAvgPoint();
		for (Point pt : pts) {
			num += (pt.getX() - avgPoint.getX()) * (pt.getY() - avgPoint.getY());
			den += Math.pow(pt.getX() - avgPoint.getX(), 2);
		}
		return num / den;
	}
	
	
	/**
	 * <h2> public static Point calcAvgPoint() </h2>
	 * Description
	 * RUNTIME COMPLEXITY: O(n) - Iterates through the entire pts LinkedList
	 * @param LinkedList<Point> pts - Contains all the points of the graph
	 * @return Point - The average or mean of all the points
	 */
	public static Point calcAvgPoint() {
		double yAvg = 0;
		for (Point pt : pts) {
			yAvg += pt.getY();
		}
		return new Point ((pts.size() - 1) / 2, yAvg / pts.size());
	}
	

	/**
	 * <h2> public static double calcFuturePrice(Point pt, double slope, double lastX, int time) </h2>
	 * Description
	 * RUNTIME COMPLEXITY: O(1) - Just does a math equation and returns it
	 * @param double slope - The slop of the line
	 * @param Point lastPoint - The last x value that you start calculating from 
	 * @param int time - From the lastX, this indicates how long you should calculate
	 * @return double - A double of the calculated future price
	 */
	public static double calcFuturePrice(double slope, Point lastPoint, int time) {
		return (slope * time) + lastPoint.getY();
	}
	
	
	/**
	 * <h2> public static void printStock(String stockName, double prediction, int time) </h2>
	 * Description
	 * RUNTIME COMPLEXITY: O(1) - Just prints
	 * @param Stock stockName - The name of the Stock
	 * @param double prediction - The exact dollar evaluation of the Stock after the time
	 * @param int time - The time from today that the Stock is calculated at
	 * @return void - It only prints the Stock description
	 */
	public static void printStock(String stockName, double prediction, int time) {
		System.out.printf("The stock %s will be $%.2f in %d days", stockName, prediction, time);
	}
	
	
	
	//Mini methods to use throughout the main
	
	/**
	 * <h2> public static String getStockLocation(Scanner scan) </h2>
	 * 
	 * @param Scanner scan - The scanner to read in information
	 * @return String - A string of the option online of offline
	 */
	public static String getStockLocation(Scanner scan) {
		String option = "";
		while (!option.equals("online") && !option.equals("offline")) {
			System.out.println("Do you want to calculate the stock online or offline?");
			option = scan.nextLine().trim().toLowerCase();
		}
		return option;
	}
	
	
	/**
	 * <h2> public static String getStockName(Scanner scan) </h2>
	 * @param scan - The scanner to read in information
	 * @return String - A string of the stock name
	 */
	public static String getStockName(Scanner scan) {
		String stockName = "";
		while (stockName.equals("")) {
			System.out.println("Enter the stock that you would like to map:");
			stockName = scan.nextLine().trim().toUpperCase();
		}
		return stockName;
	}
	
	/**
	 * <h2> public static int getStockTime(Scanner scan) </h2>
	 * @param scan - The scanner to read in information
	 * @return int - An int of the stock time
	 */
	public static int getStockTime(Scanner scan) {
		int time = 0;
		while (time <= 0) {
			System.out.println("How many days in the future do you want to predict the stock?");
			time = scan.nextInt();
		}
		return time;
	}
	
	
	/**
	 * <h2> public static LinkedList<Point> getStockPoints(String option, String stockName) </h2>
	 * 
	 * @param option - takes in the option String that is the online or offline feature
	 * @param stockName - takes in the stockName String that is the stock name
	 * @return LinkedList<Point> - returns a LinkedList of all the points read form the file
	 */
	public static void getStockPoints(String option, String stockName) {
		if (option.equals("online")) {
			loadFromScrape(stockName);
		}
		else {
			loadFromCSV(stockName);
		}
	}
	
	
	
	/**
	 * <h2> public static void main(String[] args) </h2>
	 * Description
	 * @param String[] args
	 * @return void - Nothing
	 */
	
	public static void main(String[] args) {
		
		System.out.println("YEW");
		
		//The scanner that is going to be used to read in information
		Scanner scan = new Scanner(System.in);
		
		//collects the online or offline way to receive data
		String option = getStockLocation(scan);
		
		//collects what the stock name is
		String stockName = getStockName(scan);
		
		//collects what the time should be
		int time = getStockTime(scan);
		
		//gets the LinkedList of data for online or offline
		getStockPoints(option, stockName);
		
		//prints the stock with the correct format
		printStock(stockName, calcFuturePrice(calcAvgSlope(), pts.getLast(), time), time);
		
		scan.close();
	}
	
	
	
	
	/*--------------------------------------------------------------------*/
	
	
	
	
	/*
	 * <h1> POINT </h1>
	 * 
	 * This program is a Point object that can basically be used to store
	 * x and y coordinates as doubles. This class is basically made up of
	 * the different getters and setters of the two variables.
	 * 
	 * @Jacob Downey
	 */
	protected static class Point {
		
		//instance variables that hold the x and y double values
		private double x;
		private double y;
		
		
		
		/**
		 * <h2> public Point() </h2>
		 * Constructor of the Point where there is no parameters.
		 * Sets all the instance variables to 0
		 * @param None
		 * @return None
		 */
		public Point() {
			this.x = 0f;
			this.y = 0f;
		}
		
		
		/**
		 * <h2> public Point(double x, double y) </h2>
		 * Constructor of the Point where there are two parameters.
		 * It takes the first parameter and stores it to the x and
		 * then takes the second parameter and stores it to the y
		 * @param double x
		 * @param double y
		 */
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		
		
		/**
		 * <h2> public void setX(double x) </h2>
		 * Sets the parameter to the x instance variable
		 * @param double x
		 * @return void
		 */
		public void setX(double x) {
			this.x = x;
		}
		
		
		/**
		 * <h2> public double getX() </h2>
		 * Returns the x double
		 * @param None
		 * @return double
		 */
		public double getX() {
			return x;
		}
		
		
		/**
		 * <h2> public void setY(double y) </h2>
		 * Sets the parameter to the y instance variable
		 * @param double y
		 * @return void
		 */
		public void setY(double y) {
			this.y = y;
		}
		
		
		/**
		 * <h2> public double getY() </h2>
		 * Returns the y double
		 * @param None
		 * @return double
		 */
		public double getY() {
			return y;
		}

	}
	
	
	
	
	/*--------------------------------------------------------------------*/
	
	
	
	
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
	 * @author Jacob Downey
	 */
	protected static class LinkedList<E> implements Iterable<E> {
		
		
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
}
