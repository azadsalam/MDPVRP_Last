package Main.VRP.LocalImprovement;

import Main.Solver;
import Main.VRP.Individual.Individual;

public abstract class LocalImprovement 
{
	public LocalSearch localSearch;
	int count;
	
	public LocalImprovement(LocalSearch localSearch) 
	{
		// TODO Auto-generated constructor stub
			this.localSearch = localSearch;
	//	count = populationSize/4 ;
	}
	public abstract void initialise(Individual[] population); 

	public void run(Individual[] population)
	{		
		//System.out.print("Count : "+count);

		Individual selected[] = new Individual[count];
		for(int i=0;i<count;i++)
		{
			selected[i] = selectIndividualForImprovement(population);
			//System.out.print(" "+selected[i].costWithPenalty);
			
		}
		
		//System.out.println("");
		
		for (int i = 0; i < count; i++) {
			
			localSearch.improve(selected[i], Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);			
		}
	}
	
	public abstract Individual selectIndividualForImprovement(Individual[] population);
}
