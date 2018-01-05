/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

//Random string generator
import java.util.Random;
/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class AirBooking{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public AirBooking(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) {
			//System.out.println("Getting pid");
			//System.out.println(rs.getInt(1));
			return rs.getInt(1);
		}
		//System.out.println("Can't get pid");
		//System.out.println(-1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + AirBooking.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		AirBooking esql = null;
		
		try{
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new AirBooking (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Passenger");
				System.out.println("2. Book Flight");
				System.out.println("3. Review Flight");
				System.out.println("4. Insert or Update Flight");
				System.out.println("5. List Flights From Origin to Destination");
				System.out.println("6. List Most Popular Destinations");
				System.out.println("7. List Highest Rated Destinations");
				System.out.println("8. List Flights to Destination in order of Duration");
				System.out.println("9. Find Number of Available Seats on a given Flight");
				System.out.println("10. < EXIT");
				
				switch (readChoice()){
					case 1: AddPassenger(esql); break;
					case 2: BookFlight(esql); break;
					case 3: TakeCustomerReview(esql); break;
					case 4: InsertOrUpdateRouteForAirline(esql); break;
					case 5: ListAvailableFlightsBetweenOriginAndDestination(esql); break;
					case 6: ListMostPopularDestinations(esql); break;
					case 7: ListHighestRatedRoutes(esql); break;
					case 8: ListFlightFromOriginToDestinationInOrderOfDuration(esql); break;
					case 9: FindNumberOfAvailableSeatsForFlight(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	
	//done
	public static void AddPassenger(AirBooking esql){//1
		try{
			String query = String.format("SELECT pID FROM Passenger");
			List<List<String>> data = esql.executeQueryAndReturnResult(query);
			int pID = data.size() + 1;
			System.out.print("Please enter passport number: ");
			String passport_num = in.readLine();
			System.out.print("Please enter full name: ");
			String name = in.readLine();
			System.out.print("Please enter birth date: ");
			String dob = in.readLine();
			System.out.print("Please enter country: ");
			String country = in.readLine();
			
			query = String.format("INSERT INTO Passenger (pID, passNum, fullName, bdate, country) VALUES (%d ,'%s', '%s', '%s', '%s')", pID, passport_num, name, dob, country);
			esql.executeUpdate(query); 
			
			query = String.format("SELECT p.* FROM Passenger p WHERE pID = %d", pID);
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		//Add a new passenger to the database
	}
	
	//done
	public static void BookFlight(AirBooking esql){//2
		try{
			////create a booking id
			char bookingID[] = "0123456789".toCharArray();
			char alpha[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
			char digits[] = "0123456789".toCharArray();
			Random rand = new Random();
			for(int i = 0; i < 5; i++)
				bookingID[i] = alpha[rand.nextInt(26)];
			for(int i = 6; i < 9; i++)
				bookingID[i] = digits[rand.nextInt(9)];
			bookingID[9] = alpha[rand.nextInt(26)];
			String booking = new String(bookingID);
			
			System.out.print("\nPlease enter passport number: ");
			String passport_num = in.readLine();
			System.out.print("\nPlease enter flight orgin: ");
			String orgin = in.readLine();
			System.out.print("\nPlease enter flight destination: ");
			String dest = in.readLine();
			System.out.print("\nPlease enter departure date: ");
			String depart = in.readLine();
			
			String query = String.format("SELECT P.pID FROM Passenger P WHERE passNum = '%s'", passport_num);
			List<List<String>> data = esql.executeQueryAndReturnResult(query);
			//System.out.println(data  + " " + booking);
			
			query = String.format("SELECT F.flightNum FROM Flight F WHERE F.origin = '%s' AND F.destination = '%s' LIMIT 1",  orgin, dest);
			data.add(esql.executeQueryAndReturnResult(query).get(0));
			//System.out.println(data  + " " + booking);
			
			query = String.format("INSERT INTO Booking(bookRef, departure, flightNum, pID) VALUES( '%s','%s','%s', %d)", booking, depart, data.get(1).get(0), Integer.parseInt(data.get(0).get(0)));
			esql.executeUpdate(query);
			
			query = String.format("SELECT B.* FROM Booking B WHERE B.pID = '%s'", data.get(0).get(0));
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
		}catch(Exception e){
			//if(e.getMessage().contains("Index: 0"))
				//System.err.println("Data does not exist");
			System.err.println(e.getMessage());
		}
		//Book Flight for an existing customer
	}
	
	//done
	public static void TakeCustomerReview(AirBooking esql){//3
		
		try{
			String query = String.format("SELECT rID FROM Ratings");
			List<List<String>> data = esql.executeQueryAndReturnResult(query);
			int rID = data.size() + 1;
			//System.out.println(rID);
		
			System.out.print("Please enter passport number: ");
			String passport_num = in.readLine();
			query = String.format("SELECT pID FROM Passenger WHERE passNum = '%s'",passport_num);
			data = esql.executeQueryAndReturnResult(query);
		
			int pID = Integer.parseInt(data.get(0).get(0));
			
			System.out.print("Please enter flight number: ");
			String flight_num = in.readLine();
			
			System.out.print("Please enter rating: ");
			int rating = Integer.parseInt(in.readLine());
			
			System.out.print("Optional comment: ");
			String comment = in.readLine();
			
			query = String.format("INSERT INTO Ratings(rID, pID, flightNum, score, comment) VALUES( %d, %d,'%s', %d, '%s')" , rID, pID, flight_num, rating, comment);
			esql.executeUpdate(query);
			query = String.format("SELECT R.* FROM Ratings R WHERE R.rID = %d", rID);
			int rowCount = esql.executeQueryAndPrintResult(query);
			
		}catch(Exception e){
			if(e.getMessage().contains("Index: 0"))
				System.err.println("User does not exist");
			else System.err.println(e.getMessage());
				
		}
		//Insert customer review into the ratings table
	}
	
	public static void InsertOrUpdateRouteForAirline(AirBooking esql){//4
		
		//Insert a new route for the airline
	}
	
	//done
	public static void ListAvailableFlightsBetweenOriginAndDestination(AirBooking esql) throws Exception{//5
	try {
			System.out.print("Please enter flight orgin: ");
			String orgin = in.readLine();
			System.out.print("Please enter flight destination: ");
			String dest = in.readLine();
			
			String query = String.format("SELECT F.flightNum, F.origin, F.destination, F.plane, F.duration FROM Flight F WHERE F.origin = '%s' AND F.destination = '%s'", orgin, dest);
			
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}		
		//List all flights between origin and distination (i.e. flightNum,origin,destination,plane,duration) 
	}
	
	//done
	public static void ListMostPopularDestinations(AirBooking esql){//6
		try{
			System.out.print("Please enter number to display: ");
			String limit = in.readLine();
			String query = String.format("SELECT F.destination, COUNT(F.destination) FROM Flight F GROUP BY F.destination ORDER BY COUNT(F.destination) DESC LIMIT %s", limit);
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		//Print the k most popular destinations based on the number of flights offered to them (i.e. destination, choices)
	}
	
	//done
	public static void ListHighestRatedRoutes(AirBooking esql){//7
		try{
			System.out.print("Please enter number to display: ");
			String limit = in.readLine();
			
			String query = String.format("SELECT A.airId, F.flightNum, F.origin, F.destination, F.plane, AVG (R.score) FROM Airline A, Flight F, Ratings R WHERE A.airId = F.airId AND F.flightNum = R.flightNum GROUP BY A.airId, F.flightNum ORDER BY AVG(R.score) DESC LIMIT %s", limit);
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		
		//List the k highest rated Routes (i.e. Airline Name, flightNum, Avg_Score)
	}
	
	//done
	public static void ListFlightFromOriginToDestinationInOrderOfDuration(AirBooking esql){//8
		try {
			System.out.print("Please enter flight orgin: ");
			String orgin = in.readLine();
			System.out.print("Please enter flight destination: ");
			String dest = in.readLine();
			System.out.print("Please enter number to display: ");
			String limit = in.readLine();
			
			String query = String.format("SELECT A.name, F.flightNum, F.origin, F.destination, F.plane, F.duration FROM Airline A, Flight F WHERE A.airId = F.airId AND F.origin = '%s' AND F.destination = '%s' GROUP BY A.name, F.flightNum, F.duration ORDER BY F.duration ASC Limit %s", orgin, dest, limit);
			
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		//List flight to destination in order of duration (i.e. Airline name, flightNum, origin, destination, duration, plane)
	}
	//done
	public static void FindNumberOfAvailableSeatsForFlight(AirBooking esql){//9
		try{
			System.out.print("Please enter flight number: ");
			String flight_num = in.readLine();
			
			String query = String.format("SELECT F.flightNum, F.origin, F.destination, B.departure, COUNT(B), F.seats, F.seats - COUNT(B) AS Available FROM Flight F, Booking B WHERE F.flightNum = B.flightNum AND F.flightNum = '%s' GROUP BY F.flightNum, B.departure ORDER BY F.flightNum", flight_num);
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		
		//
		
	}
	
}
