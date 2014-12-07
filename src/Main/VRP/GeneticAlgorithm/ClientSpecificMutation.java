package Main.VRP.GeneticAlgorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import Main.Solver;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.OneOneExchange;
import Main.VRP.Individual.MutationOperators.OneZeroExchange;

/*
 * SELECTs a client in a period
 */
public class ClientSpecificMutation implements MutationInterface
{

	@Override
	public void applyMutation(Individual offspring) {
		// TODO Auto-generated method stub
		
		//for now select period-client pair uniformly
		
		while(true)
		{
			int period = Utility.randomIntExclusive(offspring.problemInstance.periodCount);
			int client = Utility.randomIntExclusive(offspring.problemInstance.customerCount);
			
			if(offspring.periodAssignment[period][client] == false) continue;
//			if(offspring.isInHallOfShamePC(period, client)==true) continue;

			
			boolean success=applyMutation(offspring, period, client,Solver.routeTimePenaltyFactor,Solver.loadPenaltyFactor);
			
			if(!success)
			{
				offspring.addToHOS_PC(period, client);
			}

			break;
		
		}
	}

	
	
	public boolean applyMutation(Individual offspring, int period, int client, double loadPenaltyFactor, double routeTimePenaltyFactor) 
	{
		// TODO Auto-generated method stub
		
	//	System.out.println("Here");
		int numberOfOperators=2;
		
		int opArray[] = new int[numberOfOperators];
		for(int i=0;i<numberOfOperators;i++) opArray[i]=i;
		fisherYatesShuffle(opArray);
		
		boolean success;
//		System.out.println(opArray.toString());
		for(int i=0;i<numberOfOperators;i++)
		{
			//apply operator number i
//			System.out.println(opArray[i]);
			
			switch (opArray[i]) {
			case 0:
				success = OneZeroExchange.mutateClientFI(offspring, period, client, loadPenaltyFactor, routeTimePenaltyFactor);
				if(success)return true;
				break;

			case 1:
				success = OneOneExchange.mutateClientFI(offspring, period, client, loadPenaltyFactor, routeTimePenaltyFactor);
				if(success)return true; 
				break;
				
			default:
				break;
			}
		}
		return false;
	}
	
	private void fisherYatesShuffle(int[] array)
	{
	    int index;
	    Random random = Utility.randomGenerator;
	    for (int i = array.length - 1; i > 0; i--)
	    {
	        index = random.nextInt(i + 1);
	        if (index != i)
	        {
	            array[index] ^= array[i];
	            array[i] ^= array[index];
	            array[index] ^= array[i];
	        }
	    }
	}

	@Override
	public void updateWeights() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mutateSpecificRoute(Individual individual, int period,
			int vehicle) {
		// TODO Auto-generated method stub
		
	}


	
}
