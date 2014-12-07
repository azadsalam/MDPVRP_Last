package Main.VRP.Individual;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;

public class InitialisePeriodAssigmentUniformly 
{

	public static void initialise(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		for(int client=0; client < problemInstance.customerCount; client++)
		{
			ArrayList <Integer> possiblilities =  problemInstance.allPossibleVisitCombinations.get(client);

			
			int size = possiblilities.size();
			int ran = Utility.randomIntInclusive(size-1);
				
			individual.visitCombination[client] = possiblilities.get(ran);
			
			int[] bitArray = problemInstance.toBinaryArray(individual.visitCombination[client]);
			for(int period = 0;period<problemInstance.periodCount;period++)
			{
				boolean bool ;
				
				if(bitArray[period]==1) bool = true;
				else bool = false;
				
				individual.periodAssignment[period][client] = bool; 
			}
			//System.out.println("At initialisation : Client : "+client+" Combinations " + Arrays.toString(possiblilities.toArray()));
			//System.out.println("Assigned Combination " + visitCombination[client]);
					
		}
	}
	
	

}
