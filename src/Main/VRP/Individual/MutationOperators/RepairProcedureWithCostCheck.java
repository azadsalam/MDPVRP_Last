package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.PopulationInitiator;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;


public class RepairProcedureWithCostCheck 
{
	
	 public static int apply = 0;
	 public static int success=0;
	
	/**
	 * Randomly selects a client,period 
	 * <br/>Inserts the client in the route, which cause minimum cost increase taking account of load violation
	 * @param individual
	 */
	/*ASSUMES THAT COST IS CALCULATED AND TOTAL*/
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
				
				if(individual.loadViolation[period][vehicle]>0)
				{
					//infeasible route
					repairRoute(individual, period, vehicle);
				}
			}
		}
			
				
		
		TotalCostCalculator.calculateCost(individual, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
		double newPenalty = individual.totalLoadViolation;

		//System.out.println("Penalty Old: "+oldPenalty+" current: "+newPenalty);
		
		if(individual.isFeasible)success++;
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	private static void repairRoute(Individual individual,int period,int vehicle)
	{
	    ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
	    double loadViolation = calculateLoadViolation(individual.problemInstance, vehicle, route);
	    //double demand;
	    int vehicleCount = individual.problemInstance.vehicleCount;
	    		
	    int retry = 0;
	    while(loadViolation>0 && retry<3)
	    {
	    	int size = route.size();
	    	int randomClientIndex = Utility.randomIntExclusive(size);
	    	
	    	int randomClient = route.get(randomClientIndex);
	    	double demand = individual.problemInstance.demand[randomClient];
	    	
	    	int bestVehicle=-1; 
	    	double bestViolation=Double.MAX_VALUE;
	    	//assign random client to some other route
	    	for(int i=0;i<vehicleCount;i++)
	    	{
	    		if(i==vehicle)continue;
	    		
	    		double currentViolation = calculateLoadViolation(individual.problemInstance, i, individual.routes.get(period).get(i));
	    		
	    		double thisModifiedViolation = currentViolation + demand ; 
	    		if(thisModifiedViolation<bestViolation)
	    		{
	    			bestViolation = thisModifiedViolation;
	    			bestVehicle = i;
	    		}
	    	}
	    	
	    	if(bestViolation<=0)
	    	{
	    		retry=0;
	    		
	    		route.remove(randomClientIndex);	    		
	    		MinimumCostInsertionInfo mi = RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, bestVehicle, randomClient, individual.routes.get(period).get(bestVehicle));
	    		individual.routes.get(period).get(bestVehicle).add(mi.insertPosition,randomClient);
	    		
	    		loadViolation = calculateLoadViolation(individual.problemInstance, vehicle, route);
	    	}
	    	else
	    	{
	    		retry++;
	    	
	    	}
	    	
	    	
	    }
	}
	
	
	private static double calculateLoadViolation(ProblemInstance problemInstance,int vehicle,ArrayList<Integer> route) 
	{
		double totalDemand = 0;
		for(int i=0;i<route.size();i++) totalDemand += problemInstance.demand[route.get(i)];
		return totalDemand - problemInstance.loadCapacity[vehicle];
	}

}
