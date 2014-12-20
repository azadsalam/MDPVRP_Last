package Main.VRP.LocalImprovement;

import Main.Solver;
import Main.VRP.Individual.Individual;
import Main.VRP.SelectionOperator.BinaryTournament;
import Main.VRP.SelectionOperator.FUSS;
import Main.VRP.SelectionOperator.SelectionOperator;


public class LocalImprovementBasedOnBT extends LocalImprovement 
{
	public static boolean tmp=true;
	public LocalImprovementBasedOnBT(LocalSearch localSearch) {
		// TODO Auto-generated constructor stub
		super(localSearch);

	}
	public LocalImprovementBasedOnBT(double loadPenaltyFactor,
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
		
		count = (int) Math.round((population.length*Solver.localSearchProportion));
		if(count>=population.length) count=population.length-1;
		
		if(tmp)
		{
			tmp=false;
			System.out.println("Number of individual under local learning: "+count);
		}
		selectionOperator = new BinaryTournament();
		selectionOperator.initialise(population, true);
	}

	@Override
	public Individual selectIndividualForImprovement(Individual[] population) {
		// TODO Auto-generated method stub
		return selectionOperator.getIndividual(population);
	}


}
