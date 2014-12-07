package Main.VRP.LocalImprovement;

import Main.VRP.Individual.Individual;
import Main.VRP.SelectionOperator.FUSS;
import Main.VRP.SelectionOperator.SelectionOperator;


public class LocalImprovementBasedOnFuss extends LocalImprovement 
{
	public LocalImprovementBasedOnFuss(LocalSearch localSearch) {
		// TODO Auto-generated constructor stub
		super(localSearch);

	}
	public LocalImprovementBasedOnFuss(double loadPenaltyFactor,
			double routeTimePenaltyFactor, LocalSearch localSearch,
			int populationSize) {
		super( localSearch);
		// TODO Auto-generated constructor stub
		
		count = populationSize/2;
	}

	SelectionOperator selectionOperator;
	@Override
	public void initialise(Individual[] population) {
		// TODO Auto-generated method stub
		
		count = population.length/2;
		selectionOperator = new FUSS();
		selectionOperator.initialise(population, true);
	}

	@Override
	public Individual selectIndividualForImprovement(Individual[] population) {
		// TODO Auto-generated method stub
		return selectionOperator.getIndividual(population);
	}


}
