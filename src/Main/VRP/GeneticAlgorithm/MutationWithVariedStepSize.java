package Main.VRP.GeneticAlgorithm;
import Main.Utility;
import Main.VRP.Individual.Individual;


public class MutationWithVariedStepSize 
{	
	int generationCount=0;
	public MutationWithVariedStepSize() {
		// TODO Auto-generated constructor stub
	}
	
	public MutationWithVariedStepSize(int generations) {
		// TODO Auto-generated constructor stub
		generationCount = generations;
	}
	
	
	void applyMutation(Individual offspring,int generation)
	{
//		int max = initialCount - generation / 50;		
		int max = 5;		

		int count = Utility.randomIntInclusive(max);
		
		int maxPeriodLim = offspring.problemInstance.periodCount;
		
		//System.out.println("gen : " + generation + " max : " + max+" count : "+count);		

		int rand = 5;
		if(offspring.problemInstance.periodCount==1)rand--;
		
		for(int i=0;i<count;i++)
		{
			int selectedMutationOperator = Utility.randomIntInclusive(rand);
			
			if(selectedMutationOperator==0)
			{
				//offspring.mutateRoutePartitionWithRandomStepSize();
			}
			else if (selectedMutationOperator == 1)
			{
				
				int c;				
				if(generationCount == 0) c = maxPeriodLim;
				else 
				{
					c = (int)(((double)(maxPeriodLim-1)/(1- generationCount)) * generation + maxPeriodLim);
					//System.out.println("gen : "+generation+" count : "+c);
				}
				//offspring.mutatePermutationMultipleTimesBySwappingAnyTwo(c);
			}
			else if (selectedMutationOperator == 2)
			{
				//offspring.mutatePermutationWithInsertion();
				int c;
				if(generationCount == 0) c = maxPeriodLim;
				else 
				{
					c = (int)(((double)(maxPeriodLim-1)/(1- generationCount)) * generation + maxPeriodLim);
					//System.out.println("gen : "+generation+" count : "+c);
				}
			//	for(int h=0;h<c;h++)
				//	offspring.mutatePermutationWithRotation();
			}
			else if (selectedMutationOperator == 3)
			{
				int c;
				if(generationCount == 0) c = maxPeriodLim;
				else 
				{
					c = (int)(((double)(maxPeriodLim-1)/(1- generationCount)) * generation + maxPeriodLim);
					//System.out.println("gen : "+generation+" count : "+c);
				}
				//for(int h=0;h<c;h++)
				//	offspring.mutatePermutationWithAdjacentSwap();
			}
			else if (selectedMutationOperator == 4)
			{
				int c;
				if(generationCount == 0) c = maxPeriodLim;
				else 
				{
					c = (int)(((double)(maxPeriodLim-1)/(1- generationCount)) * generation + maxPeriodLim);
					//System.out.println("gen : "+generation+" count : "+c);
				}

					//offspring.mutatePermutationWithRotationWithinSingleRoute(c);
			}
			else 
			{
				//offspring.mutatePeriodAssignment();
			}
		}
		
		offspring.calculateCostAndPenalty();
		
	}

}
