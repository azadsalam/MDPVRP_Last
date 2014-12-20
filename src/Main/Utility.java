package Main;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

import Main.VRP.Individual.Individual;

public class Utility 
{
//	public static long SEED = System.currentTimeMillis();
	public static long SEED = 1;
	public static Random randomGenerator = new Random(SEED);

//	public static Random randomGenerator = new Random(0);

//	static Random randomGenerator = new Random(0);

	static MyComparator mc = new MyComparator();
	//returns a random numbor between [m,n] 
	
	public static int randomIntInclusive(int m,int n)
	{
		int random = randomGenerator.nextInt(n-m+1);
		return m+random;
	}
	
	public static double randomDouble(double m,double n)
	{
		double random = randomGenerator.nextDouble();
		return m+random*(n-m);
	}
	
	/**
	 * [0,n]
	 * @param n
	 * @return
	 */
	public static int randomIntInclusive(int n)
	{
		return  randomGenerator.nextInt(n+1);
	}
	
	/**
	 * [0,n-1]
	 * @param n
	 * @return
	 */
	public static int randomIntExclusive(int n)
	{
		return randomGenerator.nextInt(n);
	}

	/**
	 * Sorts in increasing order of cost, 
	 * BETTER INDIVIDUALS HAVE LOWER INDEX, best individual at 0
	 * Assumption : All individuals cost+penalty is calculated
	 * 
	*/
	public static void sort(Individual[] array)
	{
		sort(array, array.length);
	}
	
 	/**
	 * Sorts in increasing order of cost+penalty in rance [0, length)
	 * @param array
	 * @param length
	 */
	
	public static void sort(Individual[] array,int length)
	{
		Arrays.sort(array, 0, length,mc);
	}
	
	public static class MyComparator implements Comparator<Individual>
	{

		public int compare(Individual o1, Individual o2) {
			// TODO Auto-generated method stub
			if(o1.costWithPenalty < o2.costWithPenalty)
				return -1;
			else if(o1.costWithPenalty > o2.costWithPenalty)
				return 1;
			
			return 0;
		}
		
	}

	public static void sort(Vector<Individual> vec)
	{
		Individual temp;
		//FOR NOW DONE SELECTION SORT
		//AFTERWARDS REPLACE IT WITH QUICK SORT OR SOME OTHER O(n logn) sort
		for(int i=0;i<vec.size();i++)
		{
			for(int j=i+1;j<vec.size();j++)
			{
				if(vec.get(i).costWithPenalty > vec.get(j).costWithPenalty)
				{
					temp = vec.get(i);
					vec.set(i, vec.get(j));
					vec.set(j, temp);

				}
			}
		}

	}

	
	public static void concatPopulation(Individual target[],Individual[] first,Individual[] second)
	{
		for(int i=0;i<first.length;i++) 
			target[i] = first[i];
		for(int i=0;i<second.length;i++)
			target[i+first.length] = second[i];
	}
}	
	