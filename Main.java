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

            System.out.print("successfully connected.\n");
            
            Statement stmt = con.createStatement();
            LinkedList<Queue> queues = new LinkedList<Queue>();
            ResultSet time = stmt.executeQuery("select MIN(FIRST_SWITCHED), MAX(FIRST_SWITCHED) from `flows-" + config.getDatestamp() + "v4_" + config.getInterfaceName() + "`;");
            time.next();
            boolean empty = false;
            int first = time.getInt(1),
            	last = time.getInt(2),
            	t = first;           

            /* Convert bps to Bpt */
            // 1b/s * 1B/8b * 0.000000015s/t
            double bandwidth_Bpt = config.getBandwidth() * (0.000000015/8);
            Schedule sched = new Schedule( config.getSchedule(), bandwidth_Bpt );

            /* Create queues */
            System.out.print("Creating queues...");
            for(int i=0; i<3; i++){
        		queues.add( new Queue(i) );
            }
            System.out.print("done.\n");

            /* Schedule packets */
            System.out.print("Scheduling packets...");
            do{
            	// check if there are still packets to be added to the queues
            	if(t <= last){
            		Statement stmt1 = con.createStatement();
					ResultSet flow = stmt1.executeQuery("select idx, BYTES, PACKETS, FIRST_SWITCHED, L4_DST_PORT from `flows-" + 
						config.getDatestamp() + "v4_" + 
						config.getInterfaceName() + 
						"` where FIRST_SWITCHED=" + t + "");

	            	if( flow.next() != false ){		// if a match or matches to time t is found
	            		while( flow.next() ){		// for all matches

	            			// assign priority
	            			int pri = getPriority(p.protocol, config.getSchedule());

	            			// create packets from flow data
	            			LinkedList<Packet> packets = createPackets(flow, pri);

	            			// add packets to appropriate queue
							for(Packet p: packets){
								Queue q = queues.remove(pri);
								q.add(p);
								queues.add(pri, q);
							}
	            		}
	            	}
            	}
            	
            	// process high priority queue

            	// check if queues are not empty
            	empty = queues.get(0).isEmpty();

            	// 'tick'
            	t++;

            }while(empty);// while queues are not empty 
            System.out.print("done.\n");

            /* Summarize network performance metrics */

        }catch (Exception e) {
        	System.out.print("error connecting.\n");
        	System.out.println(e);
            e.printStackTrace();
        }

	}// end of main function

}