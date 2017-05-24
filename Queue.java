import java.util.LinkedList;

public class Queue extends LinkedList<Packet>{

	/* Attribute */
	public LinkedList<Integer> start_times;	

	/* Constructors */
	public Queue(){
	}

	/* Methods*/
	public Queue updateWaitAndTimeout(int schedule_type){

		Queue promote = new Queue();

		// int i, j = 0, n = this.size();

		// for(i = 0; i < n; i++){
		// 	Packet p = this.remove(j);				// get jth packet
		// 	p.wait_time++;							// increment wait time
		// 	p.timeout--;							// decrement timeout

		// 	if(schedule_type == Schedule.WFQ && p.wait_time % 50 == 0){
		// 		promote.add(p);						// if schedule_type is WFQ and weight for higher priority is reached
		// 	}

		// 	else if(p.timeout > 0){
		// 		add(j, p);							// if packet is not yet timed out
		// 		j++;								// update j
		// 	}			
		// 	else{
		// 		packet_loss_cnt++;
		// 	}
		// }

		return promote;
	}// end of updateWaitAndTimeout()
}