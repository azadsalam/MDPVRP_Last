package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import Main.Utility;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class InterOneOneExchange 
{
	static int fail=0;
	
	//MDPVRP pr04 5693
	//MDPVRP pr06 7543.5733568281585

	
	/**
	 * Randomly selects 2 clients in a period 
	 * <br/> swap their routes with minimum cost increase heuristics
	 * <br/> 
	 * <br/>  
	 * @param individual
	 */
	public static void mutate(Individual individual)
	{
		int retry = 0;
		int period,vehicle1,vehicle2;
		boolean success=false;
		if(individual.problemInstance.vehicleCount<2) return;
		do
		{
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			vehicle1 = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
			vehicle2 = vehicle1;
			
			while(vehicle1==vehicle2) vehicle2 = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
			
			success = one_one_exchange_with_min_cost_incrs_heuristic(individual,period,vehicle1,vehicle2);			
			retry++;
		}while(success==false  && retry<3);
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	private static boolean one_one_exchange_with_min_cost_incrs_heuristic(Individual individual,int period,int vehicle1, int vehicle2)
	{
		
		MinimumCostInsertionInfo newInfo;
			
		ArrayList<Integer> route1 = individual.routes.get(period).get(vehicle1);
		ArrayList<Integer> route2 = individual.routes.get(period).get(vehicle2);
		int size1 = route1.size();
		int size2 = route2.size();
		if(size1==0 || size2==0) return false;
		int client1Index = Utility.randomIntInclusive(size1-1);
		int client2Index =  Utility.randomIntInclusive(size2-1);
		int client1 = route1.get(client1Index);
		int client2 = route2.get(client2Index);
		
		route1.remove(client1Index);
		route2.remove(client2Index);
		
		newInfo= RouteUtilities.minimumCostInsertionPosition(Individual.problemInstance, vehicle2, client1, route2);
		route2.add(newInfo.insertPosition, client1);
		
		newInfo= RouteUtilities.minimumCostInsertionPosition(Individual.problemInstance, vehicle1, client2, route1);
		route1.add(newInfo.insertPosition, client2);
		
		Neigbour_Steps_Grouped.improveRoute(individual, period, vehicle1);// improve route 1
		Neigbour_Steps_Grouped.improveRoute(individual, period, vehicle2);// improve route 2
		
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		return true;
	}
	

}
