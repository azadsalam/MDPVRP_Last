package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import Main.Utility;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class OneZeroExchangePrev 
{
	static int fail=0;
	//MDPVRP pr04 5530.768632709098
	//MDPVRP pr06 7270.992149
	/**
	 * Randomly selects a client,period 
	 * <br/> Inserts the client  in another random route with minimum cost increase heuristics
	 * <br/> Improvements can be done in selecting the other route..
	 * <br/> Like - we can select the order the routes according to avg distance from this client and assign this client to the  next/prev route 
	 * @param individual
	 */
	public static void interRouteOneZeroExchange(Individual individual,boolean insertWithMinimumCostIncreaseHeuristic, boolean improveResultantRoute)
	{
		//vehicle count 2 er kom hole ei operator call korar kono mane e nai
		if(individual.problemInstance.vehicleCount<2) return;

		int period,client;
		do
		{
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			client = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);		
		}while(individual.periodAssignment[period][client] == false);
		
		oneZeroExchange(individual,period,client, insertWithMinimumCostIncreaseHeuristic,improveResultantRoute,false);
	}

	public static void oneZeroExchangeIntra_and_Inter_both(Individual individual,boolean insertWithMinimumCostIncreaseHeuristic, boolean improveResultantRoute)
	{
		//vehicle count 2 er kom hole ei operator call korar kono mane e nai
		if(individual.problemInstance.vehicleCount<2) return;

		int period,client;
		do
		{
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			client = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);		
		}while(individual.periodAssignment[period][client] == false);
		
		oneZeroExchange(individual,period,client, insertWithMinimumCostIncreaseHeuristic,improveResultantRoute,true);
	}
	

	/**
	 * 
	 * @param individual
	 * @param period
	 * @param client
	 * @param insertWithMinimumCostIncreaseHeuristic
	 * @param improveResultantRoute
	 * @param allowIntra true hole, nijer route or onno jekono route a  swap korbe.. can be inter/ intra
	 */
	private static void oneZeroExchange(Individual individual,int period,int client,boolean insertWithMinimumCostIncreaseHeuristic,boolean improveResultantRoute,boolean allowIntra)
	{
		
		int assigendVehicle = RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);
		int position = individual.routes.get(period).get(assigendVehicle).indexOf(client);
		
		//remove the client from old route - with vehicle == assignedVehicle
		individual.routes.get(period).get(assigendVehicle).remove(position);		
	
		int vehicle= Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);	
		
		if(allowIntra == false)
		{
			while(vehicle==assigendVehicle)
			{
				vehicle = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
			}
		}

		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);

		if(insertWithMinimumCostIncreaseHeuristic)
		{
			MinimumCostInsertionInfo newInfo= RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
			route.add(newInfo.insertPosition, client);
		}
		else
		{
			int size = route.size();
			int randPos = Utility.randomIntInclusive(size);
			route.add(randPos,client);
		}
		//Mutation_Grouped.mutateRouteAssignment(individual, loadPenaltyFactor, routeTimePenaltyFactor)
		
		if(improveResultantRoute)
		{
			Neigbour_Steps_Grouped.improveRoute(individual, period, assigendVehicle); //improveOldRoute
			Neigbour_Steps_Grouped.improveRoute(individual, period, vehicle); //improve new route
		}
		
	}
	

}
