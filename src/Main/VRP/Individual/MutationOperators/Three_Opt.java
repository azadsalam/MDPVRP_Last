package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class Three_Opt {

	//mdpvrp pr10 -> 
	
	public static int apply = 0;	
	public static double totalSec=0;
	
	public static void mutateRandomRoute(Individual individual)
	{
//		System.out.println("IN");
		
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
		int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			
		mutateRouteBy_Three_Opt_with_first_better_move_optimized_once(individual, period, vehicle);
			
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
//		System.out.println("OUT");
	}
	
	public static void onAllROute(Individual individual)
	{
	//	System.err.println("in 3 opt");
		ProblemInstance problemInstance = individual.problemInstance;
		
	//	mutateRouteBy2_Opt(individual, 0, 1);if(true)return;
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle = 0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				mutateRouteBy_Three_Opt_with_best_move(individual, period, vehicle);
			}
		}
	}
	
	/**
	 * Repeatedly applies the best 3 opt move, until it gives a lower cost route
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return
	 */
	public static boolean mutateRouteBy_Three_Opt_with_best_move(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
		boolean improved = true;
		while(improved)
		{
			route  = individual.routes.get(period).get(vehicle);
			double oldCost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
			double bestCost = oldCost;
			ArrayList<Integer> bestRoute = route;
			
			for(int i=0;i<route.size();i++) // i th node er previous edge
			{
				for(int j=i ; j<route.size();j++) // j th and k th node er porer edge
				{
					for(int k=j+1;k<route.size();k++)
					{
						double newCost;
						int selected=-1;
						combinations.add(combination0(i, j, k, route));
						combinations.add(combination1(i, j, k, route));
						combinations.add(combination2(i, j, k, route));
						combinations.add(combination3(i, j, k, route));
						
						//System.out.println("\ncurrent "+" cost : "+cost);
						for(int p=0;p<4;p++)
						{
							ArrayList<Integer> newRoute = combinations.get(p);
							newCost = RouteUtilities.costForThisRoute(problemInstance, newRoute, vehicle);
							//System.out.println("comb "+ p+" cost : "+costThis);
							if(newCost<bestCost)
							{
								bestCost=newCost;
								bestRoute = newRoute;
							}
							else if(newCost==bestCost)
							{
			            	   int coin = Utility.randomIntInclusive(1);
			            	   if(coin==1) bestRoute = newRoute;	
							}
						}						
					}
				}
			}
			
			if(bestRoute != route)
			{
				route.clear();
				route.addAll(bestRoute);
			}
			
			if(bestCost == oldCost) improved=false;			
		}		
		return true;
	}


	/**
	 * Improves the selected route by repeated 3 opt moves(the first 3 opt moves that gives a better route)
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy_Three_Opt_with_first_better_move(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
		boolean improved = true;
		while(improved)
		{
			improved = false;
			route  = individual.routes.get(period).get(vehicle);
			double cost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
		//	System.out.println("\n"+route.toString());
			
			for(int i=0;i<route.size();i++) // i th node er previous edge
			{
				if(improved)break;
				for(int j=i ; j<route.size();j++) // j th and k th node er porer edge
				{
					if(improved)break;
					for(int k=j+1;k<route.size();k++)
					{
						double min=cost,costThis;
						int selected=-1;
						combinations.add(combination0(i, j, k, route));
						combinations.add(combination1(i, j, k, route));
						combinations.add(combination2(i, j, k, route));
						combinations.add(combination3(i, j, k, route));
						
						//System.out.println("\ncurrent "+" cost : "+cost);
						for(int p=0;p<4;p++)
						{
							ArrayList<Integer> routeCOmb = combinations.get(p);
							costThis = RouteUtilities.costForThisRoute(problemInstance, routeCOmb, vehicle);
							//System.out.println("comb "+ p+" cost : "+costThis);
							if(costThis<min)
							{
								min = costThis;
								selected = p;
							}
						}
						if(selected != -1)
						{
							ArrayList<Integer> selectedComb = combinations.get(selected);
							//System.out.println("Selected : "+selected);
							route.clear();
							route.addAll(selectedComb);
							improved=true;
							break;
						}
					}
				}
			}
			//for testing only
			
		}		
		return true;
	}

	
	/**returns the edge cost between the edge between (node, node+1)
	<br/>if the edge is non-existing returns 0
	<br/> if node==-1 the edge is between the node and the first customer
	 * 
	 * @param problemInstance
	 * @param route
	 * @param depot
	 * @param node
	 * @return
	 */
	public static double edgeCost(ProblemInstance problemInstance, ArrayList<Integer> route, int depot , int node)
	{
		double[][] cM = problemInstance.costMatrix;
		int dc = problemInstance.depotCount;
		int size = route.size();
		
		if(size==0 || node<-1 || node >= size)
		{
			System.err.println("Invalid Arguement!!! In Three Opt!!!!");
			System.exit(0);
			//return Double.NEGATIVE_INFINITY;
		}
		
		if(node == -1) /// [-1,0] -> depot->0
			return cM[depot][dc+route.get(0)];
		else if(node == size-1)
			return cM[dc+route.get(node)][depot];
		else
			return cM[dc+route.get(node)][dc+route.get(node+1)];
	}
	
	/**
	 * returns the cost between src->dest in the route 
	 * @param problemInstance
	 * @param route
	 * @param depot
	 * @param node
	 * @return
	 */
	public static double distB2in2Nodes(ProblemInstance problemInstance, ArrayList<Integer> route, int depot , int src, int dest)
	{
		int DEPOT = -1;
		double[][] cM = problemInstance.costMatrix;
		int dc = problemInstance.depotCount;
		int size = route.size();
		
		if(size==0 || src<-1 || src > size)
		{
			System.err.println("Invalid Arguement!!! In Three Opt!!!!");
			System.exit(0);
			//return Double.NEGATIVE_INFINITY;
		}
		if(dest<-1 || dest > size)
		{
			System.err.println("Invalid Arguement!!! In Three Opt!!!!");
			System.exit(0);
			//return Double.NEGATIVE_INFINITY;
		}
		
		if(src==-1 || src==size) src = DEPOT;
		if(dest==-1 || dest==size) dest = DEPOT;
		
		
		if(src == DEPOT && dest == DEPOT) 
		{
			System.err.println("Invalid Arguement!!! In Three Opt!!!!");
			System.exit(0);
			return Double.NEGATIVE_INFINITY;
		}
		else if(src == DEPOT)
			return cM[depot][dc+route.get(dest)];
		else if(dest == DEPOT)
			return cM[dc+route.get(src)][depot];
		else
			return cM[dc+route.get(src)][dc+route.get(dest)];
	}

	/**
	 * Improves the selected route by repeated 3 opt moves(the first 3 opt moves that gives a better route)
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return 
 	 */
	public static boolean mutateRouteBy_Three_Opt_with_first_better_move_optimized(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		int depot = problemInstance.depotAllocation[vehicle];
		boolean improved = true;
		
		//System.out.println("Original Route : "+individual.routes.get(period).get(vehicle).toString());

		while(improved)
		{
			improved = false;
			route  = individual.routes.get(period).get(vehicle);
			double cost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
			
			
			//System.out.println("Size : "+route.size());			
			for(int i=0;i<route.size();i++) // i th node er previous edge
			{
				if(improved)break;
				for(int j=i ; j<route.size();j++) // j th and k th node er porer edge
				{
					if(improved)break;
					for(int k=j+1;k<route.size();k++)
					{
						
						//System.out.format("%d %d %d\n",i,j,k);
						double min=cost;
						double costAllComb[]= new double[4];
						int selected=-1;
						
						double costOfNewEdges=0;
						
						double costOfBrokenEdges=0; // [i-1,i] [j,j+1] [k, k+1]
						costOfBrokenEdges += edgeCost(problemInstance, route, depot, i-1);
						costOfBrokenEdges += edgeCost(problemInstance, route, depot, j);
						costOfBrokenEdges += edgeCost(problemInstance, route, depot, k);
						
						
						//combination 0 
						// new edges [i-1,j+1] [k,j] [i , k+1]
						costOfNewEdges = 0;
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i-1, j+1);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, k, j);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i, k+1);
						costAllComb [0] =  cost - costOfBrokenEdges + costOfNewEdges;
						
						//combination 1
						//new [i-1,k], [j+1,i], [j,k+1]
						costOfNewEdges = 0;
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i-1, k);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, j+1, i);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, j, k+1);
						costAllComb [1] =  cost - costOfBrokenEdges + costOfNewEdges;
						
						//combination 2
						//[i-1,j] [i,k] [j+1,k+1]
						costOfNewEdges = 0;
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i-1, j);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i, k);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, j+1, k+1);
						costAllComb [2] =  cost - costOfBrokenEdges + costOfNewEdges;

						//combination 3
						//[i-1,j+1],[k,i],[j,k+1]
						costOfNewEdges = 0;
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i-1, j+1);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, k, i);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, j, k+1);
						costAllComb [3] =  cost - costOfBrokenEdges + costOfNewEdges;


						
						//System.out.println("\ncurrent "+" cost : "+cost);
						for(int p=0;p<4;p++)
						{
//							if(costAllComb[p]<min)
//							//if(min-costAllComb[p] > 0.00001)
							if(costAllComb[p]<min)
							{
								min = costAllComb[p];
								selected = p;
							}
						}
						
						if(selected != -1)
						{
							//System.out.println("Selected: "+selected);
							ArrayList<Integer> modifiedRoute;
							if(selected==0)
								modifiedRoute= combination0(i, j, k, route);
							else if(selected==1)	
								modifiedRoute= combination1(i, j, k, route);
							else if(selected==2)	
								modifiedRoute= combination2(i, j, k, route);
							else 	
								modifiedRoute= combination3(i, j, k, route);
							
							if( cost-min < 0.000001 && RouteUtilities.isDuplicate(route, modifiedRoute))
								continue;
							
/*							System.out.format("\n%d %d %d\n",i,j,k);
							System.out.println("Previous Route : "+individual.routes.get(period).get(vehicle).toString());
							System.out.println("Combinations: \n"+combination0(i, j, k, route)+"\n"+combination1(i, j, k, route)+"\n"+combination2(i, j, k, route)+"\n"+combination3(i, j, k, route));;
							System.out.println("Cost Prev:" + cost);
							System.out.print("Cost(all comb):");
							for(int sn=0;sn<4;sn++)
								System.out.print(" "+costAllComb[sn]);
							System.out.println();*/
/*							route.clear();
							for(int pn=0;pn<modifiedRoute.size();pn++)
								route.add(modifiedRoute.get(pn));
*/							
							
							route.clear();
							route.addAll(modifiedRoute);
							improved=true;
							//System.out.println("MOdified Route : "+individual.routes.get(period).get(vehicle).toString());
							break;
						}
					}
				}
			}
			//for testing only
			
		}		
		return true;
	}
	
	
	/**
	 * Improves the selected route once by a 3 opt moves(the first 3 opt moves that gives a better route)
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy_Three_Opt_with_first_better_move_optimized_once(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		int depot = problemInstance.depotAllocation[vehicle];
		
		//System.out.println("Original Route : "+individual.routes.get(period).get(vehicle).toString());

			route  = individual.routes.get(period).get(vehicle);
			double cost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
			
			
			//System.out.println("Size : "+route.size());			
			for(int i=0;i<route.size();i++) // i th node er previous edge
			{
				for(int j=i ; j<route.size();j++) // j th and k th node er porer edge
				{
					for(int k=j+1;k<route.size();k++)
					{
						
						//System.out.format("%d %d %d\n",i,j,k);
						double min=cost;
						double costAllComb[]= new double[4];
						int selected=-1;
						
						double costOfNewEdges=0;
						
						double costOfBrokenEdges=0; // [i-1,i] [j,j+1] [k, k+1]
						costOfBrokenEdges += edgeCost(problemInstance, route, depot, i-1);
						costOfBrokenEdges += edgeCost(problemInstance, route, depot, j);
						costOfBrokenEdges += edgeCost(problemInstance, route, depot, k);
						
						
						//combination 0 
						// new edges [i-1,j+1] [k,j] [i , k+1]
						costOfNewEdges = 0;
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i-1, j+1);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, k, j);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i, k+1);
						costAllComb [0] =  cost - costOfBrokenEdges + costOfNewEdges;
						
						//combination 1
						//new [i-1,k], [j+1,i], [j,k+1]
						costOfNewEdges = 0;
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i-1, k);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, j+1, i);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, j, k+1);
						costAllComb [1] =  cost - costOfBrokenEdges + costOfNewEdges;
						
						//combination 2
						//[i-1,j] [i,k] [j+1,k+1]
						costOfNewEdges = 0;
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i-1, j);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i, k);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, j+1, k+1);
						costAllComb [2] =  cost - costOfBrokenEdges + costOfNewEdges;

						//combination 3
						//[i-1,j+1],[k,i],[j,k+1]
						costOfNewEdges = 0;
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, i-1, j+1);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, k, i);
						costOfNewEdges += distB2in2Nodes(problemInstance, route, depot, j, k+1);
						costAllComb [3] =  cost - costOfBrokenEdges + costOfNewEdges;


						
						//System.out.println("\ncurrent "+" cost : "+cost);
						for(int p=0;p<4;p++)
						{
//							if(costAllComb[p]<min)
//							//if(min-costAllComb[p] > 0.00001)
							if(costAllComb[p]<min)
							{
								min = costAllComb[p];
								selected = p;
							}
						}
						
						if(selected != -1)
						{
							//System.out.println("Selected: "+selected);
							ArrayList<Integer> modifiedRoute;
							if(selected==0)
								modifiedRoute= combination0(i, j, k, route);
							else if(selected==1)	
								modifiedRoute= combination1(i, j, k, route);
							else if(selected==2)	
								modifiedRoute= combination2(i, j, k, route);
							else 	
								modifiedRoute= combination3(i, j, k, route);
							
							if( cost-min < 0.000001 && RouteUtilities.isDuplicate(route, modifiedRoute))
								continue;
							
/*							System.out.format("\n%d %d %d\n",i,j,k);
							System.out.println("Previous Route : "+individual.routes.get(period).get(vehicle).toString());
							System.out.println("Combinations: \n"+combination0(i, j, k, route)+"\n"+combination1(i, j, k, route)+"\n"+combination2(i, j, k, route)+"\n"+combination3(i, j, k, route));;
							System.out.println("Cost Prev:" + cost);
							System.out.print("Cost(all comb):");
							for(int sn=0;sn<4;sn++)
								System.out.print(" "+costAllComb[sn]);
							System.out.println();*/
/*							route.clear();
							for(int pn=0;pn<modifiedRoute.size();pn++)
								route.add(modifiedRoute.get(pn));
*/							
							
							route.clear();
							route.addAll(modifiedRoute);
							return true;
							//System.out.println("MOdified Route : "+individual.routes.get(period).get(vehicle).toString());
							
						}
					}
				}
			}
		return false;
	}


	/*private static boolean update(ArrayList<Integer> route, double costOfCurrentRoute, ArrayList<Integer> modifiedRoute) 
	{
		
	}*/
	private static ArrayList<Integer> combination0(int i, int j, int k, ArrayList<Integer> route)
	{
		//broken [i-1,i][j,j+1][k,k+1]
		//new [i-1,j+1],[k,j],[i,k+1]
		ArrayList<Integer> modifiedRoute = new ArrayList<Integer>();
		
		for(int index=0;index<i;index++)           // [0,i-1]
			modifiedRoute.add(route.get(index));
		
		for(int index = j+1; index <= k;index++)	// [j+1, k]
			modifiedRoute.add(route.get(index));
		
		for(int index = j; index >= i;index--)	   // reverse [i,j]
			modifiedRoute.add(route.get(index));
	
		for(int index = k+1; index < route.size();index++)	// [k+1, end]
			modifiedRoute.add(route.get(index));

//		System.out.printf("i: %d j: %d k: %d original -> %s \n",i,j,k,route.toString());
//		System.out.printf("i: %d j: %d k: %d Comb 1 -> %s \n",i,j,k,modifiedRoute.toString());
		return modifiedRoute;

	}
	
	private static ArrayList<Integer> combination1(int i, int j, int k, ArrayList<Integer> route)
	{
		//broken [i-1,i][j,j+1][k,k+1]
		//new [i-1,k], [j+1,i], [j,k+1]
		
		ArrayList<Integer> modifiedRoute = new ArrayList<Integer>();
		
		for(int index=0;index<i;index++)   // [0,i-1]
			modifiedRoute.add(route.get(index));
		
		for(int index=k;index>j;index-- )   // reverse(j+1,k)
			modifiedRoute.add(route.get(index));

		for(int index=i;index<=j;index++) // [i,j]
			modifiedRoute.add(route.get(index));

		for(int index = k+1; index < route.size();index++)	//[k+1,end]
			modifiedRoute.add(route.get(index));

//		System.out.printf("i: %d j: %d k: %d original -> %s \n",i,j,k,route.toString());
//		System.out.printf("i: %d j: %d k: %d Comb 2 -> %s \n",i,j,k,modifiedRoute.toString());
		return modifiedRoute;

	}

	private static ArrayList<Integer> combination2(int i, int j, int k, ArrayList<Integer> route)
	{
		//new links
		//[i-1,j] [i,k] [j+1,k+1]
		ArrayList<Integer> modifiedRoute = new ArrayList<Integer>();
		
		for(int index=0;index<i;index++)  //[0,i-1]
			modifiedRoute.add(route.get(index));
		
		for(int index=j;index>=i;index--)  //reverse[i,j]
			modifiedRoute.add(route.get(index));

		for(int index=k;index> j ; index--) //reverse[j+1,k]
			modifiedRoute.add(route.get(index));

		for(int index = k+1; index < route.size();index++)	//[k+1,end]
			modifiedRoute.add(route.get(index));

		//System.out.printf("i: %d j: %d k: %d original -> %s \n",i,j,k,route.toString());
		//System.out.printf("i: %d j: %d k: %d Comb 3 -> %s \n",i,j,k,modifiedRoute.toString());
		return modifiedRoute;

	}

	private static ArrayList<Integer> combination3(int i, int j, int k, ArrayList<Integer> route)
	{
		//new links
		//[i-1,j+1],[k,i],[j,k+1]
		
		ArrayList<Integer> modifiedRoute = new ArrayList<Integer>();
		
		for(int index=0;index<i;index++)  // [0,i-1]
			modifiedRoute.add(route.get(index));
		
		for(int index = j+1;index<=k ; index++) // [j+1,k]
			modifiedRoute.add(route.get(index));
				
		for(int index = i;index<=j;index++) // [i,j]
			modifiedRoute.add(route.get(index));
				
		for(int index = k+1; index < route.size();index++) //[k+1,end]	
			modifiedRoute.add(route.get(index));

	//	System.out.printf("i: %d j: %d k: %d original -> %s \n",i,j,k,route.toString());
	//	System.out.printf("i: %d j: %d k: %d Comb 4 -> %s \n",i,j,k,modifiedRoute.toString());
		return modifiedRoute;

	}
}
