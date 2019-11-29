/**
 * Ryan Brodsky
 * ICS 440 Programming Assignment 2
 * 6/27/2019
 */

import java.io.BufferedReader; //Imported for file reading
import java.io.File; //Imported for file reading
import java.io.FileInputStream; //Imported for file reading
import java.io.IOException; //Imported for exceptions
import java.io.InputStreamReader; //Imported for file reading
import java.util.ArrayList; //Imported to use ArrayList, Stations are loaded onto this
import java.util.Scanner; //Imported to scan input from a user
import java.util.concurrent.Callable; //Imported to have a class implement CallAble
import java.util.concurrent.ConcurrentLinkedQueue; //Imported for Using Concurrent Linked Queues
import java.util.concurrent.ExecutorService; //Imported for ThreadPools
import java.util.concurrent.Executors; //Imported for ThreadPools
import java.util.concurrent.Future; //Imported for the futures

/**
 * This is my driver class. In this class i Read the users Input, I then take all 1218 file and load them into a Concurrent Linked Queue
 * I Then assign each future to a file. The Future reads the file its assigned and returns the top 5 temperatures storing them in another Concurrent Linked Queue
 * After all 1218 futures complete, we are left with a queue that contains roughly 6,090 results (5 from each file). I then divide the results into 4, and
 * have 4 new futures take a 1/4 of the data and again find the top 5. This will leave us with 20 results. I then With a single thread find the top 5
 * in that list of 20, and prints them out along with the stations information.
 */
public class Driver {

	public static ArrayList<StationData> stations = new ArrayList<StationData>(); //Array that holds station data
	public static ConcurrentLinkedQueue<Object> queueFiles = new ConcurrentLinkedQueue<Object>(); //This is a queue of files
	public static ConcurrentLinkedQueue<WeatherData> weatherQ = new ConcurrentLinkedQueue<WeatherData>();  //Weather queue after first pass
	public static ConcurrentLinkedQueue<WeatherData> smallerweatherQ = new ConcurrentLinkedQueue<WeatherData>();//Weather queue after second pass
	public static ConcurrentLinkedQueue<WeatherData> finalweatherQ = new ConcurrentLinkedQueue<WeatherData>(); //Weather queue after final pass

	public static File folder = new File("ghcnd_hcn"); //Reads the ghcnd_hcn
	public static File[] listOfFiles = folder.listFiles(); //Creates an array of files from the folder

	static int secondPassTotal = 0; //This is used for when i divide the results into 4
	static int startingYear = 0; //Keeps track of the starting year from the user.
	static int endingYear = 0; //Keeps track of the ending year from the user.
	static int startingMonth = 0; //Keeps track of the starting month from the user.
	static int endingMonth = 0; //Keeps track of the ending Month from the user.
	static int tempchoice = 0; //Keeps track of the if the user entered max or minimum.
	static String type = ""; //This string is used to specify the element type the user chose.

	public static void main(String[] args) throws IOException {

		ExecutorService executor = Executors.newFixedThreadPool(10);// Number of threads set to 10	
		Callable<ArrayList<WeatherData>> callable1 = new FirstPass(); //Callable1 is for the first Pass
		Callable<ArrayList<WeatherData>> callable2 = new SecondPass(); //Callable2 is for the second pass
		ArrayList<WeatherData> top20array =  new ArrayList<WeatherData>(5); //Holds top 20
		ArrayList<WeatherData> finalArray =  new ArrayList<WeatherData>(5); //Holds final 5
		/**
		 * This is Where I Get User Input
		 */
		Scanner sc = new  Scanner(System.in);  // Create a Scanner object
		System.out.println("Enter Starting Year: ");
		startingYear =  sc.nextInt(); 
		System.out.println("Enter Ending Year: ");
		endingYear =  sc.nextInt();  
		System.out.println("Enter Starting Month: ");
		startingMonth =  sc.nextInt();  
		System.out.println("Enter Ending Month: ");
		endingMonth =  sc.nextInt(); 
		System.out.println("Enter 0 to find Max Temp, Enter 1 to find Min Temp:  ");
		tempchoice =  sc.nextInt(); 

		//Sets type depending on user input
		if (tempchoice == 0) {
			type = "TMAX";
		} else if (tempchoice == 1) {
			type = "TMIN";
		}

		sc.close();//Closes scanner

		FileInputStream fstream = new FileInputStream("ghcnd-stations.txt"); //Reads the Stations.txt file
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream)); //Reads the Stations.txt file
		StationData sd = new StationData(); //New station data 
		String thisLine;

		//Read File Line By Line
		while ((thisLine = br.readLine()) != null)   {

			sd.id = thisLine.substring(0,11);
			sd.latitude = Float.valueOf(thisLine.substring(12,20).trim()); 
			sd.longitude = Float.valueOf(thisLine.substring(21,30).trim());
			sd.elevation = Float.valueOf(thisLine.substring(31,37).trim());
			sd.state = thisLine.substring(38,40);
			sd.name = thisLine.substring(41,71);
			stations.add(new StationData (sd.id, sd.latitude, sd.longitude, sd.elevation, sd.state, sd.name)); //Adds Each Station to an array for later

		}
		fstream.close();//Close the input stream

		/**
		 * Adds all files to a concurrent linked queue for the futures.
		 */
		for (int k = 0; k < listOfFiles.length; k++) {
			File f = listOfFiles[k]; // Get a single file
			queueFiles.add(f); // Add the file into the queue
		}

		/**
		 * This loop assigns a future to each file in the Concurrent Queue
		 * The future returns the top 5 for its file and adds them to
		 * the WeatherQ. (Another ConcurrentLinkedQueue)
		 */
		for (int j =0; j < listOfFiles.length; j++) {
			Future<ArrayList<WeatherData>> future = executor.submit(callable1); 
			try {
				ArrayList<WeatherData> passOne =  new ArrayList<WeatherData>(5);
				passOne = future.get();
				for (int g = 0; g < passOne.size(); g++) {
					WeatherData passOneResult = passOne.get(g);
					weatherQ.add(passOneResult);
				}
			} catch (Exception e) {

			}
		}

		secondPassTotal = weatherQ.size(); //This keeps track of the size of the new queue

		/**
		 * This loop assigns 4 futures to callable2, which divides the 
		 * weatherQ's data into 4 parts and each future returns the top 5 for their 1/4
		 * of the data. This data is then added to another Concurrent Linked Queue called
		 * smallerweatherQ
		 */
		for(int l = 0; l < 4; l++) {
			Future<ArrayList<WeatherData>> future = executor.submit(callable2);
			try {
				ArrayList<WeatherData> passTwo =  new ArrayList<WeatherData>(5);
				passTwo = future.get();
				for (int g = 0; g < passTwo.size(); g++) {
					WeatherData passTwoResult = passTwo.get(g);
					smallerweatherQ.add(passTwoResult);
				}
			} catch (Exception e) {
			}
		}


		/**
		 * This loops through the top 20 queue and adds them
		 * to an array list where i will sort them with a single thread
		 */
		for (int w = 0; w < smallerweatherQ.size(); w++) {
			WeatherData top20 = smallerweatherQ.remove(); //remove from queue
			top20array.add(top20);
		}

		finalArray = findTop(top20array); //This returns the top 5.
		
		/**
		 * This adds the final 5 into the finalweatherQ
		 */
		for(int q = 0;  q < finalArray.size(); q++) {
			WeatherData FinalData = finalArray.get(q);
			finalweatherQ.add(FinalData);
		}

		/**
		 * This removes 1 at a time from the final weather Q
		 * It calls the to string method and calls the station search method
		 * This loop is what prints the results.
		 */	
		for(int r = 0; r < 5; r++) {
			WeatherData Result = finalweatherQ.remove();
			System.out.println(Result.toString());
			stationSearch(stations, Result.getId());
		}
		
		System.exit(0);
	}
	
	/**
	 * This is my first pass class which implements CallAble
	 * This is called by futures as the pull a file from the file q
	 * It reads the data from the file and returns the top 5 results
	 * that fit the criteria entered by the user
	 */
	static class FirstPass implements Callable<ArrayList<WeatherData>>	{

		@Override
		public ArrayList<WeatherData> call() throws Exception {

			ArrayList<WeatherData> weatherDataFound = new ArrayList<WeatherData>(); //Holds data in range
			ArrayList<WeatherData> top5Result = new ArrayList<WeatherData>(); //holds top 5 in range

			File file = (File) queueFiles.remove(); //removes a file from the file queue
			FileInputStream fstream = new FileInputStream(file.getPath()); //sets the file path to fstream
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream)); //buffer reader
			WeatherData wd = new WeatherData();  //weather data
			String thisLine;

			//Read File Line By Line
			while ((thisLine = br.readLine()) != null)   {

				String id = thisLine.substring(0,11);
				int year = Integer.valueOf(thisLine.substring(11,15).trim());
				int month = Integer.valueOf(thisLine.substring(15,17).trim());
				String element = thisLine.substring(17,21);
				int days = (thisLine.length() - 21) / 8;
				if(element.compareToIgnoreCase(type) == 0 && year >= startingYear && year <= endingYear && month >= startingMonth && month <= endingMonth)	{
					for (int i = 0; i < days; i++) {       
						int value = Integer.valueOf(thisLine.substring(21+8*i,26+8*i).trim());
						String qflag = thisLine.substring(27+8*i,28+8*i);
						wd.id = id;
						wd.year = year;
						wd.month = month;
						wd.element = element;
						wd.qflag = qflag;
						wd.value = value;
						wd.day = i + 1;
						if(qflag.equals(" "))	{
							weatherDataFound.add(new WeatherData(wd.id, wd.year, wd.month, wd.day, wd.element, wd.value, wd.qflag)); //Adds to found data
						}
					}
				}
			}
			top5Result = findTop(weatherDataFound); //Finds top 5 in all of data found for file
			fstream.close(); //close FStream
			return top5Result; //returns top 5 for file
		}
	}

	/**
	 * This is second pass class, it also implements CallAble. This class
	 * is called by 4 futures after the first pass has completed. Each future
	 * will find the top 5 results for 1/4 of the data
	 */
	static class SecondPass implements Callable<ArrayList<WeatherData>>	{

		@Override
		public ArrayList<WeatherData> call() throws Exception {
			int QueueTotal = secondPassTotal / 4 + 1; //Marks when the future has its 1/4
			
			ArrayList<WeatherData> fourthOfData = new ArrayList<WeatherData>(); //Holds the futures 1/4
			ArrayList<WeatherData> top20Result = new ArrayList<WeatherData>(); //holds the top 5 from the 4 futures, so 20

			int counter = 0;
			while(counter < QueueTotal && weatherQ.size() != 0 ) {
				fourthOfData.add(weatherQ.remove());
				counter++;
			}
			top20Result = findTop(fourthOfData); 
			return top20Result; 
		}
	}
	
	/**
	 * This is my findTop Method, It takes weather data as a parameter and
	 * will find the top 5 values depending on what the user is looking for.
	 * @param weather
	 */
	public static ArrayList<WeatherData> findTop(ArrayList<WeatherData> weather)	{

		ArrayList<WeatherData> top = new ArrayList<WeatherData>(); //Holds the results
		WeatherData first = new WeatherData(); //Data 1
		WeatherData second = new WeatherData(); //Data 2
		WeatherData third = new WeatherData(); //Data 3
		WeatherData fourth = new WeatherData(); //Data 4
		WeatherData fifth = new WeatherData();  //Data 5

		/**
		 * If they are looking for the max value.
		 */
		if(tempchoice == 0) {
			for(int i = 0; i < weather.size(); i++) {
				if(weather.get(i).getValue() > first.getValue()) {
					first = weather.get(i);
				}else if(weather.get(i).getValue() > second.getValue()) {
					second = weather.get(i);
				}else if(weather.get(i).getValue() > third.getValue()) {
					third = weather.get(i);
				}else if(weather.get(i).getValue() > fourth.getValue()) {
					fourth = weather.get(i);
				}else if(weather.get(i).getValue() > fifth.getValue()) {
					fifth = weather.get(i);
				}
			}

			/**
			 * Adds the results to the top list.
			 */
			top.add(0, first);
			top.add(1, second);
			top.add(2, third);
			top.add(3, fourth);
			top.add(4, fifth);

			return top;	
		}

		/**
		 * If they are looking for the minimum value.
		 */
		if(tempchoice == 1) {
			for(int i = 0; i < weather.size(); i++) {
				if(weather.get(i).getValue() < first.getValue() && weather.get(i).getValue() != -9999) {
					first = weather.get(i);
				}else if(weather.get(i).getValue() < second.getValue() && weather.get(i).getValue() != -9999) {
					second = weather.get(i);
				}else if(weather.get(i).getValue() < third.getValue() && weather.get(i).getValue() != -9999) {
					third = weather.get(i);
				}else if(weather.get(i).getValue() < fourth.getValue() && weather.get(i).getValue() != -9999) {
					fourth = weather.get(i);
				}else if(weather.get(i).getValue() < fifth.getValue() && weather.get(i).getValue() != -9999) {
					fifth = weather.get(i);
				}
			}

			/**
			 * Adds the results to the top list.
			 */
			top.add(0, first);
			top.add(1, second);
			top.add(2, third);
			top.add(3, fourth);
			top.add(4, fifth);

			return top;	
		}
		return top;
	}

	/**
	 * This is a a simple method that performs a station search. It takes the list of stations
	 * and the String ID that we are looking for. This method is called after we have found
	 * our final 5 values and is mainly for printing/displaying results
	 * @param station
	 * @param targetValue
	 */
	public static void stationSearch(ArrayList<StationData> station, String targetValue)	{
		for (int i = 0; i < station.size(); i++) {
			if(station.get(i).getId().compareToIgnoreCase(targetValue) == 0 ) {
				System.out.println("id= " + station.get(i).getId() + " latitude= " + station.get(i).getLatitude() + " longitude= " + station.get(i).getLongitude() + " elevation= " + station.get(i).getElevation() +  " state= " + station.get(i).getState() + " name= " + station.get(i).getName());         
			}
		}	
	}
}