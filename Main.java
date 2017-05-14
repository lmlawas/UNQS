/*
	To compile:
		javac -cp ".;mysql-connector-java.jar;" *.java
	To run:
		java -cp .:mysql-connector-java.jar Main simulation.conf
*/
import java.io.Console;
import java.io.FileWriter;
import java.sql.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Main{

	public static void main(String args[]){
		Console sensitive = System.console();
		Scanner input = new Scanner( System.in );
		String choice, s;
		int read = 0;
		
		Configuration config = new Configuration();
		config.setDefault();

		/* use default configuration */
		if(args.length == 0){
			do{
				System.out.print("Continue with the default configuration? (Y/N) ");
				choice = input.nextLine();

				if( choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes") ){
					read = 1;
					break;
				}

				else if(choice.equalsIgnoreCase("n") || choice.equalsIgnoreCase("no")){
					System.out.println("Please re-run program with a configuration file. (ex. Main test.conf)");
					return;
				}

				else{
					System.out.println("Invalid choice. Try again.");
				}
			}while(true);			
		}

		else if(args.length > 1){
			System.out.println("Error. Too many arguments. Please re-run program.");
			return;
		}

		/* configure using .conf file */
		else if( args.length == 1 ){
			
			read = config.readFile( args[0] );
			
			if(read == 1){
				
				if( config.hasPassword() ){
					if( sensitive == null ) return;
					System.out.print("MySQL password: ");
				    s = String.valueOf( sensitive.readPassword() );
				    config.setPassword( s );
				}
			}
		}

		if(read == 1){
			config.show();
		}

		/* connect to database */
		System.out.println("-------------------------------");	
    	System.out.print("\nConnecting to database...");
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://"+ config.getIpAddress() + ":" + config.getPortNumber() + "/" + config.getDbName(), config.getUsername(), config.getPassword());
            Schedule sched = new Schedule( config.getSchedule(), config.getBandwidth() );
            Statement stmt = con.createStatement();
            LinkedList<Queue> queues = new LinkedList<Queue>();
            ResultSet ts = stmt.executeQuery("select MIN(FIRST_SWITCHED), MAX(FIRST_SWITCHED) from `flows-" + config.getDatestamp() + "v4_" + config.getInterfaceName() + "`;");
            ts.next();            
            int first = ts.getInt(1),
            	last = ts.getInt(2),
            	t, 
            	total_bytes = 0;
            boolean added = false;

            System.out.print("successfully connected.\n");
        }catch (Exception e) {
        	System.out.print("error connecting.\n");
        	System.out.println(e);
            e.printStackTrace();
        }

	}// end of main function

}