package Main.VRP.Individual.MutationOperators;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class GENI 
{
	public static int CLOSEST_DEPOT=1;
	public static int CLOSEST_DEPOT_WITH_NO_VIOLATION=2;
	
	public static int P = 7; // P of P neighborhood
	public static int DEPOT;
	
	
	private static void assignEachClientToCLosestDepot(ProblemInstance problemInstance,ArrayList[] clientsUnderDepot)
	{
		int assigned=0;		
		boolean[] clientMap = new boolean[problemInstance.customerCount];
		while(assigned<problemInstance.customerCount)
		{
			int clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
			
			if(clientMap[clientNo]) continue;
			clientMap[clientNo]=true;
			assigned++;
			
			int depot = RouteUtilities.closestDepot(clientNo);	
			
			clientsUnderDepot[depot].add(clientNo);
		}
	}
	//period assignment done, create bigroutes for each depot, period pair
	public static void initialiseBigRouteWithClosestClients(Individual individual,int variant)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		DEPOT = problemInstance.customerCount;
		// contains the clients that are closest to this depot. in random order
		ArrayList[] clientsUnderDepot = new ArrayList[problemInstance.depotCount]; 
		for(int depot =0;depot<problemInstance.depotCount;depot++)
		{
			clientsUnderDepot[depot] = new ArrayList();
		}
		
		if(variant == CLOSEST_DEPOT)
			assignEachClientToCLosestDepot(problemInstance,clientsUnderDepot);
		
		//print
/*		for(int depot =0;depot<problemInstance.depotCount;depot++)
		{
			System.out.println("Depot: "+depot + " Closest Clients: "+ clientsUnderDepot[depot]);
		}	
*/		
		//clientsUnderDepot contains all the client, closest to this depot, in random order
		//now create route for every depot for every period
		
		
		
		for(int period = 0; period<problemInstance.periodCount;period++)
		{
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
					
				
				//if(period>0 || depot>0)break;
				
				//GENI ALGO
				//1a. create initial tour with at least 3 vertices
				//1b. initialise p neighbourhood of all vertices
				//loop until all vertices are inserted
				//	2a. take v in random order 
						// for us the random order is already done // in clients under depot arraylist, take serially from it
				// b.try inserting v in clockwise and anticlockwise, with type1 and type2 insertion 
						//4 combination, keep the best one
				// c.update p neighbourhood - add v into their neighbour list in in p neighbourhood
				
			
				ArrayList<Integer> clientsClosestToThisDepot = new ArrayList<Integer>(clientsUnderDepot[depot]);
				
				for(int i=0;i<clientsClosestToThisDepot.size();)
				{
					if(individual.periodAssignment[period][clientsClosestToThisDepot.get(i)])
						i++;
					else
						clientsClosestToThisDepot.remove(i);
				}
				//System.out.println("Depot: "+depot + " Closest Clients: "+ clientsClosestToThisDepot);

				//*********************************//////
				//totka solution
				
				if(clientsClosestToThisDepot.size()<2) 
				{
					ArrayList<Integer> bigRoute = individual.bigRoutes.get(period).get(depot);	
					while(!clientsClosestToThisDepot.isEmpty())
					{
						int node= clientsClosestToThisDepot.get(0);
						bigRoute.add(node);
						clientsClosestToThisDepot.remove(0);
					}
					continue;
				}
				
				//1a. create initial tour with at least 3 vertices , first one being the depot and other two being the first 
				//   and 2 clients from closest client list
				
				ArrayList<Integer> currentRoute = new ArrayList<Integer>();
				
				currentRoute.add(clientsClosestToThisDepot.get(0));
				currentRoute.add(clientsClosestToThisDepot.get(1));				
				clientsClosestToThisDepot.remove(0);
				clientsClosestToThisDepot.remove(0);
				
				//1b. initialise p neighbourhood of all vertices
				ArrayList[] p_neighbourhood = createNeighbourList(currentRoute,depot);
				
				//System.out.println("Initial route with 2 clients: "+currentRoute);
				//printNeighbourList(p_neighbourhood);
				
				//insert all vertices
				//loop until all vertices are inserted
				while(!clientsClosestToThisDepot.isEmpty())
				{
					
					//	2a. take v in random order 
					// for us the random order is already done // in clients under depot arraylist, take serially from it
					int v =  clientsClosestToThisDepot.get(0);
					clientsClosestToThisDepot.remove(0);
					
					//System.out.println("To be inserted: "+v);		
				   
					double minCost = Double.MAX_VALUE;
					ArrayList<Integer> soFarBestInsertion=null;
					
					
					//route created by inserting the client between every two client
					ArrayList<Integer> mciRoute= new ArrayList<Integer>(currentRoute);
					MinimumCostInsertionInfo mciInfo = RouteUtilities.minimumCostInsertionPosition2(problemInstance, depot, v, mciRoute);
					mciRoute.add(mciInfo.insertPosition, v);
					
					mciRoute.add(DEPOT);
					minCost= Individual.calculateCostOfRouteWithDepotAsANode(mciRoute, depot);
					mciRoute.remove(mciRoute.size()-1);
					//update
					soFarBestInsertion = mciRoute;
					
					
					
					//type 1 insert - forward  - clockwise
					RouteWithInfo type1_forward = insert_type1(currentRoute,v,depot,p_neighbourhood); //clockwise					
					if(type1_forward != null)
					{
						if(type1_forward.cost<minCost)
						{
							soFarBestInsertion = type1_forward.route;
							minCost = type1_forward.cost;
						}
					}
					
					//type2 insert here
					RouteWithInfo type2_forward = insert_type2(currentRoute,v,depot,p_neighbourhood); //clockwise					
					if(type2_forward != null)
					{
						if(type2_forward.cost<minCost)
						{
							soFarBestInsertion = type2_forward.route;
							minCost = type2_forward.cost;
						}
						
					}
					
					Collections.reverse(currentRoute);			
										
					//type 1 insert - backward  - anti-clockwise
					RouteWithInfo type1_reverse = insert_type1(currentRoute,v,depot,p_neighbourhood); //anticlockwise
					if(type1_reverse != null)
					{
						if(type1_reverse.cost<minCost)
						{
							soFarBestInsertion = type1_reverse.route;
							minCost = type1_reverse.cost;
						}
					}
					
					RouteWithInfo type2_reverse = insert_type2(currentRoute,v,depot,p_neighbourhood); //clockwise					
					if(type2_reverse != null)
					{
						if(type2_reverse.cost<minCost)
						{
							soFarBestInsertion = type2_reverse.route;
							minCost = type2_reverse.cost;
						}
						
					}
										
					
					
					currentRoute = soFarBestInsertion;
					
					updateNeighbourList(p_neighbourhood, v, depot);
					
				//	printNeighbourList(p_neighbourhood);
					
//					System.out.println("Period: "+period+" Depot: "+depot+" Current Route: "+currentRoute);

				}
						
				ArrayList<Integer> bigRoute = individual.bigRoutes.get(period).get(depot);	
				while(!currentRoute.isEmpty())
				{
					int node= currentRoute.get(0);
					bigRoute.add(node);
					currentRoute.remove(0);
				}
//				System.out.println("Period: "+period+" Depot: "+depot+" Big Route: "+individual.bigRoutes.get(period).get(depot));
				// b.try inserting v in clockwise and anticlockwise, with type1 and type2 insertion 
						//4 combination, keep the best one
				// c.update p neighbourhood - add v into their neighbour list in in p neighbourhood
			
			}
		}
	}
	
	
	static void printNeighbourList(ArrayList[] p_neighbourhood)
	{
		System.out.println("Printing Neighbourhood: ");
		for(int i=0;i<p_neighbourhood.length;i++)
		{
			System.out.println(i+": "+ p_neighbourhood[i]);
		}
	}
	
	static ArrayList[] createNeighbourList(ArrayList<Integer> route, int depot)
	{
		ArrayList[] p_neighbourhood = new ArrayList[Individual.problemInstance.customerCount+1];
		for(int i =0; i<p_neighbourhood.length;i++) p_neighbourhood[i] = new ArrayList<Integer>();
		
		for(int j=0;j<route.size();j++)
		{
			updateNeighbourList(p_neighbourhood, route.get(j),depot);
		}
		updateNeighbourList(p_neighbourhood, DEPOT,depot);

		return p_neighbourhood;
	}
	
	static void updateNeighbourList(ArrayList[] p_neighbourhood, int v,int depot)
	{
		//p_neighborhood[i] -> the set of p nodes, already in tour, closest to i
		
		for(int i=0;i<p_neighbourhood.length;i++)
		{
			//current node -> i, jetar neighborhood update kortesi 
			//node to be inserted v
			
			if(v==i) continue;
			int j=0;
			for(j=0;j<p_neighbourhood[i].size();j++)
			{
				double cost_i__v = costBetweenNodes(i, v,depot);
				double cost_i_j = costBetweenNodes(i, (int)p_neighbourhood[i].get(j),depot);
				if(cost_i__v<cost_i_j)
				{
					p_neighbourhood[i].add(j, v);
					break;
				}
			}
			if( j== p_neighbourhood[i].size() ) p_neighbourhood[i].add( v);
				
			if(p_neighbourhood[i].size()>P)
				p_neighbourhood[i].remove(P);
				
		}
	}
	
	static double costBetweenNodes(int src, int dest, int depot)
	{
		ProblemInstance problemInstance = Individual.problemInstance;
		if(src==DEPOT)
			return problemInstance.costMatrix[depot][dest+problemInstance.depotCount];
		else if(dest==DEPOT)
			return problemInstance.costMatrix[src+problemInstance.depotCount][depot];
		else
			return problemInstance.costMatrix[src+problemInstance.depotCount][dest+problemInstance.depotCount];
	}	
	
	// add depot internally and remove it while retuning the route
	static RouteWithInfo insert_type1(ArrayList<Integer> route, int v,int depot, ArrayList[] p_neighbourhood)
	{
		RouteWithInfo bestRoute = new RouteWithInfo();
		//System.out.println("ON METHOD");

		ArrayList<Integer> inputRoute = new ArrayList<Integer>(route);
		
		//System.out.println("Route Before Insertion: "+inputRoute);
		inputRoute.add(0, DEPOT);

/*		System.out.println("Node to be inserted: "+v);
		System.out.println("Route After Insertion of Depot: "+inputRoute);
*/		
		//printNeighbourList(p_neighbourhood);
		
		for(int i=0;i<inputRoute.size();i++)
		{
			
			if(!p_neighbourhood[v].contains(inputRoute.get(0)) && !p_neighbourhood[inputRoute.get(0)].contains(v) )
			{
				//left rotate once
				/*if(v==188)
					System.out.println("Didnt consider "+inputRoute.get(0)+" -> "+v+" edge, as "+inputRoute.get(0)+" (i) not in p-neigbourhood of "+v);*/
				inputRoute.add(inputRoute.get(0));
				inputRoute.remove(0);
	
				continue;
			}
				
			for(int j=1; j<inputRoute.size();j++)
			{
				//add check for p neighborhood
				
				if(!p_neighbourhood[v].contains(inputRoute.get(j)) && !p_neighbourhood[inputRoute.get(j)].contains(v))
				{
/*					if(v==188)
						System.out.println("Didnt consider "+inputRoute.get(j)+" -> "+v+" edge, as "+inputRoute.get(j)+" (j) not in p-neigbourhood of "+v);
*/		
					continue;
				}				
				for(int k= j+1;k<inputRoute.size();k++)
				{
					int v_i_plus_1 = inputRoute.get(1);
					int v_k = inputRoute.get(k);
					if(!p_neighbourhood[v_i_plus_1].contains(v_k) && !p_neighbourhood[v_k].contains(v_i_plus_1))
					{
/*						if(v==188)
							System.out.println("Didnt consider "+v_i_plus_1+" -> "+v_k+" edge, as "+v_k+" (j) not in p-neigbourhood of "+v_i_plus_1);
*/
						continue;
					}
					
					//check  K for p neighborhood of i+1
					ArrayList<Integer> newRoute = new ArrayList<>();
					

					newRoute.add(inputRoute.get(0));		//append  v[0]
					newRoute.add(v);						//append v - the node to be inserted
					for(int tmp=j; tmp>= 1;tmp--)		//append from v_J to v_(i+1)
					{
						newRoute.add(inputRoute.get(tmp));
					}
					for(int tmp=k;tmp>=(j+1);tmp--)		//append vk to v(J+1)
					{
						newRoute.add(inputRoute.get(tmp));
					}
					
					for(int tmp=k+1;tmp<inputRoute.size();tmp++)//append v(k+1) to end
					{
						newRoute.add(inputRoute.get(tmp));
					}
					
					
					//System.out.print("i: "+i+" j: "+j+" k: "+ k+ " Route After Insertion: "+newRoute+" ");
					//calculate for cost, update best route in cost less
					double thisCost = Individual.calculateCostOfRouteWithDepotAsANode(newRoute, depot);
					//System.out.println("Cost: "+thisCost);
					
					
					if( thisCost< bestRoute.cost)
					{
						
						bestRoute.route = newRoute;
						bestRoute.cost = thisCost;
					}
					
				}	
			}
			
			//left rotate once
			inputRoute.add(inputRoute.get(0));
			inputRoute.remove(0);
		}
		
		//remove depot node from best route
		if(bestRoute.route==null)
		{
			System.out.println("Could not perform GENI Insertion Type 1, P= "+P);
			return null;
		}
		bestRoute.route.remove(new Integer(DEPOT));
		return bestRoute;
	
	}


	// add depot internally and remove it while retuning the route
	static RouteWithInfo insert_type2(ArrayList<Integer> route, int v,int depot, ArrayList[] p_neighbourhood)
	{
		RouteWithInfo bestRoute = new RouteWithInfo();
		ArrayList<Integer> inputRoute = new ArrayList<Integer>(route);
		
		inputRoute.add(0, DEPOT);

		//System.out.println("Node to be inserted: "+v);
		//System.out.println("Route After Insertion of Depot: "+inputRoute);
		
		//printNeighbourList(p_neighbourhood);
		
		for(int i=0;i<inputRoute.size();i++)
		{
			
			if(!p_neighbourhood[v].contains(inputRoute.get(0)) && !p_neighbourhood[inputRoute.get(0)].contains(v) )
			{
				inputRoute.add(inputRoute.get(0));
				inputRoute.remove(0);	
				continue;
			}
				
			for(int l=2;l<inputRoute.size();l++)
			{
				for(int j=l; j<inputRoute.size();j++)
				{
					//add check for p neighborhood
					
					if(!p_neighbourhood[v].contains(inputRoute.get(j)) && !p_neighbourhood[inputRoute.get(j)].contains(v))
					{
						continue;
					}
					
					
					if(inputRoute.size()>=(j+1))
						continue;
					int v_j_plus_1 = inputRoute.get(j+1);
					int v_l = inputRoute.get(l);
					if(!p_neighbourhood[v_j_plus_1].contains(v_l) && !p_neighbourhood[v_l].contains(v_j_plus_1))
					{
						continue;
					}
				
					
					for(int k= j+2;k<inputRoute.size();k++)
					{
						//check  K for p neighborhood of i+1
						int v_i_plus_1 = inputRoute.get(1);
						int v_k = inputRoute.get(k);
						if(!p_neighbourhood[v_i_plus_1].contains(v_k) && !p_neighbourhood[v_k].contains(v_i_plus_1))
						{
							continue;
						}
						
						ArrayList<Integer> newRoute = new ArrayList<>();
						
						newRoute.add(inputRoute.get(0));		//append  v[0]
						newRoute.add(v);						//append v - the node to be inserted
						for(int tmp=j;tmp>=l;tmp--)				// j -> l
						{
							newRoute.add(inputRoute.get(tmp));
						}				
						for(int tmp=j+1;tmp<=(k-1);tmp++)		// j+1 -> k-1
						{
							newRoute.add(inputRoute.get(tmp));							
						}
						for(int tmp=l-1;tmp>=1;tmp--)			//l-1 -> i+1
						{
							newRoute.add(inputRoute.get(tmp));
						}
						for(int tmp=k;tmp<inputRoute.size();tmp++)	//k -> last
						{
							newRoute.add(inputRoute.get(tmp));
						}
						
						
						if(v==15)
							System.out.print("i: "+i+" L: "+l+" j: "+j+" k: "+ k+ " Route After Insertion: "+newRoute+" ");
						double thisCost = Individual.calculateCostOfRouteWithDepotAsANode(newRoute, depot);
						if(v==15) System.out.println("Cost: "+thisCost);
						if( thisCost< bestRoute.cost)
						{
							
							bestRoute.route = newRoute;
							bestRoute.cost = thisCost;
						}	
					}	
				}
			}
			//left rotate once
			inputRoute.add(inputRoute.get(0));
			inputRoute.remove(0);
		}
		
		//remove depot node from best route
		if(bestRoute.route==null)
		{
			//System.out.println("Could not perform GENI Insertion Type 2, P= "+P);
			return null;
		}
		bestRoute.route.remove(new Integer(DEPOT));
		return bestRoute;
	
	}

	

}

class RouteWithInfo
{
	ArrayList<Integer> route;
	double cost= Double.MAX_VALUE;
}