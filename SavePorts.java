import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;

public class SavePorts{
	
	public static void main(String args[]){

		String filename = "service-names-port-numbers.csv";

		// get the existing ports
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thesis", "root", "pArkEr09");

			System.out.print("successfully connected.\n");

			Statement stmt = con.createStatement();            
			ResultSet rs = stmt.executeQuery("select distinct L4_DST_PORT from ( select L4_DST_PORT from `flows-04-17-17v4_0` union select L4_DST_PORT from `flows-04-18-17v4_0` union select L4_DST_PORT from `flows-04-25-17v4_0` union select L4_DST_PORT from `flows-04-28-17v4_0` union select L4_DST_PORT from `flows-04-29-17v4_0`) t order by L4_DST_PORT asc;");

			FileWriter fw = new FileWriter("l4_dst_port.txt", true);

			while(rs.next()){				
				int line_no = 1;

				try{
		        	FileReader fp = new FileReader(filename);
					BufferedReader br = new BufferedReader(fp);
					String line = null;			

		        	while((line = br.readLine()) != null){
		        		String[] cols = line.split(",");
		        		if(cols.length>=3){
			        		String service_name = cols[0];
			        		String port = cols[1];

			        		if( port.contains("-") ){
			        			String[] values = port.split("-");
			        			int high = Integer.parseInt( values[1] ),
			        				low = Integer.parseInt( values[0] );

			        			// System.out.println("+"\tlength_arr:"+values.length);
			        			if(low <= rs.getInt(1) && rs.getInt(1) <= high ){
			        				System.out.println(rs.getInt(1) + "\t" + service_name);
									fw.write( rs.getInt(1) + "\t" + service_name + "\n");
									break;
			        			}
			        		}
			        		else{
			        			if( rs.getString(1).equals(port) ){
				        			System.out.println(rs.getInt(1) + "\t" + service_name);
									fw.write( rs.getInt(1) + "\t" + service_name + "\n");
									break;
				        		}			        		
			        		}
		        		}
		        		
		        		line_no++;
		        	}
		        }catch (FileNotFoundException e) {
		        	e.printStackTrace();
		        }

				// fw.write(rs.getInt(1)+"\n");
			} 
			fw.close();
        }catch (Exception e) {
        	e.printStackTrace();
        }
 
	}
}