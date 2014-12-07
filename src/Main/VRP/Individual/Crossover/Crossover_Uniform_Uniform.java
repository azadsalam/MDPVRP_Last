package Main.VRP.Individual.Crossover;
import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;


public class Crossover_Uniform_Uniform 
{
	private static  void uniformCrossoverForRoutes(Individual child1,Individual child2, Individual parent1, Individual parent2,ProblemInstance problemInstance)
	{
		int coin;
		
		Individual temp1,temp2;
		for(int period = 0; period<problemInstance.periodCount; period++)
		{
			coin = Utility.randomIntInclusive(1);
			
			if(coin==0)
			{
				temp1=child1;
				temp2=child2;
			}
			else
			{
				temp1=child2;
				temp2=child1;
			}	
			
			
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				
				ArrayList<Integer> parent1Route = parent1.routes.get(period).get(vehicle);
				ArrayList<Integer> parent2Route = parent2.routes.get(period).get(vehicle);
				ArrayList<Integer> child1Route = temp1.routes.get(period).get(vehicle);
				ArrayList<Integer> child2Route = temp2.routes.get(period).get(vehicle);
				
				child1Route.clear();
				child2Route.clear();
				
				//copy temp1 <- parent1				
				for(int clientIndex=0;clientIndex<parent1Route.size();clientIndex++)
				{
					int node = parent1Route.get(clientIndex);
					if(temp1.periodAssignment[period][node])
						child1Route.add(node);
				}
				
				//copy temp2 <- parent2				
				for(int clientIndex=0;clientIndex<parent2Route.size();clientIndex++)
				{
					int node = parent2Route.get(clientIndex);
					if(temp2.periodAssignment[period][node])
						child2Route.add(node);
				}
			}
			
			
			for(int client=0;client<problemInstance.customerCount;client++)
			{
				//repair offspring route 1
				if(temp1.periodAssignment[period][client]==true)
				{
					if(RouteUtilities.doesRouteContainThisClient(problemInstance, temp1, period, client)==false)
					{
						//int vehicle = temp1.mostProbableRoute(client);
						int vehicle = 0; //test
						
						temp1.routes.get(period).get(vehicle).add(client);
					}
				}
				//repair offspring route 2

				if(temp2.periodAssignment[period][client]==true)
				{
					if(RouteUtilities.doesRouteContainThisClient(problemInstance, temp2, period, client)==false)
					{
						temp2.routes.get(period).get(0).add(client);
					}
				}
			}
			
		}
		
	}
		
	public static void crossOver_Uniform_Uniform(ProblemInstance problemInstance,Individual parent1,Individual parent2,Individual child1,Individual child2)
	{
		//with 50% probability swap parents
		int ran = Utility.randomIntInclusive(1);
		if(ran ==1)
		{
			Individual temp = parent1;
			parent1 = parent2;
			parent2 = temp;
		}
		
		UniformCrossoverPeriodAssigment.uniformCrossoverForPeriodAssignment(child1,child2,parent1, parent2,problemInstance);
		uniformCrossoverForRoutes(child1, child2, parent1, parent2, problemInstance);
		
		
		//update cost and penalty
		child1.calculateCostAndPenalty();
		child2.calculateCostAndPenalty();
	}

}
