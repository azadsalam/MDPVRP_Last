package Main.VRP.Individual.MutationOperators;

import java.security.AllPermission;
import java.util.ArrayList;

import sun.misc.Perf;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
import com.sun.security.ntlm.Client;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class PatternImprovementNew {

	public static int apply = 0;	
	public static double totalSec=0;

	public static boolean print= false;
	
	public static void patternImprovement(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean improveResultantRoute)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;

		//now the client is chosen
		int chosenClient;
		int noOfPossiblePatterns;
		do
		{
			chosenClient = Utility.randomIntInclusive(problemInstance.customerCount-1);
			noOfPossiblePatterns = problemInstance.allPossibleVisitCombinations.get(chosenClient).size();
		}while(noOfPossiblePatterns==1);

		ArrayList<Integer> allComb = problemInstance.allPossibleVisitCombinations.get(chosenClient);
		
		double costBefore=-1,costAfter=-1;
		
/*		if(print)
		{
			System.out.println("Patterns: "+allComb);
			TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			costBefore  = individual.costWithPenalty;
		}
*/		
		
		//now calculate the cost of the routes on which the client is currently present
		//remove them from those routes
		//calculate the new cost of those routes
		//calculate the improvement		
		double improvementOfOldRoutes = removeOccurancesOfThisClientFromAllPeriod(individual,chosenClient,loadPenaltyFactor,routeTimePenaltyFactor,true,improveResultantRoute);
		
		//now repeatedly add different patterns - chose the best one
		double bestImprovement = Double.NEGATIVE_INFINITY;
		int bestPattern = -1;
		for(int i=0; i<noOfPossiblePatterns;i++)
		{
			
			int newPattern = allComb.get(i);
			
			//original
			updatePeriodAssignment(individual, chosenClient, newPattern);
			double improvementOfNewRoutes = assignNewPatternToClient(individual,chosenClient,newPattern,loadPenaltyFactor,routeTimePenaltyFactor,true,false,false);
			//for print
		//	double improvementOfNewRoutes = assignNewPatternToClient(individual,chosenClient,newPattern,loadPenaltyFactor,routeTimePenaltyFactor,true,true,false);
			
			double improvement = improvementOfNewRoutes+improvementOfOldRoutes;

			//add again and return 
			if(improvement>bestImprovement)
			{
				
				bestImprovement = improvement;
				bestPattern = newPattern;			
				if(print)
				{
					System.out.println("NEW BEST FOUND");
					System.out.println("CURRENT: Best Improvement: "+bestImprovement+" Best Pattern: "+bestPattern);
				}
			}

/*			if(print)
			{
				System.out.println("New Pattern: "+newPattern);
				TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
				costAfter  = individual.costWithPenalty;
				System.out.println("Cost Before: "+costBefore);
				System.out.println("Cost After: "+costAfter);
				System.out.println("Improvement by explicit Evaluation: "+ (costBefore-costAfter));
				System.out.println("Improvement after removing occurances: "+ improvementOfOldRoutes);
				System.out.println("Improvement after adding occurances: "+ improvementOfNewRoutes);
				System.out.println("Total Improvement Calculated: "+ (improvementOfOldRoutes+improvementOfNewRoutes));
				updatePeriodAssignment(individual, chosenClient, newPattern);
				removeOccurancesOfThisClientFromAllPeriod(individual, chosenClient, loadPenaltyFactor, routeTimePenaltyFactor, false, false);
			}
*/
			
				
		}
		
		if(print)
		{
			System.out.println("Best Improvement: "+bestImprovement+" Best Pattern: "+bestPattern);
		}
		
		updatePeriodAssignment(individual, chosenClient, bestPattern);
		assignNewPatternToClient(individual, chosenClient, bestPattern, loadPenaltyFactor, routeTimePenaltyFactor, false, true, improveResultantRoute);
		
			
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}
	
	
	/**
	 * removes the client from all visited periods. Also resets the period assignment
	 * <br/> if calculateImprovement is true it returns the improvement gained by removing the client
	 * <br/> the method calculates the cost of the routes before and after removing the client, and returns the difference (improvement)
	 * @param individual
	 * @param client
	 * @param loadPenaltyFactor
	 * @param routeTimePenaltyFactor
	 * @param calculateImprovement
	 * @return
	 */
	public static double removeOccurancesOfThisClientFromAllPeriod(Individual individual, int client, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean calculateImprovement, boolean improveRoutes) 
	{
		if(print)System.out.println("Client to be removed: "+client);
		ProblemInstance pi = individual.problemInstance;
		double improvement=0;
		
		for(int p=0;p<pi.periodCount;p++)
		{
			if(individual.periodAssignment[p][client]==false)continue;
			
			int vehicle = RouteUtilities.assignedVehicle(individual, client, p, pi);
			if(print)System.out.println("Period: "+p);
			if(print)System.out.println("Before removal of "+client+" ->"+individual.routes.get(p).get(vehicle));

			
			if(calculateImprovement) improvement += individual.calculateCostWithPenalty(p, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			
			individual.routes.get(p).get(vehicle).remove(new Integer(client));
			//individual.periodAssignment[p][client]=false;
			
			if(improveRoutes)Neigbour_Steps_Grouped.improveRoute(individual, p, vehicle);
			if(calculateImprovement) improvement -= individual.calculateCostWithPenalty(p, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);					
			if(print)System.out.println("After removal of "+client+" ->"+individual.routes.get(p).get(vehicle));

		}
		return improvement;
	}

	public static double assignNewPatternToClient(Individual individual, int client, int pattern, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean calculateImprovement, boolean addActually, boolean improveRoutes) 
	{
		double improvement=0;
		
		if(print)System.out.println("Client to be assigned new pattern(inserted): "+client);
		ProblemInstance pi = individual.problemInstance;
		
		//int[] periodAssignment = pi.toBinaryArray(pattern);
		
		for(int p=0;p<pi.periodCount;p++)
		{
			if(individual.periodAssignment[p][client]==false)continue;
			//insert this client to the best possible place :D :D

			

			MinimumCostInsertionInfo minInfo = findBestPossibleInsertion(individual, p, client, loadPenaltyFactor, routeTimePenaltyFactor);

			if(print)System.out.println("Period: "+p);
			if(print)System.out.println("Before addition  "+client+" ->"+individual.routes.get(p).get(minInfo.vehicle));

			//old cost
			improvement += individual.calculateCostWithPenalty(p, minInfo.vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			//add originally
			individual.routes.get(p).get(minInfo.vehicle).add(minInfo.insertPosition, client);
			
			//new cost
			improvement -= individual.calculateCostWithPenalty(p, minInfo.vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			if(print)System.out.println("After adding  "+client+" ->"+individual.routes.get(p).get(minInfo.vehicle));

			
			if(!addActually)individual.routes.get(p).get(minInfo.vehicle).remove(new Integer(client));
			
			if(addActually && improveRoutes) Neigbour_Steps_Grouped.improveRoute(individual, p, minInfo.vehicle);
		}
		return improvement;
	}
	
	public static MinimumCostInsertionInfo findBestPossibleInsertion(Individual individual, int period, int client, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{

		MinimumCostInsertionInfo min = new  MinimumCostInsertionInfo();
		MinimumCostInsertionInfo newInfo;
		min.increaseInCost=Double.MAX_VALUE;
		min.loadViolationContribution = Double.MAX_VALUE;
		min.insertPosition=-1;
		min.routeTimeViolationContribution = Double.MAX_VALUE;
		min.routeTimeViolation = Double.MAX_VALUE;
		
		for(int vehicle = 0;vehicle<Individual.problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			newInfo= RouteUtilities.minimumCostInsertionPosition(Individual.problemInstance, vehicle, client, route);
			
			double minCostWithPenalty = min.increaseInCost + min.loadViolationContribution * loadPenaltyFactor + min.routeTimeViolationContribution * routeTimePenaltyFactor;
			double newCostWithPenalty = newInfo.increaseInCost + newInfo.loadViolationContribution * loadPenaltyFactor + newInfo.routeTimeViolationContribution * routeTimePenaltyFactor;
						
			if(newCostWithPenalty < minCostWithPenalty)
			{
				min = newInfo;
			}
			else if (newCostWithPenalty == minCostWithPenalty)
			{
				int coin = Utility.randomIntInclusive(1);
				if(coin==1)
					min=newInfo;
			}
		}		
		return min;
	}

	public static void updatePeriodAssignment(Individual individual, int client, int pattern)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		individual.visitCombination[client]=pattern;
		int[] newBitArray = problemInstance.toBinaryArray(pattern);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(newBitArray[period]==1) 
			{
				individual.periodAssignment[period][client] = true;
			}	
			else
			{
				individual.periodAssignment[period][client] = false;
			}
		}
	}

	/*public static void resetPeriodAssignment(Individual individual, int client)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			individual.periodAssignment[period][client] = false;
		}
	}
	*/
}
