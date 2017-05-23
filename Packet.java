import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Packet{
	
	/* Attributes */	
	public double size;
	public int no_of_packets;
	public int protocol;
	public int start_time;

	/* Constructors */
	public Packet(){		
	}

	public Packet(double size, int no_of_packets, int protocol, int start_time){		
		this.size = size;
		this.no_of_packets = no_of_packets;
		this.protocol = protocol;
		this.start_time = start_time;
	}

	/* Methods */
	public int getPriority(int schedule_type){

		// if schedule type uses priority queue or weighted fair queueing
		if(schedule_type == Schedule.PQ || schedule_type == Schedule.WFQ){

			// check all text files
			for(int i=0; i<3; i++){
				try{
					String filename = "priority/"+i+".txt";
					FileReader fp = new FileReader(filename);
					BufferedReader br = new BufferedReader(fp);
					String line = null;

					try{
						while((line = br.readLine()) != null){

							// if protocol is found in text file
							if(protocol == Integer.parseInt(line)){
								br.close();
								fp.close();
								return i;
							}
						}
					}catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}catch (FileNotFoundException e2) {
					e2.printStackTrace();
				}
			}		
		}
		return 0;
	}// end of getPriority()
}