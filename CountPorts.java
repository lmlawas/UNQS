import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;

public class CountPorts{
	
	public static void main(String args[]){

		String filename = "service-names-port-numbers.csv";

		// get the existing ports
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thesis", "root", args[0]);

			System.out.print("successfully connected.\n");

			Statement stmt = con.createStatement();            
			ResultSet rs = stmt.executeQuery("select distinct L4_DST_PORT from ( select L4_DST_PORT from `flows-04-17-17v4_0` union select L4_DST_PORT from `flows-04-18-17v4_0` union select L4_DST_PORT from `flows-04-25-17v4_0` union select L4_DST_PORT from `flows-04-28-17v4_0` union select L4_DST_PORT from `flows-04-29-17v4_0`) t order by L4_DST_PORT asc;");

			FileWriter fw = new FileWriter("l4_dst_port_counts.txt", true);

			while(rs.next()){				
				int line_no = 1;
				Statement stmt1 = con.createStatement();
				ResultSet rs1 = stmt1.executeQuery("select count(*) from `flows-04-17-17v4_0` where L4_DST_PORT ="+ rs.getInt(1));
				Statement stmt2 = con.createStatement();
				ResultSet rs2 = stmt2.executeQuery("select count(*) from `flows-04-18-17v4_0` where L4_DST_PORT = "+ rs.getInt(1));
				Statement stmt3 = con.createStatement();
				ResultSet rs3 = stmt3.executeQuery("select count(*) from `flows-04-25-17v4_0` where L4_DST_PORT = "+ rs.getInt(1));	
				Statement stmt4 = con.createStatement();
				ResultSet rs4 = stmt4.executeQuery("select count(*) from `flows-04-28-17v4_0` where L4_DST_PORT = "+ rs.getInt(1));
				Statement stmt5 = con.createStatement();
				ResultSet rs5 = stmt5.executeQuery("select count(*) from `flows-04-29-17v4_0` where L4_DST_PORT ="+ rs.getInt(1));

				rs1.next();
				rs2.next();
				rs3.next();
				rs4.next();
				rs5.next();

				int total = rs1.getInt(1) + rs2.getInt(1) + rs3.getInt(1) + rs4.getInt(1) + rs5.getInt(1);

				fw.write( rs.getInt(1) + ":\t" + total + "\n");

			} 
			fw.close();
        }catch (Exception e) {
        	e.printStackTrace();
        }
 
	}
}