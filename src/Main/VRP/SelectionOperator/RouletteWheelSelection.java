package Main.VRP.SelectionOperator;
import Main.Utility;
import Main.VRP.Individual.Individual;


public class RouletteWheelSelection extends SelectionOperator
{

	boolean mark[];
	
	double fitness[];
	double cdf[];
	
	@Override
	public void initialise(Individual[] population, boolean survivalSelection) 
	{
		// TODO Auto-generated method stub
		super.initialise(population, survivalSelection);

		this.survivalSelection = survivalSelection;
		if(survivalSelection) mark = new boolean[population.length];

		
		
		int i,j;

		double sumOfFitness = 0;
		double sumOfProability = 0;

		fitness = new double[population.length];
		cdf = new double[population.length];

		for( i=0;i<population.length;i++)
		{
			fitness[i] = 1 / population[i].costWithPenalty ; // the original fitness			
			sumOfFitness += fitness[i];
		}
		
		for( i=0;i<population.length;i++)
		{
			sumOfProability = cdf[i] = sumOfProability + (fitness[i]/sumOfFitness);
		}

	}
	
	//if survival selection, makes sure that one individual is selected only ones
	@Override
	public Individual getIndividual(Individual[] population) 
	{
		// TODO Auto-generated method stub
		int index;

		if(survivalSelection)
		{
		    do
		    {
		    	index = getIndividualIndex(population);
		    }while(mark[index]==true);
		    
		    mark[index]=true;
		    
		    return population[index];
		}
		else
		{
			index = getIndividualIndex(population);
			return population[index];
		}
	}
	
	public int getIndividualIndex(Individual[] population) 
	{
		double indicator = Utility.randomDouble(0, 1);

		//find the smallest index i, with cdf[i] greater than indicator
		//binary search for smallest index i, having cdf[i] greater than indicator
		
		for(int i=0;i<population.length;i++)
			if(cdf[i]>=indicator)
				return i;
		return population.length-1;

	}
	

}
