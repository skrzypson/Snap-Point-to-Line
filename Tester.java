import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import javax.swing.JFrame;


public class Tester extends JFrame{
	
	private static void GUIinit(){
		
		EventQueue.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            Tester ex = new Tester();
	            ex.setSize(500,500);
	            ex.setTitle("Position tracker");
	            ex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		ex.setLocationRelativeTo(null);
	    		ex.setBackground(Color.WHITE);
	            ex.setVisible(true);
	            ex.add(new Tester_board());
	            
	        }
	    });
		
	}
	
	public static void main(String[] args) {
	    
	    GUIinit();
	    Thread connector = new Thread(new DB_connector()); 	// database connection object
	    connector.start();									// starts connection
	}

	
}
