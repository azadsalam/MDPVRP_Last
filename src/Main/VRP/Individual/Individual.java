package Main.VRP.Individual;



import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.MutationOperators.GENI;


public class Individual 
{
	//for testing 
	//public int [][] vehicleReassignmentApplied;
	
	public LinkedList<PeriodClientPair> hallOfShamePC;
	
	public static int total=0;
	public static int count=0;
	public static int max=0;
	//representation
	public boolean periodAssignment[][];
	public Vector<Vector<ArrayList<Integer>>> routes;
	
	public double cost;
	
	public double costWithPenalty;
//	Utility utility;
	public boolean isFeasible;
	public boolean feasibilitySet;

	public double costPerRoute[][];
	public double loadViolation[][];
	public double routeTimeViolation[][];
	public double routeTime[][];
	public double totalLoadViolation;

	//double totalRouteTime;
	
	public double totalRouteTimeViolation;
	public double distance = -1;
	static	double distanceRatioToEachVehicle[][]=null;
	static	double cumulativeDistanceRatioToEachVehicle[][]=null;
	
	static int[][] clientsSortedWithDistance = null;
	
	static	double closenessToEachDepot[][]=null;
	static	double cumulativeClosenessToEachDepot[][]=null;
	
	
	public int[] visitCombination;
	public static ProblemInstance problemInstance;
	
	
	public ArrayList<ArrayList<ArrayList<Integer>>> bigRoutes;
	public Individual()
	{
		cost = -1;
		costWithPenalty = -1;
		feasibilitySet = false;
		isFeasible = false;	
	}
	
	public static ProblemInstance getProblemInstance()
	{
		return problemInstance;
	}

		
	public void insertIntoBigClosestDepotRoute(int client,int depot,int period)
	{
		double min = 99999999;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		
		//System.out.println("period "+period+" depot "+depot);
		ArrayList<Integer> route = bigRoutes.get(period).get(depot);
		
		if(route.size()==0)
		{
			route.add(client);	
			return;
		}
		
		cost = 0;
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
	
		route.add(chosenInsertPosition, new Integer(client));
	}


	private void assignRoutesWithClosestDepotWithNeighbourCheckHeuristic()
	{
		//Assign customer to route
		boolean[] clientMap = new boolean[problemInstance.customerCount];
		
		int assigned=0;
		
		while(assigned<problemInstance.customerCount)
		{
			int clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
			if(clientMap[clientNo]) continue;
			clientMap[clientNo]=true;
			assigned++;
			
			
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				if(periodAssignment[period][clientNo]==false)continue;

				int depot = RouteUtilities.closestDepot(clientNo);	
				insertClientToRouteThatMinimizesTheIncreaseInActualCost(clientNo, depot, period);
			}			
		}
	}
	
	private void insertClientToRouteThatMinimizesTheIncreaseInActualCost(int client,int depot,int period)
	{
		double min = 99999999;
		int chosenVehicle =- 1;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		
		ArrayList<Integer> vehiclesUnderThisDepot = problemInstance.vehiclesUnderThisDepot.get(depot);
		
		for(int i=0; i<vehiclesUnderThisDepot.size(); i++)
		{
			int vehicle = vehiclesUnderThisDepot.get(i);
			
			ArrayList<Integer> route = routes.get(period).get(vehicle);
			
			if(route.size()==0)
			{
				cost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depot];
				if(cost<min)
				{
					min=cost;
					chosenVehicle = vehicle;
					chosenInsertPosition = 0;
				}
				continue;
			}
			
			
			cost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depotCount+route.get(0)];
			cost -= (costMatrix[depot][depotCount+route.get(0)]);
			if(cost<min)
			{
				min=cost;
				chosenVehicle = vehicle;
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
					chosenVehicle = vehicle;
					chosenInsertPosition = insertPosition;
				}
			}
			
			cost = costMatrix[depotCount+route.get(route.size()-1)][depotCount+client] + costMatrix[depotCount+client][depot];
			cost-=(costMatrix[depotCount+route.get(route.size()-1)][depot]);
			
			if(cost<min)
			{
				min=cost;
				chosenVehicle = vehicle;
				chosenInsertPosition = route.size();
			}
			
		}
		routes.get(period).get(chosenVehicle).add(chosenInsertPosition, client);
	}
    private void assignRoutesWithClosestDepotWithNeighbourAndViolationCheckHeuristic()
	{
		//Assign customer to route
		boolean[] clientMap = new boolean[problemInstance.customerCount];
		
		int assigned=0;
		
		while(assigned<problemInstance.customerCount)
		{
			int clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
			if(clientMap[clientNo]) continue;
			clientMap[clientNo]=true;
			assigned++;
			
			
			for(int period=0;period<problemInstance.periodCount;period++)
			{		
				if(periodAssignment[period][clientNo]==false)continue;

				int depot = RouteUtilities.closestDepot(clientNo);	
				insertClientToRouteThatMinimizesTheIncreaseInActualCostAndCheckViolation(clientNo, depot, period);
			}			
		}
	}

	private void insertClientToRouteThatMinimizesTheIncreaseInActualCostAndCheckViolation(int client,int depot,int period)
	{
		double min = 99999999;
		boolean chosenRouteValid=false;
		int chosenVehicle =- 1;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		
		ArrayList<Integer> vehiclesUnderThisDepot = problemInstance.vehiclesUnderThisDepot.get(depot);
		
		for(int i=0; i<vehiclesUnderThisDepot.size(); i++)
		{
			int vehicle = vehiclesUnderThisDepot.get(i);
			
			MinimumCostInsertionInfo minimumCostInsertionInfo = getMinimumCostIncreseInfo(client, vehicle, period);
			
			
//			System.out.println("Depot : "+depot+" Vehicle : "+vehicle+" cost : "+minimumCostInsertionInfo.cost+" load violation : "+minimumCostInsertionInfo.loadViolation);
			
			if(chosenRouteValid) //previously chosen route is valid / doesnt violate load constraint
			{
				if(minimumCostInsertionInfo.loadViolation<=0 && minimumCostInsertionInfo.increaseInCost<=min) //this one is also valid
				{
					chosenRouteValid = true;
					min = minimumCostInsertionInfo.increaseInCost;
					chosenVehicle = vehicle;
					chosenInsertPosition = minimumCostInsertionInfo.insertPosition;
				}
			}
			else
			{
				//if this one is valid than select this
				// if both are invalid take the one with less cost
				if(minimumCostInsertionInfo.loadViolation<=0 || minimumCostInsertionInfo.increaseInCost < min)
				{
					chosenRouteValid = true;
					min = minimumCostInsertionInfo.increaseInCost;
					chosenVehicle = vehicle;
					chosenInsertPosition = minimumCostInsertionInfo.insertPosition;
				}
				
			}
		}
//		System.out.println("Chosen vehicle : "+chosenVehicle);
		routes.get(period).get(chosenVehicle).add(chosenInsertPosition, client);
	}
 
	
	
	/**
	 * Provides minimum cost increase insertion Info 
	 * @param client
	 * @param vehicle
	 * @param period
	 * @param thisRouteLoad
	 * @return
	 */
	private MinimumCostInsertionInfo getMinimumCostIncreseInfo(int client,int vehicle,int period)
	{
		MinimumCostInsertionInfo minimumCostInfo = new MinimumCostInsertionInfo();
		double min = 99999999;
		int chosenInsertPosition =- 1;
		double cost;
		
		double [][]costMatrix = problemInstance.costMatrix;
		int depotCount = problemInstance.depotCount;
		int depot = problemInstance.depotAllocation[vehicle];	
		ArrayList<Integer> route = routes.get(period).get(vehicle);
		
		double thisRouteLoad = 0;
		
		for(int i=0;i<route.size();i++)
		{
			int cl = route.get(i);
			thisRouteLoad += problemInstance.demand[cl];
		}
		
		double loadViolation =  (thisRouteLoad + problemInstance.demand[client]) - problemInstance.loadCapacity[vehicle];
		
/*
		if(loadViolation>0)
		{
			minimumCostInfo.insertPosition=-1;
			minimumCostInfo.cost=99999999;
			minimumCostInfo.vehicle = vehicle;
			minimumCostInfo.loadViolation = loadViolation;
			return minimumCostInfo;
		}
*/		
		if(route.size()==0)
		{
			cost = costMatrix[depot][depotCount+client] + costMatrix[depotCount+client][depot];
						
			minimumCostInfo.insertPosition=0;
			minimumCostInfo.increaseInCost=cost;
			minimumCostInfo.vehicle = vehicle;
			minimumCostInfo.loadViolation = loadViolation;
			return minimumCostInfo;
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
			
		minimumCostInfo.insertPosition=chosenInsertPosition;
		minimumCostInfo.increaseInCost=min;
		minimumCostInfo.vehicle = vehicle;
		minimumCostInfo.loadViolation = loadViolation;
		return minimumCostInfo;
	}
	////////////////

	
	
	
	private int closestDepot2(int client)
	{
		int selectedDepot=-1;
		double maxProbable = closenessToEachDepot[client][0];
		//	System.out.print("Client : "+client+" Rand : " +rand );
		for(int depot=0;depot<problemInstance.depotCount;depot++)
		{
			if(maxProbable<=closenessToEachDepot[client][depot])
			{
				selectedDepot = depot;
				maxProbable = closenessToEachDepot[client][depot];
			}
		}
		return selectedDepot ;
	}

	/*---------------------THESE ARE OF NO USE NOW ---------------------*/
		
	
	
	public static void calculateAssignmentProbalityForDiefferentDepot(ProblemInstance problemInstance) 
	{
		double sum=0;
		closenessToEachDepot = new double[problemInstance.customerCount][problemInstance.depotCount];
		cumulativeClosenessToEachDepot = new double[problemInstance.customerCount][problemInstance.depotCount];
		
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				closenessToEachDepot[client][depot] = (1/problemInstance.costMatrix[depot][problemInstance.depotCount+client]);
			}
						
			sum=0;
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				sum+= closenessToEachDepot[client][depot];
			}
			

			/*for(int depot=0;depot<problemInstance.depotCount;depot++)
				System.out.print(closenessToEachDepot[client][depot]+" ");
			System.out.println("Sum : "+sum);
			*/
			
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				closenessToEachDepot[client][depot] /= sum;
			}
			
			cumulativeClosenessToEachDepot[client][0] = closenessToEachDepot[client][0];
			for(int depot=1;depot<problemInstance.depotCount;depot++)
			{
				cumulativeClosenessToEachDepot[client][depot] = cumulativeClosenessToEachDepot[client][depot-1]+ closenessToEachDepot[client][depot];
			}
			
			
			/*for(int depot=0;depot<problemInstance.depotCount;depot++)
				System.out.print(closenessToEachDepot[client][depot]+" ");
			System.out.println();			
			
			for(int depot=0;depot<problemInstance.depotCount;depot++)
				System.out.print(cumulativeClosenessToEachDepot[client][depot]+" ");
				
			System.out.println();*/
		}
	}
	private int mostProbableRoute(int client)
	{
		double rand = Utility.randomDouble(0, 1);
	//	System.out.print("Client : "+client+" Rand : " +rand );
		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			if(rand<cumulativeDistanceRatioToEachVehicle[client][vehicle])
			{
				//System.out.println("Chosen Vehicle : " +vehicle );
				return vehicle;
			}
		}
		return -1;
	}
	public static void calculateProbalityForDiefferentVehicle(ProblemInstance problemInstance) 
	{
		int depot;
		double sum=0;
		distanceRatioToEachVehicle = new double[problemInstance.customerCount][problemInstance.vehicleCount];
		cumulativeDistanceRatioToEachVehicle= new double[problemInstance.customerCount][problemInstance.vehicleCount];
		for(int client=0;client<problemInstance.customerCount;client++)
		{

			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				depot = problemInstance.depotAllocation[vehicle];
				distanceRatioToEachVehicle[client][vehicle] = (1/problemInstance.costMatrix[depot][problemInstance.depotCount+client]);
			}
			
			
			sum=0;
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				sum+= distanceRatioToEachVehicle[client][vehicle];
			}
			

			/*for(int i=0;i<problemInstance.vehicleCount;i++)
				System.out.print(distanceRatioToEachVehicle[client][i]+" ");
			System.out.println("Sum : "+sum);
			*/
			
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				distanceRatioToEachVehicle[client][vehicle] /= sum;
			}
			
			cumulativeDistanceRatioToEachVehicle[client][0] = distanceRatioToEachVehicle[client][0];
			for(int vehicle=1;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				cumulativeDistanceRatioToEachVehicle[client][vehicle] = cumulativeDistanceRatioToEachVehicle[client][vehicle-1]+ distanceRatioToEachVehicle[client][vehicle];
			}
			
			/*
			for(int i=0;i<problemInstance.vehicleCount;i++)
				System.out.print(distanceRatioToEachVehicle[client][i]+" ");
			System.out.println();			
			
			for(int i=0;i<problemInstance.vehicleCount;i++)
				System.out.print(cumulativeDistanceRatioToEachVehicle[client][i]+" ");
			
			System.out.println();*/
		}
	}
	
	/* THESE ARE OF NO USE NOW - END -------------------------------------- */
	
	public Individual(ProblemInstance problemInstance)
	{
		this.problemInstance = problemInstance;
		
		// ALLOCATING periodCount * customerCount Matrix for Period Assignment
		periodAssignment = new boolean[problemInstance.periodCount][problemInstance.customerCount];
		//ALlocating routes
		routes =  new Vector<Vector<ArrayList<Integer>>>();
		
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			routes.add(new Vector<ArrayList<Integer>>());
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				routes.get(period).add(new ArrayList<Integer>());
			}
		}

		costPerRoute = new double[problemInstance.periodCount][problemInstance.vehicleCount];
		loadViolation = new double[problemInstance.periodCount][problemInstance.vehicleCount];
		routeTimeViolation = new double[problemInstance.periodCount][problemInstance.vehicleCount];
		routeTime = new double[problemInstance.periodCount][problemInstance.vehicleCount];
		visitCombination = new int[problemInstance.customerCount];
		
		//
		//vehicleReassignmentApplied = new int[problemInstance.periodCount][problemInstance.customerCount];
		hallOfShamePC = new LinkedList<PeriodClientPair>();
	}
	
	/** Makes a copy cat individual.Copy Constructor.
	 * 
		* copies problem instance, periodAssignment, permutation, routePartition.
		 * <br>
		 * @param original
		 */
	
	public Individual(Individual original)
	{
		problemInstance = original.problemInstance;

		visitCombination  = new int[problemInstance.customerCount];
		
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			visitCombination[client] = original.visitCombination[client];
			//System.out.println("Assigned Combination " + visitCombination[client]);
			
		}
		
				
		periodAssignment = new boolean[problemInstance.periodCount][problemInstance.customerCount];
		for(int i=0;i<problemInstance.periodCount;i++)
		{
			for(int j=0;j<problemInstance.customerCount;j++)
			{
				periodAssignment[i][j] = original.periodAssignment[i][j];
			}
		}
		
		routes = new Vector<Vector<ArrayList<Integer>>>();
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			routes.add(new Vector<ArrayList<Integer>>());

			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				routes.get(period).add(new ArrayList<Integer>());
			}
		}


		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				ArrayList<Integer> originalRoute = original.routes.get(period).get(vehicle);
				ArrayList<Integer> thisRoute = routes.get(period).get(vehicle);
				thisRoute.clear();//lagbena eta yet :P
				
				for(int i=0;i<originalRoute.size();i++)
				{
					thisRoute.add(originalRoute.get(i).intValue());
				}
			}
		}

		cost = original.cost;
		costWithPenalty = original.costWithPenalty;

		//allocate demanViolationMatrix
		
		costPerRoute = new double[problemInstance.periodCount][problemInstance.vehicleCount];
        loadViolation = new double[problemInstance.periodCount][problemInstance.vehicleCount];
        routeTimeViolation = new double[problemInstance.periodCount][problemInstance.vehicleCount];
        routeTime = new double[problemInstance.periodCount][problemInstance.vehicleCount];
    
        
        
        //		
		hallOfShamePC = new LinkedList<PeriodClientPair>();


		for (int i=0;i<original.hallOfShamePC.size();i++) 
		{
			PeriodClientPair pair = original.hallOfShamePC.get(i);
			hallOfShamePC.addLast(new PeriodClientPair(pair.period, pair.client));
		}
		
	}
	
	/*public void initialiseVehiclePenaltyMatrix()
	{
		for(int i=0;i<problemInstance.periodCount;i++)
		{
			for(int j=0;j<problemInstance.customerCount;j++)
			{
				vehicleReassignmentApplied[i][j] = 0;
			}
		}
	}*/
	public void copyIndividual(Individual original)
	{
		int i,j;
		problemInstance = original.problemInstance;

		
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			visitCombination[client] = original.visitCombination[client];
			//System.out.println("Assigned Combination " + visitCombination[client]);
			
		}
		
		for( i=0;i<problemInstance.periodCount;i++)
		{
			for( j=0;j<problemInstance.customerCount;j++)
			{
				periodAssignment[i][j] = original.periodAssignment[i][j];
			}
		}

		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				ArrayList<Integer> originalRoute = original.routes.get(period).get(vehicle);
				ArrayList<Integer> thisRoute = routes.get(period).get(vehicle);
				thisRoute.clear();
				
				for( i=0;i<originalRoute.size();i++)
				{
					thisRoute.add(originalRoute.get(i).intValue());
				}
			}
		}

		cost = original.cost;
		costWithPenalty = original.costWithPenalty;
		
		
		hallOfShamePC = new LinkedList<PeriodClientPair>();

		for ( i=0;i<original.hallOfShamePC.size();i++) 
		{
			PeriodClientPair pair = original.hallOfShamePC.get(i);
			hallOfShamePC.addLast(new PeriodClientPair(pair.period, pair.client));
		}

	}

	/**
	 * Calculates cost and penalty of every individual
	 * For route time violation travelling times are not considered
	 * route time violation = maximum duration of a route - Sum of service time
	 */
	public void calculateCostAndPenalty()
	{
		double tempCost = 0;

		totalLoadViolation = 0;
		totalRouteTimeViolation = 0;
        
		//double temlLoad;
		for(int i=0;i<problemInstance.periodCount;i++)
		{
			for(int j=0;j<problemInstance.vehicleCount;j++)
			{
				costPerRoute[i][j] = calculateCost(i,j);
				tempCost += costPerRoute[i][j];
                //calculate the total load violation
                //Add only when actually the load is violated i.e. violation is positive
                if(loadViolation[i][j]>0) totalLoadViolation += loadViolation[i][j];
                if(routeTimeViolation[i][j]>0) totalRouteTimeViolation += routeTimeViolation[i][j];
			}
		}
		
		cost = tempCost;

		if(totalLoadViolation>0  || totalRouteTimeViolation > 0)
		{
			isFeasible = false;
		}
		else isFeasible = true;

		feasibilitySet = true;
		
	} 


	/**
	 * Calculate cost for specified period, vehicle pair
	 * <br/> Updates loadViolation and RouteTimeViolation Matrices
	 * @param period
	 * @param vehicle
	 * @return
	 */
	public double calculateCost(int period,int vehicle)
	{
		int assignedDepot;		
		int clientNode,previous;

		ArrayList<Integer> route = routes.get(period).get(vehicle);
		assignedDepot = problemInstance.depotAllocation[vehicle];
        
		if(route.isEmpty())
		{
			loadViolation[period][vehicle] = 0 - problemInstance.loadCapacity[vehicle];
	        routeTime[period][vehicle] = 0;
	        
			double thisRouteTimeViolation = 0;
			if(problemInstance.timeConstraintsOfVehicles[period][vehicle] != 0)
			{
				//System.out.println(problemInstance.timeConstraintsOfVehicles[period][vehicle]);
				thisRouteTimeViolation = routeTime[period][vehicle] - problemInstance.timeConstraintsOfVehicles[period][vehicle] ;
			}
			routeTimeViolation[period][vehicle] = thisRouteTimeViolation;
				
			return 0;
		}

		double costForPV = 0;
		double clientDemand=0;
		
		//First client er service time
		double totalRouteTime = problemInstance.serviceTime[route.get(0)];
		clientDemand = problemInstance.demand[route.get(0)];
		for(int i=1;i<route.size();i++)
		{
			clientNode = route.get(i);
			previous = route.get(i-1);
			
			if(periodAssignment[period][clientNode]==false) System.out.println("NEVER SHOULD HAPPEN!!!!! THIS CLIENT IS NOT PRESENT IN THIS PERIOD");
			
			costForPV +=	problemInstance.costMatrix[previous+problemInstance.depotCount][clientNode+problemInstance.depotCount];

			totalRouteTime += problemInstance.serviceTime[clientNode]; //adding service time for that node
            clientDemand += problemInstance.demand[clientNode];        //Caluculate total client demand for corresponding period,vehicle
			
            //dont need this , will add cost afterwards to sum of service times
			//ignoring travelling time for now - for cordeau MDVRP
//			totalRouteTime += problemInstance.costMatrix[previous+problemInstance.depotCount][clientNode+problemInstance.depotCount];

		}

        costForPV += problemInstance.costMatrix[assignedDepot][route.get(0)+problemInstance.depotCount];
        costForPV += problemInstance.costMatrix[route.get(route.size()-1)+problemInstance.depotCount][assignedDepot];
        
        
        totalRouteTime += costForPV; // totalRouteTime = totalServiceTime + total distance
        
        loadViolation[period][vehicle] = clientDemand - problemInstance.loadCapacity[vehicle];
        routeTime[period][vehicle] = totalRouteTime;
        
		double thisRouteTimeViolation = 0;
		
//		if(Double.compare(0.0, problemInstance.timeConstraintsOfVehicles[period][vehicle])!=0)
		if(problemInstance.timeConstraintsOfVehicles[period][vehicle] != 0)
		{
			//System.out.println(problemInstance.timeConstraintsOfVehicles[period][vehicle]);
			thisRouteTimeViolation = routeTime[period][vehicle] - problemInstance.timeConstraintsOfVehicles[period][vehicle] ;
		}
		routeTimeViolation[period][vehicle] = thisRouteTimeViolation;

		return costForPV;
	}
	
	
	public double calculateCostWithPenalty(int period,int vehicle,  double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		double cost = calculateCost(period, vehicle);
		
		double penalty = Math.max(0, routeTimeViolation[period][vehicle]) * routeTimePenaltyFactor ;
		penalty += Math.max(0, loadViolation[period][vehicle]) * loadPenaltyFactor;
		
		
		
		/*if(true)
		{
			System.out.println("cost: "+ cost
					+ " routeTimeViol "+routeTimeViolation[period][vehicle]
							+" loadViol "+loadViolation[period][vehicle]
									+" penalty"+penalty);
		}*/
		
		return cost+penalty;
		
	}
	
	
	public static double calculateCostOfRouteWithDepotAsANode(ArrayList<Integer> route, int assignedDepot)
	{
		int clientNode,previous;
		int DEPOT = problemInstance.customerCount;
        
		if(route.isEmpty())return 0;

		double costForThisRoute = 0;
		
		route.add(route.get(0));
		
		//System.out.println("YOU KNOW WHERE I AM: "+route);
		for(int i=1;i<route.size();i++)
		{
			clientNode = route.get(i);
			previous = route.get(i-1);
			
			if(clientNode == DEPOT)
				costForThisRoute += problemInstance.costMatrix[previous+problemInstance.depotCount][assignedDepot];
			else if(previous == DEPOT)
				costForThisRoute +=	problemInstance.costMatrix[assignedDepot][clientNode+problemInstance.depotCount];
			else
				costForThisRoute +=	problemInstance.costMatrix[previous+problemInstance.depotCount][clientNode+problemInstance.depotCount];

		}
		route.remove(route.size()-1);

		return costForThisRoute;
	}
	public void print()
	{
		//if(problemInstance == null) System.out.println("OUT IS NULL");
		PrintWriter out = this.problemInstance.getPrintWriter();
		int i,j;
		
		out.println("PERIOD ASSIGMENT : ");
		for( j=0;j<problemInstance.customerCount;j++)
		{
			out.printf("%3d ",j);	
		}
		out.println();
		
		for( i=0;i<problemInstance.periodCount;i++)
		{
			for( j=0;j<problemInstance.customerCount;j++)
			{
				if(periodAssignment[i][j])	out.printf("  1 ");
				else out.print("  0 ");
			}
			out.println();
		}
		
		/*out.println("VISIT COMBINATION :");
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			int comb = visitCombination[client];
			int[] bitArray = problemInstance.toBinaryArray(comb);
			
			out.println("Client "+client+" comb : "+comb+" "+ Arrays.toString(bitArray));
		}*/

		out.print("Routes : \n");
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				out.print("< ");
				ArrayList<Integer> route = routes.get(period).get(vehicle);
				for(int clientIndex=0;clientIndex<route.size();clientIndex++)
				{
						out.print(route.get(clientIndex)+" ");
				}
				out.print("> ");
			}
			out.println();
		}

		
		out.println("Vehicle Reassignment Penalty Matrix");
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			out.format("%4d",client);	
		}
		out.println();

		/*for(int period=0;period<problemInstance.periodCount;period++)
		{
			
			for(int client=0;client<problemInstance.customerCount;client++)
			{
				out.format("%4d",vehicleReassignmentApplied[period][client]);	
			}
			out.println();
		}*/
	

        // print load violation
		
		out.print("LOAD VIOLATION MATRIX : \n");
        for( i=0;i<problemInstance.periodCount;i++)
        {
            for( j=0;j<problemInstance.vehicleCount;j++)
            {
            	if(loadViolation[i][j]>0)
            	{
            		out.print("<<<<<< "+loadViolation[i][j]+" >>>>>> ");
            	}
            	else
            		out.print(loadViolation[i][j]+" ");
            }
            out.println();
        }
        
        out.print("Route time violation matrix : \n");
        for( i=0;i<problemInstance.periodCount;i++)
        {
            for( j=0;j<problemInstance.vehicleCount;j++)
            {
            	if(routeTimeViolation[i][j]>0)
            	{
            		out.print("<<<<<< "+routeTimeViolation[i][j]+" >>>>>> ");
            	}
            	else
            		out.print(routeTimeViolation[i][j]+" ");
            }
            out.println();
        }
        
        
        out.print("Route Time MATRIX : \n");
        for( i=0;i<problemInstance.periodCount;i++)
        {
            for( j=0;j<problemInstance.vehicleCount;j++)
            {
            	
            	out.print(routeTime[i][j]+" ");
            }
            out.println();
        }
        
        out.print("Cost Matrix : \n");
        for( i=0;i<problemInstance.periodCount;i++)
        {
            for( j=0;j<problemInstance.vehicleCount;j++)
            {
            	
            	out.print(costPerRoute[i][j]+" ");
            }
            out.println();
        }
        
        
        out.println("Is Feasible : "+isFeasible);
        out.println("Total Load Violation : "+totalLoadViolation);    
        //out.println("Total Route Time: "+totalRouteTimeVi)
        out.println("Total route time violation : "+totalRouteTimeViolation);		
		out.println("Cost : " + cost);
		out.println("Cost with penalty : "+costWithPenalty);
		out.println();
		
	}
	

	public void consolePrint()
	{
		//if(problemInstance == null) System.out.println("OUT IS NULL");
		PrintWriter out = new PrintWriter(System.out, true);
		int i,j;
		
		out.println("PERIOD ASSIGMENT : ");
		for( j=0;j<problemInstance.customerCount;j++)
		{
			out.printf("%3d ",j);	
		}
		out.println();
		
		for( i=0;i<problemInstance.periodCount;i++)
		{
			for( j=0;j<problemInstance.customerCount;j++)
			{
				if(periodAssignment[i][j])	out.printf("  1 ");
				else out.print("  0 ");
			}
			out.println();
		}
		
		/*out.println("VISIT COMBINATION :");
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			int comb = visitCombination[client];
			int[] bitArray = problemInstance.toBinaryArray(comb);
			
			out.println("Client "+client+" comb : "+comb+" "+ Arrays.toString(bitArray));
		}*/

		out.print("Routes : \n");
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				out.print("< ");
				ArrayList<Integer> route = routes.get(period).get(vehicle);
				for(int clientIndex=0;clientIndex<route.size();clientIndex++)
				{
						out.print(route.get(clientIndex)+" ");
				}
				out.print("> ");
			}
			out.println();
		}

		
/*		out.println("Vehicle Reassignment Penalty Matrix");
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			out.format("%4d",client);	
		}
		out.println();
*/
		/*for(int period=0;period<problemInstance.periodCount;period++)
		{
			
			for(int client=0;client<problemInstance.customerCount;client++)
			{
				out.format("%4d",vehicleReassignmentApplied[period][client]);	
			}
			out.println();
		}*/
	

        // print load violation
		
/*		out.print("LOAD VIOLATION MATRIX : \n");
        for( i=0;i<problemInstance.periodCount;i++)
        {
            for( j=0;j<problemInstance.vehicleCount;j++)
            {
            	if(loadViolation[i][j]>0)
            	{
            		out.print("<<<<<< "+loadViolation[i][j]+" >>>>>> ");
            	}
            	else
            		out.print(loadViolation[i][j]+" ");
            }
            out.println();
        }
        
        out.print("Route time violation matrix : \n");
        for( i=0;i<problemInstance.periodCount;i++)
        {
            for( j=0;j<problemInstance.vehicleCount;j++)
            {
            	if(routeTimeViolation[i][j]>0)
            	{
            		out.print("<<<<<< "+routeTimeViolation[i][j]+" >>>>>> ");
            	}
            	else
            		out.print(routeTimeViolation[i][j]+" ");
            }
            out.println();
        }
        
        
        out.print("Route Time MATRIX : \n");
        for( i=0;i<problemInstance.periodCount;i++)
        {
            for( j=0;j<problemInstance.vehicleCount;j++)
            {
            	
            	out.print(routeTime[i][j]+" ");
            }
            out.println();
        }
        
        out.print("Cost Matrix : \n");
        for( i=0;i<problemInstance.periodCount;i++)
        {
            for( j=0;j<problemInstance.vehicleCount;j++)
            {
            	
            	out.print(costPerRoute[i][j]+" ");
            }
            out.println();
        }
        
*/        
        out.println("Is Feasible : "+isFeasible);
        out.println("Total Load Violation : "+totalLoadViolation);    
        //out.println("Total Route Time: "+totalRouteTimeVi)
        out.println("Total route time violation : "+totalRouteTimeViolation);		
		out.println("Cost : " + cost);
		out.println("Cost with penalty : "+costWithPenalty);
		out.println();
		
	}

	
	public void miniPrint()
	{
		PrintWriter out = this.problemInstance.getPrintWriter();
		int i,j;
		
		out.println("PERIOD ASSIGMENT : ");
		for( i=0;i<problemInstance.periodCount;i++)
		{
			for( j=0;j<problemInstance.customerCount;j++)
			{
				out.print("( "+ j+" -> ");
				if(periodAssignment[i][j])	out.print("1 )");
				else out.print("0 )");
				
			}
			out.println();
		}
		
		out.print("Routes : \n");
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				out.print("< ");
				ArrayList<Integer> route = routes.get(period).get(vehicle);
				for(int clientIndex=0;clientIndex<route.size();clientIndex++)
				{
						out.print(route.get(clientIndex)+" ");
				}
				out.print("> ");
			}
			out.println();
		}


		/*


        // print load violation
        out.println("Is Feasible : "+isFeasible);
        out.println("Total Load Violation : "+totalLoadViolation);        
        out.println("Total route time violation : "+totalRouteTimeViolation);		
		out.println("Cost : " + cost);
		out.println("Cost with penalty : "+costWithPenalty);
		out.println("\n");
		*/
	}
	

	
	public boolean validationTest()
	{
		
		//PrintWriter out = new PrintWriter(System.out);
		PrintStream out = System.out; 
		// 1. All client match their frequency 
		// 2. All client only served once in a period
		// 3. Any others ?????
		
		boolean result = true;
		boolean print = true;
		// CHECKING IF FREQUENCY RESTRICTION IS MET OR NOT
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			int freq=0;

			for(int period=0; period<problemInstance.periodCount;period++)
			{
				if(RouteUtilities.doesRouteContainThisClient(problemInstance, this, period, client))	freq++;
			}
			
			if(problemInstance.frequencyAllocation[client] != freq) 
			{
				if(print)out.println("Client Frequency Mismatch - Client : "+client+" existing frequency : "+freq+" original freq : "+problemInstance.frequencyAllocation[client]);
				result = false;
			}
		}
		
		// CHECKING IF FREQUENCY COMBINATION IS MET OR NOT
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			int comb = visitCombination[client];
			int[] bitCOmb = problemInstance.toBinaryArray(comb);
			
			for(int period=0;period<problemInstance.periodCount;period++)
			{
				if( (bitCOmb[period]==1 && periodAssignment[period][client]==false) ||
						(bitCOmb[period]==0 && periodAssignment[period][client]==true) )
				{
					if(print)out.println("Client Frequency Pattern Mismatch - Client : "+client+" period "+period+" Comb : "+comb);
					result = false;
				}				
			}
		}
		

		
		// 2. All client only served once in a period
		for(int client=0;client<problemInstance.customerCount;client++)
		{
			for(int period=0; period<problemInstance.periodCount;period++)
			{
				boolean present = periodAssignment[period][client];
				int count = numberOfTimesClientGetsServedInAPeriod(problemInstance, this, period, client);
				if(present== true)
				{
					if(count != 1)
					{
						out.println("Client "+ client+" period "+period+" Present Error!! Should be present once , but present "+count+" times");
						
						result = false;
					}
					
				}
				else
				{
					if(count != 0)
					{
						out.println("Client "+ client+" period "+period+" Present Error!! Should  not be present , but present "+count+" times");
						result =  false;
					}
				}
			}	
		}

		return result;
	}
	
	public double calculateCostWithPenalty(double cost, double loadViolation, double routeTimeViolation,  double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		
		double penalty = Math.max(0, routeTimeViolation) * routeTimePenaltyFactor ;
		penalty += Math.max(0, loadViolation) * loadPenaltyFactor;
				
		return cost+penalty;
		
	}
	/**
	 * Checks if how many times the client is present in any route for the specified period
	 * @param problemInstance
	 * @param individual
	 * @param period
	 * @param client
	 * @return 0 if client is not present in some route <br/> 
	 * 1 if client is present exactly once in that period <br/> 
	 * 2 if client is present more than once in that period
	 */
	private static int numberOfTimesClientGetsServedInAPeriod(ProblemInstance problemInstance, Individual individual, int period, int client)
	{
		int count=0;
		for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
		{
			int first = individual.routes.get(period).get(vehicle).indexOf(client);
			int last = individual.routes.get(period).get(vehicle).lastIndexOf(client);
			
			if(first != -1)
			{
				if(first==last) count++;
				else count+=2;
			}
		}	
		return count;
	}


	public static boolean isDuplicate(ProblemInstance problemInstance,Individual i1, Individual i2)
	{
		for(int period=0; period<problemInstance.periodCount; period++)
		{
			for(int vehicle=0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				ArrayList<Integer> route1 = i1.routes.get(period).get(vehicle);
				ArrayList<Integer> route2 = i2.routes.get(period).get(vehicle);
				
				if(route1.size() != route2.size()) return false;
				for(int i=0;i<route1.size();i++)
				{
					if(route1.get(i)!= route2.get(i)) return false;
				}
			}
		}
		
		return true;
	}
	
	public void addToHOS_PC(int period, int client)
	{
		PeriodClientPair pc = new PeriodClientPair(period, client);
		
		hallOfShamePC.addFirst(pc);
		
		while(hallOfShamePC.size()>Solver.HallOfShamePCSize)
			hallOfShamePC.removeLast();
	}
	
	public boolean isInHallOfShamePC(int period, int client) 
	{
		for(PeriodClientPair pair:hallOfShamePC)
		{
			if(pair.client==period && pair.client==client) return true;
		}
		return false;
	}
	
	public void printHOS_PC() 
	{
		System.out.print("Hall Of Shame PC, Size"+ hallOfShamePC.size()+" : ");
		for (int i=0;i<hallOfShamePC.size();i++) 
		{
			PeriodClientPair pair = hallOfShamePC.get(i);
			System.out.print(pair);
		}
		System.out.println();

	}
	
}
