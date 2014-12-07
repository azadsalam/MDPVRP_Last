package Main.VRP.GeneticAlgorithm;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;


public class ProportionateMutation 
{	

	ProblemInstance problemInstance;
	double permutationFactor;
	double periodAssignmentFactor;
	double routePartitionFactor;
	double initialPercentage;
	double finalPercentage;
	int max;
	int numberOfGeneration;
	public ProportionateMutation(ProblemInstance problemInstance,double initialPerctg,double finalPerctg,int totalGeneration)
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
		
		initialPercentage = initialPerctg;
		finalPercentage = finalPerctg;
		numberOfGeneration = totalGeneration;
		
		permutationFactor  =( problemInstance.periodCount  * problemInstance.customerCount * (problemInstance.customerCount-1) ) / 2; 
		//BIASED
		routePartitionFactor = problemInstance.periodCount * problemInstance.customerCount *5;
		periodAssignmentFactor = (problemInstance.customerCount * problemInstance.periodCount * (problemInstance.periodCount-1))/2;
		
		max = (int)permutationFactor + (int)routePartitionFactor + (int)periodAssignmentFactor;
		
		double total = permutationFactor+routePartitionFactor+periodAssignmentFactor;
		
		permutationFactor /= total;
		periodAssignmentFactor /= total;
		routePartitionFactor /= total;
		
		System.out.println(" perm : "+ permutationFactor +  " period " + periodAssignmentFactor + " route "+ routePartitionFactor + " total : "+max);
		
	}
	
	void applyMutation(Individual offspring,int generation)
	{
//		int max = initialCount - generation / 50;		
		
		double percentage = initialPercentage - (initialPercentage-finalPercentage)*generation/numberOfGeneration ;
		
		double count = max * percentage/100;
		
		int periodCount = (int)(count * periodAssignmentFactor);
		int permutationCount  =(int)( count *  permutationFactor);
		int routeCount = (int)( count * routePartitionFactor);
		
		if(periodCount<=0)periodCount=1;
		if(permutationCount<=0)permutationCount=1;
		if(routeCount<=0)routeCount=1;
		
		if(problemInstance.periodCount==1)periodCount=0;

	//	System.out.println("% "+ percentage+" period " + periodCount + " perm : " + permutationCount+" rout : "+routeCount);		


		//for(int i =0 ;i<periodCount;i++)
		//	offspring.mutatePeriodAssignment();
		//for(int i=0;i<permutationCount;i++)
			//offspring.mutatePermutation();
		//for(int i=0;i<routeCount;i++)
			//offspring.mutateRoutePartition();
		
		offspring.calculateCostAndPenalty();		
	}

}
