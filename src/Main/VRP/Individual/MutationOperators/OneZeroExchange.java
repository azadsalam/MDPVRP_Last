package Main.VRP.Individual.MutationOperators;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class OneZeroExchange 
{
	public static int apply = 0;	
	public static double totalSec=0;

	
	public static boolean mutateClientFI(Individual individual, int period, int client, double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		apply++;
	//	System.out.println("1-0 apply"+apply);
		// TODO Auto-generated method stub
		ProblemInstance problemInstance = individual.problemInstance;
		int vehicleCount = problemInstance.vehicleCount;
				
		boolean map[] = new boolean[vehicleCount];
		int checked=0;
		while(checked<vehicleCount)
		{
			int vehicle = Utility.randomIntExclusive(vehicleCount);
			if(map[vehicle])continue;
			
			boolean success = oneZeroExchangeGreedyFI(individual, period, vehicle, client,loadPenaltyFactor,routeTimePenaltyFactor);
			
			if(success) return true;
			
			map[vehicle]=true;
			checked++;
		}
		return false;
		
	}
	
	//tries inserting the client into every possible position in the route selected by <period,vehicle>
	//if the client is already present in the <period,vehicle> route, then the move is an intra route insertion
	//else its inter route
	public static boolean oneZeroExchangeGreedyFI(Individual individual,int period, int vehicle, int client, double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		ProblemInstance problemInstance = individual.problemInstance;
		int assignedVehicle = RouteUtilities.assignedVehicle(individual, client, period, problemInstance);		
		
		//intra
		if(vehicle == assignedVehicle)
		{
			double oldCost = individual.calculateCostWithPenalty(period, assignedVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			double newCost;
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			int position = route.indexOf(client);
			route.remove(position);
			
			for(int i=0;i<=route.size();i++)
			{
				if(i==position) continue;
				route.add(i,client);
				newCost = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
				
				if(newCost<oldCost) return true; //found improvement -> return
				
				//revert route
				route.remove(i);
			}
			route.add(position,client);
			
		}
		else //inter
		{
			double newCost1,newCost2;
			double oldCost1 = individual.calculateCostWithPenalty(period, assignedVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			double oldCost2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);


			ArrayList<Integer> route1 = individual.routes.get(period).get(assignedVehicle);
			ArrayList<Integer> route2 = individual.routes.get(period).get(vehicle);
			

			int position = route1.indexOf(client);
			route1.remove(position);
			newCost1 = individual.calculateCostWithPenalty(period, assignedVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			

			for(int i=0;i<route2.size()+1;i++)
			{
				route2.add(i,client);
				newCost2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
				
				if(oldCost1+oldCost2 > newCost1+newCost2)
					return true;

				route2.remove(i);
			}
			
			
			route1.add(position,client);
		}
		return false;
	}
	
}
