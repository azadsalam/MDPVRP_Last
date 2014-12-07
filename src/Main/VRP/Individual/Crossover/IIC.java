package Main.VRP.Individual.Crossover;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;

import javax.print.attribute.IntegerSyntax;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;


public class IIC 
{
	
	/**.
	 * The individuals should have been calculate. for now it explicitly computes both parent cost
	 * @param problemInstance
	 * @param parent1
	 * @param parent2
	 * @param child
	 */
	public static void crossOver(ProblemInstance problemInstance,Individual parent1,Individual parent2,Individual child)
	{
		UniformCrossoverPeriodAssigment.uniformCrossoverForPeriodAssignment(child,parent1, parent2,problemInstance);

		reAlignParentRoutes(parent1, parent2, problemInstance);
		
		TotalCostCalculator.calculateCost(parent1, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		TotalCostCalculator.calculateCost(parent2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
		if(parent1.costWithPenalty < parent2.costWithPenalty)
			crossoverInner(problemInstance,parent1,parent2,child);
		else
			crossoverInner(problemInstance,parent2,parent1,child);
		
		
		//update cost and penalty
		child.calculateCostAndPenalty();
	}

	public static void reAlignParentRoutes(Individual parent1, Individual parent2, ProblemInstance problemInstance)
	{
		//for each depot re assign similar routes to same serial number vehicle
		
		boolean printn=false;

		for(int period=0;period<problemInstance.periodCount;period++)
		{			 
			for(int depot=0;depot<problemInstance.depotCount;depot++)
			{
				if(printn)System.out.println("Period"+period+" Depot: "+depot);
				
				ArrayList<Integer> vehiclesUnderThisDepot = problemInstance.vehiclesUnderThisDepot.get(depot);
				int vehicleUnderThisDepotCount = vehiclesUnderThisDepot.size();
				
				
				if(printn)
				{	
					System.out.println("Before\nParent1");
					for(int j=0;j<vehicleUnderThisDepotCount;j++)
					{
						int vehicle = vehiclesUnderThisDepot.get(j);
						ArrayList<Integer> route = parent1.routes.get(period).get(vehicle);
						System.out.println("Route "+vehicle+ " : "+route);
					}
					
					System.out.println("Parent2");
					for(int j=0;j<vehicleUnderThisDepotCount;j++)
					{
						int vehicle = vehiclesUnderThisDepot.get(j);
						ArrayList<Integer> route = parent2.routes.get(period).get(vehicle);
						System.out.println("Route "+vehicle+ " : "+route);
					}
					System.out.println();					
					
					System.out.println();						System.out.println();						System.out.println();
				
				}
				for(int i=0;i<vehicleUnderThisDepotCount-1;i++)
				{
					int selectedVehicle=-1;
					int maxMatch = -1;
					
					int vehicle1 = vehiclesUnderThisDepot.get(i);
					ArrayList<Integer> route1 = parent1.routes.get(period).get(vehicle1);
					int size1 = route1.size();
					
					if(printn)
					{	
						System.out.println("Vehicle1: "+vehicle1);
						System.out.println("Route1: "+route1);
					}
					
	
					for(int j=i;j<vehicleUnderThisDepotCount;j++)
					{
						int vehicle2 = vehiclesUnderThisDepot.get(j);
						ArrayList<Integer> route2 = parent2.routes.get(period).get(vehicle2);
						int size2 = route2.size();
						
						HashSet<Integer> union = new HashSet<>();
						union.addAll(route1);
						union.addAll(route2);
						
						int common = (size1+size2)-union.size();
						if(common>maxMatch)
						{
							maxMatch=common;
							selectedVehicle = vehicle2;
						}
						
						if(printn)
						{	
							System.out.println("Vehicle2: "+vehicle2);
							System.out.println("Route2: "+route2);
							System.out.println("Matching: "+common);
							System.out.println();
						}
					
							
					}
					
					if(printn)
					{
						System.out.println("Selected Vehicle: "+selectedVehicle);
						System.out.println();
						
						if(i==selectedVehicle)
						{
							System.out.println("\n\nafter\nParent1");
							for(int j=0;j<vehicleUnderThisDepotCount;j++)
							{
								int vehicle = vehiclesUnderThisDepot.get(j);
								ArrayList<Integer> route = parent1.routes.get(period).get(vehicle);
								System.out.println("Route "+vehicle+ " : "+route);
							}
							
							System.out.println("Parent2");
							for(int j=0;j<vehicleUnderThisDepotCount;j++)
							{
								int vehicle = vehiclesUnderThisDepot.get(j);
								ArrayList<Integer> route = parent2.routes.get(period).get(vehicle);
								System.out.println("Route "+vehicle+ " : "+route);
							}
							System.out.println();					
							
							System.out.println();						System.out.println();						System.out.println();
						}
					}
						
					//swap the route in i with route selectedVehicle in parent 2
					if(i==selectedVehicle) continue;
					
					ArrayList<Integer> route21 = parent2.routes.get(period).get(vehiclesUnderThisDepot.get(i));
					ArrayList<Integer> route22 = parent2.routes.get(period).get(selectedVehicle);
					
					ArrayList<Integer> temp = new ArrayList<Integer>(route21);
					
					route21.clear();
					route21.addAll(route22);
					
					route22.clear();
					route22.addAll(temp);
					
					if(printn)
					{	
						System.out.println("\n\nafter\nParent1");
						for(int j=0;j<vehicleUnderThisDepotCount;j++)
						{
							int vehicle = vehiclesUnderThisDepot.get(j);
							ArrayList<Integer> route = parent1.routes.get(period).get(vehicle);
							System.out.println("Route "+vehicle+ " : "+route);
						}
						
						System.out.println("Parent2");
						for(int j=0;j<vehicleUnderThisDepotCount;j++)
						{
							int vehicle = vehiclesUnderThisDepot.get(j);
							ArrayList<Integer> route = parent2.routes.get(period).get(vehicle);
							System.out.println("Route "+vehicle+ " : "+route);
						}
						System.out.println();											
						System.out.println();						
						System.out.println();						
						System.out.println();
					}
				
				}
			}
		}
		
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
			
		//first calculate r1,r2,r3;
		int r1,rShared,r2;
		int vehicleCount= problemInstance.vehicleCount;
		
		int partitions[] = partitionSet(vehicleCount);
	
		r1 = partitions[0];
		rShared = partitions[1];
		r2 = partitions[2];
	
		if(print)
			System.out.format("Partition: %d %d %d, total", r1,rShared,r2,vehicleCount);
		
		//select  three disjoint sets containing r1,rshared and r2 sets
		ArrayList<Integer> v1 = new ArrayList<>();
		ArrayList<Integer> vShared= new ArrayList<>();
		ArrayList<Integer> v2 = new ArrayList<>();

		generateVehicleSet(v1, vShared, v2, r1, rShared, r2);
		
		if(print)
		{
			System.out.format("Vehicle Count: %d, partition : %d, %d, %d \n",problemInstance.vehicleCount,r1,rShared,r2);
			System.out.println(v1.toString());
			System.out.println(vShared.toString());
			System.out.println(v2.toString());

		}
		
		
		boolean clientMap[] = new boolean[problemInstance.customerCount];
		
		//copy from parent 1 directly
		copyAllFromParent(parent1, child, period, v1, clientMap, print);
		if(print)System.out.println("-----------------------------------");
		//copy partially from parent1
		copyPartialFromParent(parent1, child, period, vShared, clientMap, print);
		if(print)System.out.println("-----------------------------------");
		//copy from parent2 directly 
		copyAllFromParent(parent2, child, period, v2, clientMap, print);
		if(print)System.out.println("-----------------------------------");
		//copy from parent2 partial
		copyAllFromParent(parent2, child, period, vShared, clientMap, print);
		
		//fill missing clients
		if(print)System.out.println("-----------------------------------");

		copyAllFromParent(parent1, child, period, vShared, clientMap, print);
		if(print)System.out.println("-----------------------------------");

		copyAllFromParent(parent1, child, period, v2, clientMap, print);
		if(print)System.out.println("-----------------------------------");

		copyAllFromParent(parent2, child, period, v1, clientMap, print);

		
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
	
	/**
	 * appends the routes specified in vehicle list of the specified periods from the parent to child. 
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
	private static void copyAllFromParent(Individual parent, Individual child, int period, ArrayList<Integer> vehicleList, boolean clientMap[], boolean print)
	{
		for(int i=0;i<vehicleList.size();i++)
		{
			int vehicle = vehicleList.get(i);
			ArrayList<Integer> parentRoute = parent.routes.get(period).get(vehicle);
			ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);			
			
			for(int cl=0;cl<parentRoute.size();cl++)
			{
				int client = parentRoute.get(cl);
				if(child.periodAssignment[period][client] == false) continue;
				if(clientMap[client] == true) continue;
				
				
				childRoute.add(client);
				clientMap[client] = true;
			}
			
			if(print)
			{
				
				System.out.println("vehicle " +vehicle);
//				System.out.println("Child Period Assignment: "+ Arrays.toString(child.periodAssignment[period]));
//				System.out.println("Client Map: "+ Arrays.toString(clientMap));
				System.out.println("Parent route: "+parentRoute.toString());
				System.out.println("Child route: "+childRoute.toString());

			}
		}

	}

	/**
	 * appends the routes specified in vehicle list of the specified periods from the parent to child. 
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
	private static void copyPartialFromParent(Individual parent, Individual child, int period, ArrayList<Integer> vehicleList, boolean clientMap[], boolean print)
	{
		for(int i=0;i<vehicleList.size();i++)
		{
			int vehicle = vehicleList.get(i);
			ArrayList<Integer> parentRoute = parent.routes.get(period).get(vehicle);
			
			int size = parentRoute.size();
			if(size==0) continue;
			
			int left = Utility.randomIntExclusive(size);
			int right = Utility.randomIntInclusive(left, size-1);
			
			ArrayList<Integer> childRoute = child.routes.get(period).get(vehicle);			
			
			
			for(int cl=left;cl<=right;cl++)
			{
				int client = parentRoute.get(cl);
				if(child.periodAssignment[period][client] == false) continue;
				if(clientMap[client] == true) continue;
				
				
				childRoute.add(client);
				clientMap[client] = true;
			}
			
			if(print)
			{
				
				System.out.println("vehicle " +vehicle);
				System.out.format("copy window: [ %d, %d] \n",left,right);
//				System.out.println("Child Period Assignment: "+ Arrays.toString(child.periodAssignment[period]));
//				System.out.println("Client Map: "+ Arrays.toString(clientMap));
				System.out.println("Parent route: "+parentRoute.toString());
				System.out.println("Child route: "+childRoute.toString());

			}
		}

	}

	public static void generateVehicleSet(ArrayList<Integer> one, ArrayList<Integer> two, ArrayList<Integer> three, int c1,int c2, int c3)
	{
		int total = c1+ c2+c3;
		int assigned;
		
		boolean vehicleMap[]=new boolean[total];
		
		assigned = 0;
		while(assigned<c1)
		{
			int rand = Utility.randomIntExclusive(total);
			if(vehicleMap[rand] == true) continue;
			
			one.add(rand);
			assigned++;
			vehicleMap[rand]=true;
		}
		
		assigned = 0;
		while(assigned<c2)
		{
			int rand = Utility.randomIntExclusive(total);
			if(vehicleMap[rand] == true) continue;
			
			two.add(rand);
			assigned++;
			vehicleMap[rand]=true;
		}
		
		assigned = 0;
		while(assigned<c3)
		{
			int rand = Utility.randomIntExclusive(total);
			if(vehicleMap[rand] == true) continue;
			
			three.add(rand);
			assigned++;
			vehicleMap[rand]=true;
		}
		
	}
	
	//return a,b,c, a+b+c = sum
	// a>b>c
	// a,b,c >=0;
	public static int[] partitionSet(int sum)
	{
		int[] num= new int[3];
		

		if(sum==2)
		{
			num[0]=1;
			num[1]=1;
			num[2]=0;
			return num;
		}
		
		//just do it now
		num[0] =  Utility.randomIntInclusive(1, sum-2);
		num[1] = Utility.randomIntInclusive(1, sum-num[0]-1);
		num[2] = sum - num[0] - num[1];		
		
		Arrays.sort(num);
		//ArrayUtils.reverse();
	    int temp = num[0];
	    num[0] = num[2];
	    num[2]=temp;
		    
		return num;
	}
	
}
