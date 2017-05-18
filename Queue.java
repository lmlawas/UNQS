import java.util.LinkedList;

public class Queue extends LinkedList<Packet>{

	/* Attributes */
	public double total_buffer_size;
	public int packet_loss_cnt;
	public int packet_swicthed_cnt;
	public int packet_wait_cnt;
	public int priority;
	public int total_wait_time;

	/* Constructors */
	public Queue(){
		total_buffer_size = 0;
		packet_loss_cnt = 0;
		packet_swicthed_cnt = 0;
		packet_wait_cnt = 0;
		priority = 2;
		total_wait_time = 0;
	}

	public Queue(int priority){
		total_buffer_size = 0;
		packet_loss_cnt = 0;
		packet_swicthed_cnt = 0;
		packet_wait_cnt = 0;
		this.priority = priority;
		total_wait_time = 0;
	}

	/* Methods*/
	public Queue updateWaitAndTimeout(int schedule_type){

		Queue promote = new Queue(this.priority);

		int i, j = 0, n = this.size();

		for(i = 0; i < n; i++){
			Packet p = this.remove(j);				// get jth packet
			p.wait_time++;							// increment wait time
			p.timeout--;							// decrement timeout

			if(schedule_type == Schedule.WFQ && p.wait_time % 50 == 0){
				promote.add(p);						// if schedule_type is WFQ and weight for higher priority is reached
			}

			else if(p.timeout > 0){
				add(j, p);							// if packet is not yet timed out
				j++;								// update j
			}			
			else{
				packet_loss_cnt++;
			}
		}

		return promote;
	}
}