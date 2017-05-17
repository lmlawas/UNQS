import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Packet{
	
	/* Attributes */
	public int id;
	public int priority;
	public int protocol;
	public double size;
	public int timeout;
	public int wait_time;

	/* Constructors */
	public Packet(){		
	}

	public Packet(int id, int priority, int protocol, double size, int timeout, int wait_time){
		this.id = id;
		this.priority = priority;
		this.protocol = protocol;
		this.size = size;
		this.timeout = timeout;
		this.wait_time = wait_time;
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
	}
}