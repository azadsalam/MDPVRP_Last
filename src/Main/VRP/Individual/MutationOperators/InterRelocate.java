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

public class InterRelocate {

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
		vehicle1 = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
		vehicle2 = vehicle1;
		
		while(vehicle2==vehicle1)
		{
			vehicle2 = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
		}
		
		
		if(print)
		{	TotalCostCalculator.calculateCost(individual, loadPenaltyFactor, routeTimePenaltyFactor);
			double cb = individual.costWithPenalty;
			System.out.format("Cost before: %f\n",cb);
		}
		
		boolean success = mutateRouteswithFirstBestMove(individual, period, vehicle1,vehicle2,loadPenaltyFactor,routeTimePenaltyFactor,print);
		

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

	
	private static boolean mutateRouteswithFirstBestMove(Individual individual, int period, int vehicle1, int vehicle2, double loadPenaltyFactor,double routeTimePenaltyFactor, boolean print) 
	{
		
		ArrayList<Integer> route1 =  new ArrayList<Integer>(individual.routes.get(period).get(vehicle1)); 
		ArrayList<Integer> route2 =  new ArrayList<Integer>(individual.routes.get(period).get(vehicle2));
		
	//	double oldCost = ;
		
		
		
		
		return false;
	}

}
