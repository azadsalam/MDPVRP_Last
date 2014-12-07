package Main.VRP.Individual;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.MutationOperators.Three_Opt;

public class RandomHeuristicInterleavedInitialisation 
{
	
	public static void initialise(Individual individual) 
	{
		//initialize period assignment

		
		ProblemInstance problemInstance = individual.problemInstance;
		
		/// PERIOD ASSIGNMENT ///
		int coin = Utility.randomIntExclusive(2);
		
		if(coin==0)
			InitialisePeriodAssigmentUniformly.initialise(individual);
		else
			InitialisePeriodAssigmentWithHeuristic.initialise(individual);
		//////////////////////////
		
		
			
		for(int period=0;period<problemInstance.periodCount;period++)
		{	
			//ArrayList<Integer> bigRoute = new ArrayList<Integer>();
			coin = Utility.randomIntExclusive(2);
			
			if(coin==0) //random route construction
			{	
				int vehicleCount = problemInstance.vehicleCount;
				int assigned=0;
				
				boolean[] clientMap = new boolean[problemInstance.customerCount];
		
				int vehicle = 0;
				
				while(assigned<problemInstance.customerCount)
				{
					int clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
					
					if(clientMap[clientNo]) continue;
					
					clientMap[clientNo]=true;
					assigned++;
					
					if(individual.periodAssignment[period][clientNo]==false)continue;
					
					individual.routes.get(period).get(vehicle).add(clientNo);	
					
					vehicle++;
					if(vehicle==vehicleCount)vehicle=0;
				}			
			}
			else //use heuristics
			{
				
				bigClosestDepot_withNoLoadViolation_greedy_cut(individual,period);
			}
			
			
			
		}	


		individual.calculateCostAndPenalty();
		
	}
	
	public static void bigClosestDepot_withNoLoadViolation_greedy_cut(Individual individual, int destPeriod)
	{
		ProblemInstance problemInstance = Individual.problemInstance;
		//Assign customer to route
		boolean[] clientMap = new boolean[problemInstance.customerCount];		
		
		//create empty lists
		individual.bigRoutes = new ArrayList<ArrayList<ArrayList<Integer>>>();		
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			individual.bigRoutes.add(new ArrayList<ArrayList<Integer>>());
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				individual.bigRoutes.get(period).add(new ArrayList<Integer>());
			}
		}
		
		//capacity for each depots
		double[] currentCapcityOfDepots = new double[problemInstance.depotCount];
		
		for(int depot=0;depot < problemInstance.depotCount;depot++)
		{
			ArrayList<Integer> vehicles = problemInstance.vehiclesUnderThisDepot.get(depot);
			double capacity=0;
			for(int i=0; i < vehicles.size() ;i++)
			{
				int vehicle = vehicles.get(i);
				capacity += problemInstance.loadCapacity[vehicle];
			}
			currentCapcityOfDepots[depot] = capacity;	
		}
		
		int assigned=0;
			
		//create big routes
		while(assigned<problemInstance.customerCount)
		{
			int clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
			if(clientMap[clientNo]) continue;
			clientMap[clientNo]=true;
			
			assigned++;
			
			
			ArrayList<Integer> closestDepots = RouteUtilities.closestDepots(clientNo);	
			double demand = problemInstance.demand[clientNo];
			//System.out.println("CLient : "+clientNo+" "+closestDepots.toString());
			
					
			if(individual.periodAssignment[destPeriod][clientNo]==false)continue;

			// add this to the closest depot if no load violation
			// else add to the next closest
			int depot;
			int i;
			for(i=0;i<closestDepots.size();i++)
			{
				depot = closestDepots.get(i);
				if(currentCapcityOfDepots[depot] >= demand)
				{
					currentCapcityOfDepots[depot] -= demand;
					individual.insertIntoBigClosestDepotRoute(clientNo, depot, destPeriod);
					break;
				}
			}
			
			if(i == problemInstance.depotCount)
			{
				individual.insertIntoBigClosestDepotRoute(clientNo, closestDepots.get(0), destPeriod);
			}
				
					
		}
		

		//now cut the routes and distribute to vehicles
		for(int depot=0; depot<problemInstance.depotCount;depot++)
		{
			Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut.greedyCutWithMinimumViolation(individual,destPeriod, depot);				
		}
	
	}



	
	
}
