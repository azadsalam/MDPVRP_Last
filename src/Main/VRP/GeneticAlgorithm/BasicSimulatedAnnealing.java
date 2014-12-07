package Main.VRP.GeneticAlgorithm;

import java.io.PrintWriter;

import Main.Solver;
import Main.Utility;
import Main.Visualiser;
import Main.visualize;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.Initialise_ClosestDepot_GENI_GreedyCut;
import Main.VRP.Individual.RandomInitialisation;
import Main.VRP.Individual.RandomInitialisationWithCyclicVehicleAssignment;
import Main.VRP.Individual.Crossover.CrossoverStatistics;
import Main.VRP.Individual.Crossover.Crossover_Uniform_Uniform;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation_GreedyCut;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation_with_No_Load_Crossover;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.RepairProcedure;
import Main.VRP.Individual.MutationOperators.Three_Opt;
import Main.VRP.Individual.MutationOperators.Intra_2_Opt;
import Main.VRP.LocalImprovement.FirstChoiceHillClimbing;
import Main.VRP.LocalImprovement.LocalImprovement;
import Main.VRP.LocalImprovement.LocalImprovementBasedOnFussandElititst;
import Main.VRP.LocalImprovement.LocalSearch;
import Main.VRP.LocalImprovement.SimulatedAnnealing;
import Main.VRP.SelectionOperator.FUSS;
import Main.VRP.SelectionOperator.RouletteWheelSelection;
import Main.VRP.SelectionOperator.SelectionOperator;


public class BasicSimulatedAnnealing implements GeneticAlgorithm
{
	//Algorithm parameters
	public static int POPULATION_SIZE = 1; 
	public static int NUMBER_OF_OFFSPRING = 0;   
	public static int NUMBER_OF_GENERATION = 1000;

	//Algorithm data structures
	Individual population[];
	Individual offspringPopulation[];
	Individual parentOffspringTotalPopulation[];

	//Operators
	MutationInterface mutation;
    SelectionOperator rouletteWheelSelection;
    SelectionOperator fussSelection;
    SelectionOperator survivalSelectionOperator;
 	
	//Utility Functions	
	PrintWriter out; 
	ProblemInstance problemInstance;

	//Temprary Variables
	Individual parent1,parent2;
	

	
	public BasicSimulatedAnnealing(ProblemInstance problemInstance) 
	{
		System.err.println("in basic sa");

		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
		out = problemInstance.out;

		mutation = new RandomMutation();
		
		//Change here if needed
		population = new Individual[POPULATION_SIZE];
		
	}

	public Individual run() 
	{
		int generation;
		

		population[0] = new Individual(problemInstance);
		RandomInitialisation.initialiseRandom(population[0]);
		
		
		TotalCostCalculator.calculateCostofPopulation(population,0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor) ;
		
		
		SimulatedAnnealing sa = new SimulatedAnnealing(mutation,20,0.045, NUMBER_OF_GENERATION, true);
		
		sa.improve(population[0], Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);


	//	TotalCostCalculator.calculateCostofPopulation(population,0,POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		
		if(Solver.printFinalSolutionToFile)
		{
			out.print("\n\n\n\n\n--------------------------------------------------\n");
		//	calculateCostWithPenalty(0, POPULATION_SIZE, generation, true);
			out.print("\n\n\nFINAL POPULATION\n\n");
			for(int i=0;i<POPULATION_SIZE;i++)
			{
				out.println("\n\nIndividual : "+i);
				population[i].print();
			}
		}
		
		if(Solver.outputTrace)
		{
			Solver.outputTraceWriter.println("");
			Solver.outputTraceWriter.flush();
		}
		

		if(Solver.showViz)Solver.visualiser.drawIndividual(population[0], "Best");

		if(Solver.gatherCrossoverStat)
			CrossoverStatistics.print();
		
		
		return population[0];
		

	}
	
	public int getNumberOfGeeration()
	{
		return NUMBER_OF_GENERATION;
	}
}
