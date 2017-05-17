import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Configuration{

	/* Attributes */
	private String datestamp,
					dbName,
					ipAddr,
					password,
					port,
					username;

	private boolean debug,
					with_password;

	private int interface_name,				
				schedule_type,
				timeout;
	
	private double bandwidth;

	/* Getters */

	// ...for connection.

	boolean hasPassword(){
		return with_password;
	}

	String getIpAddress(){
		return ipAddr;
	}

	String getPortNumber(){
		return port;
	}

	String getDbName(){
		return dbName;
	}

	String getDatestamp(){
		return datestamp;
	}

	int getInterfaceName(){
		return interface_name;
	}

	String getUsername(){
		return username;
	}

	String getPassword(){
		return password;
	}

	// ...for queue.
	int getSchedule(){
		return schedule_type;
	}

	double getBandwidth(){
		return bandwidth;
	}
	
	int getTimeout(){
		return timeout;
	}

	// ... for main
	boolean getDebug(){
		return debug;
	}

	/* Setters */

	// ...for connection.
	boolean setIpAddress(String ipAddr){
		boolean match = Pattern.matches("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
			ipAddr);
		if(match){
			this.ipAddr = ipAddr;
			return true;
		}
		return false;
	}

	boolean setPortNumber(String port){
		boolean match = Pattern.matches("[0-9]{1,5}+", port);
		if(match){
			this.port = port;
			return true;
		}
		return false;
	}

	void setDbName(String dbName){
		this.dbName = dbName;
	}

	boolean setDatestamp(String datestamp){
		boolean match = Pattern.matches("[0-1][0-9]-[0-3][0-9]-[0-9]{2}?", datestamp);
			
		if( match ){
			if( isValidDate(datestamp) ){
				this.datestamp = datestamp;
				return true;
			}
		}
		return false;
	}

	void setUsername(String username){
		this.username = username;
	}

	void setPassword(String password){
		this.password = password;
	}

	void setInterfaceName(int interface_name){
		this.interface_name = interface_name;
	}

	// for queue
	void setSchedule(int schedule_type){
		this.schedule_type = schedule_type;
	}

	void setBandwidth(double bandwidth){
		this.bandwidth = bandwidth;
	}	

	void setTimeout(int timeout){
		this.timeout = timeout;
	}

	// ...for main
	void setDebug(boolean d){
		debug = d;
	}

	/* Methods */

	/* reference: http://stackoverflow.com/questions/2149680/regex-date-format-validation-on-java */
	boolean isValidDate(String input) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yy");
		try {
		  format.parse(input);
		  return true;
		}
		catch(Exception e){
		  return false;
		}
	}

	/* reference: https://www.mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/ */
	String getDateToday(){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yy");
		LocalDate localDate = LocalDate.now();
		return dtf.format(localDate);
	}

	void setDefault(){

		// connection configuration
		ipAddr = "localhost";
		port = "3306";
		dbName = "ntopng";
		datestamp = getDateToday();
		username = "root";
		interface_name = 0;
		with_password = false;
		password = "";

		// queue configuration
		bandwidth = 1000000000; // 1Gb
		schedule_type = 0;		// FIFO
		timeout = 90;			// 90 s

		// program mode
		debug = false;
	}

	int updateConfig(String input, int line_no){
		boolean match = false;

		// comment
		match = Pattern.matches("#.*", input);
		if(match){
			return 0; // no update
		}

		// white space
		match = Pattern.matches("", input);
		if(match){
			return 0; // no update
		}

		match = Pattern.matches("^--ip-address="+
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
			input);
		if(match){
			String[] parts = input.split("=");
			setIpAddress(parts[1]);
			return 1;
		}

		match = Pattern.matches("^--port="+"[0-9]{1,5}+", input);
		if(match){
			String[] parts = input.split("=");
			setPortNumber(parts[1]);
			return 1;
		}

		match = Pattern.matches("^--database=.+", input);
		if(match){
			String[] parts = input.split("=");
			setDbName(parts[1]);
			return 1;
		}

		match = Pattern.matches("^--username=.+", input);
		if(match){
			String[] parts = input.split("=");
			setUsername(parts[1]);
			return 1;
		}

		match = Pattern.matches("^--password", input);
		if(match){
			with_password = true;
			return 1;
		}

		match = Pattern.matches("^--interface=[0-2]", input);
		if(match){
			String[] parts = input.split("=");
			setInterfaceName( Integer.parseInt(parts[1]) );
			return 1;
		}		

		match = Pattern.matches("^--date=[0-1][0-9]-[0-3][0-9]-[0-9]{2}?", input);
		if(match){
			String[] parts = input.split("=");			
			if(isValidDate(parts[1])){
				setDatestamp(parts[1]);
				return 1;
			}
			return -1;			
		}

		match = Pattern.matches("^--bandwidth=[0-9]+?", input);
		if(match){
			String[] parts = input.split("=");
			setBandwidth( Double.parseDouble(parts[1]) );
			return 1;
		}

		match = Pattern.matches("^--schedule=[0-2]", input);
		if(match){
			String[] parts = input.split("=");
			setSchedule( Integer.parseInt(parts[1]) );
			return 1;
		}

		match = Pattern.matches("^--timeout=[0-9]+?", input);
		if(match){
			String[] parts = input.split("=");
			setTimeout( Integer.parseInt(parts[1]) );
			return 1;
		}

		match = Pattern.matches("^--debug", input);
		if(match){
			debug = true;
			return 1;
		}

		else{
			System.out.println("Error in .conf file line " + line_no);
			return -1;
		}
	}

	int readFile(String filename){
		try{
			FileReader fp = new FileReader(filename);
			BufferedReader br = new BufferedReader(fp);
			String line = null;
			int i=1, update=0;

			while((line = br.readLine()) != null) {
				update = updateConfig(line, i);
				i++;
				if(update == -1) return -1;
			}

			return 1;
		}catch(Exception e){
			return -1;
		}
	}

	void show(){

		String sched = null;
		if(schedule_type == Schedule.FIFO){
			sched = "FIFO";
		}
		else if(schedule_type == Schedule.PQ){
			sched = "PQ";
		}
		else if(schedule_type == Schedule.WFQ){
			sched = "WFQ";
		}

		System.out.println("-------------------------------");		
		System.out.println("CONFIGURATION");

		// connection configuration
		System.out.println("\t[ database connection ]");
		System.out.println("\tusername = " + username);
		System.out.println("\twith_password = " + with_password);	
		System.out.println("\tipAddr = " + ipAddr);
		System.out.println("\tport = " + port);
		System.out.println("\tdbName = " + dbName);
		System.out.println("\tdatestamp = " + datestamp);		
		System.out.println("\tinterface_name = " + interface_name);
			

		// queue configuration
		System.out.println("\n\t[ queue ]");
		System.out.println("\tbandwidth = " + bandwidth + " bps");
		System.out.println("\tschedule = " + sched);
		System.out.println("\ttimeout = " + timeout + " s");		

		// program mode
		System.out.println("\n\t[ program mode ]");
		System.out.println("\tdebug = " + debug +"\n");
	}
}