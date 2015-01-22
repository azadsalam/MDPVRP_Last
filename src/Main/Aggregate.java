package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Aggregate {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		Scanner in = new Scanner(new File("inputResults.csv"));
		
		String prev= "nomatch";
		int count=0;
		double min=1000,max=-1,sum=0,sqr_sum=0;
		//HashMap<String, ArrayList<Double>> map = new HashMap<String, ArrayList<Double>>();

		System.out.println("Problem Instance"+", "+"Min"+", "+"Avg"+", "+"Max"+", "+"SD"+", "+"Count"+", ");

		while(in.hasNext())
		{
			String line=  in.nextLine();
			StringTokenizer st = new StringTokenizer(line,",");
			
			String pi = (String)st.nextElement();
			double cost = Double.parseDouble((String)st.nextElement());
			
			if(pi.equals(prev))
			{
				count++;

				if(cost<min) min = cost;
				if(cost>max) max = cost;
				sum+=cost;
				sqr_sum += (cost*cost);
			}
			else
			{
				if(!prev.equals("nomatch"))
				{
					double avg = (sum/count);
					double sd = Math.sqrt(sqr_sum/(count)- avg*avg);
					System.out.println(prev+", "+min+", "+avg+", "+max+", "+sd+", "+count+", ");
				}
				
				min = cost;
				max=cost;
				sum = cost;				
				prev = pi;
				sqr_sum = cost*cost;
				count=1;
			}
			//System.out.println(pi+" ->"+cost );
			
		}
		double avg = (sum/count);
		double sd = Math.sqrt(sqr_sum/(count)- avg*avg);
		System.out.println(prev+", "+min+", "+avg+", "+max+", "+sd+", "+count+", ");

	}

}
