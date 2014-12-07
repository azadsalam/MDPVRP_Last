package Main.VRP.Individual.MutationOperators;

import java.util.ArrayList;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class MutatePeriodAssignment {

	
	/** do not updates cost + penalty
	 if sobgula client er frequency = period hoy tahole, period assignment mutation er kono effect nai
	*/
	public static void mutatePeriodAssignment(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor,boolean improveResultantRoute)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		boolean success;
		int clientNo;
		int total = problemInstance.customerCount;
		do
		{
			clientNo = Utility.randomIntInclusive(problemInstance.customerCount-1);
			success = mutatePeriodAssignment(individual,clientNo,loadPenaltyFactor,routeTimePenaltyFactor,improveResultantRoute);
			total--;
		}while(success==false && total>0);
	
	}
	
	//returns 0 if it couldnt mutate as period == freq
	//need to edit this- must repair 
	private static boolean mutatePeriodAssignment(Individual individual, int clientNo, double loadPenaltyFactor, double routeTimePenaltyFactor,boolean improveResultantRoute)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
		ArrayList<Integer> allPossibleCombinationsForThisClient =  problemInstance.allPossibleVisitCombinations.get(clientNo);
		
		int size = allPossibleCombinationsForThisClient.size();
		if( size == 1) return false;
		
		
		//problemInstance.out.println("Client "+clientNo);
		
		int previousCombination = individual.visitCombination[clientNo];
		int newCombination = previousCombination;
		//int size = 
		
		while(newCombination == previousCombination)
		{
			int ran = Utility.randomIntInclusive(size-1);
			newCombination = allPossibleCombinationsForThisClient.get(ran);
		}
		
		individual.visitCombination[clientNo] = newCombination; 
		
		//remove the client from previous combinations
		// add client to new combinations
		int[] bitArrayPrev = problemInstance.toBinaryArray(previousCombination);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(individual.periodAssignment[period][clientNo]) 
			{
				PatternImprovement.removeClientFromPeriod(individual, period, clientNo,improveResultantRoute);
				//individual.periodAssignment[period][clientNo] = false;
			}	
		}
		
		int[] newBitArray = problemInstance.toBinaryArray(newCombination);
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			if(newBitArray[period]==1) 
			{
				PatternImprovement.addClientIntoPeriodGreedy(individual, period, clientNo,loadPenaltyFactor,routeTimePenaltyFactor,improveResultantRoute);
				individual.periodAssignment[period][clientNo] = true;
			}	
			else
			{
				individual.periodAssignment[period][clientNo] = false;
			}
		}
		
		return true;
	}
	
	
}
