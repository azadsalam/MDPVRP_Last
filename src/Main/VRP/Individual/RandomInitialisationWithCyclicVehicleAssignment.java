package Main.VRP.Individual;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.MutationOperators.Three_Opt;

public class RandomInitialisationWithCyclicVehicleAssignment 
{
	
	public static void initialiseRandom(Individual individual) 
	{
		// NOW INITIALISE WITH VALUES
		//initialize period assignment

		int freq,allocated,random;
		//Randomly allocate period to clients equal to their frequencies

		ProblemInstance problemInstance = individual.problemInstance;
		InitialisePeriodAssigmentUniformly.initialise(individual);
		
		int vehicleCount = problemInstance.vehicleCount;
		for(int period=0;period<problemInstance.periodCount;period++)
		{	
			//ArrayList<Integer> bigRoute = new ArrayList<Integer>();
			
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

		/*for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				//ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
				Three_Opt.mutateRouteBy_Three_Opt_with_best_move(individual, period, vehicle);
			}
		}*/

		individual.calculateCostAndPenalty();
		
	}


}
