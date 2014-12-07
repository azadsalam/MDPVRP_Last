package Main.VRP.LocalImprovement;

import Main.Solver;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.SelectionOperator.FUSS;
import Main.VRP.SelectionOperator.SelectionOperator;


public class LocalImprovementAll extends LocalImprovement 
{
	public LocalImprovementAll(LocalSearch localSearch) 
	{
		super( localSearch);
		// TODO Auto-generated constructor stub
		
		//count = populationSize/4;
	}

	SelectionOperator selectionOperator;
	int elitistCount;

	@Override
	public void initialise(Individual[] population) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(Individual[] population) 
	{
		for (int i = 0; i < population.length; i++) {
			localSearch.improve(population[i], Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);			
		}

	}
	@Override
	public Individual selectIndividualForImprovement(Individual[] population) {
		// TODO Auto-generated method stub
		return selectionOperator.getIndividual(population);
	}


}
