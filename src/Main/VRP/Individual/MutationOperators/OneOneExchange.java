package Main.VRP.Individual.MutationOperators;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class OneOneExchange 
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
			
			boolean success = oneOneExchangeGreedyFI(individual, period, vehicle, client,loadPenaltyFactor,routeTimePenaltyFactor);
			
			if(success) return true;
			
			map[vehicle]=true;
			checked++;
		}
		return false;
		
	}
	
	//tries inserting the client into every possible position in the route selected by <period,vehicle>
	//if the client is already present in the <period,vehicle> route, then the move is an intra route insertion
	//else its inter route
	public static boolean oneOneExchangeGreedyFI(Individual individual,int period, int vehicle, int client, double loadPenaltyFactor, double routeTimePenaltyFactor) 
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
			
			for(int i=0;i<route.size();i++)
			{
				if(i==position) continue;
				
				int otherClient = route.get(i);
				
				route.set(i, client);
				route.set(position, otherClient);
				

				newCost = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
				
				if(newCost<oldCost) return true; //found improvement -> return
				
				route.set(i, otherClient);
				route.set(position, client);
				
				//revert changes
			}
			
		}
		else //inter
		{
			double newCost1,newCost2;
			double oldCost1 = individual.calculateCostWithPenalty(period, assignedVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			double oldCost2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);


			ArrayList<Integer> route1 = individual.routes.get(period).get(assignedVehicle);
			ArrayList<Integer> route2 = individual.routes.get(period).get(vehicle);
			

			int position = route1.indexOf(client);
			

			for(int i=0;i<route2.size();i++)
			{
				int otherClient = route2.get(i);

				route2.set(i, client);
				route1.set(position, otherClient);
				
				
				newCost1 = individual.calculateCostWithPenalty(period, assignedVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
				newCost2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
				
				if(oldCost1+oldCost2 > newCost1+newCost2)
					return true;

				
				route2.set(i, otherClient);
				route1.set(position, client);
				
			}
			
			
		}
		return false;
	}
	
}
