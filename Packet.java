
public class Packet{
	
	// attributes
	public int id;
	public int priority;
	public double size;
	public int timeout;
	public int wait_time;	
	public int weight;

	// constructors
	public Packet(){		
	}

	public Packet(int id, int priority, double size, int timeout, int wait_time, int weight){
		this.id = id;
		this.priority = priority;
		this.size = size;
		this.timeout = timeout;
		this.wait_time = wait_time;
		this.weight = weight;
	}
}