package Main.VRP.Individual;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.MutationOperators.GENI;

/*closest depot, greedy cut*/
public class Initialise_ClosestDepot_GENI_GreedyCut 
{
	
	
	public static void initialise(Individual individual) 
	{
		// TODO Auto-generated method stub		
	//	InitialisePeriodAssigmentUniformly.initialise(individual);
		InitialisePeriodAssigmentWithHeuristic.initialise(individual);
		ClosestDepot_GENI_RouteWithGreedyCut(individual);
		individual.calculateCostAndPenalty();
	}

	private static void ClosestDepot_GENI_RouteWithGreedyCut(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		//Assign customer to route
		
		
		//allocate array lists big routes
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
		GENI.initialiseBigRouteWithClosestClients(individual,GENI.CLOSEST_DEPOT);
		
		//now cut the routes and distribute to vehicles
		for(int period=0; period<problemInstance.periodCount;period++)
		{
			for(int depot=0; depot<problemInstance.depotCount;depot++)
			{

				//greedyCutWithMinimumViolation(individual,period, depot);
				Initialise_ClosestDepot_UniformCut.uniformCut(individual,period, depot);				
				/*int vehicle = problemInstance.vehiclesUnderThisDepot.get(depot).get(0);
				ArrayList<Integer >route = routes.get(period).get(vehicle);
				route.clear();
				route.addAll(bigRoutes.get(period).get(depot));*/
				
			}
		}
	}


}

