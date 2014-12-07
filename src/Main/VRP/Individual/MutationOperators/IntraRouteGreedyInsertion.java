package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

/**
 * Single route 
 * <br/>
 * Randomly selects a route,
 * then randomly selects a client within the route, inserts it in a new position.
 * <br> tries to insert it in every possible adjacent nodes
 * <br> if new improved route found, return
 * <br/> else return
 * @author Azad
 *
 */
public class IntraRouteGreedyInsertion 
{
	static int fail=0;
	
	public static void mutate(Individual individual)
	{
		int retry = 0;
		int period,vehicle;
		boolean success;
		do
		{
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			vehicle = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
			success = mutateRouteWithInsertion(individual,period,vehicle);
			retry++;
		}while(success==false && retry<3);
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	private static boolean mutateRouteWithInsertion(Individual individual,int period,int vehicle)
	{
		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
		
		if(route.size() ==0 ) return false;
		
		int previousIndex = Utility.randomIntInclusive(route.size()-1);
		int selectedClient = route.get(previousIndex);
		route.remove(previousIndex);
		
		
		int newIndex= RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, selectedClient, route).insertPosition;
		
		if(previousIndex==newIndex)
		{
			route.add(previousIndex, selectedClient);
			return false;
		}
		
		route.add(newIndex, selectedClient);
		
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		return true;
	}
	

}
