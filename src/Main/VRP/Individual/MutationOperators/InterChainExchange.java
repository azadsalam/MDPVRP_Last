package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class InterChainExchange 
{
	static int fail=0;
	
	static Individual individual;
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
		InterChainExchange.individual = individual;
		if(individual.problemInstance.vehicleCount<2) return;
		do
		{
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			vehicle1 = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
			vehicle2 = vehicle1;
			
			while(vehicle1==vehicle2) vehicle2 = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
			
			success = chainExchange(individual,period,vehicle1,vehicle2);			
			retry++;
		}while(success==false  && retry<3);
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	private static boolean chainExchange(Individual individual,int period,int vehicle1, int vehicle2)
	{
		
		MinimumCostInsertionInfo newInfo;
			
		ArrayList<Integer> route1 = individual.routes.get(period).get(vehicle1);
		ArrayList<Integer> route2 = individual.routes.get(period).get(vehicle2);
		ProblemInstance pi = individual.problemInstance;
		
		int dc = pi.depotCount;
		int size1 = route1.size();
		int size2 = route2.size();
		
		for(int i=0;i<size1-1;i++)
		{
			for(int j=0;j<size2-1;j++)
			{
				int u1 = route1.get(i);
				int v1 = route1.get(i+1);
				
				int u2 = route2.get(j);
				int v2 = route2.get(j+1);
				
				double u1v1 = pi.costMatrix[dc+u1][dc+v1];
				double u2v2 = pi.costMatrix[dc+u2][dc+v2];
				
				double u1v2 = pi.costMatrix[dc+u1][dc+v2];
				double u2v1 = pi.costMatrix[dc+u2][dc+v1];
				
				if(u1v1+u2v2 > u1v2+u2v1)
				{
					
					//if(Solver.showViz)
					//	Solver.visualiser.drawIndividual(InterChainExchange.individual, "Before Chaninng");
					
					//connect u1 to v2 
					//connect u2 to v1
					ArrayList<Integer> route1Remainder = new ArrayList<>(route1.subList(i+1, size1)); // [i,j)
					ArrayList<Integer> route2Remainder = new ArrayList<>(route2.subList(j+1, size2)); // [i,j)
					
					route1.removeAll(route1Remainder);
					route2.removeAll(route2Remainder);
					
					route1.addAll(route2Remainder);
					route2.addAll(route1Remainder);
					
					Neigbour_Steps_Grouped.improveRoute(individual, period, vehicle1);// improve route 1
					Neigbour_Steps_Grouped.improveRoute(individual, period, vehicle2);// improve route 2
					
					if(Solver.showViz)
					{
						//Solver.visualiser.drawIndividual(InterChainExchange.individual, "After Chaninng");
					}


					return true;
				}
				
			}
		}
		
		
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		return false;
	}
	

}
