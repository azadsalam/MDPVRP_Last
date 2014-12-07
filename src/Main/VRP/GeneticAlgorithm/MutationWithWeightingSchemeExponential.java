package Main.VRP.GeneticAlgorithm;
import java.util.Arrays;

import org.omg.CORBA.portable.ApplicationException;

import Main.Solver;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomInsertion;
import Main.VRP.Individual.MutationOperators.IntraRouteGreedyInsertion;
import Main.VRP.Individual.MutationOperators.MutatePeriodAssignment;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomSwap;
import Main.VRP.Individual.MutationOperators.InterOneOneExchange;
import Main.VRP.Individual.MutationOperators.OneZeroExchangePrev;
import Main.VRP.Individual.MutationOperators.Inter_Or_Opt;
import Main.VRP.Individual.MutationOperators.Three_Opt;
import Main.VRP.Individual.MutationOperators.Intra_2_Opt;
import Main.VRP.Individual.MutationOperators.*;

public class MutationWithWeightingSchemeExponential implements MutationInterface
{	
	double weights[],probabilities[];
	double cumulativeProbability[];
	int totalOperators;
	
	double oldRatio = 0.75;
	double newRatio = 0.25;
	
	double exponentialLembda=2.5;

	int [] applicationCount;
	double [] episodicImprovementRatio;
	public MutationWithWeightingSchemeExponential()
	{
		totalOperators=Solver.numberOfmutationOperator;
		
		applicationCount = new int[totalOperators];
		episodicImprovementRatio = new double[totalOperators];
		
		//INIT episodic variables
		initEpsiodicVariables();
		
		weights=new double[Solver.numberOfmutationOperator];
		probabilities=new double[Solver.numberOfmutationOperator];
		cumulativeProbability=new double[Solver.numberOfmutationOperator];
		
		Arrays.fill(weights, 1.0/totalOperators);
		Arrays.fill(probabilities, 1.0/totalOperators);
		
		cumulativeProbability[0]=probabilities[0];
		
		for(int i=1;i<totalOperators;i++) 
		{
			cumulativeProbability[i]=cumulativeProbability[i-1]+probabilities[i];
		}
	}
	
	private void initEpsiodicVariables() 
	{
		Arrays.fill(applicationCount, 0);
		Arrays.fill(episodicImprovementRatio, 0);
		
	}
	public void updateWeights()
	{
		/*System.out.print("Count : ");
		for(int i=0;i<totalOperators;i++) 
			System.out.print(applicationCount[i]+" ");
		System.out.println();
*/
		
/*		System.out.print("Weights Before : ");
		for(int i=0;i<totalOperators;i++) 
			System.out.print(weights[i]+" ");
		System.out.println();
*/		
		
		//UPDATE WEIGHT
//		System.out.print("DEL Weights  : ");
	    for(int i=0;i<totalOperators;i++)
	    {
	    	//System.out.println(" "+ applicationCount[i]);
	    	if(applicationCount[i]==0) continue;
	    	
	    	
	    	double avgImrpvementRatio = (episodicImprovementRatio[i] / applicationCount[i]);
	    	
//	    	System.out.print(avgImrpvementRatio+" ");
	    	//weights[i] = oldRatio * weights[i] + newRatio * avgImrpvementRatio;
	    	weights[i]= weights[i]*Math.pow(exponentialLembda, avgImrpvementRatio);
	    }
//		System.out.println();

		
/*	    System.out.print("Weights After : ");
		for(int i=0;i<totalOperators;i++) 
			System.out.print(weights[i]+" ");
		System.out.println();
*/		
		
	    updateProbabilities();
		//INIT episodic variables
		initEpsiodicVariables();
	}
	
	private void updateProbabilities() 
	{		
/*		System.out.print("Probabilities Before : ");
		for(int i=0;i<totalOperators;i++) 
			System.out.print(probabilities[i]+" ");
		System.out.println();
*/		
		double sum = 0;
		
		for(int i=0;i<totalOperators;i++) sum += weights[i];
		
		for(int i=0;i<totalOperators;i++) probabilities[i] = weights[i] / sum;
		
		cumulativeProbability[0]=probabilities[0];
		
		for(int i=1;i<totalOperators;i++) 
		{
			cumulativeProbability[i]=cumulativeProbability[i-1]+probabilities[i];
		}
		
/*		System.out.print("Probabilities After : ");
		for(int i=0;i<totalOperators;i++) 
			System.out.print(probabilities[i]+" ");
		System.out.println();
*/	}
	
	public void applyMutation(Individual offspring)
	{
		/*double randNumber=Utility.randomDouble(0, 1);
		int appliedOperator=-1;
		
		double oldCost =  offspring.costWithPenalty;
		
		for(int i=0;i<totalOperators;i++)
		{
			if(cumulativeProbability[i]>randNumber)
			{
				int selectedMutationOperator = i;
				
				if(selectedMutationOperator==0)
				{
					//greedy //intra
					IntraRouteGreedyInsertion.mutate(offspring);
				}
				else if (selectedMutationOperator == 1)
				{			
					//greedy //inter
					GreedyVehicleReAssignment.mutate(offspring);
				}
				else if (selectedMutationOperator == 2)
				{
					//random //inter
					OneZeroExchange.mutate(offspring);
//					offspring.mutateRouteWithInsertion();
				}
				else if (selectedMutationOperator == 3)
				{
					//random //intra
					IntraRouteRandomInsertion.mutate(offspring);
				}
				else if (selectedMutationOperator == 4)
				{
					//intra       
					Two_Opt.mutateRandomRoute(offspring);
				}
				else if (selectedMutationOperator == 5)
				{
					//random+greedy       //inter
					OneOneExchange.mutate(offspring);
				}
				else if (selectedMutationOperator == 6)
				{
					//greedy       //inra	
					Or_Opt.mutateRandomRoute(offspring);
				}
				else if (selectedMutationOperator == 7)
				{
					//greedy       //inra	
					Three_Opt.mutateRandomRoute(offspring);
				}
				else 
				{
					//random //inter
				   MutatePeriodAssignment.mutatePeriodAssignment(offspring);
				}
				appliedOperator = i;
				
				break;
			}
			
			
		}
		
		
		
		TotalCostCalculator.calculateCost(offspring, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
		double newCost = offspring.costWithPenalty;

		
		
		if(newCost<oldCost)
		{
			double improvementRatio = (oldCost - newCost)/oldCost;
			//System.out.println(improvementRatio+" is by operator "+appliedOperator);
			episodicImprovementRatio[appliedOperator] += improvementRatio;
			applicationCount[appliedOperator]++;
			//System.out.println(appliedOperator+ " -> new count ->  "+ applicationCount[appliedOperator] );
		}
		else
		{
			//System.out.println("couldnt improve "+newCost);
		}
*/	}

}
