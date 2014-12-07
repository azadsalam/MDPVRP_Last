package Main.VRP.LocalImprovement;

import java.util.ArrayList;

import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Mutation;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.Three_Opt;


public class FirstChoiceHillClimbingIMPROVE_ALL_ROUTE extends LocalSearch {

	MutationInterface mutaion;
	
	public FirstChoiceHillClimbingIMPROVE_ALL_ROUTE(MutationInterface mutation) {
		// TODO Auto-generated constructor stub
		
		this.mutaion = mutation;
		//System.err.println("FIRST CHOICE HILL CLIMB, RETRY = 7");
		
	}
	
	@Override
	public void improve(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		// TODO Auto-generated method stub

		improveAllRoute(individual, loadPenaltyFactor, routeTimePenaltyFactor);	
		improveRouteAssignment(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		improveAllRoute(individual, loadPenaltyFactor, routeTimePenaltyFactor);	
		
	}
	
	public void improveRouteAssignment(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		ProblemInstance problemInstance = individual.problemInstance;
		int customerCount = problemInstance.customerCount;
		
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int client=0;client<customerCount;client++)
			{
				
			}
		}
	}
	
	public void improveAllRoute(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		// TODO Auto-generated method stub

		ProblemInstance problemInstance = individual.problemInstance;
			
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				
				improveSpecificRoute(individual, loadPenaltyFactor, routeTimePenaltyFactor, period, vehicle);
				
			}
		}
		
	}

	
	public void improveSpecificRoute(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor,int period, int vehicle)
	{
		ArrayList<Integer> oldRoute  = new ArrayList<Integer>(individual.routes.get(period).get(vehicle));
		
		double oldCost = individual.calculateCost(period, vehicle);
		
		int retry=0;
		
		/*if(period==0 && vehicle==0)
		{
			System.out.println("INITIAL ROute: "+ oldRoute.toString()+" Cost: "+oldCost);
		}
*/				
		
		while(retry<7)
		{
			/*if(period==0 && vehicle==0)
			{
				System.out.println("Before mutate: Original Route: "+ individual.routes.get(period).get(vehicle).toString()+" Cost: "+individual.calculateCost(period, vehicle)+" Retry: "+retry);
			}*/
			
			mutaion.mutateSpecificRoute(individual, period, vehicle);
			double newCost = individual.calculateCost(period, vehicle);
			
			/*if(period==0 && vehicle==0)
			{
				System.out.println("After mutate: Modified ROute: "+ individual.routes.get(period).get(vehicle).toString()+" Cost: "+newCost+" Retry: "+retry);
			}*/
			
			if(newCost<oldCost)
			{
				retry=0;
				oldCost = newCost;
				oldRoute  = new ArrayList<Integer>(individual.routes.get(period).get(vehicle));
			}
			else
			{
				individual.routes.get(period).get(vehicle).clear();
				individual.routes.get(period).get(vehicle).addAll(oldRoute);
				retry++;
			}
			
			
		}
		
		/*if(period==0 && vehicle==0)
		{
			System.out.println("Final ROute: "+ individual.routes.get(period).get(vehicle).toString() +" Cost: "+individual.calculateCost(period, vehicle));
		}*/


		
	}

}
