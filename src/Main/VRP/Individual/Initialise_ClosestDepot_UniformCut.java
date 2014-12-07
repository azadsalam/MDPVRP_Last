package Main.VRP.Individual;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;

/*
 assign to closest depot,
 cut big route uniformly
 */
public class Initialise_ClosestDepot_UniformCut {

	public static void initiialise(Individual individual) 
	{
		// TODO Auto-generated method stub
		InitialisePeriodAssigmentUniformly.initialise(individual);
		bigClosestDepotRouteWithUniformCut(individual);
		individual.calculateCostAndPenalty();
	}

	private static void bigClosestDepotRouteWithUniformCut(Individual individual)
	{
		ProblemInstance problemInstance = Individual.problemInstance;
		//Assign customer to route
		boolean[] clientMap = new boolean[problemInstance.customerCount];
		
		int assigned=0;
		individual.bigRoutes = new ArrayList<ArrayList<ArrayList<Integer>>>();
		
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			individual.bigRoutes.add(new ArrayList<ArrayList<Integer>>());
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				individual.bigRoutes.get(period).add(new ArrayList<Integer>());
			}
		}
		
		//create big routes
		while(assigned<problemInstance.customerCount)
		{
			int clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
			if(clientMap[clientNo]) continue;
			clientMap[clientNo]=true;
			assigned++;
			
			
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				if(individual.periodAssignment[period][clientNo]==false)continue;

				int depot = RouteUtilities.closestDepot(clientNo);	
				individual.insertIntoBigClosestDepotRoute(clientNo, depot, period);
			}			
		}
		
		/*Re insert all clients */
/*		for(int period=0; period<problemInstance.periodCount;period++)
		{
			for(int depot=0; depot<problemInstance.depotCount;depot++)
			{
				ArrayList<Integer> bigRoute = individual.bigRoutes.get(period).get(depot);		

				//make a copy
				ArrayList<Integer> copy = new ArrayList<Integer> ();
				for(int i=0;i<bigRoute.size();i++)copy.add(bigRoute.get(i));
				
				//remove and add again every client serially  
				for(int i=0; i<copy.size();i++)
				{
					int clientNo = copy.get(i);
					
					int index = bigRoute.indexOf(clientNo); //remove
					bigRoute.remove(index);
					
					//insert again
					MinimumCostInsertionInfo minPos = RouteUtilities.minimumCostInsertionPosition2(problemInstance, depot, clientNo, bigRoute);
					bigRoute.add(minPos.insertPosition, clientNo);
				}
				
			}
		}
	*/
		//now cut the routes and distribute to vehicles
		for(int period=0; period<problemInstance.periodCount;period++)
		{
			for(int depot=0; depot<problemInstance.depotCount;depot++)
			{

				uniformCut(individual,period, depot);
				/*int vehicle = problemInstance.vehiclesUnderThisDepot.get(depot).get(0);
				ArrayList<Integer >route = routes.get(period).get(vehicle);
				route.clear();
				route.addAll(bigRoutes.get(period).get(depot));*/
				
			}
		}
	}
	
	public static void uniformCut(Individual individual,int period,int depot) 
	{
		ArrayList<Integer> bigRoute = individual.bigRoutes.get(period).get(depot);		
		ArrayList<Integer> vehicles = individual.problemInstance.vehiclesUnderThisDepot.get(depot);
		
		int currentVehicleIndex = 0;
		int bigRouteSize = bigRoute.size();
		int clientPerVehicle = bigRouteSize/vehicles.size();
		
		for(currentVehicleIndex=0;currentVehicleIndex<vehicles.size();currentVehicleIndex++)
		{
			for(int i=0;i<clientPerVehicle;i++)
			{
				int vehicle = vehicles.get(currentVehicleIndex);
				int client = bigRoute.get(0);
				individual.routes.get(period).get(vehicle).add(client);
				bigRoute.remove(0);
			}
		}
		
		if(!bigRoute.isEmpty())
		{
			
			while(!bigRoute.isEmpty())
			{
				int client = bigRoute.get(0);
				int vehicle = vehicles.get(vehicles.size()-1);
				
				individual.routes.get(period).get(vehicle).add(client);
				bigRoute.remove(0);
			}
		}

		

	}

		

}
