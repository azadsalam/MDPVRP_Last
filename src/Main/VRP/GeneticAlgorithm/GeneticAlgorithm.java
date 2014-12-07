package Main.VRP.GeneticAlgorithm;
import Main.VRP.Individual.Individual;


public interface GeneticAlgorithm 
{
/*	public static int POPULATION_SIZE = -1; 
	public static int NUMBER_OF_OFFSPRING = -1;   
	public static int NUMBER_OF_GENERATION = -1;
	public static double loadPenaltyFactor = 100000;
	public static double routeTimePenaltyFactor = 100000;
*/
	public Individual run();
	public int getNumberOfGeeration();
}
