package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.lang.model.element.NestingKind;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class Intra_Or_Opt {

	//mdpvrp pr10 -> 10865
	
	public static int apply = 0;	
	public static double totalSec=0;

	public static void mutateRandomRoute(Individual individual)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
		int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			
		mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, vehicle);
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		

	}
	
/*	public static void onAllROute(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
	//	mutateRouteBy2_Opt(individual, 0, 1);if(true)return;
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle = 0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				mutateRouteBy_Or_Opt_withBestMove(individual, period, vehicle);
			}
		}
	}
*/	
/*	public static boolean mutateRouteBy_Or_Opt_withBestMove(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		int routeSize = individual.routes.get(period).get(vehicle).size();
		int k = 3;
		for(k=3;k>=1;k--)
		{			
			
			route = individual.routes.get(period).get(vehicle);
			ArrayList<Integer> bestRoute=route;
			double oldCost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
			double bestCost=oldCost;
			
			int startNode=0;
			for(startNode=0; startNode+k-1< routeSize; startNode++)
			{
				
				ArrayList<Integer> routeAfterCut = new ArrayList<>(route);
				ArrayList<Integer> cutPortion = new ArrayList<Integer>();
				
				for(int i=0;i<k;i++)
				{
					cutPortion.add(routeAfterCut.remove(startNode));
				}
				
				for(int insertIndex=0;insertIndex<=routeAfterCut.size();insertIndex++)
				{
					ArrayList<Integer> modifiedRoute = new ArrayList<Integer>(routeAfterCut);
					modifiedRoute.addAll(insertIndex, cutPortion);
					
					double newCost = RouteUtilities.costForThisRoute(problemInstance, modifiedRoute, vehicle);
					if(newCost<bestCost)
					{
						bestCost= newCost;
						bestRoute = modifiedRoute;
					}
					else if(newCost == bestCost)
					{
						int coin = Utility.randomIntInclusive(1);
						if(coin==1) bestRoute = modifiedRoute;
					}					
				}
			}
			
			if(bestRoute != route)
			{
				route.clear();
				route.addAll(bestRoute);
			}
		}
		
		return true;
	}
*/
	/**
	 * 
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
/*	public static boolean mutateRouteBy_Or_Opt_withFirstBetterMove(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		int routeSize = individual.routes.get(period).get(vehicle).size();
		int k = 3;
		for(k=3;k>=1;k--)
		{
			
			int startNode=0;
			boolean improved = false;
			for(startNode=0; startNode+k-1< routeSize; startNode++)
			{
				if(improved) break;
				
				route = individual.routes.get(period).get(vehicle);
				double oldCost = RouteUtilities.costForThisRoute(problemInstance, route, vehicle);
				
				ArrayList<Integer> routeAfterCut = new ArrayList<>(route);
				ArrayList<Integer> cutPortion = new ArrayList<Integer>();
				
				for(int i=0;i<k;i++)
				{
					cutPortion.add(routeAfterCut.remove(startNode));
				}
				
				System.out.println("Initial : "+route.toString());
				System.out.println("Cost : "+oldCost);
				System.out.println("routeAfterCut : "+routeAfterCut.toString());
				System.out.println("cut portion : "+cutPortion.toString());
				
				for(int insertIndex=0;insertIndex<=routeAfterCut.size();insertIndex++)
				{
					ArrayList<Integer> modifiedRoute = new ArrayList<Integer>(routeAfterCut);
					modifiedRoute.addAll(insertIndex, cutPortion);
					
					double newCost = RouteUtilities.costForThisRoute(problemInstance, modifiedRoute, vehicle);
					if(newCost<oldCost)
					{
						route.clear();
						route.addAll(modifiedRoute);
						improved=true;
						
						System.out.println("Modified Route : "+modifiedRoute.toString());
						System.out.println("Cost : "+newCost);
						break;
					}
				}
			}
		}
		
		return true;
	}
*/

	public static ProblemInstance problemInstance=null;
	public static int assignedDepot;
	/**
	 * 
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(Individual individual, int period, int vehicle)
	{
		boolean print=false;
		problemInstance = individual.problemInstance;
		int DEPOT_NODE = problemInstance.customerCount;
		assignedDepot = problemInstance.depotAllocation[vehicle];
		
		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
		int coin = Utility.randomIntInclusive(1);
		if(coin == 1) Collections.reverse(route);
		
		int routeSize = route.size()+1;
		route.add(DEPOT_NODE);
		
		if(print)System.out.println("route size: "+routeSize);


		for(int k=3;k>=1;k--)
		{
			if(routeSize<=k)continue; 
			
			if(print) System.out.println("k: "+k);
			
			boolean improved = false;
			
			route = individual.routes.get(period).get(vehicle);
			if(print) System.out.println("original: "+route);
			double oldCost = individual.calculateCostOfRouteWithDepotAsANode(route, problemInstance.depotAllocation[vehicle]);
			if(print)System.out.println("cost: "+oldCost);
			
			for(int i=0; i < routeSize; i++)
			{
				
				if(improved) break;
								
				for(int pos=0;pos<routeSize;pos++)
				{
					if( i+k< routeSize && i<=pos && pos <= i+k) continue;
					else if ( i+k>=  routeSize )
						if(pos>=i || pos<= (i+k)%routeSize) continue;
					
					
					int im1 = i-1;
					if(im1<0) im1+=routeSize;
					int ipkm1 = (i+k - 1) % routeSize;
					int ipk = (i+k) % routeSize;					
					int posm1 = (pos-1) ;
					if(posm1<0) posm1+=routeSize;

					//System.out.println("i-1 i+k-1 i+k pos-1");

//					System.out.println(im1+" "+ipkm1+" "+ipk+" "+posm1);
					//System.out.format("old - (%d,%d) - (%d,%d) + (%d,%d) + (%d,%d) + (%d,%d) - (%d,%d)\n",im1,i,ipkm1,ipk,im1,ipk,posm1,i,ipkm1,pos,posm1,pos);
											//im1,i,ipkm1,ipk,im1,ipk,
											// posm1,i,ipkm1,pos,posm1,pos
					double newCost = oldCost;					
					newCost -= cE(route.get(im1),route.get(i)); 
					newCost -= cE(route.get(ipkm1),route.get(ipk)) ;
					newCost += cE(route.get(im1),route.get(ipk));
					
					newCost += cE(route.get(posm1),route.get(i));
					newCost += cE(route.get(ipkm1),route.get(pos));
					newCost -= cE(route.get(posm1),route.get(pos));
					
					
					
					//update route just for testing
					
						// cut (i to i+k-1) 
						// insert before position   (0....pos-1)(i .. i-k+1)(i+k...)


					/*if(print)
						System.out.println("New cost: "+newCost);
					*/
					if(newCost < oldCost)
					{
/*						System.out.println("i: "+i);
						System.out.println("pos: "+pos);
*/

						int NodeAtPos = route.get(pos);
						
						ArrayList<Integer>tmp = new ArrayList<>();
						for(int tm=0;tm<k;tm++)
						{
							if(i<route.size())
								tmp.add(route.remove(i));
							else
								tmp.add(route.remove(0));
						}
						int insIndex= route.indexOf(NodeAtPos);
						int tmpSize = tmp.size()-1;
						while(tmpSize>=0)
						{							
							route.add(insIndex,tmp.remove(tmpSize));
							tmpSize--;
						}
						

					/*	if(print)
						{
							System.out.println("Modifeied route: "+route.toString());
							System.out.println("New cost: "+newCost);
							System.out.println("New cost(BY FUNCTION): "+individual.calculateCostOfRouteWithDepotAsANode(route, problemInstance.depotAllocation[vehicle]));
						}
					*/	
						improved=true;
						break;
					}
					
					newCost = oldCost;					
					newCost -= cE(route.get(im1),route.get(i)); 
					newCost -= cE(route.get(ipkm1),route.get(ipk)) ;
					newCost += cE(route.get(im1),route.get(ipk));
					
					newCost += cE(route.get(posm1),route.get(ipkm1));
					newCost += cE(route.get(i),route.get(pos));
					newCost -= cE(route.get(posm1),route.get(pos));
					
					if(newCost < oldCost)
					{
/*						System.out.println("i: "+i);
						System.out.println("pos: "+pos);
*/

						int NodeAtPos = route.get(pos);
						
						ArrayList<Integer>tmp = new ArrayList<>();
						for(int tm=0;tm<k;tm++)
						{
							if(i<route.size())
								tmp.add(route.remove(i));
							else
								tmp.add(route.remove(0));
						}
						int insIndex= route.indexOf(NodeAtPos);
						int tmpSize = tmp.size()-1;
						while(tmpSize>=0)
						{							
							route.add(insIndex,tmp.remove(0));
							tmpSize--;
						}
						

						if(print)
						{
							System.out.println("Modifeied route (Reverse): "+route.toString());
							System.out.println("New cost: "+newCost);
							System.out.println("New cost(BY FUNCTION): "+individual.calculateCostOfRouteWithDepotAsANode(route, problemInstance.depotAllocation[vehicle]));
						}
						
						improved=true;
						break;
					}


				}
			}
		}
		
		route = individual.routes.get(period).get(vehicle);
		int index = route.indexOf(DEPOT_NODE);
		int size = route.size();
		if(index < size/2)
		{
//			System.out.print("LEFT");
			for(int x=0;x<index;x++)
				route.add(route.remove(0));
			route.remove(0);
		}
		else
		{
//			System.out.print("RIGHT");
			int x;
			for( x=0;x<(size-index-1);x++)
			{
				int client=route.remove(size-1);
				route.add(0,client);
			}
			route.remove(size-1);
		}
		if(print)
			System.out.println("RESULTANT ROUTE: "+individual.routes.get(period).get(vehicle)+"\n\n\n");
		return true;
	}
	
/*
	public static boolean mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(ArrayList<Integer> route, int assignedDepot)
	{
		boolean print=false;
		
		int DEPOT_NODE = problemInstance.customerCount;
		
		int coin = Utility.randomIntInclusive(1);
		if(coin == 1) Collections.reverse(route);
		
		int routeSize = route.size()+1;
		route.add(DEPOT_NODE);
		
		if(print)System.out.println("route size: "+routeSize);


		for(int k=3;k>=1;k--)
		{
			if(routeSize<=k)continue; 
			
			if(print) System.out.println("k: "+k);
			
			boolean improved = false;
			
			route = individual.routes.get(period).get(vehicle);
			if(print) System.out.println("original: "+route);
			double oldCost = individual.calculateCostOfRouteWithDepotAsANode(route, problemInstance.depotAllocation[vehicle]);
			if(print)System.out.println("cost: "+oldCost);
			
			for(int i=0; i < routeSize; i++)
			{
				
				if(improved) break;
								
				for(int pos=0;pos<routeSize;pos++)
				{
					if( i+k< routeSize && i<=pos && pos <= i+k) continue;
					else if ( i+k>=  routeSize )
						if(pos>=i || pos<= (i+k)%routeSize) continue;
					
					
					int im1 = i-1;
					if(im1<0) im1+=routeSize;
					int ipkm1 = (i+k - 1) % routeSize;
					int ipk = (i+k) % routeSize;					
					int posm1 = (pos-1) ;
					if(posm1<0) posm1+=routeSize;

					//System.out.println("i-1 i+k-1 i+k pos-1");

//					System.out.println(im1+" "+ipkm1+" "+ipk+" "+posm1);
					//System.out.format("old - (%d,%d) - (%d,%d) + (%d,%d) + (%d,%d) + (%d,%d) - (%d,%d)\n",im1,i,ipkm1,ipk,im1,ipk,posm1,i,ipkm1,pos,posm1,pos);
											//im1,i,ipkm1,ipk,im1,ipk,
											// posm1,i,ipkm1,pos,posm1,pos
					double newCost = oldCost;					
					newCost -= cE(route.get(im1),route.get(i)); 
					newCost -= cE(route.get(ipkm1),route.get(ipk)) ;
					newCost += cE(route.get(im1),route.get(ipk));
					
					newCost += cE(route.get(posm1),route.get(i));
					newCost += cE(route.get(ipkm1),route.get(pos));
					newCost -= cE(route.get(posm1),route.get(pos));
					
					
					
					//update route just for testing
					
						// cut (i to i+k-1) 
						// insert before position   (0....pos-1)(i .. i-k+1)(i+k...)


					if(print)
						System.out.println("New cost: "+newCost);
					
					if(newCost < oldCost)
					{
						System.out.println("i: "+i);
						System.out.println("pos: "+pos);


						int NodeAtPos = route.get(pos);
						
						ArrayList<Integer>tmp = new ArrayList<>();
						for(int tm=0;tm<k;tm++)
						{
							if(i<route.size())
								tmp.add(route.remove(i));
							else
								tmp.add(route.remove(0));
						}
						int insIndex= route.indexOf(NodeAtPos);
						int tmpSize = tmp.size()-1;
						while(tmpSize>=0)
						{							
							route.add(insIndex,tmp.remove(tmpSize));
							tmpSize--;
						}
						

						if(print)
						{
							System.out.println("Modifeied route: "+route.toString());
							System.out.println("New cost: "+newCost);
							System.out.println("New cost(BY FUNCTION): "+individual.calculateCostOfRouteWithDepotAsANode(route, problemInstance.depotAllocation[vehicle]));
						}
						
						improved=true;
						break;
					}
					
					newCost = oldCost;					
					newCost -= cE(route.get(im1),route.get(i)); 
					newCost -= cE(route.get(ipkm1),route.get(ipk)) ;
					newCost += cE(route.get(im1),route.get(ipk));
					
					newCost += cE(route.get(posm1),route.get(ipkm1));
					newCost += cE(route.get(i),route.get(pos));
					newCost -= cE(route.get(posm1),route.get(pos));
					
					if(newCost < oldCost)
					{
						System.out.println("i: "+i);
						System.out.println("pos: "+pos);


						int NodeAtPos = route.get(pos);
						
						ArrayList<Integer>tmp = new ArrayList<>();
						for(int tm=0;tm<k;tm++)
						{
							if(i<route.size())
								tmp.add(route.remove(i));
							else
								tmp.add(route.remove(0));
						}
						int insIndex= route.indexOf(NodeAtPos);
						int tmpSize = tmp.size()-1;
						while(tmpSize>=0)
						{							
							route.add(insIndex,tmp.remove(0));
							tmpSize--;
						}
						

						if(print)
						{
							System.out.println("Modifeied route (Reverse): "+route.toString());
							System.out.println("New cost: "+newCost);
							System.out.println("New cost(BY FUNCTION): "+individual.calculateCostOfRouteWithDepotAsANode(route, problemInstance.depotAllocation[vehicle]));
						}
						
						improved=true;
						break;
					}


				}
			}
		}
		
		route = individual.routes.get(period).get(vehicle);
		int index = route.indexOf(DEPOT_NODE);
		int size = route.size();
		if(index < size/2)
		{
//			System.out.print("LEFT");
			for(int x=0;x<index;x++)
				route.add(route.remove(0));
			route.remove(0);
		}
		else
		{
//			System.out.print("RIGHT");
			int x;
			for( x=0;x<(size-index-1);x++)
			{
				int client=route.remove(size-1);
				route.add(0,client);
			}
			route.remove(size-1);
		}
		if(print)
			System.out.println("RESULTANT ROUTE: "+individual.routes.get(period).get(vehicle)+"\n\n\n");
		return true;
	}
	*/

	public static double cE(int src, int dest)
	{
		if(src==problemInstance.customerCount)
			return problemInstance.costMatrix[assignedDepot][problemInstance.depotCount+dest];
		else if(dest==problemInstance.customerCount)
			return problemInstance.costMatrix[problemInstance.depotCount+src][assignedDepot];
		else
			return problemInstance.costMatrix[problemInstance.depotCount+src][problemInstance.depotCount+dest];
		//return Double.MAX_VALUE;
	}

}
