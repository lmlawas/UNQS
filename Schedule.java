import java.io.FileWriter;
import java.util.LinkedList;

public class Schedule{

	/* Constants */
	final public static int FIFO = 0;
	final public static int PQ = 1;
	final public static int WFQ = 2;

	/* Attributes */
	public double packet_switched_size;
	public int packet_loss_cnt;
	public int packet_swicthed_cnt;
	public int packet_wait_cnt;
	public int total_wait_time;

	/* Constructors */
	public Schedule(){
		// for throughput
		packet_switched_size = 0;

		// for total packets
		packet_loss_cnt = 0;
		packet_swicthed_cnt = 0;

		// for average wait time
	 	packet_wait_cnt = 0;
	 	total_wait_time = 0;
	}

	/* Methods */
	public void process(double bandwidth, int schedule_type, int current_time, LinkedList<Queue> queues){
		
		if(schedule_type == FIFO){
			Queue q = queues.remove();			// get queue with priority 0

			/* start switching the packet */
			if(!q.isEmpty()){

				Packet p = q.remove();
				p.size = p.size - bandwidth;		// switch portion of the packet
				// update wait_time and timeout of packets in queue 'q'

				if(p.size > 0){
					q.add(0, p);					// if packet is not done switching, re-add to queue's head			
				}
				else{
					packet_swicthed_cnt = packet_swicthed_cnt + p.no_of_packets; // else, packet is successfully switched
					packet_switched_size = packet_switched_size + p.size; // store total bytes
					if( (current_time-p.start_time) > 0){
						packet_wait_cnt = packet_wait_cnt + p.no_of_packets; // update count for total packets that waited
						total_wait_time = total_wait_time + (current_time-p.start_time); // add wait time
					}
				}
			}
			queues.add(0, q);					// re-add queue with priority 0
			return;
		}// end of FIFO

		else if(schedule_type == PQ || schedule_type == WFQ){

			boolean previous_is_empty = true;
			int i;

			for(i = 2; i >= 0; i--){

				Queue q = queues.remove(i);				// get queue with priority i

				if(!q.isEmpty() && previous_is_empty){					

					/* start switching the packet */
					Packet p = q.remove();
					p.size = p.size - bandwidth;		// switch portion of the packet					

					if(p.size > 0){
						q.add(0, p);					// if packet is not done switching, re-add to queue's head						
					}
					else{
						packet_swicthed_cnt = packet_swicthed_cnt + p.no_of_packets; // else, packet is successfully switched
						packet_switched_size = packet_switched_size + p.size; // store total bytes
						if( (current_time-p.start_time) > 0){
							packet_wait_cnt = packet_wait_cnt + p.no_of_packets; // update count for total packets that waited
							total_wait_time = total_wait_time + (current_time-p.start_time); // add wait time
						}
					}

					if(!q.isEmpty()){
						previous_is_empty = false;		// check after processing if queue is empty
					}
				}
				else{
					previous_is_empty = true;
				}

				queues.add(i, q);						// re-add queue with priority i

				// update wait_time and timeout of packets in queue 'q'
				LinkedList<Packet> promote = new LinkedList<Packet>();//q.updateWaitAndTimeout(schedule_type);

				if(schedule_type == WFQ && !promote.isEmpty() && i < 2){

					q = queues.remove(i+1);				// get higher priority queue
					
					for(Packet p: promote){
						q.add(p);						// promote packets to higher priority queue
					}

					queues.add(i+1, q);					// re-add queue with priority i+1
				}				
			}

		}// end of PQ & WFQ

	}

	// public void status (LinkedList<Queue> queues, int t) throws Exception{

	// 	FileWriter debugger = new FileWriter("debug.txt", true);
	// 	int tps = 0, tpw = 0, tpl = 0;

	// 	debugger.write("\n\n-------------------------------");
	// 	debugger.write("\n  Time:" + t + " ticks");
	// 	for (int i=0; i<3; i++) {
	// 		Queue q = queues.get(i);
	// 		debugger.write("\n\tPriority " + i + ": " + q.size() + " packets");
	// 		tps = tps + q.packet_swicthed_cnt;
	// 		tpl = tpl + q.packet_loss_cnt;
	// 		tpw = tpw + q.packet_wait_cnt;
	// 	}

	// 	debugger.write("\n\tswitched:" + tps);
	// 	debugger.write("\n\tlost:" + tpl);
	// 	debugger.write("\n\twait:" + tpw);

	// 	debugger.close();
	// }// end of status()
}