package Main.VRP.GeneticAlgorithm.TestAlgo;

import java.io.PrintWriter;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.GeneticAlgorithm.Mutation;
import Main.VRP.GeneticAlgorithm.PopulationInitiator;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.LocalImprovement.FirstChoiceHillClimbing;
import Main.VRP.LocalImprovement.LocalSearch;


public class LocalSearchTestAlgo  implements GeneticAlgorithm
{
	PrintWriter out; 
	
	int POPULATION_SIZE = 10;
	int NUMBER_OF_OFFSPRING = 10;
	int NUMBER_OF_GENERATION = 1;
	
	ProblemInstance problemInstance;
	Individual population[];

	//for storing new generated offsprings
	Individual offspringPopulation[];

	//for temporary storing
	Individual temporaryPopulation[];

	// for selection - roulette wheel
	double fitness[];
	double cdf[];

	double loadPenaltyFactor;
	double routeTimePenaltyFactor;
	
	
	public LocalSearchTestAlgo(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
		out = problemInstance.out;
		
		population = new Individual[POPULATION_SIZE];
		offspringPopulation = new Individual[NUMBER_OF_OFFSPRING];
		temporaryPopulation = new Individual[NUMBER_OF_GENERATION];
		
		fitness = new double[POPULATION_SIZE];
		cdf = new double[POPULATION_SIZE];
		
		loadPenaltyFactor = 10;
		routeTimePenaltyFactor = 1;
		
	}

	public Individual run() 
	{
		
		int selectedParent1,selectedParent2;
		int i;
		
		Individual parent1,parent2,offspring;

		LocalSearch localSearch = new FirstChoiceHillClimbing(new Mutation());
		
		// INITIALISE POPULATION
		PopulationInitiator.initialisePopulation(population, POPULATION_SIZE, problemInstance);
		TotalCostCalculator.calculateCostofPopulation(population,0,POPULATION_SIZE, loadPenaltyFactor, routeTimePenaltyFactor);
		Utility.sort(population);
		for(int generation=0;generation<1;generation++)
		{

			for( i=0;i<POPULATION_SIZE;i++)
			{
				double prev = population[i].costWithPenalty;
				localSearch.improve(population[i], loadPenaltyFactor, routeTimePenaltyFactor);
				double newf= population[i].costWithPenalty;	
				System.out.println("Prev : "+prev+" New : "+newf+ " Imrvmnt : "+(prev-newf));
			}
		}

		return population[0];

	}
	
	
	@Override
	public int getNumberOfGeeration() {
		// TODO Auto-generated method stub
		return NUMBER_OF_GENERATION;
	}

}
