package Main.VRP.Individual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Main.Utility;
import Main.VRP.ProblemInstance;

public class InitialisePeriodAssigmentWithHeuristic 
{

	//private static boolean createBigRouteOnce = true;
	private static boolean bigRouteCreated = false;
	private static ProblemInstance savedProblemInstance=null;
	private static ArrayList<Integer> bigGuidingRoute;
	private static double limitPerPeriod;
	
	public static void createBigGuidingRoute(ProblemInstance problemInstance)
	{
		
		//System.err.println("TESTING IN HEURISTIC PA, CREATES NEW GUIDING ROUTE EVERY ITERATION");
		if(savedProblemInstance == problemInstance && bigRouteCreated) return;
		
		savedProblemInstance = problemInstance;
		
		limitPerPeriod = 0;
		for(int vehicle =0; vehicle< problemInstance.vehicleCount;vehicle++)
		{
			limitPerPeriod += problemInstance.loadCapacity[vehicle];
		}
			
		
		int customerCount = problemInstance.customerCount;
		int assigned = 0;
		boolean map[] = new boolean[customerCount];
		
		bigGuidingRoute = new ArrayList<Integer>();
		
		while(assigned < customerCount)
		{
			int randClient = Utility.randomIntExclusive(customerCount);
			if(map[randClient]) continue;
			map[randClient] = true;
			assigned++;
			
			MinimumCostInsertionInfo minInfo = RouteUtilities.minimumCostInsertionPositionWithoutDepot(problemInstance, randClient, bigGuidingRoute);
			
			bigGuidingRoute.add(minInfo.insertPosition, randClient);
			
		}
		
		//System.out.println(limitPerPeriod);
		
		bigRouteCreated = true;
	}
	public static void initialise(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		//int customerCount = problemInstance.customerCount;
		//create one big route
		createBigGuidingRoute(problemInstance);

		int bigGuidingRouteSize = bigGuidingRoute.size();
		int position = Utility.randomIntExclusive(bigGuidingRouteSize);
		int assigned = 0;
		
		double[] loadPerDay = new double [problemInstance.periodCount];
		
		// assign pattern for first customer randomly
		int client = bigGuidingRoute.get(position);
		ArrayList <Integer> possiblilities =  problemInstance.allPossibleVisitCombinations.get(client);
		// assign pattern for first customer randomly
		
		int size = possiblilities.size();
		int ran = Utility.randomIntInclusive(size-1);
		individual.visitCombination[client] = possiblilities.get(ran);	
		initialisePAChromosome(problemInstance, individual, client,loadPerDay,false);
		assigned++;
		position++;
		if(position>=bigGuidingRouteSize)
			position -= bigGuidingRouteSize;

		
		
		while(assigned<bigGuidingRouteSize)
		{
			int previousClientPosition = position-1;
			if(previousClientPosition < 0 ) previousClientPosition = bigGuidingRouteSize-1;

			int previousClient = bigGuidingRoute.get(previousClientPosition);
			int previousClientPattern = individual.visitCombination[previousClient];
			
					
			client = bigGuidingRoute.get(position);
			possiblilities =  problemInstance.allPossibleVisitCombinations.get(client);
			
			//among all the possibilities, find the one that matches previous pattern most
			
/*			System.out.println("CLient: "+client+" load: "+problemInstance.demand[client]);
			System.out.println("Previous Client Pattern "+ previousClientPattern);
*/			int max = -1;
			int maxMatchinPoint = -1;
			int bestPattern = -1;
			int bestMatch = -1;
			for(int i=0;i<possiblilities.size();i++)
			{
				int thisPattern = possiblilities.get(i);
				int matchingPoint = matchPoint(thisPattern, previousClientPattern);
				individual.visitCombination[client] = thisPattern;	
				double violation = initialisePAChromosome(problemInstance, individual, client, loadPerDay, true);
				
								
				if(matchingPoint > max && violation <= 0)
				{
					max = matchingPoint;
					bestPattern = thisPattern;
				}
				else if(matchingPoint == max && violation <= 0)
				{
					if(Utility.randomIntInclusive(1) == 1)
						bestPattern = thisPattern;
				}
				
				
				if(matchingPoint > maxMatchinPoint) 
				{
					maxMatchinPoint  = matchingPoint;
					bestMatch = thisPattern;
				}
				
/*				System.out.println("This Pattern "+ thisPattern);
				System.out.println("Mtching Point: "+ matchingPoint+" violation: "+violation+" best pattern: "+bestPattern+" best match: "+bestMatch+" max match point "+maxMatchinPoint);
				for(int j=0;j<loadPerDay.length;j++)
					System.out.print(loadPerDay[j]+" ");
				System.out.println();
*/				
			}
			
			if(bestPattern == -1) bestPattern = bestMatch ; 
			
			individual.visitCombination[client] = bestPattern;
			initialisePAChromosome(problemInstance, individual, client,loadPerDay,false);
			
			//advance cursor
			assigned++;
			position++;
			if(position>=bigGuidingRouteSize)
				position -= bigGuidingRouteSize;
		}
		
		
//		System.out.println(Arrays.asList(loadPerDay));

/*		for(int j=0;j<loadPerDay.length;j++)
			System.out.print(loadPerDay[j]+" ");
		System.out.println();
*/	}
	

    static int matchPoint(int pattern1, int pattern2)
    {
        int pat = pattern1 & pattern2;
        int res = 0;
        while(pat != 0)
        {
            res += (pat &1);
            pat = pat >>> 1;
        }
        //System.out.println("In match function: "+" pat1: "+pattern1+" pat2: "+pattern2+" match point "+res);
        return res;
    }
    
	/**
	 *  the patternChromosome( VIsit Combination) must be defined before calling this procedure
	 * @param problemInstance
	 * @param individual
	 * @param client
	 */
	private static double initialisePAChromosome(ProblemInstance problemInstance,Individual individual, int client, double[] loadPerDay, boolean onlyCheck) 
	{
		
		double violation=0;
		int[] bitArray = problemInstance.toBinaryArray(individual.visitCombination[client]);
		for(int period = 0;period<problemInstance.periodCount;period++)
		{
			boolean bool ;
			
			if(bitArray[period]==1) 
			{
				bool = true;
				
				if(onlyCheck == false)
					loadPerDay[period] += problemInstance.demand[client];
				
				double newLoad = loadPerDay[period] + problemInstance.demand[client];

				if(newLoad > limitPerPeriod)
				{
					violation += (newLoad - limitPerPeriod);
					//System.out.println("new Load: "+newLoad+" limit: "+limitPerPeriod+" violation increment: "+(newLoad-limitPerPeriod)+" violation: "+violation);

				}
			}			
			else bool = false;
			
			individual.periodAssignment[period][client] = bool; 
		}

		//System.out.println("in violation func. pattern: "++"violation: "+violation);

		return violation;
	}
	
	
	

}
