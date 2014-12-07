package Main.VRP.Individual.Crossover;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;


public class Uniform_VariedEdgeRecombnation_GreedyCut 
{
	
	// option 1 => normal edge recombination variant, appends the node which has smallest neighbor set
	// option 2 => greedy edge recombination variant, appends the node which has least cost with the previous node
	private static final int NORMAL_VARIANT = 1;
	private static final int GREEDY_VARIANT = 2;
	
	
	
	static ProblemInstance problemInstance;
	
	public static void crossOver_Uniform_VariedEdgeRecombination(ProblemInstance pi,Individual parent1,Individual parent2,Individual child)
	{

		//crossover for period assignment
		initialise(pi, parent1, parent2, child);		
		
		variedEdgeRecombinationCrossoverForRoutes(child, parent1, parent2, GREEDY_VARIANT);
		
		/*if(child.validationTest())
			pi.out.println("CHILD IS NOT VALID");
		*/
		
		//update cost and penalty
		child.calculateCostAndPenalty();
		if(Solver.gatherCrossoverStat) CrossoverStatistics.gatherData(parent1, parent2, child);

		/*pi.out.println("Child: ");
		child.print();
		*/
		
	}
	
	
	private static void initialise(ProblemInstance pi,Individual parent1,Individual parent2,Individual child) 
	{
		problemInstance = pi;
		
		
		//with 50% probability swap parents
		int ran = Utility.randomIntInclusive(1);
		if(ran ==1)
		{
			Individual temp = parent1;
			parent1 = parent2;
			parent2 = temp;
		}
		
		UniformCrossoverPeriodAssigment.uniformCrossoverForPeriodAssignment(child,parent1, parent2,problemInstance);
		
	}

	
	private static void variedEdgeRecombinationCrossoverForRoutes(Individual child, Individual parent1, Individual parent2,int option)
	{

		
		int coin;

		// Step 1. Create Neighbour/Adjacency List

		//depot nodes are labeled with clientCount, clientsCount+1,..., clientCount+depotCount-1
		ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>();
		
		for(int client =0;client<problemInstance.customerCount+problemInstance.depotCount;client++)
		{
			adjacencyList.add(new ArrayList<Integer>());
		}

		createAdjacencyList(adjacencyList,parent1,parent2);
		
/*		boolean print=true;	
		if(print)
		{		

			problemInstance.out.println("IN CROSSOVER, PARENTS: ");
			
			parent1.print();
			parent2.print();
			
			problemInstance.out.println("Neighbour List : ");
			for(int node=0;node<problemInstance.customerCount+problemInstance.depotCount;node++)
			{	
				if(node<problemInstance.customerCount)
					problemInstance.out.print("Neigbours of client "+node+" : ");
				else
					problemInstance.out.print("Neigbours of depot "+node+" : ");
				
				for(int i=0;i< adjacencyList.get(node).size();i++)
					problemInstance.out.print(adjacencyList.get(node).get(i)+" ");
				problemInstance.out.println("");
				
			}
			
			problemInstance.out.flush();
		}
		*/
		
		
		for(int period=0;period<problemInstance.periodCount;period++)
		{			 
			// Step 2. Assign depot to vehicle         
			// Step 3. Create Route For Each Depot
			// Step 4. Cut the routes
						
			//assignedDepot[c] => d means, client c is under depot d .... if no assigned vehicle d=-1
			int[] assignedDepot = new int[problemInstance.customerCount];
			for(int i=0;i<problemInstance.customerCount;i++) assignedDepot[i] = -1;

			//numberOfCustomerServed[d] -> n means, depot d serves n clients
			int[] numberOfCustomerServed = new int[problemInstance.vehicleCount];
								
			
			assignClientToDepot(child,parent1,parent2,assignedDepot, numberOfCustomerServed,period);
			
			//System.out.println("here");
			/*if(print)
			{
				problemInstance.out.println("Depot assignment : period : "+period);
				for(int client=0;client<problemInstance.customerCount;client++)
					problemInstance.out.print("[ "+client+" -> "+assignedDepot[client]+" ] ");
				problemInstance.out.println("");
						
			}
			*/
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{

				ArrayList<Integer> bigRoute = createRoute(parent1, parent2, child, adjacencyList,numberOfCustomerServed,depot, period, assignedDepot, option);
				//problemInstance.out.println("Deopt: "+ depot +" Big Route: "+bigRoute);
				//cut the routes
				RouteUtilities.greedyCutWithMinimumViolation(child, period, depot, bigRoute);

				/*
				ArrayList<Integer> vehicles = problemInstance.vehiclesUnderThisDepot.get(depot);

				for(int v=0;v < vehicles.size(); v++ )
				{
					problemInstance.out.println("vehicle :"+vehicles.get(v)+" route: "+ child.routes.get(period).get(vehicles.get(v)));
				}*/
			}
			
			

		}
	
	}
	
	


	private static void assignClientToDepot(Individual child, Individual parent1, Individual parent2, int[] assignedDepot, int[] numberOfCustomerServed,int period) 
	{
		for(int client=0; client<problemInstance.customerCount;client++)
		{
			if(child.periodAssignment[period][client]==false) continue;
			
			int depot1 = RouteUtilities.assignedDepot(parent1, client, period, problemInstance);
			int depot2 = RouteUtilities.assignedDepot(parent2, client, period, problemInstance);

			int chosenDepot=500000;
			
			if(depot1==-1 && depot2==-1) System.out.println("ERROR!!!! NEVER SHOULD HAPPEN!!!!!!!!!! in varied edge recombination op....");
	
			else if(depot1==-1)
				chosenDepot = depot2;
			else if(depot2==-1)
				chosenDepot = depot1; 
			else 
			{
				// HERE SOME IMPROVEMENT CAN BE DONE 
				int coin = Utility.randomIntInclusive(1);
				if(coin==0) chosenDepot = depot1;
				else chosenDepot = depot2;
			}
			
			assignedDepot[client]=chosenDepot;
			numberOfCustomerServed[chosenDepot]++;
		}
	}

	private static ArrayList<Integer> createRoute(Individual parent1,Individual parent2,Individual child, 
			ArrayList<ArrayList<Integer>> adjacencyList, int[] numberOfCustomerServed,
		    int depot, int period, int[] assignedDepot, int option ) 
	{
				
		ArrayList<ArrayList<Integer>> tempAdjacencyList = new ArrayList<ArrayList<Integer>>();
		
		for(int node=0;node< problemInstance.customerCount+problemInstance.depotCount;node++)
			tempAdjacencyList.add(adjacencyList.get(node));
		
		int clientsUnderThisDepot  = numberOfCustomerServed[depot];
			
		int currentNode = problemInstance.customerCount+depot;
		ArrayList<Integer> route = new ArrayList<Integer>();
		
		while(route.size()<clientsUnderThisDepot )
		{
			if(currentNode < problemInstance.customerCount && child.periodAssignment[period][currentNode] && depot == assignedDepot[currentNode]) 
				route.add(currentNode);
			
			if(route.size()==clientsUnderThisDepot) break; 
			
			//now delete the node from adjacency list
			for(int node=0;node < problemInstance.customerCount+problemInstance.depotCount ;node++) 
				tempAdjacencyList.get(node).remove(new Integer(currentNode));
			
			int nextNode=-1;
			
			ArrayList<Integer> neigbourList = tempAdjacencyList.get(currentNode);
			
			if(neigbourList.size() != 0)
			{
				//highest 4 neighbours
				int min= Integer.MAX_VALUE;
				double minCost = Double.POSITIVE_INFINITY;
				for(int j = 0;j<neigbourList.size();j++)
				{
					int neighbour = neigbourList.get(j);
											
					if(option == NORMAL_VARIANT)
					{
						// append the node with smallest adjacency list //
						int size = tempAdjacencyList.get(neighbour).size();
						
						if(size < min)
						{
							nextNode = neighbour;
							min=size;
						}
						else if(size == min)
						{
							int coin = Utility.randomIntInclusive(1);
							if(coin==1)
								nextNode = neighbour;
						}
					}
					else if(option == GREEDY_VARIANT)
					{
						//find the closest neigbour
						// if distance from current node to neighbour is less then minCost, nextNode=Neighbour
						
						int d  = problemInstance.depotCount;
						int c= problemInstance.customerCount;
						double thisNeighbourDistance;
						
						if(currentNode < problemInstance.customerCount && neighbour < problemInstance.customerCount)
						{
							thisNeighbourDistance = problemInstance.costMatrix[(currentNode+d)][neighbour+d];
						}
						else if(currentNode >= problemInstance.customerCount && neighbour < problemInstance.customerCount)
						{	
							thisNeighbourDistance = problemInstance.costMatrix[currentNode-c][neighbour+d];
						}
						else if(currentNode < problemInstance.customerCount && neighbour >= problemInstance.customerCount)
						{	
							thisNeighbourDistance = problemInstance.costMatrix[currentNode+d][neighbour-c];
						}
						else //
							thisNeighbourDistance = problemInstance.costMatrix[currentNode-c][neighbour-c];
							
							
						
						
						if(thisNeighbourDistance < minCost)
						{
							nextNode = neighbour;
							minCost=thisNeighbourDistance;
						}
						else if(thisNeighbourDistance == minCost)
						{
							int coin = Utility.randomIntInclusive(1);
							if(coin==1)
								nextNode = neighbour;
						}	
					}
	
				}
			}
			else
			{
				// scope for improvement here
				int random = Utility.randomIntInclusive(problemInstance.customerCount-1);
				while(child.periodAssignment[period][random]==false || route.contains(random) )
				{
					random = Utility.randomIntInclusive(problemInstance.customerCount-1);
				}
					
				nextNode = random;	
			}
			
			currentNode = nextNode;
		}
		
		
		tempAdjacencyList.remove(problemInstance.customerCount);
		
		return route;
	}
	

	private static void createAdjacencyList(ArrayList<ArrayList<Integer>> adjacencyList, Individual parent1, Individual parent2)
	{
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int client=0;client<problemInstance.customerCount;client++)
			{
				updateNeighbourLists(parent1, adjacencyList, client, period);
				updateNeighbourLists(parent2, adjacencyList, client, period);
			}
		}
	}
	
	private static void updateNeighbourLists(Individual parent, ArrayList<ArrayList<Integer>> adjacencyList, int client, int period)
	{
		int vehicleParent = RouteUtilities.assignedVehicle(parent, client, period, problemInstance);
		if(vehicleParent==-1) return;
		
		int depotParent = problemInstance.depotAllocation[vehicleParent];
		
		ArrayList<Integer> parentRoute = parent.routes.get(period).get(vehicleParent);
		int indexInParentRoute = parentRoute.indexOf(client); 
		
		if(indexInParentRoute ==- 1) System.out.println("ERROR! NEVER SHOULD HAPPEN !!! CROSSOVER!!");
		
		
		if(indexInParentRoute==0 || indexInParentRoute == parentRoute.size()-1)
		{
			/*
			  add the client-depot edge,  label/index depot d as customercount+d;
			 */
			
			int depotLabel = problemInstance.customerCount + depotParent;
			if( ! adjacencyList.get(client).contains(depotLabel))
			{
					adjacencyList.get(client).add(depotLabel);
			}
			
			if(!adjacencyList.get(depotLabel).contains(client))
			{
					adjacencyList.get(depotLabel).add(client);
			}
			
		}
	
		
		if(indexInParentRoute>0)
		{
			int neighbourLeft = parentRoute.get(indexInParentRoute-1);

			if(!adjacencyList.get(client).contains(neighbourLeft))
				adjacencyList.get(client).add(neighbourLeft);
		
		}
		
		if(indexInParentRoute+1<parentRoute.size())
		{
			int neighbourRight = parentRoute.get(indexInParentRoute+1);

			if(!adjacencyList.get(client).contains(neighbourRight))
				adjacencyList.get(client).add(neighbourRight);
			
		}
		
	}
	
}
