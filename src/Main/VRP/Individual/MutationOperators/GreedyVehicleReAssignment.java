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

public class GreedyVehicleReAssignment 
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
		int period,client;
		boolean success=false;
		do
		{
			
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			client = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);
			
			if(individual.periodAssignment[period][client] == false) continue;
			
			success = mutateVehicleAssignmentGreedy(individual,period,client,loadPenaltyFactor,routeTimePenaltyFactor);			
			retry++;
			
		}while(success==false && retry<3);
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	
	 public static int apply = 0;
	 public static int success=0;
	
	/**
	 * Randomly selects a client,period 
	 * <br/>Inserts the client in the route, which cause minimum cost increase taking account of load violation
	 * @param individual
	 */

	public static void repair(Individual individual)
	{
		apply++;
		//System.out.print(Solver.loadPenaltyFactor+ " " );
		TotalCostCalculator.calculateCost(individual, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
		double oldPenalty = individual.totalLoadViolation;
		
		//System.out.println("IN REPAIR");
		int periodCount = individual.problemInstance.periodCount;
		int vehicleCount = individual.problemInstance.vehicleCount;
		
		for(int period = 0; period<periodCount; period++)
		{
			for(int vehicle = 0; vehicle<vehicleCount;vehicle++)
			{
				ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			    double loadViolation = calculateLoadViolation(individual.problemInstance, vehicle, route);
			    
				if(loadViolation>0)
				{
					//infeasible route
					//repair
				    
				    //double demand;
				    		
				    int retry = 0;
				    while(loadViolation>0 && retry<3)
				    {
				    	int size = route.size();
				    	int randomClientIndex = Utility.randomIntExclusive(size);
				    	
				    	int randomClient = route.get(randomClientIndex);

				    	boolean success = mutateVehicleAssignmentGreedy(individual, period, randomClient,Solver.loadPenaltyFactor,Solver.routeTimePenaltyFactor);
				    	
				    	if(success)
				    	{
				    		retry=0;
				    		loadViolation = calculateLoadViolation(individual.problemInstance, vehicle, route);
				    	}
				    	else
				    	{
				    		retry++;
				    	}
				    	
				    	
				    }

				}
			}
		}
			
				
		
		TotalCostCalculator.calculateCost(individual, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
		double newPenalty = individual.totalLoadViolation;

		//System.out.println("Penalty Old: "+oldPenalty+" current: "+newPenalty);
		
		if(individual.isFeasible)success++;
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	


	public static boolean mutateVehicleAssignmentGreedy(Individual individual,int period,int client, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		//initialising minInfo 
		MinimumCostInsertionInfo min = new  MinimumCostInsertionInfo();
		min.increaseInCost = Double.MAX_VALUE;
		min.loadViolationContribution = Double.MAX_VALUE;
		min.routeTimeViolationContribution = Double.MAX_VALUE;
		
		MinimumCostInsertionInfo newInfo;
		
		//remove the client from old route -assigned vehicle
		int assigendVehicle = RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);
		int position = individual.routes.get(period).get(assigendVehicle).indexOf(client);		
		individual.routes.get(period).get(assigendVehicle).remove(position);
		
		
		for(int vehicle = 0;vehicle<individual.problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			
			newInfo= RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
			
			double minCostContribution = min.increaseInCost + min.loadViolationContribution * loadPenaltyFactor + min.routeTimeViolationContribution * routeTimePenaltyFactor;
			double newCostContribution = newInfo.increaseInCost + newInfo.loadViolationContribution * loadPenaltyFactor +newInfo.routeTimeViolationContribution * routeTimePenaltyFactor;
			
			if(newCostContribution < minCostContribution)
			{
				min = newInfo;					
			}
			else if (newCostContribution == minCostContribution)
			{
				int coin = Utility.randomIntInclusive(1);
				if(coin==1)
					min=newInfo;
			}
			
		}
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		
		individual.routes.get(period).get(min.vehicle).add(min.insertPosition, client);
		
		if(min.vehicle==assigendVehicle && min.insertPosition==position) return false;
		else
		{

			Neigbour_Steps_Grouped.improveRoute(individual, period, assigendVehicle); //improve old route
			Neigbour_Steps_Grouped.improveRoute(individual, period, min.vehicle);     //improve new route 
			
			return true;
		}
	}

	private static double calculateLoadViolation(ProblemInstance problemInstance,int vehicle,ArrayList<Integer> route) 
	{
		double totalDemand = 0;
		for(int i=0;i<route.size();i++) totalDemand += problemInstance.demand[route.get(i)];
		return totalDemand - problemInstance.loadCapacity[vehicle];
	}


}

