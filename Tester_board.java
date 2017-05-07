import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Tester_board extends JPanel implements MouseListener{
	
	public int q1; // location of inserted point
	public int q2;
	static double p1_min_key;
	Map<Double, Double> p1_dists = new LinkedHashMap<>();
	Map<Integer, Double> indexes = new LinkedHashMap<>();
	Map<Integer, Double> y_intercepts = new LinkedHashMap<>();
	Map<Integer, Double> v_dists = new LinkedHashMap<>();
	Map<Integer, Double> line_dists = new LinkedHashMap<>();
	double v_dist;
	static double m_p1;
	static double m_y_intercept;
	static double y_intercept;
	public static int p2_x;
	public static int p2_y;
	static double b0_v_y_intercept; 
	static double b1_v_y_intercept;
	static int y0_formula;
	static int y1_formula;
	static int ii = 0;
	static int iii = 0;
	public static double p1_min;
	public static double pp_min;
	public static int p1_min_index;
	public static int pp_min_index;
	public static boolean point_is_out_of_bounds;
	static boolean we_have_snap;
	public double sum_l_dist = 0;
	public boolean notifier;
	public boolean dragging = false; //describes state of mouse dragging
	private int QV_x;
	private int QV_y;
	
	// polyline coords
	public int[][] v_x = {{40, 106}, {106, 230}, {230, 358}, {358, 390}, {390, 450}};
    	public int[][] v_y = {{60, 153}, {153, 283}, {283, 350}, {350, 312}, {312, 250}};
    	int v = v_x.length;    
    
    public static double round_no(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
    private static void min_value_retriever(Map<Double, Double> map, double smallest_key){
    	Collection<Double> c = map.values();
    	p1_min = Collections.min(c); // the smallest distance to Polyline
    	for (Entry<Double, Double> entry : map.entrySet()) {  
            if (entry.getValue().equals(p1_min)) {
                smallest_key = (double) entry.getKey();
                p1_min_key = smallest_key;
                break;
            }
    	}
    }
    
    private static void min_value_index_retriever(Map<Integer, Double> map, int smallest_key){
    	Collection<Double> c = map.values();
    	p1_min = Collections.min(c); // the smallest distance to Polyline
    	for (Entry<Integer, Double> entry : map.entrySet()) {  
            if (entry.getValue().equals(p1_min)) {
                smallest_key = (int) entry.getKey();
                p1_min_index = smallest_key;
                break;
            }
    	}
    }
    
    private static void min_value_index_retriever2(Map<Integer, Double> map, int smllst_key){
    	Collection<Double> c = map.values();
    	pp_min = Collections.min(c); // the smallest distance to Polyline
    	System.out.println("pp_min: " + pp_min);
    	for (Entry<Integer, Double> entry : map.entrySet()) {  
            if (entry.getValue().equals(pp_min)) {
                smllst_key = (int) entry.getKey();
                pp_min_index = smllst_key;
                System.out.println("pp_min_index: " + pp_min_index);
                break;
            }
    	}
    }
    
    private static void pointChecker(double a, int b, int c, int[][] d, int[][] e, int i){
    	m_p1 = -round_no((1/a), 5); // inverted p1
    	m_y_intercept = round_no(c - m_p1*b, 5); // q2 - inverted p1 * q1
    	y_intercept = round_no(((e[i][0] - a*d[i][0])), 5); 
    	p2_x = (int) Math.abs((y_intercept - m_y_intercept)/(m_p1 - a)); // x cord of point on line
    	p2_y = (int) (p2_x*m_p1 + m_y_intercept); // y cord of point on line
    	
    	b0_v_y_intercept = round_no((e[i][0] - m_p1*d[i][0]), 5); // intercept for line perpendicular to point 0
    	b1_v_y_intercept = round_no((e[i][1] - m_p1*d[i][1]), 5); // intercept for line perpendicular to point 1
    	y0_formula = (int) (d[i][0]*m_p1 + b0_v_y_intercept); // y position of point 0 
    	y1_formula = (int) (d[i][1]*m_p1 + b1_v_y_intercept); // y position of point 1
    }
    
    public Tester_board(){
    	addMouseMotionListener(new EventMouseMotionListener());
    	addMouseListener(new EventMouseListener());
    	}
    
    
    public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		displayMLines(g, v, v_x, v_y);
		if (notifier == true){
			displayPoint(g, q1, q2, Color.BLACK);
			calculateInlinePoint(g, v_x, v_y, q1, q2);
		}
		if (notifier == false){
			displayPoint(g, QV_x, QV_y, Color.RED);
			System.out.println("editing, QV_x and QV_y: " + QV_x + ", " + QV_y);
		}
		//calculateDistanceOnRoute(g); <----- 
		
	}
	
    private void calculateInlinePoint(Graphics g, int[][] v_x, int[][] v_y, int q1, int q2){
    	
    	// creates hashmaps for distances between inserted point and lines
    	for (int o = 0; o < v; o++){
        	double p1 = ((double) (v_y[o][1] - v_y[o][0])/(v_x[o][1] - v_x[o][0]));
        	y_intercept = round_no(((v_y[o][0] - p1*v_x[o][0])), 5); // for line equation
        	double normalLength = Math.sqrt((v_x[o][1]-v_x[o][0])*(v_x[o][1]-v_x[o][0])+(v_y[o][1]-v_y[o][0])*(v_y[o][1]-v_y[o][0]));
            double d = Math.abs((q1-v_x[o][0])*(v_y[o][1]-v_y[o][0])-(q2-v_y[o][0])*(v_x[o][1]-v_x[o][0]))/normalLength;
        	p1_dists.put(p1, d);
        	indexes.put(o, d);
        	}
    	
    	// creates hashmap for distances between inserted point and line vertexes
    	for (int i = 0; i < v; i++){
			if (i == 0){
				double p2_v_dist = Math.sqrt(Math.abs((q2 - v_y[i][0])*(q2 - v_y[i][0]))+Math.abs((q1 - v_x[i][0])*(q1 - v_x[i][0])));
				v_dists.put(i, p2_v_dist);
			}
			else {
				double p2_v_dist = Math.sqrt(Math.abs((q2 - v_y[i-1][1])*(q2 - v_y[i-1][1]))+Math.abs((q1 - v_x[i-1][1])*(q1 - v_x[i-1][1])));
				v_dists.put(i, p2_v_dist);
				if (i == v - 1){
					p2_v_dist = Math.sqrt(Math.abs((q2 - v_y[i][1])*(q2 - v_y[i][1]))+Math.abs((q1 - v_x[i][1])*(q1 - v_x[i][1])));
					v_dists.put(i+1, p2_v_dist);
					break;
				}
			}
		}

    	min_value_retriever(p1_dists, p1_min_key);
    	min_value_index_retriever(indexes, p1_min_index);
    	pointChecker(p1_min_key, q1, q2, v_x, v_y, p1_min_index);
    	min_value_index_retriever2(v_dists, pp_min_index);
    	
    	int u1 = 0;
    	int u2 = 0;
    	
    	while (point_is_out_of_bounds == false || we_have_snap == false){
    		if (v_y[p1_min_index][0] < v_y[p1_min_index][1]){ // while line vertex 0 is above line vertex 1
        		if (((q2 < (q1*m_p1 + b0_v_y_intercept) || q2 > q1*m_p1 + b1_v_y_intercept)) || (p1_min > 100) || (pp_min < p1_min)){
        			u1++;
        			System.out.println("u1: " + u1);
        			if ((q2 < (q1*m_p1 + b0_v_y_intercept) || q2 > q1*m_p1 + b1_v_y_intercept)){System.out.println("war1 spelniony");}
        			if (p1_min > 100){System.out.println("war2 spelniony");}
        			if (pp_min < p1_min){System.out.println("war3 spelniony");}
        			p1_dists.remove(p1_min_key);
        			indexes.remove(p1_min_index);
        			System.out.println("p1_dists size " + p1_dists.size());
        			if (p1_dists.size() == 0){
        				if (pp_min_index == 0){
        					p2_x = v_x[pp_min_index][0];
        					p2_y = v_y[pp_min_index][0];
        				}
        				else if (pp_min_index != 0){
        					p2_x = v_x[pp_min_index -1][1];
        					p2_y = v_y[pp_min_index -1][1];
        				}
        				// displayPoint(g, p2_x, p2_y, Color.BLUE);
        				point_is_out_of_bounds = true;
        				break;
        			};
        			min_value_retriever(p1_dists, p1_min_key);
        	    	min_value_index_retriever(indexes, p1_min_index);
        	    	pointChecker(p1_min_key, q1, q2, v_x, v_y, p1_min_index);
        	    	}
        		
        		else{
        			// displayPoint(g, p2_x, p2_y, Color.BLUE);
        			we_have_snap = true;
        			System.out.println("we have snap!");
        			break;
        		}
            	
        	}
    		
    		if (point_is_out_of_bounds == false || we_have_snap == false){
    			if (v_y[p1_min_index][0] > v_y[p1_min_index][1]){
            		if ((q2 > (q1*m_p1 + b0_v_y_intercept) || q2 < ( q1*m_p1 + b1_v_y_intercept)) || (p1_min > 100) || (pp_min < p1_min)){
            			u2++;
            			System.out.println("u2: " + u2);
            			if ((q2 < (q1*m_p1 + b0_v_y_intercept) || q2 > q1*m_p1 + b1_v_y_intercept)){System.out.println("war1 spelniony");}
            			if (p1_min > 100){System.out.println("war2 spelniony");}
            			if (pp_min < p1_min){System.out.println("war3 spelniony");}
            			p1_dists.remove(p1_min_key);
            			indexes.remove(p1_min_index);
            			System.out.println("p1_dists size " + p1_dists.size());
            			if (p1_dists.size() == 0){
            				if (pp_min_index == 0){
            					p2_x = v_x[pp_min_index][0];
            					p2_y = v_y[pp_min_index][0];
            				}
            				else if (pp_min_index != 0){
            					p2_x = v_x[pp_min_index -1][1];
            					p2_y = v_y[pp_min_index -1][1];
            				}
            				// displayPoint(g, p2_x, p2_y, Color.BLUE);
            				point_is_out_of_bounds = true;
            				break;
            			};
            			min_value_retriever(p1_dists, p1_min_key);
            	    	min_value_index_retriever(indexes, p1_min_index);
            	    	pointChecker(p1_min_key, q1, q2, v_x, v_y, p1_min_index);
            	    	}
            		else {
            			// displayPoint(g, p2_x, p2_y, Color.BLUE);
            			we_have_snap = true;
            			System.out.println("we have snap!");
            			break;
            			}
            		}
    			}
    		
    		else {
    			break;
    			}
    		}
    	
    	System.out.println("p2_x and p2_y: " + p2_x + " ," + p2_y);
    		
    	if (point_is_out_of_bounds == false && we_have_snap == true){
    		g.setColor(Color.RED);
    		g.drawRect(v_x[p1_min_index][0] - 3, v_y[p1_min_index][0] - 3, 6, 6);
    		g.drawRect(v_x[p1_min_index][1] - 3, v_y[p1_min_index][1] - 3, 6, 6);
        	g.drawLine(v_x[p1_min_index][0], v_y[p1_min_index][0], v_x[p1_min_index][1], v_y[p1_min_index][1]);
    	}
    	
    	double single_l_dist;
    	if (pp_min < 100 && point_is_out_of_bounds == true){
    		displayPoint(g, p2_x, p2_y, Color.GREEN);
    		g.drawLine(q1, q2, p2_x, p2_y);
    		for (int i = 0; i < pp_min_index; i++){
    			if (pp_min_index == 0){
    				break;
    				}
    			
    			single_l_dist = Math.sqrt(Math.abs((v_y[i][1] - v_y[i][0])*(v_y[i][1] - v_y[i][0]))
    					+Math.abs((v_x[i][1] - v_x[i][0])*(v_x[i][1] - v_x[i][0])));
    			sum_l_dist = sum_l_dist + single_l_dist;
    			System.out.println("Distance of line i " + i + ": " + single_l_dist);
    		}
    		
    		System.out.println("Green - Distance traveled: " + sum_l_dist);
    		g.drawString("Green - Distance traveled: " + sum_l_dist, 10, 10);
        	sum_l_dist = 0;
    	}
    	
    	else if (p1_min < 100 && point_is_out_of_bounds == false){
    		displayPoint(g, p2_x, p2_y, Color.BLUE);
    		g.drawLine(q1, q2, p2_x, p2_y);
    		if (p1_min_index == 0){
    			single_l_dist = Math.sqrt(Math.abs((p2_y - v_y[p1_min_index][0])*(p2_y - v_y[p1_min_index][0]))
    					+Math.abs((p2_x - v_x[p1_min_index][0])*(p2_x - v_x[p1_min_index][0])));
    			sum_l_dist = sum_l_dist + single_l_dist;
    			System.out.println("Distance of line i " + ": " + single_l_dist);
    		}
    		
    		else {
    			single_l_dist = Math.sqrt(Math.abs((p2_y - v_y[p1_min_index][0])*(p2_y - v_y[p1_min_index][0]))
    					+Math.abs((p2_x - v_x[p1_min_index][0])*(p2_x - v_x[p1_min_index][0])));
    			sum_l_dist = sum_l_dist + single_l_dist;
    			for (int i = 0; i < p1_min_index; i++){
        				single_l_dist = Math.sqrt(Math.abs((v_y[i][1] - v_y[i][0])*(v_y[i][1] - v_y[i][0]))
        						+Math.abs((v_x[i][1] - v_x[i][0])*(v_x[i][1] - v_x[i][0])));
        				System.out.println("Distance of line " + i + ": " + single_l_dist);
        				sum_l_dist = sum_l_dist + single_l_dist;
        				if (i == p1_min_index){
        					break;
        					}	
        			}
    			}
    		
    		System.out.println("Distance traveled: " + sum_l_dist);
    		g.drawString("Blue - Distance traveled: " + sum_l_dist, 10, 10);
        	sum_l_dist = 0;
    	}
    	    	
    	System.out.println(p1_min + "; " + pp_min);
    	p1_dists.clear();
    	indexes.clear();
    	v_dists.clear();
    	point_is_out_of_bounds = false;
    	
    }
    
    private void displayPoint(Graphics g, int x, int y, Color col){
    	
    	int xPoints[] = {(int) Math.round(x + 1), (int) Math.round(x - 1), 
    			(int) Math.round(x - 1), (int) Math.round(x + 1)};
        int yPoints[] = {(int) Math.round(y - 1), (int) Math.round(y - 1), 
    			(int) Math.round(y + 1), (int) Math.round(y + 1)};
        int nPoints = 4;
    	Graphics2D g2d = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(rh);
        
        g.setColor(col);
        g.drawPolygon(xPoints, yPoints, nPoints);
        //g.drawString(Integer.toString(x) + " , " + Integer.toString(y), x + 5, y + 2);
	}
    
	private void displayMLines(Graphics g, int v, int[][] v_x, int[][] v_y){
		
		Graphics2D g2d = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(rh);
	    g2d.setColor(Color.BLACK);
		for (int i = 0; i < v; i++){
			g.drawLine(v_x[i][0], v_y[i][0], v_x[i][1], v_y[i][1]);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
	class EventMouseMotionListener extends MouseMotionAdapter{
		public void mouseDragged(MouseEvent e){
			if (SwingUtilities.isLeftMouseButton(e)){
				if (dragging){
					Point p = e.getPoint();
					q1 = (int) p.getX();
					q2 = (int) p.getY();
					notifier = true;
					Tester_board.this.repaint();
				}
			}
		}
	}
	class EventMouseListener extends MouseAdapter{
		public void mousePressed(MouseEvent e){
			if (SwingUtilities.isLeftMouseButton(e)){
				q1 = e.getX();
				q2 = e.getY();
				notifier = true;
				dragging = true;
				repaint();
			}
			if (SwingUtilities.isRightMouseButton(e)){
				QV_x = chooseClosestVertex.choose(QV_x, v_x, pp_min_index);
				QV_y = chooseClosestVertex.choose(QV_y, v_y, pp_min_index);
				System.out.println("QV_x and QV_y: " + QV_x + ", " + QV_y);
				notifier = false;
				repaint();
			}
		}
		public void mouseReleased(MouseEvent e){
			dragging = false;
		}
	}
}
