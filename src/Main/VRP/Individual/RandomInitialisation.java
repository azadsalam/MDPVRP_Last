package Main.VRP.Individual;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.MutationOperators.Three_Opt;

public class RandomInitialisation 
{
	
	public static void initialiseRandom(Individual individual) 
	{
		// NOW INITIALISE WITH VALUES
		//initialize period assignment

		int freq,allocated,random;
		//Randomly allocate period to clients equal to their frequencies

		ProblemInstance problemInstance = individual.problemInstance;
		InitialisePeriodAssigmentUniformly.initialise(individual);
		//Assign customer to route
		for(int clientNo=0;clientNo<problemInstance.customerCount;clientNo++)
		{
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				if(individual.periodAssignment[period][clientNo]==false)continue;

				int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);				
				individual.routes.get(period).get(vehicle).add(clientNo);
			}
		}


		//randomize the pattern for each route
		//adjacent swap
		int coin;
		int ran;

		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
//			ran = Utility.randomIntInclusive(3);

			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				ArrayList<Integer> route = individual.routes.get(period).get(vehicle);

				for( int i = route.size()-1;i>=1;i--)
			    {
					int j = Utility.randomIntInclusive(0, i);
					int tmp = route.get(j);
					route.set(j, route.get(i));
					route.set(i, tmp);
			    }
				
			}
		}

		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				//ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
				Three_Opt.mutateRouteBy_Three_Opt_with_first_better_move(individual, period, vehicle);
			}
		}
		
		individual.calculateCostAndPenalty();
		
	}


}
