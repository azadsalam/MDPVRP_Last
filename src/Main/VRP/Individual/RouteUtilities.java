package Main.VRP.Individual;
import java.util.ArrayList;

import Main.VRP.ProblemInstance;



public class RouteUtilities 
{
	
	public static boolean isDuplicate( ArrayList<Integer> route1, ArrayList<Integer> route2) 
	{
		int size1 = route1.size();
		int size2 = route2.size();
		
		if(size1 != size2) return false;
		
		boolean sameInForwardDirection=true;
		for(int i=0;i<size1;i++)
		{
			if(route1.get(i)!=route2.get(i)) 
			{
				sameInForwardDirection=false;
				break;
			}
		}
		
		boolean sameInReverseDirection=true;

		for(int i=0;i<size1;i++)
		{
			if(route1.get(i)!=route2.get(size1-i-1)) 
			{
				sameInReverseDirection=false;
				break;
			}
		}
		
		if(sameInForwardDirection || sameInReverseDirection)
			return true;
		else
			return false;
	}
	
	/**
	 * Checks if the client is present in any route or not,  for the specified period
	 * @param problemInstance
	 * @param individual
	 * @param period
	 * @param client
	 * @return true if client is present in some route <br/> else false
	 */
	public static boolean doesRouteContainThisClient(ProblemInstance problemInstance, Individual individual, int period, int client)
	{

		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			if(individual.routes.get(period).get(vehicle).contains(client))
			{
				return true;
			}
		}	
		return false;
	}


	/**
	 * Returns the vehicleNumber which serves the client  
	 * @param individual
	 * @param client
	 * @param period
	 * @param problemInstance
	 * @return vehicleNumber, v ; if the client is present in the period
	 * <br/> -1 if not present
	 */	
	public static int assignedVehicle(Individual individual, int client, int period,ProblemInstance problemInstance)
	{
		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			if(route.contains(client)) return vehicle;
		}	
		return -1;
	}


	/**
	 * Returns the depotNUmber which serves the client  
	 * @param individual
	 * @param client
	 * @param period
	 * @param problemInstance
	 * @return vehicleNumber, v ; if the client is present in the period
	 * <br/> -1 if not present
	 */	
	public static int assignedDepot(Individual individual, int client, int period,ProblemInstance problemInstance)
	{
		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
			if(route.contains(client))
			{
				return problemInstance.depotAllocation[vehicle];
			}
		}	
		return -1;
	}
	
	public static void greedyCutWithMinimumViolation(Individual individual,int period,int depot, ArrayList<Integer> bigRoute) 
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> vehicles = problemInstance.vehiclesUnderThisDepot.get(depot);
		
		
		int currentVehicleIndex = 0;
		double currentLoad=0;
		
		while(!bigRoute.isEmpty() && currentVehicleIndex <vehicles.size())
		{
			int vehicle = vehicles.get(currentVehicleIndex);
			int client = bigRoute.get(0);
			
			double thisCapacity = problemInstance.loadCapacity[vehicle];
			double thisClientDemand = problemInstance.demand[client];
			
			double loadViolation = (currentLoad+thisClientDemand) - (thisCapacity);
			
			if(loadViolation <= 0) //add this client to this vehicle route, update info
			{
				individual.routes.get(period).get(vehicle).add(client);
				currentLoad += thisClientDemand;
				bigRoute.remove(0);
			}
			else
			{
				currentVehicleIndex++;
				currentLoad=0;
			}
		}
		
		if(!bigRoute.isEmpty())
		{
			
			//System.out.println("LEFT : "+bigRoute.size());
			while(!bigRoute.isEmpty())
			{
				int client = bigRoute.get(0);
				int vehicle = vehicles.get(vehicles.size()-1);
				
				individual.routes.get(period).get(vehicle).add(client);
				bigRoute.remove(0);
			}
		}
	}

	
	
	/**
	 * returns the index in which the client can be inserted with minimum cost increase,
	 *  should be used for big route, when the vehicles are still unassigned
	 * @param problemInstance
	 * @param client
	 * @param route
	 * @return
	 */
	public static MinimumCostInsertionInfo minimumCostInsertionPosition2(ProblemInstance problemInstance,int depot,int client,ArrayList<Integer> route)
	{
		double min = Double.MAX_VALUE;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		//int depot = problemInstance.depotAllocation[vehicle];
		
		
		if(route.size()==0)
		{
		//	route.add(client);	
			
			MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
			info.increaseInCost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depot];
			info.insertPosition=0;
			info.loadViolation= -1;
			//info.vehicle = vehicle;
			return info;
		}
		
		cost=0;
		cost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(0)];
		cost -= (costMatrix[depot][depotCount+route.get(0)]);
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = 0;
		}
		
		
		for(int insertPosition=1;insertPosition<route.size();insertPosition++)
		{
			//insert the client between insertPosition-1 and insertPosition and check 
			cost = costMatrix[depotCount+route.get(insertPosition-1)][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(insertPosition)];
			cost -= (costMatrix[depotCount+route.get(insertPosition-1)][depotCount+route.get(insertPosition)]);
			if(cost<min)
			{
				min=cost;
				chosenInsertPosition = insertPosition;
			}
		}
		
		cost = costMatrix[depotCount+route.get(route.size()-1)][depotCount+client] + costMatrix[depotCount+client][depot];
		cost-=(costMatrix[depotCount+route.get(route.size()-1)][depot]);
		
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = route.size();
		}
		
		MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
		info.increaseInCost = min;
		info.insertPosition=chosenInsertPosition;
		info.loadViolation= -1;
		//info.vehicle = vehicle;
		return info;
	
	}

	/**
	 * returns the MinimumCostInsertionInfo for a route consisting of only clients, no depot is considered, does not consider load violation
	 * @param problemInstance
	 * @param client
	 * @param route
	 * @return
	 */
	public static MinimumCostInsertionInfo minimumCostInsertionPositionWithoutDepot(ProblemInstance problemInstance,int client,ArrayList<Integer> route)
	{
		double min = Double.MAX_VALUE;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		//int depot = problemInstance.depotAllocation[vehicle];
		
		
		if(route.size()==0)
		{
		//	route.add(client);	
			
			MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
			info.increaseInCost = 0;
			info.insertPosition=0;
			info.loadViolation= -1;
			//info.vehicle = vehicle;
			return info;
		}
		
		
		
		for(int insertPosition=1;insertPosition<route.size();insertPosition++)
		{
			//insert the client between insertPosition-1 and insertPosition and check 
			cost = costMatrix[depotCount+route.get(insertPosition-1)][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(insertPosition)];
			cost -= (costMatrix[depotCount+route.get(insertPosition-1)][depotCount+route.get(insertPosition)]);
			if(cost<min)
			{
				min=cost;
				chosenInsertPosition = insertPosition;
			}
		}
		
		cost = costMatrix[depotCount+route.get(route.size()-1)][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(0)];
		cost-=(costMatrix[depotCount+route.get(route.size()-1)][depotCount+route.get(0)]);
		
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = route.size();
		}
		
		MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
		info.increaseInCost = min;
		info.insertPosition=chosenInsertPosition;
		info.loadViolation= -1;
		//info.vehicle = vehicle;
		return info;
	
	}

	
	
	/**
	 * returns the index in which the client can be inserted with minimum cost increase
	 *  - route time violation added 
	 *  	(Assuming - route time limit is same for all period)
	 *  	(Assuming - travel time == cost )
	 * @param problemInstance
	 * @param vehicle
	 * @param client
	 * @param route
	 * @return
	 */
	public static MinimumCostInsertionInfo minimumCostInsertionPosition(ProblemInstance problemInstance,int vehicle,int client,ArrayList<Integer> route)
	{
		double min = Double.MAX_VALUE;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		int depot = problemInstance.depotAllocation[vehicle];
		
		
		if(route.size()==0)
		{
		//	route.add(client);				
			MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
			
			info.increaseInCost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depot];
			info.insertPosition=0;
			
			info.loadViolation= problemInstance.demand[client] - problemInstance.loadCapacity[vehicle];
			
			if(info.loadViolation>0) info.loadViolationContribution = info.loadViolation;
			else info.loadViolationContribution=0;
			
			
			double maxTimeLimit = problemInstance.timeConstraintsOfVehicles[0][vehicle];
			
			if( maxTimeLimit == 0)
			{
				info.routeTimeViolation = 0;
				info.routeTimeViolationContribution = 0;
			}
			else
			{
				info.routeTimeViolation =  problemInstance.serviceTime[client] - maxTimeLimit;
				
				if(info.routeTimeViolation >0 ) 
					info.routeTimeViolationContribution = info.routeTimeViolation;
				else 
					info.routeTimeViolationContribution = 0;
			}
			
			
			info.vehicle = vehicle;
			return info;
		}
		
		cost=0;
		cost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(0)];
		cost -= (costMatrix[depot][depotCount+route.get(0)]);
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = 0;
		}
		
		for(int insertPosition=1;insertPosition<route.size();insertPosition++)
		{
			//insert the client between insertPosition-1 and insertPosition and check 
			cost = costMatrix[depotCount+route.get(insertPosition-1)][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(insertPosition)];
			cost -= (costMatrix[depotCount+route.get(insertPosition-1)][depotCount+route.get(insertPosition)]);
			if(cost<min)
			{
				min=cost;
				chosenInsertPosition = insertPosition;
			}
		}
		
		cost = costMatrix[depotCount+route.get(route.size()-1)][depotCount+client] + costMatrix[depotCount+client][depot];
		cost-=(costMatrix[depotCount+route.get(route.size()-1)][depot]);
		
		if(cost<min)
		{
			min=cost;
			chosenInsertPosition = route.size();
		}
		
		//calculate load violation
		//calculate total load
		double totalLoad=0,previousLoad=0;
		
		for(int i=0;i<route.size();i++)
		{
			int node = route.get(i);
			previousLoad += problemInstance.demand[node];
		}
		totalLoad = previousLoad + problemInstance.demand[client];
		
		double vehicleCapacity = problemInstance.loadCapacity[vehicle];
		double totalLoadViolation = totalLoad - vehicleCapacity;
		
		double loadViolationContribution=0;
		if(previousLoad > vehicleCapacity) loadViolationContribution = problemInstance.demand[client];
		else
		{ 
			if(totalLoadViolation>0)loadViolationContribution = totalLoadViolation;
			else loadViolationContribution = 0;
		} 
		
				
		
		double routeTimeViolation = 0;
		double routeTimeViolationContribution = 0;
		double maxTimeLimit = problemInstance.timeConstraintsOfVehicles[0][vehicle];

		if(maxTimeLimit == 0)
		{
			routeTimeViolation = 0;
			routeTimeViolationContribution = 0;
		}
		else
		{
			// calculate Route Time Violation
			double totalRouteTimePrevious = 0;
			double totalRouteTimeNow=0;
			//first calculate the total route time, after that adjust for the new link

			//depot -> node 0
			totalRouteTimePrevious +=  problemInstance.costMatrix[depot][depotCount+route.get(0)] ;
			for(int i=1;i<route.size();i++) 
			{
				totalRouteTimePrevious +=  problemInstance.costMatrix[depotCount+route.get(i-1)][depotCount+route.get(i)] ;
			}
			totalRouteTimePrevious += problemInstance.costMatrix[depotCount+route.get(route.size()-1)][depot];
			
			totalRouteTimeNow += min;

			
			//
			if(totalRouteTimeNow<=maxTimeLimit) 
			{
				routeTimeViolation = 0;
				routeTimeViolationContribution = 0;
			}
			else
			{
				if(totalRouteTimePrevious>maxTimeLimit) //age thekei route time violate korto
				{
					routeTimeViolation = totalRouteTimeNow - maxTimeLimit;
					routeTimeViolation = min;
				}
				else // ei client add korar por violation hoise
				{
					routeTimeViolation = totalRouteTimeNow - maxTimeLimit;
					routeTimeViolationContribution = routeTimeViolation;
				}
			}
			
			
		}
		
		
		
		MinimumCostInsertionInfo info = new MinimumCostInsertionInfo();
		info.increaseInCost = min;
		info.insertPosition = chosenInsertPosition;
		info.loadViolation = totalLoadViolation;
		info.loadViolationContribution = loadViolationContribution;
		info.routeTimeViolation = routeTimeViolation;
		info.routeTimeViolationContribution = routeTimeViolationContribution;
		info.vehicle = vehicle;
		return info;
	
	}

	
	public static double costForSingleRoute(Individual individual, int period, int vehicle)
	{
		int assignedDepot;		
		int clientNode,previous;

		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
		assignedDepot = individual.problemInstance.depotAllocation[vehicle];
		ProblemInstance problemInstance = individual.problemInstance;
		if(route.isEmpty())return 0;

		double costForPV = 0;
		
		for(int i=1;i<route.size();i++)
		{
			clientNode = route.get(i);
			previous = route.get(i-1);
			costForPV +=	problemInstance.costMatrix[previous+problemInstance.depotCount][clientNode+problemInstance.depotCount];
		}

        costForPV += problemInstance.costMatrix[assignedDepot][route.get(0)+problemInstance.depotCount];
        costForPV += problemInstance.costMatrix[route.get(route.size()-1)+problemInstance.depotCount][assignedDepot];

		return costForPV;

	}
	/**
	 * Returns the cost of this route
	 * @param problemInstance
	 * @param route
	 * @param vehicle
	 * @return
	 */
	public static double costForThisRoute(ProblemInstance problemInstance, ArrayList<Integer> route, int vehicle)
	{
		int assignedDepot;		
		int clientNode,previous;

		assignedDepot = problemInstance.depotAllocation[vehicle];
		if(route.isEmpty())return 0;

		double costForPV = 0;
		
		for(int i=1;i<route.size();i++)
		{
			clientNode = route.get(i);
			previous = route.get(i-1);
			costForPV +=	problemInstance.costMatrix[previous+problemInstance.depotCount][clientNode+problemInstance.depotCount];
		}

        costForPV += problemInstance.costMatrix[assignedDepot][route.get(0)+problemInstance.depotCount];
        costForPV += problemInstance.costMatrix[route.get(route.size()-1)+problemInstance.depotCount][assignedDepot];

		return costForPV;

	}
	
	public static int closestDepot(int client)
	{
		ProblemInstance problemInstance = Individual.getProblemInstance();
		int clientIndex = problemInstance.depotCount + client;
		
		int selectedDepot=0;
		double minDistance = problemInstance.costMatrix[0][clientIndex] + problemInstance.costMatrix[clientIndex][0];
		minDistance /=2;
		
		for(int depot=1;depot<problemInstance.depotCount;depot++)
		{
			double distance = problemInstance.costMatrix[depot][clientIndex] + problemInstance.costMatrix[clientIndex][depot];
			distance /= 2;
			if(distance <= minDistance)
			{
				selectedDepot = depot;
				minDistance = distance;
			}
		}
		return selectedDepot ;
	}
	
	/**
	 * Returns K depots, sorted in ascending order with distance to customer client
	 * @param client
	 * @param k
	 * @return
	 */
	public static ArrayList<Integer> closestDepots(int client)
	{
		ProblemInstance problemInstance = Individual.getProblemInstance();
		int clientIndex = problemInstance.depotCount + client;
		
		ArrayList<Integer> selectedDepot= new ArrayList<Integer>();
		ArrayList<Double> distances = new ArrayList<Double>();
		//double minDistance = problemInstance.costMatrix[0][clientIndex] + problemInstance.costMatrix[clientIndex][0];
		//minDistance /=2;
		
		double minDistance = Double.MAX_VALUE;
		for(int depot=0;depot<problemInstance.depotCount;depot++)
		{
			double distance = problemInstance.costMatrix[depot][clientIndex] + problemInstance.costMatrix[clientIndex][depot];
			distance /= 2;
			
			int i=0;
			while(i<distances.size() && distances.get(i) <= distance) i++;
			
			distances.add(i, distance);
			selectedDepot.add(i,depot);
		}
		return selectedDepot ;
	}

	
	public static double neighbourDistanceForThisPosition(Individual individual, int period, int vehicle, int position)
	{
		ProblemInstance pi = individual.problemInstance;
		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
		int dc = individual.problemInstance.depotCount;
		int depot = pi.depotAllocation[vehicle];
		
		int node = route.get(position);
		
		double distance=0;
		if(position == 0)
		{
			// depot -> node0 + node0 -> node 1
			if(route.size()==1)
				distance = pi.costMatrix[depot][dc+node] + pi.costMatrix[dc+node][depot];
			else
				distance = pi.costMatrix[depot][dc+node] + pi.costMatrix[dc+node][dc + route.get(1)];
		}
		
		else if(position == route.size()-1)
		{
			// last node -> last node er ager node + last node -> depot
			distance = pi.costMatrix[dc + route.get( position - 1 )][dc+node] + pi.costMatrix[dc+node][depot];
		}
		
		else
		{
			distance = pi.costMatrix[dc + route.get( position - 1 )][dc+node] + pi.costMatrix[dc+node][dc + route.get( position + 1 )];
		}
		
		return distance;
	}
	
	
	
}
