package Main.VRP.Individual.Crossover;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;


public class UniformCrossoverPeriodAssigment 
{

	public static  void uniformCrossoverForPeriodAssignment(Individual child1,Individual child2, Individual parent1, Individual parent2,ProblemInstance problemInstance)
	{
		int coin;
		int client;
		
		Individual temp1,temp2;
		for(client=0;client<problemInstance.customerCount;client++)
		{
			coin = Utility.randomIntInclusive(1);
			
			if(coin==0)
			{
				temp1=child1;
				temp2=child2;
			}
			else
			{
				temp1=child2;
				temp2=child1;
			}	
			
			temp1.visitCombination[client] = parent1.visitCombination[client];
			temp2.visitCombination[client] = parent2.visitCombination[client];
			for(int period = 0; period<problemInstance.periodCount; period++)
			{
				//if(parent1==null)System.out.print("nul");
				temp1.periodAssignment[period][client] = parent1.periodAssignment[period][client];
				temp2.periodAssignment[period][client] = parent2.periodAssignment[period][client];
			}
		}
		
	}

	public static  void uniformCrossoverForPeriodAssignment(Individual child, Individual parent1, Individual parent2,ProblemInstance problemInstance)
	{
		int coin;
		

		for(int client=0;client<problemInstance.customerCount;client++)
		{
			coin = Utility.randomIntInclusive(1);
			
			if(coin==0)
			{
				child.visitCombination[client] = parent1.visitCombination[client];
			}
			else
			{
				child.visitCombination[client] = parent2.visitCombination[client];	
			}
			
			for(int period = 0; period<problemInstance.periodCount; period++)
			{	
				if(coin==0)
				{
					child.periodAssignment[period][client] = parent1.periodAssignment[period][client];
				}
				else
				{
					child.periodAssignment[period][client] = parent2.periodAssignment[period][client];
				}
				
			}
		}
		
	}

}
