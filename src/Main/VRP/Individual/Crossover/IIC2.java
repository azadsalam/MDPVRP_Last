package Main.VRP.Individual.Crossover;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;

import javax.print.attribute.IntegerSyntax;
import javax.swing.text.html.MinimalHTMLWriter;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;


public class IIC2 
{
	
	/**.
	 * The individuals must have been their fitness calculated. for now it explicitly computes both parent cost
	 * @param problemInstance
	 * @param parent1
	 * @param parent2
	 * @param child
	 */
	public static void crossOver(ProblemInstance problemInstance,Individual parent1,Individual parent2,Individual child)
	{
		UniformCrossoverPeriodAssigment.uniformCrossoverForPeriodAssignment(child,parent1, parent2,problemInstance);

//		reAlignParentRoutes(parent1, parent2, problemInstance);
		
		TotalCostCalculator.calculateCost(parent1, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		TotalCostCalculator.calculateCost(parent2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
		if(parent1.costWithPenalty < parent2.costWithPenalty)
			crossoverInner(problemInstance,parent1,parent2,child);
		else
			crossoverInner(problemInstance,parent2,parent1,child);
		
		
		//update cost and penalty
		child.calculateCostAndPenalty();
	}

	private static void crossoverInner(ProblemInstance problemInstance,Individual parent1,Individual parent2,Individual child)
	{
		int periodCount= problemInstance.periodCount;
		for(int period=0;period<periodCount;period++)
			crossover(problemInstance, parent1, parent2, child,period);
	}
	
	private static void crossover(ProblemInstance problemInstance,Individual parent1,Individual parent2,Individual child, int period)
	{
		boolean print = false;
		
		if(print) 
		{
			System.out.format("Cost: p1 : %f \np2: %f\n",parent1.costWithPenalty,parent2.costWithPenalty);
		}
					
		boolean clientMap[] = new boolean[problemInstance.customerCount];
		
		copyPartialFromParent(parent1, child, period, problemInstance.vehicleCount, clientMap, print);
	    
		insertMissingFromRest(parent2, child, period, problemInstance.vehicleCount, clientMap, print);
		insertMissingFromRest(parent1, child, period, problemInstance.vehicleCount, clientMap, print);
		
		int remaining=problemInstance.customerCount;
		for(int i=0;i<problemInstance.customerCount;i++)
		{
			if(clientMap[i]==true && child.periodAssignment[period][i]==false)
				System.out.println("ERRROR!!!!-MISTAKENLY ASSIGNED!!!\n\n");
			else if(clientMap[i]==false && child.periodAssignment[period][i]==true)
				System.out.println("ERRROR!!!!- MISSED ASSIGNING ROUTE\n\n");
			else if(clientMap[i]==false && child.periodAssignment[period][i]==false)
				remaining--;
			else if(clientMap[i]==true && child.periodAssignment[period][i]==true)
				remaining--;			
		}
		if(remaining != 0)
			System.out.println("ERROR - REmaining: "+remaining);
	}
	
	/**/
	private static void insertMissingFromRest(Individual parent, Individual child, int period,int vehicleCount, boolean clientMap[], boolean print)
	{
		if(print)
		{
			System.out.println("here");
		}
		for(int vehicle=0;vehicle<vehicleCount;vehicle++)	
		{
			ArrayList<Integer> parentRoute = parent.routes.get(period).get(vehicle);
			if(print)
			{
				
				System.out.println("vehicle " +vehicle);
				System.out.println("Parent route: "+parentRoute.toString());
			}

			int size = parentRoute.size();
			

			for(int i=0;i<size;i++)
			{
				
				int client = parentRoute.get(i);
				if(print)
				{
					System.out.println("client to be inserted: "+client);
				}
				if(child.periodAssignment[period][client] == false) continue;
				if(clientMap[client] == true) continue;
				
				//insert in best position
				MinimumCostInsertionInfo bestInfo= new MinimumCostInsertionInfo();
				bestInfo.increaseInCost=Double.POSITIVE_INFINITY;
				/*int selectedVehicle=-1;
				int selectedPosition=-1;
				*/
				for(int v=0;v<vehicleCount;v++)
				{
					ArrayList<Integer> childRoute = child.routes.get(period).get(v);			
					
					MinimumCostInsertionInfo minInfo =  RouteUtilities.minimumCostInsertionPosition(child.problemInstance, v, client, childRoute);
					
					if(minInfo.increaseInCost<bestInfo.increaseInCost)
					{
						bestInfo = minInfo;
					}
				}
				
				child.routes.get(period).get(bestInfo.vehicle).add(bestInfo.insertPosition, client);
				clientMap[client]=true;
			}
			
		}
	}
	
	/**
	 * appends partially from every routes of the specified period from the parent to child. 
	 * <br/> Doesnt copy if in that period client is not served
	 * <br/> Doesnt copy in the client is already assigned a route, flagged by a TRUE value in client map
	 * <br/> Updates clientMap array - assigns true for clients who are assigned routes. 
	 * @param vehicleList
	 * @param parent
	 * @param child
	 * @param period
	 * @param clientMap
	 * @param print
	 */
	private static void copyPartialFromParent(Individual parent, Individual child, int period,int vehicleCount, boolean clientMap[], boolean print)
	{
		for(int vehicle=0;vehicle<vehicleCount;vehicle++)
		{
			ArrayList<Integer> parentRoute = parent.routes.get(period).get(vehicle);
			
			int size = parentRoute.size();
			if(size==0) continue;
			
			int chunkSize = Utility.randomIntInclusive(size/2, size);
			int start = Utility.randomIntInclusive(0,size-chunkSize);
			
			
			ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);			
			
			int copied=0;
			while(copied<chunkSize)
			{
				int client = parentRoute.get(start+copied);
				copied++;
				
				if(child.periodAssignment[period][client] == false) continue;
				if(clientMap[client] == true) continue;
				
				
				childRoute.add(client);
				clientMap[client] = true;
				
			}
			
			if(print)
			{
				
				System.out.println("vehicle " +vehicle);
				System.out.format("copy start:  %d \n",start);
				System.out.format("chunk length:  %d \n",chunkSize);
				
//				System.out.println("Child Period Assignment: "+ Arrays.toString(child.periodAssignment[period]));
//				System.out.println("Client Map: "+ Arrays.toString(clientMap));
				System.out.println("Parent route: "+parentRoute.toString());
				System.out.println("Child route: "+childRoute.toString());
				System.out.println("\n\n\n\n");
			}
		}

	}
}
