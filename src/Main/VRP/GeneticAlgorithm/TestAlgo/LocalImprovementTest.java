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
import Main.VRP.LocalImprovement.LocalImprovement;
import Main.VRP.LocalImprovement.LocalImprovementBasedOnFussandElititst;


public class LocalImprovementTest  implements GeneticAlgorithm
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
	
	
	public LocalImprovementTest(ProblemInstance problemInstance) 
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

		
		
		
		
		
		// INITIALISE POPULATION
		PopulationInitiator.initialisePopulation(population, POPULATION_SIZE, problemInstance);
		TotalCostCalculator.calculateCostofPopulation(population,0,POPULATION_SIZE, loadPenaltyFactor, routeTimePenaltyFactor);
	
		LocalImprovement li = new LocalImprovementBasedOnFussandElititst(new FirstChoiceHillClimbing(new Mutation()));
		
		
		double prev[] = new double[POPULATION_SIZE];
		double imp[] = new double[POPULATION_SIZE];
				
		Utility.sort(population);
		for(i=0;i<POPULATION_SIZE;i++)
		{
			System.out.print(" "+population[i].costWithPenalty);
			prev[i] = population[i].costWithPenalty;
		}
		System.out.println();

		
		li.initialise(population);
		li.run(population);
	
		
		TotalCostCalculator.calculateCostofPopulation(population,0,POPULATION_SIZE, loadPenaltyFactor, routeTimePenaltyFactor);
		
		for(i=0;i<POPULATION_SIZE;i++)
		{
			System.out.print(" "+population[i].costWithPenalty);
			imp[i] = population[i].costWithPenalty;
		}
		System.out.println();
		
		for(i=0;i<POPULATION_SIZE;i++)
		{
			System.out.print(" "+(prev[i] - imp[i]));
		}
		System.out.println();


		return population[0];

	}
	
	@Override
	public int getNumberOfGeeration() {
		// TODO Auto-generated method stub
		return NUMBER_OF_GENERATION;
	}

}
