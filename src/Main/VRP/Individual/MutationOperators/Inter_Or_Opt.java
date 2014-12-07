package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import javax.lang.model.element.NestingKind;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class Inter_Or_Opt {

	public static int apply = 0;	
	public static double totalSec=0;

	public static void mutate(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
		int vehicle1 = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
		
		int vehicle2= vehicle1;
		while(vehicle2 == vehicle1) vehicle2 = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			
		
/*		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double cb = individual.costWithPenalty;
*/		
//		boolean success = mutateRouteBy_Or_Opt_withFirstBetterMove(individual, period, vehicle1,vehicle2,loadPenaltyFactor,routeTimePenaltyFactor);
		boolean success = mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, vehicle1,vehicle2,loadPenaltyFactor,routeTimePenaltyFactor);	
/*		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double ca = individual.costWithPenalty;
*/		
		
/*		System.out.format("Cost before: %f, After:%f\n",cb,ca);
		if(success) System.out.println("Successful");
		else System.out.println("Fail");
*/		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}

	

	public static void mutateSpecificClientByCheckingHOS(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		
		int period=-1,client=-1;
	

		if(print)
		{
			System.out.print("HOS BEFORE: ");
			individual.printHOS_PC();
		}
		while(true)
		{
			period = Utility.randomIntInclusive(problemInstance.periodCount-1);
			client = Utility.randomIntExclusive(problemInstance.customerCount);
			
			while (individual.periodAssignment[period][client]==false)
				client = Utility.randomIntExclusive(problemInstance.customerCount);
			
			if(print)System.out.format("Seleceted PC : %d, %d\n",period,client);
			
			if(individual.isInHallOfShamePC(period, client)==false) break;
			
			if(print)System.err.println("IN HOS, Retry");

			//new Scanner(System.in).nextLine();
		}
		
		if(print)
		{	TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			double cb = individual.costWithPenalty;
			System.out.format("Cost before: %f\n",cb);
		}
		
		boolean success = mutateClientRouteBy_Or_Opt_withFirstBetterMove(individual, period, client,loadPenaltyFactor,routeTimePenaltyFactor);

		if(!success)
		{
			individual.addToHOS_PC(period, client);
		}

		if(print)
		{
			TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			double ca = individual.costWithPenalty;
			System.out.format("Cost After:%f\n",ca);
			if(success) System.out.println("Successful");
			else System.out.println("Fail");

			System.out.print("HOS AFTER: ");
			individual.printHOS_PC();

		}
		
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		
		//System.exit(1);
	}


	
	public static void mutateSpecificClient(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		
		
		int period = Utility.randomIntInclusive(problemInstance.periodCount-1);

		int client = Utility.randomIntExclusive(problemInstance.customerCount);
		
		while (individual.periodAssignment[period][client]==false)
			client = Utility.randomIntExclusive(problemInstance.customerCount);
		
		
/*		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double cb = individual.costWithPenalty;
*/		
		boolean success = mutateClientRouteBy_Or_Opt_withFirstBetterMove(individual, period, client,loadPenaltyFactor,routeTimePenaltyFactor);
		
/*		TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
		double ca = individual.costWithPenalty;
*/		
		
/*		System.out.format("Cost before: %f, After:%f\n",cb,ca);
		if(success) System.out.println("Successful");
		else System.out.println("Fail");
*/		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}

	public static boolean mutateClientRouteBy_Or_Opt_withFirstBetterMove(Individual individual, int period, int client, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		boolean print=false;
		
		if(print) System.out.println("CLient: "+client);
		
		int vehicle1 =  RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);				
		int route1Size = individual.routes.get(period).get(vehicle1).size();
		
		//random vehicle choice
		int vehicle2 = vehicle1;
		
		while(vehicle2 == vehicle1) vehicle2 = Utility.randomIntExclusive(problemInstance.vehicleCount);
		
		int route2Size = individual.routes.get(period).get(vehicle2).size();

		
		int clientPostion = individual.routes.get(period).get(vehicle1).indexOf(client);
		if(print)System.out.println("Client Position: "+clientPostion);
	
		
		double oldCost1 = individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor);
		double oldCost2 = individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor);

		
		if(print)
		{
			System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: "+oldCost1);
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: "+oldCost2);			
		}

		for(int k=3;k>=1;k--)
		{
			if(route1Size<k)continue;
						
			if(print) System.out.println("k: "+k);			
						

			for(int i=Math.max(0,clientPostion-k+1); i <=clientPostion && i+k-1<route1Size; i++)
			{

				ArrayList<Integer> modifiedRoute1 = individual.routes.get(period).get(vehicle1);
				ArrayList<Integer> savedRoute1 = new ArrayList<Integer>(modifiedRoute1);
				
				ArrayList<Integer> relocatedChain = new ArrayList<Integer>();
				
				for(int tmp=0;tmp<k;tmp++)
				{
					relocatedChain.add(0,modifiedRoute1.remove(i));
				}
				
				double newCost1 = individual.calculateCostWithPenalty(period, vehicle1, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);

				if(print) 
				{
					System.out.println("Removed "+relocatedChain+" from "+i+" yielding "+modifiedRoute1.toString()+"\n New Cost1: "+newCost1);
				}

				ArrayList<Integer> modifiedRoute2 = individual.routes.get(period).get(vehicle2);
				ArrayList<Integer> savedRoute2 = new ArrayList<>(modifiedRoute2);
				
				
				for(int j=0;j<=route2Size;j++)
				{
					modifiedRoute2.addAll(j,relocatedChain);
					double newCost2 = individual.calculateCostWithPenalty(period, vehicle2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
					
					if(print) System.out.println("Inserted in "+j+": "+modifiedRoute2.toString()+"\n New Cost2: "+newCost2);

					
					if((oldCost1+oldCost2-newCost1-newCost2)>0)
					{
						if(print)System.out.println("FOUND IMPROVEMENT");
						return true;
					}
					
					modifiedRoute2.clear();
					modifiedRoute2.addAll(savedRoute2);
					
					Collections.reverse(relocatedChain);
					modifiedRoute2.addAll(j,relocatedChain);
					newCost2 = individual.calculateCostWithPenalty(period, vehicle2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
					
					if(print) System.out.println("Inserted in "+j+"(Reveresed): "+modifiedRoute2.toString()+"\n New Cost2: "+newCost2);

					
					if((oldCost1+oldCost2-newCost1-newCost2)>0)
					{
						if(print)System.out.println("FOUND IMPROVEMENT");
						return true;
					}
					
					modifiedRoute2.clear();
					modifiedRoute2.addAll(savedRoute2);
					Collections.reverse(relocatedChain);

				}
				
				
				modifiedRoute1.clear();
				modifiedRoute1.addAll(savedRoute1);
			}
		}

		if(print)
		{
			System.out.println("After Inter Route Or-Opt\nOriginal Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: "+oldCost1);
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: "+oldCost2);			
		}

		return false;
	}
	

	/**
	 * 
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy_Or_Opt_withFirstBetterMove(Individual individual, int period, int vehicle1, int vehicle2, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		
		int coin = Utility.randomIntInclusive(1);
		if(coin == 1) Collections.reverse(individual.routes.get(period).get(vehicle1));		

		int route1Size = individual.routes.get(period).get(vehicle1).size();
		int route2Size = individual.routes.get(period).get(vehicle2).size();

		double oldCost1 = individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor);
		double oldCost2 = individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor);

		
		if(print)
		{
			System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: "+oldCost1);
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: "+oldCost2);			
		}

		for(int k=3;k>=1;k--)
		{
			if(route1Size<k)continue;
						
			if(print) System.out.println("k: "+k);			
						
			for(int i=0; i+k-1 < route1Size; i++)
			{
				ArrayList<Integer> modifiedRoute1 = individual.routes.get(period).get(vehicle1);
				ArrayList<Integer> savedRoute1 = new ArrayList<Integer>(modifiedRoute1);
				
				ArrayList<Integer> relocatedChain = new ArrayList<Integer>();
				
				for(int tmp=0;tmp<k;tmp++)
				{
					relocatedChain.add(0,modifiedRoute1.remove(i));
				}
				
				double newCost1 = individual.calculateCostWithPenalty(period, vehicle1, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);

				if(print) System.out.println("Removed from "+i+": "+modifiedRoute1.toString()+"\n New Cost1: "+newCost1);

				ArrayList<Integer> modifiedRoute2 = individual.routes.get(period).get(vehicle2);
				ArrayList<Integer> savedRoute2 = new ArrayList<>(modifiedRoute2);
				
				
				for(int j=0;j<=route2Size;j++)
				{
					modifiedRoute2.addAll(j,relocatedChain);
					double newCost2 = individual.calculateCostWithPenalty(period, vehicle2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
					
					if(print) System.out.println("Inserted in "+j+": "+modifiedRoute2.toString()+"\n New Cost2: "+newCost2);

					
					if((oldCost1+oldCost2-newCost1-newCost2)>0)
					{
						if(print)System.out.println("FOUND IMPROVEMENT");
						return true;
					}
					
					modifiedRoute2.clear();
					modifiedRoute2.addAll(savedRoute2);
					
					Collections.reverse(relocatedChain);
					modifiedRoute2.addAll(j,relocatedChain);
					newCost2 = individual.calculateCostWithPenalty(period, vehicle2, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
					
					if(print) System.out.println("Inserted in "+j+"(Reveresed): "+modifiedRoute2.toString()+"\n New Cost2: "+newCost2);

					
					if((oldCost1+oldCost2-newCost1-newCost2)>0)
					{
						if(print)System.out.println("FOUND IMPROVEMENT");
						return true;
					}
					
					modifiedRoute2.clear();
					modifiedRoute2.addAll(savedRoute2);
					Collections.reverse(relocatedChain);

				}
				
				
				modifiedRoute1.clear();
				modifiedRoute1.addAll(savedRoute1);
			}
		}

		if(print)
		{
			System.out.println("After Inter Route Or-Opt\nOriginal Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: "+oldCost1);
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: "+oldCost2);			
		}
		return false;
	}

	
	public static boolean mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(Individual individual, int period, int vehicle1, int vehicle2, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		ProblemInstance pi = individual.problemInstance;
		
		double costMatrix[][] = individual.problemInstance.costMatrix;
		int dc= individual.problemInstance.depotCount;
		int depot1 = individual.problemInstance.depotAllocation[vehicle1];
		int depot2 = individual.problemInstance.depotAllocation[vehicle2];
		
/*		int coin = Utility.randomIntInclusive(1);
		if(coin == 1) Collections.reverse(individual.routes.get(period).get(vehicle1));		
*/
		
		ArrayList<Integer>route1 = individual.routes.get(period).get(vehicle1);
		ArrayList<Integer>route2 = individual.routes.get(period).get(vehicle2);
		
		int route1Size = route1.size();
		int route2Size = route2.size();

		double oldCost1 = individual.calculateCost(period, vehicle1);
		double oldRouteViolation1 = individual.routeTimeViolation[period][vehicle1];
		double oldLoadViolation1 = individual.loadViolation[period][vehicle1];
		double oldCostWithPenalty1 = individual.calculateCostWithPenalty(oldCost1, oldLoadViolation1, oldRouteViolation1, loadPenaltyFactor, routeTimePenaltyFactor);
		
		double oldCost2 = individual.calculateCost(period, vehicle2);
		double oldRouteViolation2 = individual.routeTimeViolation[period][vehicle2];
		double oldLoadViolation2 = individual.loadViolation[period][vehicle2];
		double oldCostWithPenalty2 = individual.calculateCostWithPenalty(oldCost2, oldLoadViolation2, oldRouteViolation2, loadPenaltyFactor, routeTimePenaltyFactor);
		
		
		if(print)
		{

//			System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
//			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());

/*			System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
			System.out.println("Old cost1: (Calculated)"+oldCostWithPenalty1);
			System.out.println("Old cost1: (Evaluated)"+individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor));
			
			System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
			System.out.println("Old cost2: (Calculated)"+oldCostWithPenalty2);
			System.out.println("Old cost2: (Evaluated)"+individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor));
*/		}

		
		for(int k=3;k>=1;k--)
		{
			if(route1Size<k)continue;
						
			//if(print) System.out.println("k: "+k);			
						
			for(int i=0; i+k-1 < route1Size; i++)
			{
				//calculate cost as if removed k nodes from i (inclusive)
				
				double newCostWithPenalty1=0;
				
				double segmentRouteCost=0;
				double segmentServiceTime=0;
				double segmentDemand=0;
				double newLoadViolation1=0;
				double newRouteTimeViolation1=0;
				double newCost1=0;
				
				//first determine segment route cost				
				for(int tmp_i=0; tmp_i < k-1 ; tmp_i++) 
					segmentRouteCost += edgeCost(individual.problemInstance, route1, depot1, i+tmp_i);

				for(int tmp_i=0; tmp_i < k ; tmp_i++) 
					segmentServiceTime += pi.serviceTime[route1.get(i+tmp_i)];
				
				for(int tmp_i=0; tmp_i < k ; tmp_i++) 
					segmentDemand += pi.demand[route1.get(i+tmp_i)];
				
				
				if(route1Size==k) // modified route 1 is empty
				{
					newCostWithPenalty1=0;
				}

				else // the resultant route will have at least 1 node // the current route has at least k+1 nodes
				{
					//2 edges are broken + the segment route cost is to be deducted
					// delete : (i-1,i)  & (i+k-1,i+k)
					//deduct segment cost
					//one new edge to be added: (i-1, i+k)
					newCost1 = oldCost1;
					double routeTimeIncrement=0;
	
					routeTimeIncrement -= edgeCost(pi, route1, depot1, i-1);
					routeTimeIncrement -= edgeCost(pi, route1, depot1, i+k-1);
					routeTimeIncrement -= segmentRouteCost; 
					routeTimeIncrement += disBetweenTwoNode(pi, route1, depot1, i-1, i+k);

					newCost1 += routeTimeIncrement;
					
					newLoadViolation1 = oldLoadViolation1 - segmentDemand;
					newRouteTimeViolation1 = 0;
					
					if(pi.timeConstraintsOfVehicles[period][vehicle1] != 0)
					{
						newRouteTimeViolation1 = oldRouteViolation1;
						newRouteTimeViolation1 += routeTimeIncrement; // route cost barle violation barbe and vice verca
						newRouteTimeViolation1 -= segmentServiceTime;
					}
					
					newCostWithPenalty1 = individual.calculateCostWithPenalty(newCost1, newLoadViolation1, newRouteTimeViolation1, loadPenaltyFactor, routeTimePenaltyFactor);
				
					
				
				}
				
				if(print)
				{

					//System.out.println("i: "+i);
					ArrayList<Integer> relocatedChain = new ArrayList<>();
					// really delete k edges and crosscheck
					//put them in list backwards
					for(int tmp_i=0;tmp_i<k;tmp_i++)
						relocatedChain.add(0,route1.remove(i));
					
					boolean condition = Math.abs(newCost1-individual.calculateCost(period, vehicle1)) > 0.0000001;
					condition  |= Math.abs(newLoadViolation1-individual.loadViolation[period][vehicle1]) > 0.0000001;
					condition  |= Math.abs(newRouteTimeViolation1-individual.routeTimeViolation[period][vehicle1]) > 0.0000001;
					
					
					if(condition && route1Size != k)
					{
						System.out.println("\nERROR");
						System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
						System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
						
						System.out.println("\nRoute1:");
						System.out.println("Calculated CostWithPenalty: "+ newCostWithPenalty1);
						System.out.println("Evaluated CostWithPenalty: "+ individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor));				
						System.out.println("Calculated Cost: "+ newCost1);
						System.out.println("Evaluated Cost: "+ individual.calculateCost(period, vehicle1));				
						
						System.out.println("Calculated LoadViolation: "+ newLoadViolation1);
						System.out.println("Evaluated LoadViolation: "+ individual.loadViolation[period][vehicle1]);				
						System.out.println("Calculated RouteViolation: "+ newRouteTimeViolation1);
						System.out.println("Evaluated RouteViolation: "+ individual.routeTimeViolation[period][vehicle1]);				
						
						//add those culprits back
						System.exit(1);
					}

					//insert the deleted list again
					while(relocatedChain.isEmpty()!=true)
						route1.add(i,relocatedChain.remove(0));
					
					//System.out.println("ROUTE1 After Calulcation: "+route1);

				}
				
				
				for(int j=0;j<=route2Size;j++)
				{
					
					// edges added : 1) route2[j-1] -> route1[i]
					//					2) route1[i+k-1] -> route2[j]
					//						3) the segment edge costs
					
					//edges deleted route2[j-1] -> route2[j]
					
					
					//handle the case when route 2 is empty// -> added an condition before deducting the cost of edge deleted.

					double newCost2 = oldCost2;
					double costIncrement =0;
					costIncrement += disBetweenThisRoutesNode_and_another_Client(pi, route2, depot2, j-1, route1.get(i));
					costIncrement += disBetweenThisRoutesNode_and_another_Client(pi, route2, depot2, j, route1.get(i+k-1));
					costIncrement += segmentRouteCost;
					
					if(route2Size!=0) costIncrement -= edgeCost(pi, route2, depot2, j-1);
					newCost2 += costIncrement;
					
					double newLoadViolation2 = oldLoadViolation2 + segmentDemand;
					double newRouteTimeViolation2 = 0;
					
					if(pi.timeConstraintsOfVehicles[period][vehicle1] != 0)
					{
						newRouteTimeViolation2 = oldRouteViolation2;
						newRouteTimeViolation2 += costIncrement;
						newRouteTimeViolation2 += segmentServiceTime;
					}
					
					double newCostWithPenalty2 = individual.calculateCostWithPenalty(newCost2, newLoadViolation2, newRouteTimeViolation2, loadPenaltyFactor, routeTimePenaltyFactor);
					
					
					
					
					if(print)
					{

						//add actually
						for(int tmp_i=k-1;tmp_i>=0;tmp_i--)
							route2.add(j,route1.get(i+tmp_i));
						
						boolean condition = Math.abs(newCost2-individual.calculateCost(period, vehicle2)) > 0.0000001;
						condition  |= Math.abs(newLoadViolation2-individual.loadViolation[period][vehicle2]) > 0.0000001;
						condition  |= Math.abs(newRouteTimeViolation2-individual.routeTimeViolation[period][vehicle2]) > 0.0000001;
						
						if(condition)
						{
							System.out.println("Route2 After Adding Madafucka: "+route2);

							System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
							System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
							
							System.out.println("Old Load Violation2: "+oldLoadViolation2);
							
							System.out.println("\nRoute2:");
							System.out.println("Calculated CostWithPenalty: "+ newCostWithPenalty2);
							System.out.println("Evaluated CostWithPenalty: "+ individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor));				
							System.out.println("Calculated Cost: "+ newCost2);
							System.out.println("Evaluated Cost: "+ individual.calculateCost(period, vehicle2));				
							
							System.out.println("Calculated LoadViolation: "+ newLoadViolation2);
							System.out.println("Evaluated LoadViolation: "+ individual.loadViolation[period][vehicle2]);				
							System.out.println("Calculated RouteViolation: "+ newRouteTimeViolation2);
							System.out.println("Evaluated RouteViolation: "+ individual.routeTimeViolation[period][vehicle2]);				
							
							System.exit(0);
						}	
														
						

						for(int tmp_i=k-1;tmp_i>=0;tmp_i--)
							route2.remove(j);
						
						//System.out.println("Route2 After Calculation: "+route2);
						
					}
					
					if(newCostWithPenalty1+newCostWithPenalty2 < oldCostWithPenalty1+oldCostWithPenalty2)
					{

/*						if(print)
						{
							
							System.out.println("Inter Route Or-Opt\nOld Route1: "+individual.routes.get(period).get(vehicle1).toString());
							System.out.println("Old Route2: "+individual.routes.get(period).get(vehicle2).toString());
							System.out.format("i: %d j: %d k: %d\n",i,j,k);
						}
*/
						//do it now 
						//add to route 2 
						for(int tmp_i=k-1;tmp_i>=0;tmp_i--)
							route2.add(j,route1.get(i+tmp_i));
						
						//remove from route 1
						for(int tmp_i=0;tmp_i<k;tmp_i++)
							route1.remove(i);
						

						if(print)
						{
						//	System.out.println("After Inter Route Or-Opt\nNew Route1: "+individual.routes.get(period).get(vehicle1).toString());
						//	System.out.println("New Route2: "+individual.routes.get(period).get(vehicle2).toString());
						}

						return true;
					}
					
					
					//now add in reverse order
					
					// edges added : 1) route2[j-1] -> route1[i+k-1] ***
					//					2) route1[i] -> route2[j]    ***
					//						3) the segment edge costs
					
					//edges deleted route2[j-1] -> route2[j]
					
					
					// so we only need to update the new links
					
					double increment=0;
					increment -= disBetweenThisRoutesNode_and_another_Client(pi, route2, depot2, j-1, route1.get(i));
					increment -= disBetweenThisRoutesNode_and_another_Client(pi, route2, depot2, j, route1.get(i+k-1));
					
					increment += disBetweenThisRoutesNode_and_another_Client(pi, route2, depot2, j-1, route1.get(i+k-1));
					increment += disBetweenThisRoutesNode_and_another_Client(pi, route2, depot2, j, route1.get(i));
					
					newCost2+= increment;
					
					//loadViolation is still the same
					//routeViolation needs to be updated 
					newRouteTimeViolation2 += increment;
					
					newCostWithPenalty2 = individual.calculateCostWithPenalty(newCost2, newLoadViolation2, newRouteTimeViolation2, loadPenaltyFactor, routeTimePenaltyFactor);

					if(print)
					{
						//add actually - now in reverse order
						for(int tmp_i=0;tmp_i<k;tmp_i++)
							route2.add(j,route1.get(i+tmp_i));

						
						boolean condition = Math.abs(newCost2-individual.calculateCost(period, vehicle2)) > 0.0000001;
						condition  |= Math.abs(newLoadViolation2-individual.loadViolation[period][vehicle2]) > 0.0000001;
						condition  |= Math.abs(newRouteTimeViolation2-individual.routeTimeViolation[period][vehicle2]) > 0.0000001;
						
						if(condition)
						{
							
							System.out.println("Route2 After Adding Madafucka in reverse: "+route2);

							System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
							System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
							System.out.format("i: %d j: %d k: %d\n",i,j,k);
							System.out.println("Old Load Violation2: "+oldLoadViolation2);
							
							System.out.println("\nRoute2:");
							System.out.println("Calculated CostWithPenalty: "+ newCostWithPenalty2);
							System.out.println("Evaluated CostWithPenalty: "+ individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor));				
							System.out.println("Calculated Cost: "+ newCost2);
							System.out.println("Evaluated Cost: "+ individual.calculateCost(period, vehicle2));				
							
							System.out.println("Calculated LoadViolation: "+ newLoadViolation2);
							System.out.println("Evaluated LoadViolation: "+ individual.loadViolation[period][vehicle2]);				
							System.out.println("Calculated RouteViolation: "+ newRouteTimeViolation2);
							System.out.println("Evaluated RouteViolation: "+ individual.routeTimeViolation[period][vehicle2]);				
							
							System.exit(0);
						}	
														
						

						for(int tmp_i=k-1;tmp_i>=0;tmp_i--)
							route2.remove(j);
						
						//System.out.println("Route2 After Calculation: "+route2);

						
					}
					
					if(newCostWithPenalty1+newCostWithPenalty2 < oldCostWithPenalty1+oldCostWithPenalty2)
					{

						//do it now 
						//add to route 2 in reverse order
						for(int tmp_i=0;tmp_i<k;tmp_i++)
							route2.add(j,route1.get(i+tmp_i));
						
						//remove from route 1
						for(int tmp_i=0;tmp_i<k;tmp_i++)
							route1.remove(i);
						
						if(print)
						{
					//		System.out.println("After Inter Route Or-Opt\nNew Route1: "+individual.routes.get(period).get(vehicle1).toString());
						//	System.out.println("New Route2: "+individual.routes.get(period).get(vehicle2).toString());
						}

						return true;
					}


				}

			}
		}

		if(print)
		{
	//		System.out.println("After Inter Route Or-Opt- FAILED\nNew Route1: "+individual.routes.get(period).get(vehicle1).toString());
	//		System.out.println("New Route2: "+individual.routes.get(period).get(vehicle2).toString());
		}

		return false;
	}
	
	/**
	 * returns the cost of the edge (nodeIndex, nodeIndex+1)
	 * @param route
	 * @param depot
	 * @param nodeIndex
	 * @return
	 */
	public static double edgeCost(ProblemInstance problemInstance,ArrayList<Integer> route, int depot, int nodeIndex) 
	{
		int routeSize = route.size();
		double[][] cm = problemInstance.costMatrix;
		int dc = problemInstance.depotCount;
		
//		System.out.println(dc+route.get(nodeIndex));
		if(nodeIndex==-1)//depot -> node 0 
			return cm[depot][dc+route.get(0)];
		
		else if(nodeIndex+1 == routeSize) // last node -> depot
			return cm[dc+route.get(nodeIndex)][depot];
		
		else return cm[dc+route.get(nodeIndex)][dc+route.get(nodeIndex+1)];
	}
	
	/**
	 * return distance between route[index1] and route[index2]
	 * <br/> at least one from node1 and node2 can be depots
	 * <br/> assumes that index1 is always less than index2 
	 * @param problemInstance
	 * @param route
	 * @param depot
	 * @param index1
	 * @param index2
	 * @return
	 */
	public static double disBetweenTwoNode(ProblemInstance problemInstance,ArrayList<Integer> route, int depot, int index1, int index2) 
	{
		int routeSize = route.size();
		double[][] cm = problemInstance.costMatrix;
		int dc = problemInstance.depotCount;
	
		if(index1 == -1)
			return cm[depot][dc+route.get(index2)];
		else if(index2 == routeSize)
			return cm[dc+route.get(index1)][depot];
		else
			return cm[dc+route.get(index1)][dc+route.get(index2)];
		
	}
	
	/**
	 * returns the distance between route[index1] -> another_client
	 * @param problemInstance
	 * @param route
	 * @param depot
	 * @param index1
	 * @param index2
	 * @return
	 */
	public static double disBetweenThisRoutesNode_and_another_Client(ProblemInstance problemInstance,ArrayList<Integer> route, int depot, int index1, int another_client) 
	{
		int routeSize = route.size();
		double[][] cm = problemInstance.costMatrix;
		int dc = problemInstance.depotCount;
	
		if(index1 == -1 || index1 == routeSize)
			return cm[depot][dc+another_client];
		else
			return cm[dc+route.get(index1)][dc+another_client];
		
	}
}
