
public class Packet{
	
	/* Attributes */
	public int id;
	public int priority;
	public double size;
	public int timeout;
	public int wait_time;

	/* Constructors */
	public Packet(){		
	}

	public Packet(int id, int priority, double size, int timeout, int wait_time){
		this.id = id;
		this.priority = priority;
		this.size = size;
		this.timeout = timeout;
		this.wait_time = wait_time;
	}
}