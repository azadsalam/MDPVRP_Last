package Main.VRP.Individual;

import java.util.ArrayList;
import java.util.Arrays;

import Main.Utility;
import Main.VRP.ProblemInstance;

/*
  -->> assigns the closest depot if no violation, if violation the next depot ...
  	if every depot is full, assign the client to closest one
  -->> assigns clients to vehicles until no violation
  
  */
public class Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut {

	
	
	public static void initiialise(Individual individual) 
	{
		// TODO Auto-generated method stub
		//System.out.println("HERE");
		//InitialisePeriodAssigmentUniformly.initialise(individual);
		InitialisePeriodAssigmentWithHeuristic.initialise(individual);
		bigClosestDepot_withNoLoadViolation_uniform_cut(individual);
		individual.calculateCostAndPenalty();
	}


	public static void bigClosestDepot_withNoLoadViolation_uniform_cut(Individual individual)
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
		double[][] currentCapcityOfDepots = new double[problemInstance.periodCount][problemInstance.depotCount];
		
		for(int depot=0;depot < problemInstance.depotCount;depot++)
		{
			ArrayList<Integer> vehicles = problemInstance.vehiclesUnderThisDepot.get(depot);
			double capacity=0;
			for(int i=0; i < vehicles.size() ;i++)
			{
				int vehicle = vehicles.get(i);
				capacity += problemInstance.loadCapacity[vehicle];
			}
			for(int period=0;period<problemInstance.periodCount;period++)
			{
					currentCapcityOfDepots[period][depot] = capacity;	
//				System.out.print(currentCapcityOfDepots[period][depot] +" ");
			}	
//			System.out.println();
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
			
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				if(individual.periodAssignment[period][clientNo]==false)continue;

				// add this to the closest depot if no load violation
				// else add to the next closest
				int depot;
				int i;
				for(i=0;i<closestDepots.size();i++)
				{
					depot = closestDepots.get(i);
					if(currentCapcityOfDepots[period][depot] >= demand)
					{
						currentCapcityOfDepots[period][depot] -= demand;
						individual.insertIntoBigClosestDepotRoute(clientNo, depot, period);
						break;
					}
				}
				
				if(i == problemInstance.depotCount)
				{
					individual.insertIntoBigClosestDepotRoute(clientNo, closestDepots.get(0), period);
				}
				
			}			
		}
		

		//now cut the routes and distribute to vehicles
		for(int period=0; period<problemInstance.periodCount;period++)
		{
			for(int depot=0; depot<problemInstance.depotCount;depot++)
			{
				Initialise_ClosestDepot_UniformCut.uniformCut(individual,period, depot);				
			}
		}
	}

		

}
