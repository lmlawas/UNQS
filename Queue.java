import java.util.LinkedList;

public class Queue extends LinkedList<Packet>{

	// attributes
	public double current_buffer_size;
	public int packet_loss_cnt;
	public int packet_swicthed_cnt;
	public int packet_wait_cnt;
	public int priority;

	// constructors
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

}