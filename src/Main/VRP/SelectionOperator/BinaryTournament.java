package Main.VRP.SelectionOperator;
import Main.Utility;
import Main.VRP.Individual.Individual;


public class BinaryTournament extends SelectionOperator
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
		int randIndex1 = Utility.randomIntInclusive(population.length-1);
		int randIndex2=randIndex1;
		
		while(randIndex1 == randIndex2)
		{
			randIndex2 = Utility.randomIntInclusive(population.length-1);
		}
		
		
		if(population[randIndex1].costWithPenalty < population[randIndex2].costWithPenalty)
		{
			//System.out.println("Chosen 1st - Index 1 "+randIndex1+ " costWithPenalty" + population[randIndex1].costWithPenalty+" index 2"+randIndex2 +"cost"+ population[randIndex2].costWithPenalty);
			return randIndex1;
		}
		else
		{
			//System.out.println("Chosen 2nd - Index 1 "+randIndex1+ " costWithPenalty" + population[randIndex1].costWithPenalty+" index 2"+randIndex2 +"cost"+ population[randIndex2].costWithPenalty);

			return randIndex2;
		}
		
	}

}
