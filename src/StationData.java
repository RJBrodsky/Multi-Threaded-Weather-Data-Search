/**
 * Ryan Brodsky
 * ICS 440 Programming Assignment 2
 * 6/27/2019
 */

/**
 * This is my Station Data class, It creates the object station data, and it also
 * Contains my gettter methods which i use later on.
 */
public class StationData { 
	String id; //Station ID
	float latitude; //Station Latitude
	float longitude; //Station Longitude
	float elevation; //Stations elevation
	String state; //Stations State Abbreviation
	String name; //Name of the Weather Station
	
	//Creates a Station Data Object
	public StationData(String id, float latitude, float longitude, float elevation, String state, String name) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
		this.state = state;
		this.name = name;
	}
	
	public StationData() {
		super();
	}
	
	//Gets the StationData ID
	public String getId() {
		return id;
	}
	
	//Gets the StationData Latitude
	public float getLatitude() {
		return latitude;
	}
	
	//Gets the StationData Longitude
	public float getLongitude() {
		return longitude;
	}
	
	//Gets the StationData Elevation
	public float getElevation() {
		return elevation;
	}
	
	//Gets the StationData State
	public String getState() {
		return state;
	}
	
	//Gets the StationData Name
	public String getName() {
		return name;
	} 
} 