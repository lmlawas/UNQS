import java.io.FileWriter;
import java.sql.*;

public class SavePorts{
	
	public static void main(String args[]){

		 try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thesis", "root", "pArkEr09");

            System.out.print("successfully connected.\n");
            
            Statement stmt = con.createStatement();            
            ResultSet rs = stmt.executeQuery("select distinct L4_DST_PORT from ( select L4_DST_PORT from `flows-04-17-17v4_0` union select L4_DST_PORT from `flows-04-18-17v4_0` union select L4_DST_PORT from `flows-04-25-17v4_0` union select L4_DST_PORT from `flows-04-28-17v4_0` union select L4_DST_PORT from `flows-04-29-17v4_0`) t order by L4_DST_PORT asc;");

			FileWriter fw = new FileWriter("ports.txt", true);

			while(rs.next()){
				System.out.println(rs.getInt(1));
				fw.write( rs.getInt(1) + "\n");
			}
			fw.close();
        }catch (Exception e) {
        	
        }
		
	}
}