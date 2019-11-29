/**
 * Ryan Brodsky
 * ICS 440 Programming Assignment 2
 * 6/27/2019
 */

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is my Weather Data class, It creates the object weather data, and also contains
 * my getters, setters and to string method.
 *
 */
public class WeatherData { 
	
	String id; //Station and File ID
	int year; //Year weather occurred
	int month; //Month weather occurred
	int day; //day weather occurred
	String element; //What type of weather, TMIN, TMAX, SNOW, PRCP, SNWD
	int value; //The Value Recorded for that element on a specific day
	String qflag; //only look for " "
	
	public static ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<Object>();
	public static WeatherData marker;
	WeatherData mark;
	
	//Creates an WeatherData Object
	public WeatherData(String id, int year, int month, int day, String element, int value, String qflag) {
		super();
		this.id = id;
		this.year = year;
		this.month = month;
		this.day = day;
		this.element = element;
		this.value = value;
		this.qflag = qflag;
		add(this);
	}
	
	public WeatherData() {
		super();
	}
	
	//Get WeatherData ID
	public String getId() {
		return id;
	}
	
	//Get Weather Data Year
	public int getYear() {
		return year;
	}
	
	//Get Weather Data Month
	public int getMonth() {
		return month;
	}
	
	//Get Weather Data to String 
	@Override
	public String toString() {
		return "id=" + id + ", year=" + year + ", month=" + month + ", day=" + day + ", element=" + element
				+ ", value=" + (double) value / 10 + "C, qflag=" + qflag;
	}

	//Get Weather Data Day
	public int getDay() {
		return day;
	}
	
	//Get Weather Data Element
	public String getElement() {
		return element;
	}
	
	//Get Weather Data Value
	public int getValue() {
		return value;
	}
	
	//Get Weather Data QFlag
	public String getQflag() {
		return qflag;
	}
	
	//Add Method
	public static void add(WeatherData wd) {
		queue.add(wd);
	}
}