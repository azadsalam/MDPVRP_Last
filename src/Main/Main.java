package Main;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main 
{
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException 
	{
		final Solver solver = new Solver();
		//solver.initialise();
		try {
			solver.solve();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
					

		
	}

}
