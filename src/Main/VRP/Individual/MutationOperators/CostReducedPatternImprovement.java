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

public class CostReducedPatternImprovement {

	public static int apply = 0;	
	public static double totalSec=0;

	
	public static void patternImprovement(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean improveResultantRoute)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		int chosenClient = Utility.randomIntInclusive(problemInstance.customerCount-1);
		
		int previousPattern = individual.visitCombination[chosenClient];
		ArrayList<Integer> prevVehicles=new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> originalSavedRoutes = new ArrayList<ArrayList<Integer>>();
		
	
		double costb1=0;
		double costa1=0;
		
		//remove the client from all previous routes, then optimize those routes
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(individual.periodAssignment[period][chosenClient]==false)continue;
			
			int assigendVehicle = RouteUtilities.assignedVehicle(individual, chosenClient, period, problemInstance);
			
			ArrayList<Integer> oldRoute = individual.routes.get(period).get(assigendVehicle);
			
			//insert in last
			prevVehicles.add(assigendVehicle);
			originalSavedRoutes.add(new ArrayList<Integer>(oldRoute));
			
			
			costb1 += individual.calculateCostWithPenalty(period, assigendVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			individual.routes.get(period).get(assigendVehicle).remove(new Integer(chosenClient));
			
			Inter_Or_Opt.mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, assigendVehicle);
			
			costa1 += individual.calculateCostWithPenalty(period, assigendVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
		}
		
		//Individual bestNode = individual;
		
		int noOfPossiblePatterns = problemInstance.allPossibleVisitCombinations.get(chosenClient).size();
		
		for(int i=0;i<noOfPossiblePatterns ;i++)
		{
			int newVisitPattern = problemInstance.allPossibleVisitCombinations.get(chosenClient).get(i);
			
			//now ignore
			//if(newVisitPattern==previousPattern) continue;
			
			//try this new pattern - if improvement ok + change the period assignment chromosome
								 // - revert changes
						
			
			// add client to new combinations
			
			ArrayList<Integer> tmpvehicles=new ArrayList<Integer>();
			ArrayList<ArrayList<Integer>> tmproutes = new ArrayList<ArrayList<Integer>>();
			
			int[] newBitArray = problemInstance.toBinaryArray(newVisitPattern);

			double improvement=0;

			for(int period=0;period<problemInstance.periodCount;period++)
			{
				if(newBitArray[period]==1) 
				{
					Object[] bestInsertion = CostReducedVehicleReAssignment.addClientToThisPeriod(individual, period, chosenClient, loadPenaltyFactor, routeTimePenaltyFactor);
					improvement += (Double)bestInsertion[0];
					tmpvehicles.add((Integer)bestInsertion[1]);
					tmproutes.add((ArrayList<Integer>)bestInsertion[2]);
				}
				
			}
			
						
			
		}
		
	
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}
	
	
	

	//change the visit pattern of individual to newVisitCombination 
	private static void changeVisitPattern(Individual individual, int clientNo, int newVisitCombination, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		
	}
	

	
	
}
