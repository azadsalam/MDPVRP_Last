package Main.VRP.SelectionOperator;
import Main.Utility;
import Main.VRP.Individual.Individual;


public class FUSS extends SelectionOperator
{
	double min,max;
	boolean survivalSelection;
	boolean mark[];
	
	/**
	 * Sorts the population
	 * 
	 */
	@Override
	public void initialise(Individual[] population, boolean survivalSelection) {
		// TODO Auto-generated method stub
		super.initialise(population,survivalSelection);
		
		Utility.sort(population);
		
		min = population[0].costWithPenalty;
		max = population[population.length-1].costWithPenalty;
		
		this.survivalSelection = survivalSelection;
		if(survivalSelection) mark = new boolean[population.length];
		
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
		// TODO Auto-generated method stub
		
	    double randomFitness = Utility.randomDouble(min, max);
	    int i; 
	    for(i=0;i<population.length;i++)
	    {
	    	if(population[i].costWithPenalty > randomFitness)
	    		break;
	    }
		
	    if(i == population.length) return Utility.randomIntInclusive(0,i-1);
	    else if( Math.abs(population[i].costWithPenalty - randomFitness) < Math.abs(population[i-1].costWithPenalty - randomFitness) )
	    	return i;
	    else
	    	return (i-1);
	    
	}

}
