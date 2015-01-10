package Main.VRP.GeneticAlgorithm;
import java.util.ArrayList;

import javax.swing.plaf.SliderUI;
import javax.swing.text.Utilities;

import Main.Solver;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;
import Main.VRP.Individual.MutationOperators.CostReducedVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignmentQuickSelect;
import Main.VRP.Individual.MutationOperators.Inter_2_Opt;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomInsertion;
import Main.VRP.Individual.MutationOperators.IntraRouteGreedyInsertion;
import Main.VRP.Individual.MutationOperators.Intra_Or_Opt;
import Main.VRP.Individual.MutationOperators.MutatePeriodAssignment;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomSwap;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.InterOneOneExchange;
import Main.VRP.Individual.MutationOperators.OneZeroExchangePrev;
import Main.VRP.Individual.MutationOperators.Inter_Or_Opt;
import Main.VRP.Individual.MutationOperators.PatternImprovement;
import Main.VRP.Individual.MutationOperators.PatternImprovementNew;
import Main.VRP.Individual.MutationOperators.PatternMutation;
import Main.VRP.Individual.MutationOperators.Three_Opt;
import Main.VRP.Individual.MutationOperators.Intra_2_Opt;


public class Neigbour_Steps_Grouped implements MutationInterface
{	
	//ProblemInstance problemInstance;
	
	
	/*
	public Mutation(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
	}
	*/
	
	//category 3 
	// 1 - intra route improvement
	// 2 - inter route -> 1) same period
	//					  2) different period
	
	public void applyMutation(Individual offspring)
	{
		applyMutation(offspring,Solver.loadPenaltyFactor,Solver.routeTimePenaltyFactor);
	}
	
	public void applyMutation(Individual offspring, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		
		int count=2;
		if(offspring.problemInstance.periodCount==1)
			count--;
		
		int rand= Utility.randomIntInclusive(count);

		//System.out.println("rand: "+rand);
		
		if(rand==0)
			mutateRouteAssignment(offspring,loadPenaltyFactor,routeTimePenaltyFactor);
		else if(rand==1)
			mutateRoute(offspring);			
		else  
			mutatePeriodAssignment(offspring,loadPenaltyFactor,routeTimePenaltyFactor);
			
		/*if(rand<2) // 0 and 1
		{
			int totalCategory = 2;
			if(offspring.problemInstance.periodCount==1)totalCategory-=1;
			
			int selectedCategory = Utility.randomIntExclusive(totalCategory);
			//System.out.println("MUTATION WITH GROUPING");
		
			if(selectedCategory==0) mutateRouteAssignment(offspring,loadPenaltyFactor,routeTimePenaltyFactor);
			else mutatePeriodAssignment(offspring,loadPenaltyFactor,routeTimePenaltyFactor);
			
			//for test
//			mutateRouteAssignment(offspring,loadPenaltyFactor,routeTimePenaltyFactor);
		}
		else
		{
			mutateRoute(offspring);
		}*/
		
		
				
		offspring.calculateCostAndPenalty();

	}
	
	public void applyMutationWhsitler(Individual offspring, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		int totalCategory = 2;
		if(offspring.problemInstance.periodCount==1)totalCategory-=1;
		
		int selectedCategory = Utility.randomIntExclusive(totalCategory);
		
		//System.out.println("MUTATION WITH GROUPING");
		
		if(selectedCategory==0) mutateRouteAssignment(offspring,loadPenaltyFactor,routeTimePenaltyFactor);
		else mutatePeriodAssignment(offspring,loadPenaltyFactor,routeTimePenaltyFactor);
		
		
		int coin = Utility.randomIntInclusive(1);
		if(coin==1)mutateRoute(offspring);
		
			
		offspring.calculateCostAndPenalty();

	}
	
	public void mutateRoute(Individual offspring)
	{
		/*int totalRouteImprovementOperators = 4;
		int selectedMutationOperator = Utility.randomIntExclusive(totalRouteImprovementOperators);
		if(selectedMutationOperator==0)
		{
			//intra       
			Two_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 1)
		{
			//greedy       //intra	
			Or_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 2)
		{
			//random //intra // replace it with unstringing - stringing
			IntraRouteRandomInsertion.mutate(offspring); 
		}		
		else if (selectedMutationOperator == 3)
		{
			IntraRouteRandomSwap.mutate(offspring);
		}*/
		int totalRouteImprovementOperators = 5;
		int selectedMutationOperator = Utility.randomIntExclusive(totalRouteImprovementOperators);
		if(selectedMutationOperator==0)
		{
			//intra       
			Intra_2_Opt.mutateRandomRouteOnce(offspring);
		}
		else if (selectedMutationOperator == 1)
		{			
			//greedy       //intra	
			Three_Opt.mutateRandomRoute(offspring);
//			IntraRouteRandomSwap.mutate(offspring);
		}
		else if (selectedMutationOperator == 2)
		{
			//greedy       //intra	
			Intra_Or_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 3)
		{
			//random //intra // replace it with unstringing - stringing
			IntraRouteRandomInsertion.mutate(offspring); 
		}		
		else if (selectedMutationOperator == 4)
		{
			IntraRouteRandomSwap.mutate(offspring);
		}
	}
	
	public void mutatePeriodAssignment(Individual offspring, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		int totalOperators = 2;
		int selectedMutationOperator = Utility.randomIntExclusive(totalOperators);
		
		if (selectedMutationOperator == 0)
		{
			//greedy       //inter period	
			//PatternImprovement.patternImprovementOptimzed(offspring,loadPenaltyFactor,routeTimePenaltyFactor,true);
			PatternImprovementNew.patternImprovement(offspring, loadPenaltyFactor, routeTimePenaltyFactor, true);
			//for test
			//PatternImprovement.patternImprovementOfAllClients(offspring, loadPenaltyFactor, routeTimePenaltyFactor);
		}
		else 
		{
			PatternMutation.mutate(offspring, loadPenaltyFactor, routeTimePenaltyFactor, true);
			//random //inter period
		 //   MutatePeriodAssignment.mutatePeriodAssignment(offspring,loadPenaltyFactor,routeTimePenaltyFactor,true);
		}
	}
	
	public void mutateRouteAssignment(Individual offspring, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		int totalOperators = 2;
		int selectedMutationOperator = Utility.randomIntExclusive(totalOperators);

		if (selectedMutationOperator == 0)
		{
			//greedy //inter
			//GreedyVehicleReAssignment.mutate(offspring,loadPenaltyFactor,routeTimePenaltyFactor);
			
			//CostReducedVehicleReAssignment.mutateFI(offspring, loadPenaltyFactor, routeTimePenaltyFactor);
			Inter_Or_Opt.mutate(offspring, loadPenaltyFactor, routeTimePenaltyFactor);
			//Inter_Or_Opt.mutateSpecificClient(offspring, loadPenaltyFactor, routeTimePenaltyFactor);
			//Inter_Or_Opt.mutateSpecificClientByCheckingHOS(offspring, loadPenaltyFactor, routeTimePenaltyFactor);
		}
		else if (selectedMutationOperator == 1)
		{
			//random+greedy       //inter
			InterOneOneExchange.mutate(offspring);
		}
/*		else if (selectedMutationOperator == 1)
		{
			//random+greedy       //inter
			InterOneOneExchange.mutate(offspring);
		}
*/		
		
		
	/*	else if (selectedMutationOperator == 2)
		{
			//only 1 vehicle per depot // cannot apply 2-opt*
			if(offspring.problemInstance.vehicleCount == offspring.problemInstance.depotCount)
				Inter_Or_Opt.mutate(offspring, loadPenaltyFactor, routeTimePenaltyFactor);
				//Inter_Or_Opt.mutateSpecificClient(offspring, loadPenaltyFactor, routeTimePenaltyFactor);
			else
				Inter_2_Opt.mutate(offspring, loadPenaltyFactor, routeTimePenaltyFactor);
		}
	*/
	}
	
	@Override
 	public void updateWeights() {
		// TODO Auto-generated method stub
		
	}

	
	public static void improveRoute(Individual individual, int period, int vehicle)
	{
	//	Two_Opt.mutateRouteBy2_Opt_with_BestCombination(individual, period, vehicle);
/*
		int coin = Utility.randomIntInclusive(1);
		if( coin == 0)
			Two_Opt.mutateRouteBy2_Opt_with_BestCombination(individual, period, vehicle);
		else if(coin == 1)
			Or_Opt.mutateRouteBy_Or_Opt_withBestMove(individual, period, vehicle);*/
	/*	int coin = Utility.randomIntInclusive(2);
		if( coin == 0)
			Intra_2_Opt.mutateRouteBy2_Opt_with_BestCombination(individual, period, vehicle);
		else if(coin == 1)
			Three_Opt.mutateRouteBy_Three_Opt_with_best_move(individual, period, vehicle);
		else if(coin ==2)
			Intra_Or_Opt.mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, vehicle);
	*/	
		//else
			
			
		Three_Opt.mutateRouteBy_Three_Opt_with_first_better_move_optimized(individual, period, vehicle);
		
		/*
		double cost,newCost;
		do
		{
			cost = RouteUtilities.costForThisRoute(individual.problemInstance, individual.routes.get(period).get(vehicle), vehicle);
			
			
			int coin = Utility.randomIntInclusive(1);
			if(coin==0)
			{
				Three_Opt.mutateRouteBy_Three_Opt_with_best_move(individual, period, vehicle);
				Or_Opt.mutateRouteBy_Or_Opt_withBestMove(individual, period, vehicle);
			}
			else
			{
				Or_Opt.mutateRouteBy_Or_Opt_withBestMove(individual, period, vehicle);
				Three_Opt.mutateRouteBy_Three_Opt_with_best_move(individual, period, vehicle);			
			}
			
			newCost = RouteUtilities.costForThisRoute(individual.problemInstance, individual.routes.get(period).get(vehicle), vehicle);
		}while(newCost<cost);*/
		
	}

	@Override
	public void mutateSpecificRoute(Individual individual, int period, int vehicle) {
		// TODO Auto-generated method stub
		int totalRouteImprovementOperators = 5;
		int selectedMutationOperator = Utility.randomIntExclusive(totalRouteImprovementOperators);
		if(selectedMutationOperator==0)
		{
			//intra       
			Intra_2_Opt.mutateRouteBy2_Opt_with_BestCombination(individual, period, vehicle);
		}
		else if (selectedMutationOperator == 1)
		{			
			//greedy       //intra	
			
			Three_Opt.mutateRouteBy_Three_Opt_with_best_move(individual, period, vehicle);
		}
		else if (selectedMutationOperator == 2)
		{
			//greedy       //intra	
			Inter_Or_Opt.mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, vehicle);
		}
		else if (selectedMutationOperator == 3)
		{
			//random //intra // replace it with unstringing - stringing
			IntraRouteRandomInsertion.mutateRouteWithInsertion(individual, period, vehicle);
		}		
		else if (selectedMutationOperator == 4)
		{
			IntraRouteRandomSwap.mutateRouteBySwapping(individual, period, vehicle);
		}
		
	}
}
