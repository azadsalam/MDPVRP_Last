package Main.VRP.LocalImprovement;

import Main.VRP.GeneticAlgorithm.Mutation;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.Three_Opt;


public class FirstChoiceHillClimbing extends LocalSearch {

	MutationInterface mutaion;
	
	public FirstChoiceHillClimbing(MutationInterface mutation) {
		// TODO Auto-generated constructor stub
		
		this.mutaion = mutation;
		//System.err.println("FIRST CHOICE HILL CLIMB, RETRY = 7");
		
	}
	@Override
	public void improve(Individual initialNode, double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		// TODO Auto-generated method stub

		//Mutation mutation = new Mutation();
		int retry=0;
		
		Individual node,neighbour;
		node = new Individual(initialNode);
		
		
		while(retry<7)
		{			
			neighbour = new Individual(node);
			applyMutation(neighbour);
			TotalCostCalculator.calculateCost(neighbour, loadPenaltyFactor, routeTimePenaltyFactor);
			
			//better
			if(neighbour.costWithPenalty <= node.costWithPenalty)
			{
				node = neighbour;
				retry=0;
			}
			else
			{
				retry++;
			}
		}
		
        //Three_Opt.onAllROute(node);
		initialNode.copyIndividual(node);
		
	}

	
	void applyMutation(Individual offspring)
	{
			mutaion.applyMutation(offspring);
	}
}
