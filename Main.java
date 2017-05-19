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

	public static final double SECONDS_PER_TICK = 0.000000015;
	public static final int BITS_PER_BYTE = 8;

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
            double bandwidth_Bpt = config.getBandwidth() * (SECONDS_PER_TICK/BITS_PER_BYTE);
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

	            			// create packets from flow data
	            			LinkedList<Packet> packets = createPackets(flow.getInt(1), flow.getDouble(2), flow.getInt(3), flow.getInt(5), (int)(config.getTimeout()/SECONDS_PER_TICK), config.getSchedule());

	            			// add packets to appropriate queue
							for(Packet p: packets){
								Queue q = queues.remove(p.priority);
								q.add(p);
								queues.add(p.priority, q);
							}
	            		}
	            	}
            	}
            	
            	// process high priority queue
            	sched.process(queues);

            	// check if queues are not empty
            	for(int priority=2; priority>=0; priority--){
            		empty = queues.get(priority).isEmpty();
            		if(!empty){
            			break;
            		}
            	}

            	// increment time
            	t++;

            }while(!empty);// while queues are not empty 
            System.out.print("done.\n");

            /* Compute the network performance metrics */
            double tbs = 0, total_wait_time = 0;
            int tpl = 0, tps = 0, tpw = 0;
            for(Queue q: queues){
            	tbs = tbs + (q.total_buffer_size*BITS_PER_BYTE);	// convert bytes to bits then add to tbs
            	tpl = tpl + q.packet_loss_cnt;
            	tps = tps + q.packet_swicthed_cnt;
            	tpw = tpw + q.packet_wait_cnt;
            	total_wait_time = total_wait_time + q.total_wait_time;
            }
            double duration = (t*SECONDS_PER_TICK);			// convert ticks to seconds
            double throughput = tbs/duration;            

            Statement stmt2 = con.createStatement();
            ResultSet ave_packet_size = stmt2.executeQuery("select SUM(BYTES), SUM(PACKETS) from `flows-" + config.getDatestamp() + "v4_" + config.getInterfaceName() + "`;");
            ave_packet_size.next();

            /* Summarize network performance metrics */
            System.out.println("\n-------------------------------");
            System.out.println("Bandwidth:\t" + config.getBandwidth() + " bps");
            System.out.println("Duration:\t" + duration + " seconds");
            System.out.println("Throughput:\t" + throughput + " bps");
            System.out.println("Count packets...");
            System.out.println("\t...lost:\t" + tpl + " packets (" + ((double)tpl*100/(double)(tpl+tps+tpw)) +"%)");
            System.out.println("\t...switched:\t" + tps + " packets (" + ((double)tps*100/(double)(tpl+tps+tpw)) +"%)");
            System.out.println("\t...waited:\t" + tpw + " packets (" + ((double)tpw*100/(double)(tpl+tps+tpw)) +"%)");
            System.out.println("Average packet...");
            System.out.println("\t...size:\t" + (ave_packet_size.getDouble(1)/ave_packet_size.getDouble(2)) + " bits");
            System.out.println("\t...wait time:\t" + (total_wait_time/(double)tpw) + " seconds");

        }catch (Exception e) {
        	System.out.print("error connecting.\n");
        	System.out.println(e);
            e.printStackTrace();
        }

	}// end of main function	

	public static LinkedList<Packet> createPackets(int idx, double size, int no_of_packets, int protocol, int timeout, int schedule_type){
		LinkedList<Packet> packets = new LinkedList<Packet>();

		for(int i=0; i<no_of_packets; i++){
			Packet p = new Packet(idx, 0, protocol, size/no_of_packets, timeout, 0);
			p.priority = p.getPriority(schedule_type);
			packets.add(p);
		}

		return packets;
	}

}