import java.util.LinkedList;

public class Schedule{

	/* Constants */
	final public static int FIFO = 0;
	final public static int PQ = 1;
	final public static int WFQ = 2;

	/* Attributes */
	public int schedule_type;
	public double bandwidth;

	/* Constructors */
	public Schedule(){
		schedule_type = FIFO;
		bandwidth = 125000000; // 125 Megabytes per second or 1 Gigabit per second
	}

	public Schedule(int schedule_type, double bandwidth){
		this.schedule_type = schedule_type;
		this.bandwidth = bandwidth;
	}

	/* Methods */
	public void process(LinkedList<Queue> queues){
		
		if(schedule_type == FIFO){
			Queue q = queues.remove();			// get queue with priority 0

			/* start switching the packet */
			Packet p = q.remove();
			p.size = p.size - bandwidth;		// switch portion of the packet
			q.updateWaitAndTimeout(schedule_type);// update wait_time and timeout of packets in queue 'q'

			if(p.size > 0){
				q.add(0, p);					// if packet is not done switching, re-add to queue's head			
			}
			else{
				q.packet_swicthed_cnt++;		// else, packet is successfully switched
			}

			queues.add(0, q);					// re-add queue with priority 0

			return;
		}// end of FIFO

		else if(schedule_type == PQ){

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
						q.packet_swicthed_cnt++;		// else, packet is successfully switched
					}

					if(!q.isEmpty()){
						previous_is_empty = false;		// check after processing if queue is empty
					}
				}
				else{
					previous_is_empty = true;
				}

				q.updateWaitAndTimeout(schedule_type);	// update wait_time and timeout of packets in queue 'q'

				queues.add(i, q);						// re-add queue with priority i
			}

		}// end of PQ

		else if(schedule_type == WFQ){

		}

	}
}