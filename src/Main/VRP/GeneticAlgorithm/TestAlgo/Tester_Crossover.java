package Main.VRP.GeneticAlgorithm.TestAlgo;


import java.io.PrintWriter;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.GeneticAlgorithm.PopulationInitiator;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.Initialise_ClosestDepot_GENI_GreedyCut;
import Main.VRP.Individual.Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut;
import Main.VRP.Individual.RandomInitialisation;
import Main.VRP.Individual.RandomInitialisationWithCyclicVehicleAssignment;
import Main.VRP.Individual.Crossover.IIC;
import Main.VRP.Individual.Crossover.IIC2;
import Main.VRP.Individual.Crossover.PIX;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation_GreedyCut;


public class Tester_Crossover  implements GeneticAlgorithm
{
	PrintWriter out; 
	
	public static int POPULATION_SIZE = 200; 
	public static int NUMBER_OF_OFFSPRING = 500;   
	public static int NUMBER_OF_GENERATION = 1;

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
	
	
	public Tester_Crossover(ProblemInstance problemInstance) 
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
		routeTimePenaltyFactor = 10;
		
	}

	public Individual run() 
	{
		//problemInstance.print();
		// INITIALISE POPULATION
		
//		initialisePopulation();
		PopulationInitiator.initialisePopulation(population, POPULATION_SIZE, problemInstance);
		TotalCostCalculator.calculateCostofPopulation(population,0,POPULATION_SIZE, loadPenaltyFactor, routeTimePenaltyFactor);
		
		//if(true)return null;
		
		
		/*Individual child = new Individual(problemInstance);
		Individual parent1 = population[0];
		Individual parent2 = population[1];
		IIC2.crossOver(problemInstance, parent1, parent2, child);
		
		if(Solver.showViz==true)
		{
			Solver.visualiser.drawIndividual(parent1, "Parent1");
			Solver.visualiser.drawIndividual(parent2, "Parent2");
			Solver.visualiser.drawIndividual(child, "Child");
		}
		
		if(child.validationTest()==false)
		{
			System.out.println("INVALID BACCHA");
			return null;
		}*/

		
		for(int t=0;t<NUMBER_OF_OFFSPRING;t++)
		{
			Individual child = new Individual(problemInstance);
			int one = Utility.randomIntExclusive(POPULATION_SIZE);
			int two = Utility.randomIntExclusive(POPULATION_SIZE);
			Individual parent1 = population[one];
			Individual parent2 = population[two];
/*			if(Solver.showViz==true)
			{
				Solver.visualiser.drawIndividual(population[one], "Parent1");
				Solver.visualiser.drawIndividual(population[two], "Parent2");
			}
*/			
			IIC2.crossOver(problemInstance, parent1, parent2, child);
			
			if(child.validationTest()==false)
			{
				System.out.println("INVALID BACCHA");
				return null;
			}
		}
		System.out.println("here");
		//Uniform_VariedEdgeRecombnation_GreedyCut.crossOver_Uniform_VariedEdgeRecombination(problemInstance, population[0], population[1],child );

		
		
		
		Utility.sort(population);

		return population[0];
	}
	
	
	

	
	
	void initialisePopulation()
	{
		Individual.calculateAssignmentProbalityForDiefferentDepot(problemInstance);
		Individual.calculateProbalityForDiefferentVehicle(problemInstance);
		//out.print("Initial population : \n");
		for(int i=0; i<POPULATION_SIZE; i++)
		{
			population[i] = new Individual(problemInstance);
			if(i%3==0)
				Initialise_ClosestDepot_GENI_GreedyCut.initialise(population[i]);
			else if(i%3 == 1)
				RandomInitialisationWithCyclicVehicleAssignment.initialiseRandom(population[i]);
			else if(i%3 == 2)
				Initialise_ClosestDepot_withNoLoadViolation_Greedy_cut.initiialise(population[i]);
		
		//	RandomInitialisation.initialiseRandom(population[i]);
			//population[i].initialise_Closest_Depot_Greedy_Cut();
			//out.println("Printing Initial individual "+ i +" : \n");
			TotalCostCalculator.calculateCost(population[i], loadPenaltyFactor, routeTimePenaltyFactor);
			//population[i].print();
		}
	}

	
	public int getNumberOfGeeration() {
		// TODO Auto-generated method stub
		return NUMBER_OF_GENERATION;
	}

}
