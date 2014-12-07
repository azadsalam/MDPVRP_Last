package Main.VRP.GeneticAlgorithm.TestAlgo;


import java.io.PrintWriter;
import java.util.Arrays;

import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.Individual.Individual;
import Main.VRP.SelectionOperator.FUSS;
import Main.VRP.SelectionOperator.SelectionOperator;


public class TestDistance  implements GeneticAlgorithm
{
	PrintWriter out; 
	
	int POPULATION_SIZE = 20;
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
	
	
	public TestDistance(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
		out = problemInstance.out;
		
		population = new Individual[POPULATION_SIZE];
		offspringPopulation = new Individual[NUMBER_OF_OFFSPRING];
		temporaryPopulation = new Individual[NUMBER_OF_GENERATION];
		
		fitness = new double[POPULATION_SIZE];
		cdf = new double[POPULATION_SIZE];
		
		loadPenaltyFactor = 500;
		routeTimePenaltyFactor = 0.6;
		
	}

	public Individual run() 
	{
		
		Individual selectedParent1,selectedParent2;
		//int i;
		
		Individual parent1,parent2,offspring;

		// INITIALISE POPULATION
		initialisePopulation();


		//SelectionOperator so = new FUSS();
		
		for(int i=0;i<POPULATION_SIZE;i+=2)
		{
			if(Individual.isDuplicate(problemInstance,population[i], population[i+1]))
			{
				out.println("--------------------\n--------------------------\nDUPLICATES : \n");
				
				population[i].print();
				population[i+1].print();
				
				out.println("--------------------\n\n");
			}
			else
			{
				out.println("------------------NOT DUPLICATES : \n");
				
				population[i].print();
				population[i+1].print();
				
				out.println("--------------------------------\n\n");
			}
		}
		
		
/*		double[] distances = new double[NUMBER_OF_OFFSPRING];
		for(int generation=0;generation<1;generation++)
		{
			
			for( i=0;i<POPULATION_SIZE;i++)
			{
				//double d = Individual.distance(problemInstance, population[i], population[i]);
				//System.out.println(d);
			}
			
			

			so.initialise(population, false);
			for( i=0;i<NUMBER_OF_OFFSPRING;i++)
			{
					parent1=so.getIndividual(population);
					parent2=so.getIndividual(population);


					out.println("\n\n");
					double d = Individual.distance(problemInstance, parent1, parent2);
					out.println(""+d);
					distances[i]=d;
					parent1.print();
					parent2.print();
					out.println("\n\n");
			}

	

		}

		Arrays.sort(distances);
		for( i=0;i<NUMBER_OF_OFFSPRING;i++)
		{
			out.println(distances[i]);
		}
		
*/		return population[0];

	}
	
	
	void initialisePopulation()
	{
		//out.print("Initial population : \n");
		for(int i=0; i<POPULATION_SIZE; i++)
		{
			population[i] = new Individual(problemInstance);
		//	population[i].initialise_Closest_Depot_Greedy_Cut();
			//out.println("Printing individual "+ i +" : \n");
			//population[i].print();
		}
	}

	public int getNumberOfGeeration() {
		// TODO Auto-generated method stub
		return NUMBER_OF_GENERATION;
	}

}
