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

public class Inter_2_Opt {

	public static int apply = 0;	
	public static double totalSec=0;

	public static void mutate(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;
		
		int period, depot, vehicle1, vehicle2;
		
		// check for empty routes 
		
		period = Utility.randomIntInclusive(problemInstance.periodCount-1);
		depot = Utility.randomIntExclusive(problemInstance.depotCount);
		
		ArrayList<Integer> vehicles = problemInstance.vehiclesUnderThisDepot.get(depot);
		
		int size = vehicles.size();
		vehicle1 = vehicles.get(Utility.randomIntExclusive(size));
		vehicle2 = vehicle1;
		
		while(vehicle2==vehicle1)
		{
			vehicle2 = vehicles.get(Utility.randomIntExclusive(size));
		}
		
		
		if(print)
		{	TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			double cb = individual.costWithPenalty;
			System.out.format("Cost before: %f\n",cb);
		}
		boolean success = mutateRoutesBy_Two_Opt_withFirstBetterMove(individual, period, vehicle1,vehicle2,loadPenaltyFactor,routeTimePenaltyFactor);
		

		if(print)
		{
			TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			double ca = individual.costWithPenalty;
			System.out.format("Cost After:%f\n",ca);
			
			if(success) System.out.println("Successful");
			else System.out.println("Fail");
		}
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
	}

	

	/**
	 * 
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRoutesBy_Two_Opt_withFirstBetterMove(Individual individual, int period, int vehicle1, int vehicle2, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		
/*		int DEPOT_NODE = individual.problemInstance.customerCount;
		int depot1 = individual.problemInstance.depotAllocation[vehicle1];
		int depot2 = individual.problemInstance.depotAllocation[vehicle2];
		
		if(depot1 != depot2) 
		{
			System.err.println("ERROR IN INTER TWO OPT!!! ROUTES FROM DIFFERENT DEPOTS");
			System.exit(1);
		}
		int depot = depot1;
*/
		
		ArrayList<Integer> route1 = new ArrayList<Integer>(individual.routes.get(period).get(vehicle1));
		ArrayList<Integer> route2 = new ArrayList<Integer>(individual.routes.get(period).get(vehicle2));
						
		int route1Size = route1.size();
		int route2Size = route2.size();
		
		double oldCost1 = individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor);
		double oldCost2 = individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor);

		
		

		// lets just for the sake of love of the Almighty and his this 
		// good for nothing slave  - restrict the operation with vehicles if same depot for now !!
		// will analyze the operation between vehicles under different depot later
		// 
		
		//the edge (i,i+1) and (j,j+1) are Broken 
		for(int i=0;i<route1Size-1;i++)
		{	
			for(int j=0;j<route2Size-1;j++)
			{
				
				if(print)
				{
					System.out.println("Original Route1: "+individual.routes.get(period).get(vehicle1).toString());
					System.out.println("Old cost1: "+oldCost1);
					System.out.println("Original Route2: "+individual.routes.get(period).get(vehicle2).toString());
					System.out.println("Old cost2: "+oldCost2);			
				}
				
				ArrayList<Integer> r1part1 = new ArrayList<Integer>(route1.subList(0, i+1));
				ArrayList<Integer> r1part2 = new ArrayList<Integer>(route1.subList(i+1, route1Size));
				
				
				ArrayList<Integer> r2part1 = new ArrayList<Integer>(route2.subList(0, j+1));
				ArrayList<Integer> r2part2 = new ArrayList<Integer>(route2.subList(j+1, route2Size));
				
				
				//first set of routes -
				// route (0->i) + (j+1 -> end) //r1part1 + r2part2 
				// route (0->j) + (i+1 -> end) //r2part1 + r1part2
				
				ArrayList<Integer> r1Set1 = new ArrayList<Integer>(r1part1);
				r1Set1.addAll(r2part2);
				
				ArrayList<Integer> r2Set1 = new ArrayList<Integer>(r2part1);
				r2Set1.addAll(r1part2);
				
				individual.routes.get(period).get(vehicle1).clear();
				individual.routes.get(period).get(vehicle1).addAll(r1Set1);
				
				individual.routes.get(period).get(vehicle2).clear();
				individual.routes.get(period).get(vehicle2).addAll(r2Set1);
				
				double cr1s1 = individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor);
				double cr2s1 = individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor);
				
				
				
				double improvement = oldCost1+oldCost2 -cr1s1- cr2s1;
				
				
				if(print)
				{
					System.out.format("i: %d j: %d\n",i,j);
					System.out.println("\nForward: ");
					System.out.println("Route 1: "+r1part1+" | "+r1part2);
					System.out.println("Route 2: "+r2part1+" | "+r2part2);
					System.out.println("Modified Route1: "+individual.routes.get(period).get(vehicle1).toString());
					System.out.println("Cost1 Set1: "+cr1s1);
					System.out.println("Modified Route2: "+individual.routes.get(period).get(vehicle2).toString());
					System.out.println("Cost2 Set1: "+cr2s1);
					System.out.println("Improvement: "+improvement);
					System.out.println("\n");
				}
				if(improvement>0)
				{
					if(print)
						System.out.println("Improvement Found!!.. RETURN FROM HERE FOR FI");
					return true;
				}
					
				//create routes of set2
				// route (0->i) + reverse(0->j)     // r1part1 + reverse(r2part1)
				// route reverse(j+1->end) + (i+1->end) // reverse(r2part2) + r1part2
				
				ArrayList<Integer> r2part1rev =  new ArrayList<>(r2part1);
				Collections.reverse(r2part1rev);
				
				ArrayList<Integer> r2part2rev =  new ArrayList<>(r2part2);
				Collections.reverse(r2part2rev);
				
				
				ArrayList<Integer> r1Set2 = new ArrayList<Integer>(r1part1);
				r1Set2.addAll(r2part1rev);
				
				ArrayList<Integer> r2Set2 = new ArrayList<Integer>(r2part2rev);
				r2Set2.addAll(r1part2);
				
				individual.routes.get(period).get(vehicle1).clear();
				individual.routes.get(period).get(vehicle1).addAll(r1Set2);
				
				individual.routes.get(period).get(vehicle2).clear();
				individual.routes.get(period).get(vehicle2).addAll(r2Set2);
				
				double cr1s2 = individual.calculateCostWithPenalty(period, vehicle1, loadPenaltyFactor, routeTimePenaltyFactor);
				double cr2s2 = individual.calculateCostWithPenalty(period, vehicle2, loadPenaltyFactor, routeTimePenaltyFactor);
				
				
				improvement = oldCost1+oldCost2 -cr1s2- cr2s2;
				
				if(print)
				{
					System.out.println("Reverse: ");
					System.out.println("Route 1: "+r1part1+" |  "+r1part2);
					System.out.println("Route 2: "+r2part1+" |  "+r2part2);
					System.out.println("Route 2: reverse->"+r2part1rev+" | reverse->"+r2part2rev);
					System.out.println("Modified Route1: "+individual.routes.get(period).get(vehicle1).toString());
					System.out.println("Cost1 Set2: "+cr1s2);
					System.out.println("Modified Route2: "+individual.routes.get(period).get(vehicle2).toString());
					System.out.println("Cost2 Set2: "+cr2s2);
					System.out.println("Improvement: "+improvement);
					System.out.println("\n");
				}
				
				if(improvement>0)
				{
					if(print)System.out.println("Improvement Found in reverse!!.. RETURN FROM HERE");
					
					return true;
				}
 				
				
				//revert the original route
				individual.routes.get(period).get(vehicle1).clear();
				individual.routes.get(period).get(vehicle1).addAll(route1);
				
				individual.routes.get(period).get(vehicle2).clear();
				individual.routes.get(period).get(vehicle2).addAll(route2);

			}
		}
			
		//System.exit(1);
		return false;
	}
	

}
