/*
	To compile:
		javac -cp ".;mysql-connector-java.jar;" *.java
	To run:
		java -cp .:mysql-connector-java.jar Main simulation.conf
*/
import java.io.Console;
import java.io.FileWriter;
import java.sql.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Main{

	public static void main(String args[]){
		Console sensitive = System.console();
		Scanner input = new Scanner( System.in );
		String choice, s;
		int read = 0;
		
		Configuration config = new Configuration();
		config.setDefault();

		if(args.length == 0){
			do{
				System.out.print("Continue with the default configuration? (Y/N) ");
				choice = input.nextLine();

				if( choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes") ){
					read = 1;
					break;
				}

				else if(choice.equalsIgnoreCase("n") || choice.equalsIgnoreCase("no")){
					System.out.println("Please re-run program with a configuration file. (ex. Main test.conf)");
					return;
				}

				else{
					System.out.println("Invalid choice. Try again.");
				}
			}while(true);			
		}

		else if(args.length > 1){
			System.out.println("Error. Too many arguments. Please re-run program.");
			return;
		}

		/* configure using .conf file */
		else if( args.length == 1 ){
			
			read = config.readFile( args[0] );
			
			if(read == 1){
				
				if( config.hasPassword() ){
					if( sensitive == null ) return;
					System.out.print("MySQL password: ");
				    s = String.valueOf( sensitive.readPassword() );
				    config.setPassword( s );
				}
			}
		}

		if(read == 1){
			config.show();
		}

	}
}