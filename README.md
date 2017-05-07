# Snap-Point-to-Line

A GUI-based program written in Java that simulates a snap point to line feature using the AWT and SWING libraries. The program has predefined coordinates for a polyline that is rendered in a window. Clicking the left mouse button on the area in the vicinity of the line will result in a projection of the point onto the polyline along with a perpindicular projection line. The locations of the projected points can be saved into a pre-existing MySQL database with a frequency of every 0.5s. To disable the database connection thread, disable last two lines in main method in Tester.java.

I made this when I was just learning Java, that is why it is mostly "Spaghetti Code". Comments are scarce. It might not work for any random polyline. 
