//STEP 1. Import required packages
import java.sql.*;

public class DB_connector implements Runnable{
	
	public static int x,y;
	public static int tally;
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = " ... driver name ... ";  
	static final String DB_URL = " ... database url ... ";

	//  Database credentials
	static final String USER = " ... db user ... ";
	static final String PASS = " ... db password ... ";
	
	public DB_connector(){
		tally = 0;		
	}
	
	@Override
	public void run(){
		Connection conn = null;
		Statement stmt = null;
		String sql;
		
		
		try{
		//STEP 2: Register JDBC driver
		Class.forName("com.mysql.jdbc.Driver");
		
		//STEP 3: Open a connection
		System.out.println("Connecting to a selected database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		System.out.println("Connected database successfully...");
		stmt = conn.createStatement();
		
		stmt.executeUpdate("DELETE FROM positions");
		
		while (tally < 10){
			if (x != Tester_board.p2_x || y != Tester_board.p2_y){
				sql = "INSERT INTO positions " + "VALUES (" + tally + ", " + Tester_board.p2_x 
						+ ", " + Tester_board.p2_y + ", CURRENT_TIMESTAMP)";
				stmt.executeUpdate(sql);
				x = Tester_board.p2_x;
				y = Tester_board.p2_y;
				tally++;
			}
			else{
				System.out.println("sleeping...");
				Thread.sleep(500);
				
			}
		}
		
	}
		
	catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			}
	catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
				}
	finally{
		try{
	         if(stmt!=null)
	            conn.close();
	      }catch(SQLException se){
	    	  
	      }// do nothing
		//finally block used to close resources
		try{
			if(conn!=null)
				conn.close();
			}
		catch(SQLException se){
				se.printStackTrace();
				}//end finally try
		}//end try
	
	
	
	System.out.println("Goodbye!");
	}//end main
}//end DB_getter