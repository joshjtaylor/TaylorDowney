
package first;

//The imports that the Main uses
import java.io.File;
import java.util.Scanner;

import com.jaunt.UserAgent;
import com.jaunt.component.Table;


/*
 * <h1> STOCK PREDICTION PROGRAM </h1>
 * 
 * This program calculates the future stock price of a stock based off of
 * all of the points from a stock. The program first asks for the user to
 * input a stock name which they have to input correctly. Then it asks the
 * user to input a time in the future to predict from. In addition it asks
 * to get the info online or offline. These points are gotten through web
 * scraping yahoo finance or reading from a csv file. From there I use a
 * formula to find the average slope between the average point and the rest
 * of the points. From there I use the average slope in cordinance with the
 * last point of the stock to plug into a y=mx+b equation where y is the
 * output, m is the slope, x is the time in the future, and b is the last y
 * value that you will start from. Then I output the future stock price.
 * 
 * CS108
 * December 12th, 2017
 * @Jacob Downey
 * @Cole Browser
 */
public class Main {
	
	
	
	/**
	 * <h2> private static LinkedList<Point> loadFromScrape(String name) </h2>
	 * This method visits the website for yahoo finance given that the name is correct.
	 * It them takes all the points that it gets from this website table and stores them
	 * in a Linked List. It has to store from the front because the table where all
	 * the points are given is written in the wrong direction. 
	 * RUNTIME COMPLEXITY: O(n) - Iterating through the entire website for the points
	 * @param String name - the name of the stock to scrap from
	 * @return LinkedList<Point> - A list of Points written from the file
	 */
	private static LinkedList<Point> loadFromScrape(String name) {
		LinkedList<Point> pts = new LinkedList<>();
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
		
		return pts;
	}
	
	
	/**
	 * <h2> private static LinkedList<Point> loadFromCSV(String filename) </h2>
	 * This program takes in the string of a file name and looks for the csv
	 * file and opens it. It then takes the information that is needed from
	 * the file using the scanner. After that it stores the points into a 
	 * Linked List data structure to have them secure and returns that
	 * Linked List.
	 * RUNTIME COMPLEXITY: O(n) - Iterating through the entire csv file
	 * @param String filename - The file name without the ending
	 * @return LinkedList<Point> - A list of Points written from the file
	 */
	private static LinkedList<Point> loadFromCSV(String filename) {
		Scanner scan = null;
		LinkedList<Point> pts = null;
		try {
			scan = new Scanner(new File(filename + ".csv"));
			scan.nextLine();
			pts = new LinkedList<Point>();
			double counter = 0;
			while (scan.hasNextLine()) {
				String[] strArray = scan.nextLine().split(",");
				pts.add(new Point(counter++, Double.parseDouble(strArray[4])));
			}
			return pts;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (scan != null) {
				scan.close();
			}
		}
	}
	
	
	/**
	 * <h2> private static double calcAvgSlope(LinkedList<Point> pts) </h2>
	 * This method takes all of the points and uses a formula from
	 * https://www.varsitytutors.com/hotmath/hotmath_help/topics/line-of-best-fit
	 * to calculate just the average slope. With that it needs an average point
	 * so it calls to the calcAvgPoint() method to find the average point
	 * RUNTIME COMPLEXITY: O(n) - Iterating through the entire pts LinkedList
	 * @param LinkedList<Point> pts - Contains all the points of the graph
	 * @return double - Returns the average slope of all points
	 */
	private static double calcAvgSlope(LinkedList<Point> pts) {
		double num = 0;
		double den = 0;
		Point avgPoint = calcAvgPoint(pts);
		for (Point pt : pts) {
			num += (pt.getX() - avgPoint.getX()) * (pt.getY() - avgPoint.getY());
			den += Math.pow(pt.getX() - avgPoint.getX(), 2);
		}
		return num / den;
	}
	
	
	/**
	 * <h2> private static Point calcAvgPoint(LinkedList<Point> pts) </h2>
	 * This method runs through the entire linked list of pts and adds
	 * up all the y values then divides it by the size of the list to get
	 * the average y value. Since all the x values are in order from 0 - n
	 * the average x value is just (n - 1) / 2 and that is returned
	 * RUNTIME COMPLEXITY: O(n) - Iterates through the entire pts LinkedList
	 * @param LinkedList<Point> pts - Contains all the points of the graph
	 * @return Point - The average or mean of all the points
	 */
	private static Point calcAvgPoint(LinkedList<Point> pts) {
		double yAvg = 0;
		for (Point pt : pts) {
			yAvg += pt.getY();
		}
		return new Point ((pts.size() - 1) / 2, yAvg / pts.size());
	}
	

	/**
	 * <h2> private static double calcFuturePrice(Point pt, double slope, double lastX, int time) </h2>
	 * This method calculates the future stock price by using a y=mx+b equation
	 * where y is the output, m is the slope, x is the time in the future, and
	 * b is the last y value that you will start from. 
	 * RUNTIME COMPLEXITY: O(1) - Just does a math equation and returns it
	 * @param double slope - The slop of the line
	 * @param Point lastPoint - The last x value that you start calculating from 
	 * @param int time - From the lastX, this indicates how long you should calculate
	 * @return double - A double of the calculated future price
	 */
	private static double calcFuturePrice(double slope, Point lastPoint, int time) {
		return (slope * time) + lastPoint.getY();
	}
	
	
	/**
	 * <h2> private static void printStock(String stockName, double prediction, int time) </h2>
	 * This method prints out the stock info in a perfect length that is needed
	 * RUNTIME COMPLEXITY: O(1) - Just prints
	 * @param Stock stockName - The name of the Stock
	 * @param double prediction - The exact dollar evaluation of the Stock after the time
	 * @param int time - The time from today that the Stock is calculated at
	 * @return void - It only prints the Stock description
	 */
	private static void printStock(String stockName, double prediction, int time) {
		System.out.printf("The stock %s will be $%.2f in %d days", stockName, prediction, time);
	}
	
	
	
	//Mini methods to use throughout the main
	
	/**
	 * <h2> public static String getStockLocation(Scanner scan) </h2>
	 * This method returns a string of either online or offline
	 * which will dictate how the stock information is gotten.
	 * @param Scanner scan - The scanner to read in information
	 * @return String - A string of the option online of offline
	 */
	private static String getStockLocation(Scanner scan) {
		String option = "";
		while (!option.equals("online") && !option.equals("offline")) {
			System.out.println("Do you want to calculate the stock online or offline?");
			option = scan.nextLine().trim().toLowerCase();
		}
		return option;
	}
	
	
	/**
	 * <h2> public static String getStockName(Scanner scan) </h2>
	 * This method returns the stock name by prompting the user
	 * @param scan - The scanner to read in information
	 * @return String - A string of the stock name
	 */
	private static String getStockName(Scanner scan) {
		String stockName = "";
		while (stockName.equals("")) {
			System.out.println("Enter the stock that you would like to map:");
			stockName = scan.nextLine().trim().toUpperCase();
		}
		return stockName;
	}
	
	
	/**
	 * <h2> public static int getStockTime(Scanner scan) </h2>
	 * This method returns the integer of time in the future that
	 * it will predict by prompting the user.
	 * @param scan - The scanner to read in information
	 * @return int - An int of the stock time
	 */
	private static int getStockTime(Scanner scan) {
		int time = 0;
		while (time <= 0) {
			System.out.println("How many days in the future do you want to predict the stock?");
			time = scan.nextInt();
		}
		return time;
	}
	
	
	/**
	 * <h2> public static LinkedList<Point> getStockPoints(String option, String stockName) </h2>
	 * This method takes either the online or offline option and calls the methods
	 * that basically gather the info for the pts Linked List
	 * @param option - takes in the option String that is the online or offline feature
	 * @param stockName - takes in the stockName String that is the stock name
	 * @return LinkedList<Point> - returns a LinkedList of all the points read form the file
	 */
	private static LinkedList<Point> getStockPoints(String option, String stockName) {
		if (option.equals("online")) {
			return loadFromScrape(stockName);
		}
		else {
			return loadFromCSV(stockName);
		}
	}
	
	
	
	
	/**
	 * <h2> public static void main(String[] args) </h2>
	 * This is the main where all the magic is done
	 * @param String[] args
	 * @return void - Nothing
	 */
	
	public static void main(String[] args) {
		
		//The scanner that is going to be used to read in information
		Scanner scan = new Scanner(System.in);
		
		//The LinkedList of all the points of the stock
		LinkedList<Point> pts = null;
		
		//collects the online or offline way to receive data
		String option = getStockLocation(scan);
		
		//collects what the stock name is
		String stockName = getStockName(scan);
		
		//collects what the time should be
		int time = getStockTime(scan);
		
		//gets the LinkedList of data for online or offline
		pts = getStockPoints(option, stockName);
		
		//prints the stock with the correct format
		printStock(stockName, calcFuturePrice(calcAvgSlope(pts), pts.getLast(), time), time);
		
		scan.close();
	}

}
