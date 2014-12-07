package Main.VRP.GeneticAlgorithm.TestAlgo;

import java.io.PrintWriter;

import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;
import Main.VRP.Individual.MutationOperators.Intra_2_Opt;


public class TestAlgo  implements GeneticAlgorithm
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
	
	
	public TestAlgo(ProblemInstance problemInstance) 
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
		
		Individual parent1,parent2,offspring1,offspring2;

		
		Individual.calculateProbalityForDiefferentVehicle(problemInstance);
		//problemInstance.print();
		// INITIALISE POPULATION
		initialisePopulation();
//		TotalCostCalculator.calculateCostofPopulation(population,0,POPULATION_SIZE, loadPenaltyFactor, routeTimePenaltyFactor);
	

		Individual individual = population[0];
		
		individual.print();
		
		//System.out.printf(" %f\n", RouteUtilities.costForThisRoute(problemInstance, individual.routes.get(0).get(0), 0 ));
		Intra_2_Opt.onAllROute(individual);
		
		//System.out.println(Two_Opt.twoOptSwap(individual.routes.get(0).get(0), 2, 6).toString());
		
		individual.print();
		
		return population[0];

	}
	
	
	

	
	
	void initialisePopulation()
	{
		out.print("Initial population : \n");
		Individual.calculateAssignmentProbalityForDiefferentDepot(problemInstance);
		for(int i=0; i<POPULATION_SIZE; i++)
		{
			population[i] = new Individual(problemInstance);
			//population[i].initialise_Closest_Depot_Greedy_Cut();
			//out.println("Printing individual "+ i +" : \n");
			//population[i].print();
		}
	}

	
	public int getNumberOfGeeration() {
		// TODO Auto-generated method stub
		return NUMBER_OF_GENERATION;
	}

}
