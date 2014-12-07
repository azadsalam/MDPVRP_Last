package Main.VRP.Individual.MutationOperators;

import java.security.AllPermission;
import java.util.ArrayList;

import com.sun.security.ntlm.Client;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class PatternImprovementNew_CONFLICTED_BY_GOOGLE {

	public static int apply = 0;	
	public static double totalSec=0;

	public static boolean print= true;
	
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
		
		if(print)
		{
			TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			costBefore  = individual.costWithPenalty;
		}
		
		//now calculate the cost of the routes on which the client is currently present
		//remove them from those routes
		//calculate the new cost of those routes
		//calculate the improvement		
		double improvementOfOldRoutes = removeOccurancesOfThisClientFromAllPeriod(individual,chosenClient,loadPenaltyFactor,routeTimePenaltyFactor,true,improveResultantRoute);
		
		
		for(int i=0; i< noOfPossiblePatterns;i++)
		{
			int newPattern = allComb.get(i);
			
			double improvementOfNewRoutes = assignNewPatternToClient(individual,chosenClient,newPattern,loadPenaltyFactor,routeTimePenaltyFactor,true,improveResultantRoute);
			
			if(print)
			{
				TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
				costAfter  = individual.costWithPenalty;
				System.out.println("Cost Before: "+costBefore);
				System.out.println("Cost After: "+costAfter);
				System.out.println("Improvement by explicit Evaluation: "+ (costBefore-costAfter));
				System.out.println("Improvement after removing occurance: "+ improvementOfOldRoutes);
			}
		}
		/*for(int i=0;i<noOfPossiblePatterns ;i++)
		{
			
		}*/
			
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		System.exit(0);
	}
	
	
	/**
	 * removes the client from all visited periods
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
			if(print)System.out.println("Before removal of "+client+" ->"+individual.routes.get(p).get(vehicle));

			
			if(calculateImprovement) improvement += individual.calculateCostWithPenalty(p, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			individual.routes.get(p).get(vehicle).remove(new Integer(client));
			
			
			if(improveRoutes)Neigbour_Steps_Grouped.improveRoute(individual, p, vehicle);
			if(calculateImprovement) improvement -= individual.calculateCostWithPenalty(p, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);					
			if(print)System.out.println("After removal of "+client+" ->"+individual.routes.get(p).get(vehicle));

		}
		return improvement;
	}

	public static double assignNewPatternToClient(Individual individual, int client, int pattern, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean calculateImprovement, boolean improveRoutes) 
	{
		double improvement=0;
		
		if(print)System.out.println("Client to be assigned new pattern(inserted): "+client);
		ProblemInstance pi = individual.problemInstance;
		
		for(int p=0;p<pi.periodCount;p++)
		{
			if(individual.periodAssignment[p][client]==false)continue;
			//insert this client to the best possible place :D :D
			
			
			
		}
		return improvement;
	}
	
	public static MinimumCostInsertionInfo addClientIntoPeriodGreedy(Individual individual, int period, int client, double loadPenaltyFactor, double routeTimePenaltyFactor,boolean improveResultantRoute)
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

}
