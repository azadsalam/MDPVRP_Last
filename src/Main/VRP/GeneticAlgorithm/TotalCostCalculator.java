package Main.VRP.GeneticAlgorithm;
import Main.VRP.Individual.Individual;

public class TotalCostCalculator 
{	
	/**
	 * Enumerates each individual and calculate the cost with penalty for each individual in population array in [start,start+populationSize)
	 * @param population
	 * @param loadPenaltyFactor
	 * @param routeTimePenaltyFactor
	 */
	public static void calculateCostofPopulation(Individual[] population,int start,int populationSize, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		double penalty;
		for(int i=start; i<start+populationSize; i++)
		{
			population[i].calculateCostAndPenalty();
			
			penalty = 0;
			penalty += population[i].totalLoadViolation * loadPenaltyFactor;
			penalty += population[i].totalRouteTimeViolation * routeTimePenaltyFactor;
			//penalty *= (generation+1);
			
			population[i].costWithPenalty = population[i].cost + penalty;
			
		}
	}
	
	/**
	 * Enumerates the individual and calculate the cost with penalty 
	 * @param population
	 * @param loadPenaltyFactor
	 * @param routeTimePenaltyFactor
	 */
	public static void calculateCost(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		double penalty;
		
		individual.calculateCostAndPenalty();
		
		penalty = 0;
		penalty += individual.totalLoadViolation * loadPenaltyFactor;
		penalty += individual.totalRouteTimeViolation * routeTimePenaltyFactor;
		//penalty *= (generation+1);
		
		individual.costWithPenalty = individual.cost + penalty;
			
	}
	
	

}
