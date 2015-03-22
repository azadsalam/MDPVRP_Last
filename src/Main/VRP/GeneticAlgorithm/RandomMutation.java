package Main.VRP.GeneticAlgorithm;
import Main.Solver;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomInsertion;
import Main.VRP.Individual.MutationOperators.IntraRouteGreedyInsertion;
import Main.VRP.Individual.MutationOperators.MutatePeriodAssignment;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomSwap;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.InterOneOneExchange;
import Main.VRP.Individual.MutationOperators.OneZeroExchangePrev;
import Main.VRP.Individual.MutationOperators.Inter_Or_Opt;
import Main.VRP.Individual.MutationOperators.PatternImprovement;
import Main.VRP.Individual.MutationOperators.PatternMutation;
import Main.VRP.Individual.MutationOperators.Three_Opt;
import Main.VRP.Individual.MutationOperators.Intra_2_Opt;


public class RandomMutation implements MutationInterface
{	
	//ProblemInstance problemInstance;
	
	
	/*
	public Mutation(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
	}
	*/
	
	
	public void applyMutation(Individual offspring)
	{
		int rand = 3;
		if(offspring.problemInstance.periodCount==1)rand-=1;
		
		int selectedMutationOperator = Utility.randomIntExclusive(rand);
		
		if(selectedMutationOperator==0)
		{
			OneZeroExchangePrev.interRouteOneZeroExchange(offspring,false, false);
		}
		else if(selectedMutationOperator==1)
		{
			IntraRouteRandomInsertion.mutate(offspring);
		}		
		else
		{
			PatternMutation.mutate(offspring, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor, true);
			//MutatePeriodAssignment.mutatePeriodAssignment(offspring, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor,false);
		}
		
		TotalCostCalculator.calculateCost(offspring, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
	}
	
	public void applyMutationBasic(Individual offspring)
	{
		int rand = 2;
		if(offspring.problemInstance.periodCount==1)rand-=1;
		
		int selectedMutationOperator = Utility.randomIntExclusive(rand);
		
		if(rand==0)
		{
			OneZeroExchangePrev.oneZeroExchangeIntra_and_Inter_both(offspring,false, false);
		}
		else
		{
			
			MutatePeriodAssignment.mutatePeriodAssignment(offspring, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor,false);
		}
		
		TotalCostCalculator.calculateCost(offspring, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
	}

	@Override
	public void updateWeights() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mutateSpecificRoute(Individual individual, int period,
			int vehicle) {
		// TODO Auto-generated method stub
		
	}

}
