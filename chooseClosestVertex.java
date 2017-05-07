
public class chooseClosestVertex {
	
	public static int choose(int a, int[][] A, int p){
    	if (p == 0){
		a = A[p][0];
		System.out.println("a: " + a);
	}
	else if (p != 0){
		a = A[p -1][1];
		System.out.println("a: " + a);
	}
    	return a;
    }
}
