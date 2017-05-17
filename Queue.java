import java.util.LinkedList;

public class Queue extends LinkedList<Packet>{

	/* Attributes */
	public double current_buffer_size;
	public int packet_loss_cnt;
	public int packet_swicthed_cnt;
	public int packet_wait_cnt;
	public int priority;

	/* Constructors */
	public Queue(){
		current_buffer_size = 0;
		packet_loss_cnt = 0;
		packet_swicthed_cnt = 0;
		packet_wait_cnt = 0;
		priority = 2;
	}

	public Queue(int priority){
		current_buffer_size = 0;
		packet_loss_cnt = 0;
		packet_swicthed_cnt = 0;
		packet_wait_cnt = 0;
		this.priority = priority;
	}

	/* Methods*/
	public Queue updateWaitAndTimeout(int schedule_type){

		Queue promote = new Queue(this.priority);

		int i, n = this.size();

		for(i = 0; i < n; i++){
			Packet p = this.remove(i);				// get ith packet
			p.wait_time++;							// increment wait time
			p.timeout--;							// decrement timeout

			if(schedule_type == Schedule.WFQ && p.wait_time % 50 == 0){
				promote.add(p);						// if schedule_type is WFQ and weight for higher priority is reached
			}

			else if(p.timeout > 0){
				add(i, p);							// if packet is not yet timed out
			}			
			else{
				packet_loss_cnt++;
			}
		}

		return promote;
	}
}