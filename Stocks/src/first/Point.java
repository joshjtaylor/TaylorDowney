
package first;


/*
 * <h1> POINT </h1>
 * 
 * This program is a Point object that can basically be used to store
 * x and y coordinates as doubles. This class is basically made up of
 * the different getters and setters of the two variables.
 * 
 * CS108
 * December 12th, 2017
 * @Jacob Downey
 */
public class Point {
	
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
