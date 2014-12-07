package Main.VRP.Individual.Crossover;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;


public class Uniform_VariedEdgeRecombnation_with_No_Load_Crossover 
{
	
	// option 1 => normal edge recombination variant, appends the node which has smallest neighbor set
	// option 2 => greedy edge recombination variant, appends the node which has least cost with the previous node
	private static final int NORMAL_VARIANT = 1;
	private static final int GREEDY_VARIANT = 2;
	
	private static  int DEPOT; 
	static ProblemInstance problemInstance;
	
	
	public static void crossOver_Uniform_VariedEdgeRecombination_cost_greedy(ProblemInstance pi,Individual parent1,Individual parent2,Individual child)
	{
		initialise(pi, parent1, parent2, child);
			
		variedEdgeRecombinationCrossoverForRoutes(child, parent1, parent2, GREEDY_VARIANT);
		//update cost and penalty
		child.calculateCostAndPenalty();
		if(Solver.gatherCrossoverStat) CrossoverStatistics.gatherData(parent1, parent2, child);

	}
	
	private static void initialise(ProblemInstance pi,Individual parent1,Individual parent2,Individual child) 
	{
		problemInstance = pi;
		DEPOT = problemInstance.customerCount;
		
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

		for(int period=0;period<problemInstance.periodCount;period++)
		{			 
			// Step 1. Assign client to vehicle
			// Step 2. Create Neighbour/Adjacency List
			// Step 3. Create Route For Each Vehicle
			
			//assignedVehicle[c] => v means, client c is under vehicle v .... if no assigned vehicle v=-1
			int[] assignedVehicle = new int[problemInstance.customerCount];
			for(int i=0;i<problemInstance.customerCount;i++) assignedVehicle[i] = -1;
			
			//numberOfCustomerServed[v] -> n means, vehicle v serves n clients
			int[] numberOfCustomerServed = new int[problemInstance.vehicleCount];
			
			
			ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>();
			
			for(int client =0;client<problemInstance.customerCount;client++)
			{
				adjacencyList.add(new ArrayList<Integer>());
			}
			
			ArrayList<ArrayList<Integer>> vehicleAdjacencyList = new ArrayList<ArrayList<Integer>>();
			
			for(int vehicle =0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				vehicleAdjacencyList.add(new ArrayList<Integer>());
			}
			
			
			assignClientToVehicle(child,parent1,parent2,assignedVehicle, numberOfCustomerServed,period);
			createAdjacencyList(adjacencyList, vehicleAdjacencyList, period, assignedVehicle,child,parent1,parent2);
			
			boolean print=false;	
			if(print)
			{
				problemInstance.out.println("Vehicle assignment : period : "+period);
				for(int client=0;client<problemInstance.customerCount;client++)
					problemInstance.out.print("[ "+client+" -> "+assignedVehicle[client]+" ] ");
				problemInstance.out.println("");
			
				problemInstance.out.println("Neighbour List : Period : "+period);
				for(int client=0;client<problemInstance.customerCount;client++)
				{				
					problemInstance.out.print("Neigbours of "+client+" : ");
					for(int i=0;i< adjacencyList.get(client).size();i++)
						problemInstance.out.print(adjacencyList.get(client).get(i)+" ");
					problemInstance.out.println("");
					
				}		
						
			}
			
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				createRoute(parent1, parent2, child, adjacencyList, vehicleAdjacencyList,numberOfCustomerServed,vehicle, period, assignedVehicle, option);
			}
			
		}
	}
	
	private static void createRoute(Individual parent1,Individual parent2,Individual child, 
			ArrayList<ArrayList<Integer>> adjacencyList, ArrayList<ArrayList<Integer>> vehicleAdjacencyList, int[] numberOfCustomerServed,
		    int vehicle, int period, int[] assignedVehicle, int option ) 
	{
		
		adjacencyList.add(new ArrayList<Integer>());
		
		for(int i=0;i<vehicleAdjacencyList.get(vehicle).size();i++)
			adjacencyList.get(problemInstance.customerCount).add(vehicleAdjacencyList.get(vehicle).get(i));
		
		int assignedDepot = problemInstance.depotAllocation[vehicle];
		int clientsUnderThisVehicle  = numberOfCustomerServed[vehicle];
			
		int currentNode = problemInstance.customerCount;
		ArrayList<Integer> route = child.routes.get(period).get(vehicle);
		
		while(route.size()<clientsUnderThisVehicle)
		{
			if(currentNode != problemInstance.customerCount) route.add(currentNode);
			
			if(route.size()==clientsUnderThisVehicle) break; 
			
			//now delete the node from adjacency list
			for(int node=0;node < problemInstance.customerCount+1 ;node++)
			{
				adjacencyList.get(node).remove(new Integer(currentNode));
			}
			
			int nextNode=-1;
			ArrayList<Integer> neigbourList = adjacencyList.get(currentNode);
			
			if(neigbourList.size() != 0)
			{
				//highest 4 neighbours ?
				int min= 5;
				double minCost = Double.POSITIVE_INFINITY;
				for(int j = 0;j<neigbourList.size();j++)
				{
					int neighbour = neigbourList.get(j);
					
					if(option == NORMAL_VARIANT)
					{
						// append the node with smallest adjacency list //
						int size = adjacencyList.get(neighbour).size();
						
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
						double thisNeighbourDistance;
						
						if(currentNode != problemInstance.customerCount)
							thisNeighbourDistance = problemInstance.costMatrix[(currentNode+d)][neighbour+d];
						else
							thisNeighbourDistance = problemInstance.costMatrix[assignedDepot][neighbour+d];
						
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
				// scope for improvment here
				int random = Utility.randomIntInclusive(problemInstance.customerCount-1);
				while(child.periodAssignment[period][random]==false ||  assignedVehicle[random] != vehicle || route.contains(random) )
				{
					random = Utility.randomIntInclusive(problemInstance.customerCount-1);
				}
					
				nextNode = random;	
			}
			
			currentNode = nextNode;
		}
		
		//
		adjacencyList.remove(problemInstance.customerCount);
	}
	
	private static void assignClientToVehicle(Individual child, Individual parent1, Individual parent2, int[] assignedVehicle, int[] numberOfCustomerServed,int period) 
	{
		int retryLimit = problemInstance.vehicleCount;
		double expansionFactor = 1;
		double[] loadPerVehicle = new double[problemInstance.vehicleCount];
		
		for(int client=0; client<problemInstance.customerCount;client++)
		{
			if(child.periodAssignment[period][client]==false) continue;
			
			int vehicle1 = RouteUtilities.assignedVehicle(parent1, client, period, problemInstance);
			int vehicle2 = RouteUtilities.assignedVehicle(parent2, client, period, problemInstance);
			
			int chosenVehicle=500000;
			double demandThisClient = problemInstance.demand[client];
			
			if(vehicle1==-1 && vehicle2==-1) System.out.println("ERROR!!!! NEVER SHOULD HAPPEN!!!!!!!!!! in varied edge recombination op....");
	
			else if(vehicle1==-1)
			{

				double capacityOfVehicle= problemInstance.loadCapacity[vehicle2];
				if( loadPerVehicle[vehicle2] + demandThisClient <= capacityOfVehicle * expansionFactor)
					chosenVehicle = vehicle2;
				else //assign random vehicle with no violation
				{
					int retry = 0;
					int randVehicle= vehicle2;
					while(loadPerVehicle[randVehicle] + demandThisClient > problemInstance.loadCapacity[randVehicle] * expansionFactor
							&& retry<= retryLimit)
					{
						randVehicle = Utility.randomIntExclusive(problemInstance.vehicleCount);
						retry++;
					}
					if(loadPerVehicle[randVehicle] + demandThisClient <= problemInstance.loadCapacity[randVehicle] * expansionFactor)
						chosenVehicle = randVehicle;
					else 
						chosenVehicle = vehicle2;
				}
			}
			else if(vehicle2==-1)
			{	

				if( loadPerVehicle[vehicle1] + demandThisClient <= problemInstance.loadCapacity[vehicle1] * expansionFactor)
					chosenVehicle = vehicle1; 
				else
				{
					int retry = 0;
					int randVehicle= vehicle1;
					while(loadPerVehicle[randVehicle] + demandThisClient > problemInstance.loadCapacity[randVehicle] * expansionFactor
							&& retry <= retryLimit)
					{
						randVehicle = Utility.randomIntExclusive(problemInstance.vehicleCount);
						retry++;
					}
					
					if(loadPerVehicle[randVehicle] + demandThisClient <= problemInstance.loadCapacity[randVehicle] * expansionFactor)
						chosenVehicle = randVehicle;
					else 
						chosenVehicle = vehicle1;
				}
			}
			else 
			{
//				if(vehicle1!=vehicle2) 
//					System.out.println();
				int coin = Utility.randomIntInclusive(1);
				if(coin==0) 
				{
					if( loadPerVehicle[vehicle1] + demandThisClient <= problemInstance.loadCapacity[vehicle1] * expansionFactor)
						chosenVehicle = vehicle1;
					else if( loadPerVehicle[vehicle2] + demandThisClient <= problemInstance.loadCapacity[vehicle2] * expansionFactor)
						chosenVehicle = vehicle2;
					else
					{
						int retry = 0;
						int randVehicle= vehicle1;
						while(loadPerVehicle[randVehicle] + demandThisClient > problemInstance.loadCapacity[randVehicle] * expansionFactor
								&& retry<=retryLimit)
						{
							retry++;
							randVehicle = Utility.randomIntExclusive(problemInstance.vehicleCount);
						}

						if(loadPerVehicle[randVehicle] + demandThisClient <= problemInstance.loadCapacity[randVehicle] * expansionFactor)
							chosenVehicle = randVehicle;
						else 
							chosenVehicle = vehicle1;
					}
				}
				else 
				{
					if( loadPerVehicle[vehicle2] + demandThisClient <= problemInstance.loadCapacity[vehicle2] * expansionFactor)
						chosenVehicle = vehicle2;
					else if( loadPerVehicle[vehicle1] + demandThisClient <= problemInstance.loadCapacity[vehicle1] * expansionFactor)
						chosenVehicle = vehicle1;
					else
					{
						int retry = 0;
						int randVehicle= vehicle1;
						while(loadPerVehicle[randVehicle] + demandThisClient > problemInstance.loadCapacity[randVehicle] * expansionFactor
								&& retry<=retryLimit)
						{
							retry++;
							randVehicle = Utility.randomIntExclusive(problemInstance.vehicleCount);
						}

						if(loadPerVehicle[randVehicle] + demandThisClient <= problemInstance.loadCapacity[randVehicle] * expansionFactor)
							chosenVehicle = randVehicle;
						else 
							chosenVehicle = vehicle2;
					}
				}
			}
			
			assignedVehicle[client]=chosenVehicle;
			numberOfCustomerServed[chosenVehicle]++;
			loadPerVehicle[chosenVehicle] += demandThisClient;
		}
	}

	private static void createAdjacencyList(ArrayList<ArrayList<Integer>> adjacencyList, ArrayList<ArrayList<Integer>> vehicleAdjacencyList, int period, int[] assignedVehicle,Individual child, Individual parent1, Individual parent2)
	{
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			if(child.periodAssignment[period][client]==false)continue;
			updateNeighbourLists(parent1, child, adjacencyList, vehicleAdjacencyList, client, period, assignedVehicle);
			updateNeighbourLists(parent2, child, adjacencyList, vehicleAdjacencyList, client, period, assignedVehicle);
		}
	}
	
	private static void updateNeighbourLists(Individual parent,Individual child, ArrayList<ArrayList<Integer>> adjacencyList, ArrayList<ArrayList<Integer>> vehicleAdjacencyList, int client, int period, int[] assignedVehicle )
	{
		int vehicleParent = RouteUtilities.assignedVehicle(parent, client, period, problemInstance);
		int vehicleChild = assignedVehicle[client];

		if(vehicleParent==-1) return;
		
		ArrayList<Integer> parentRoute = parent.routes.get(period).get(vehicleParent);
		int indexInParentRoute = parentRoute.indexOf(client); 
		
		if(indexInParentRoute ==- 1) System.out.println("ERROR! NEVER SHOULD HAPPEN !!! CROSSOVER!!");
		
		
		if(indexInParentRoute==0 || indexInParentRoute == parentRoute.size()-1)
		{
			/* in parent this client may be under different depot  
			  add the client-depot edge, only when the client is under same depot in both parent and child
			 */
			
			//int vehicleParent = RouteUtilities.assignedVehicle(parent, client, period, problemInstance);
			
			int depotParent = problemInstance.depotAllocation[vehicleParent]; 
			int depotChild = problemInstance.depotAllocation[vehicleChild];
			
			if(depotParent == depotChild)
			{
				if(!adjacencyList.get(client).contains(DEPOT))
				{
						adjacencyList.get(client).add(DEPOT);
				}
				
				if(!vehicleAdjacencyList.get(vehicleChild).contains(client)) 
				{
					vehicleAdjacencyList.get(vehicleChild).add(client);
				}
			}
		}
	
		
		if(indexInParentRoute>0)
		{
			int neighbour = parentRoute.get(indexInParentRoute-1);
			//int neighbrVehicle = RouteUtilities.assignedVehicle(parent, neighbour, period, problemInstance);
			int neighbrVehicle = assignedVehicle[neighbour];
			if( neighbrVehicle == vehicleChild && child.periodAssignment[period][neighbour]==true)
			{
				if(!adjacencyList.get(client).contains(neighbour))
					adjacencyList.get(client).add(neighbour);
			}
		}
		
		if(indexInParentRoute+1<parentRoute.size())
		{
			int neighbour = parentRoute.get(indexInParentRoute+1);
			//int neighbrVehicle = RouteUtilities.assignedVehicle(parent, neighbour, period, problemInstance);
			int neighbrVehicle = assignedVehicle[neighbour];
			if( neighbrVehicle == vehicleChild && child.periodAssignment[period][neighbour]==true)
			{
				if(!adjacencyList.get(client).contains(neighbour))
					adjacencyList.get(client).add(neighbour);
			}
		}
		
	}
	
}
