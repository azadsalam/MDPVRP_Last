package Main.VRP.Individual.Crossover;

import Main.Solver;
import Main.VRP.Individual.Individual;

public class CrossoverStatistics 
{

	public static int apply,improvedThanBothParent,improvedThanOneParent, numberOfFeasibleSolution;
	public static double bestImprovement,totalImprovement;
	
	public static void init() 
	{
		apply=0;
		
		improvedThanBothParent = 0;
		improvedThanOneParent = 0;
		numberOfFeasibleSolution = 0;
		bestImprovement = Double.MAX_VALUE;
		totalImprovement = 0;
	}
	
	public static void gatherData(Individual parent1, Individual parent2, Individual offspring) 
	{
		apply++;
		
		if(offspring.isFeasible) numberOfFeasibleSolution++;
		
		if(offspring.cost <= parent1.cost && offspring.cost <= parent2.cost ) improvedThanBothParent++;
		else if(offspring.cost <= parent1.cost || offspring.cost <= parent2.cost ) improvedThanOneParent++;
		
		double cost1 = parent1.cost + Solver.loadPenaltyFactor * parent1.totalLoadViolation + Solver.routeTimePenaltyFactor * parent1.totalRouteTimeViolation;
		double cost2 = parent2.cost + Solver.loadPenaltyFactor * parent2.totalLoadViolation + Solver.routeTimePenaltyFactor * parent2.totalRouteTimeViolation;
		double costOffspring = offspring.cost + Solver.loadPenaltyFactor * offspring.totalLoadViolation + Solver.routeTimePenaltyFactor * offspring.totalRouteTimeViolation;
		
		double costParent;
		if(cost1<cost2) costParent = cost1;
		else costParent = cost2;
		
		
		double percentGapFromParent = (costOffspring-costParent)*100/costParent;
		
		if(percentGapFromParent<bestImprovement) bestImprovement = percentGapFromParent;
		totalImprovement += percentGapFromParent;
		
	}
	
	public static void print() 
	{
		System.out.println("Applied: "+apply);
		System.out.println("Feasible Offspring: "+numberOfFeasibleSolution);
		System.out.println("Better Than Both Parents wrto only Cost: "+improvedThanBothParent);
		System.out.println("Better Than Only One Parent wrto only Cost: "+improvedThanOneParent);	
		System.out.println("Avg Deviation From less cost parent( -ve means better): "+totalImprovement/apply*100+"%");
		System.out.println("Best percent improve: "+bestImprovement+"%");
	}
}
