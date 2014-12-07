package Main.VRP.Individual.MutationOperators;

import java.security.AllPermission;
import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class PatternImprovement {

	public static int apply = 0;	
	public static double totalSec=0;

	
	public static void patternImprovementOptimzed(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean improveResultantRoute)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		int chosenClient = Utility.randomIntInclusive(problemInstance.customerCount-1);
		
		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double currentCost = individual.costWithPenalty; 		
		int currentVisitPattern = individual.visitCombination[chosenClient]; 
		//Individual bestNode = individual;
		
		int chosenVisitPattern=currentVisitPattern;		
		int noOfPossiblePatterns = problemInstance.allPossibleVisitCombinations.get(chosenClient).size();
		
		for(int i=0;i<noOfPossiblePatterns ;i++)
		{
			int newVisitPattern = problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i);
			if(newVisitPattern==currentVisitPattern) continue;
			
			Individual newNode = new Individual(individual);
			
			changeVisitPattern(newNode, chosenClient, newVisitPattern,loadPenaltyFactor,routeTimePenaltyFactor,false);
			TotalCostCalculator.calculateCost(newNode, loadPenaltyFactor, routeTimePenaltyFactor);
			if(newNode.costWithPenalty<currentCost)
			{
				chosenVisitPattern = newVisitPattern;
				currentCost = newNode.costWithPenalty;		
			}
			else if(newNode.costWithPenalty == currentCost)
			{
				int coin = Utility.randomIntInclusive(1);
				if(coin==1)	
				{
					chosenVisitPattern = newVisitPattern;
				}
			}
		}
		
		if(chosenVisitPattern != currentVisitPattern)
			changeVisitPattern(individual, chosenClient, chosenVisitPattern,loadPenaltyFactor,routeTimePenaltyFactor,improveResultantRoute);
	
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}
	
	/*
	 
	public static void patternImprovement(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean improveResultantRoute)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		int chosenClient = Utility.randomIntInclusive(problemInstance.customerCount-1);
		
		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double currentCost = individual.costWithPenalty; 		
		int currentVisitPattern = individual.visitCombination[chosenClient]; 
		//Individual bestNode = individual;
		
		int chosenVisitPattern=currentVisitPattern;		
		int noOfPossiblePatterns = problemInstance.allPossibleVisitCombinations.get(chosenClient).size();
		
		for(int i=0;i<noOfPossiblePatterns ;i++)
		{
			int newVisitPattern = problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i);
			if(newVisitPattern==currentVisitPattern) continue;
			
			Individual newNode = new Individual(individual);
			
			changeVisitPattern(newNode, chosenClient, newVisitPattern,loadPenaltyFactor,routeTimePenaltyFactor,false);
			TotalCostCalculator.calculateCost(newNode, loadPenaltyFactor, routeTimePenaltyFactor);
			if(newNode.costWithPenalty<currentCost)
			{
				chosenVisitPattern = newVisitPattern;
				currentCost = newNode.costWithPenalty;		
			}
			else if(newNode.costWithPenalty == currentCost)
			{
				int coin = Utility.randomIntInclusive(1);
				if(coin==1)	
				{
					chosenVisitPattern = newVisitPattern;
				}
			}
		}
		
		if(chosenVisitPattern != currentVisitPattern)
			changeVisitPattern(individual, chosenClient, chosenVisitPattern,loadPenaltyFactor,routeTimePenaltyFactor,improveResultantRoute);
	
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}
	 
	 */
	
	public static void patternImprovementOfAllClients(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		//int totalcustomer = problemInstance.customerCount;
		
		for(int chosenClient=0;chosenClient<problemInstance.customerCount;chosenClient++)
		{
//			int chosenClient = Utility.randomIntInclusive(problemInstance.customerCount-1);
			
			int noOfPossiblePatterns = problemInstance.allPossibleVisitCombinations.get(chosenClient).size();
			
			
			double min = individual.costWithPenalty;
			int chosenVisitPattern = individual.visitCombination[chosenClient]; 
			for(int i=0;i<noOfPossiblePatterns ;i++)
			{
				changeVisitPattern(individual, chosenClient, problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i),loadPenaltyFactor,routeTimePenaltyFactor,false);
				TotalCostCalculator.calculateCost(individual,loadPenaltyFactor,routeTimePenaltyFactor);
				if(individual.costWithPenalty<min)
				{
					chosenVisitPattern = problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i);
					min = individual.costWithPenalty;
				}
			}
			
			changeVisitPattern(individual, chosenClient, chosenVisitPattern,loadPenaltyFactor,routeTimePenaltyFactor,true);
		}
	}

	

	//change the visit pattern of individual to newVisitCombination 
	private static void changeVisitPattern(Individual individual, int clientNo, int newVisitCombination, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean improveResultantRoute)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		int previousCombination = individual.visitCombination[clientNo];
		int newCombination = newVisitCombination;
				
		individual.visitCombination[clientNo] = newCombination; 
		
		//remove the client from previous combinations
		int[] bitArrayPrev = problemInstance.toBinaryArray(previousCombination);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(individual.periodAssignment[period][clientNo]) 
			{
				removeClientFromPeriod(individual, period, clientNo,improveResultantRoute);
			}	
		}
		
		// add client to new combinations
		int[] newBitArray = problemInstance.toBinaryArray(newCombination);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(newBitArray[period]==1) 
			{
				addClientIntoPeriodGreedy(individual, period, clientNo,loadPenaltyFactor,routeTimePenaltyFactor,improveResultantRoute);
				individual.periodAssignment[period][clientNo] = true;
			}	
			else
			{
				individual.periodAssignment[period][clientNo] = false;
			}
		}
	}
	
	/**
	 * 
	 * @param individual
	 * @param period
	 * @param client
	 */
	public static void addClientIntoPeriodGreedy(Individual individual, int period, int client, double loadPenaltyFactor, double routeTimePenaltyFactor,boolean improveResultantRoute)
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
			
			
			//just checking the cost WITHOUT PENALTY 
			/*double minCostWithPenalty = min.increaseInCost ;
			double newCostWithPenalty = newInfo.increaseInCost ;
			*/
			
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
		individual.routes.get(period).get(min.vehicle).add(min.insertPosition, client);	
		
		//improve new route
		if(improveResultantRoute)
			Neigbour_Steps_Grouped.improveRoute(individual, period, min.vehicle);
	}
	
	/** Removes client from that periods route
	 * 
	 * @param period
	 * @param client
	 * @return number of the vehicle, of which route it was present.. <br/> -1 if it wasnt present in any route
	 */
	public static int removeClientFromPeriod(Individual individual, int period, int client,boolean improveResultantRoute)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			if(route.contains(client))
			{
				route.remove(new Integer(client));
				//improve route
				if(improveResultantRoute)Neigbour_Steps_Grouped.improveRoute(individual, period, vehicle);
				return vehicle;
			}
		}
		return -1;
	}
	
}
