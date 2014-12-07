package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class GreedyVehicleReAssignmentQuickSelect 
{
	static int fail=0;
	
	/**
	 * Randomly selects a client,period 
	 * <br/>Inserts the client in the route, which cause minimum cost increase taking account of load+route time violation
	 * @param individual
	 */
	public static void mutate(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		int retry = 0;
		int period,client,vehicle;
		boolean success=false;
		do
		{			
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			vehicle = Utility.randomIntExclusive(individual.problemInstance.vehicleCount);
			
			client = mostCostlyNeighbourhoodClient(individual, period, vehicle);
			
			if(client == -1) continue;
			if(individual.periodAssignment[period][client] == false) continue;
			
			success = GreedyVehicleReAssignment.mutateVehicleAssignmentGreedy(individual,period,client,loadPenaltyFactor,routeTimePenaltyFactor);			
			retry++;
			
		}while(success==false && retry<3);
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	
	static private int mostCostlyNeighbourhoodClient(Individual individual, int period, int vehicle)
	{
		
		
		ArrayList<Integer> route  = individual.routes.get(period).get(vehicle);
		
		if(route.size()==0) return -1;
		
		int selectedClientPosition=-1;
		
		double max = -1;
		
		for(int i=0;i<route.size();i++)
		{
			double distance = RouteUtilities.neighbourDistanceForThisPosition(individual, period, vehicle, i);
			
			if(distance >= max)
			{
				max = distance;
				selectedClientPosition = i;
			}
			
		}
		
		return route.get(selectedClientPosition);
		
	}
	
	
	
	 public static int apply = 0;
	 public static int success=0;


}

