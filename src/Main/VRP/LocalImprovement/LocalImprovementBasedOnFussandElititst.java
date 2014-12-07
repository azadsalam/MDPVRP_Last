package Main.VRP.LocalImprovement;

import Main.Solver;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.SelectionOperator.FUSS;
import Main.VRP.SelectionOperator.SelectionOperator;


public class LocalImprovementBasedOnFussandElititst extends LocalImprovement 
{
	public LocalImprovementBasedOnFussandElititst(LocalSearch localSearch) 
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
		//5% elitist
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		elitistCount = (int)(population.length  * Solver.LocalImprovementElitistRation) ;
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		count = (int)(population.length*33.0/100);
		if(elitistCount>count) count=elitistCount;
		//elitistCount = (int) (count * Solver.ServivorElitistRation);
		
		Utility.sort(population);
		
		// TODO Auto-generated method stub
		Individual selected[] = new Individual[count];
		
		//System.out.println("Count : "+count+" Elitist COunt : "+ elitistCount + " pop : "+population.length   );
		
		int i;
		for(i=0;i<elitistCount;i++)
		{
			selected[i] = population[i];
		}
		
		Individual[] newPop = new Individual[population.length - elitistCount];
		
		for(i=elitistCount;i<population.length;i++)
		{
			newPop[i-elitistCount] = population[i];
		}
		selectionOperator = new FUSS();
		selectionOperator.initialise(newPop, true); // true= wont select same individual twice
	
		for(i=elitistCount;i<count;i++)
		{
			selected[i] = selectIndividualForImprovement(newPop);
			//System.out.print(" "+selected[i].costWithPenalty);
			
		}
		for ( i = 0; i < count; i++) {
			localSearch.improve(selected[i], Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);			
		}

	}
	@Override
	public Individual selectIndividualForImprovement(Individual[] population) {
		// TODO Auto-generated method stub
		return selectionOperator.getIndividual(population);
	}


}
