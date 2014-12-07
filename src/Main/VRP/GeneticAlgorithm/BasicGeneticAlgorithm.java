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


public class BasicGeneticAlgorithm implements GeneticAlgorithm
{
	//Algorithm parameters
	public static int POPULATION_SIZE = 50; 
	public static int NUMBER_OF_OFFSPRING = 50;   
	public static int NUMBER_OF_GENERATION = 100;

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
	

	
	public BasicGeneticAlgorithm(ProblemInstance problemInstance) 
	{
		System.err.println("in basic ga");

		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
		out = problemInstance.out;

		mutation = new BasicRandomMutation();
		
		//Change here if needed
		population = new Individual[POPULATION_SIZE];
		offspringPopulation = new Individual[NUMBER_OF_OFFSPRING];		
		parentOffspringTotalPopulation = new Individual[POPULATION_SIZE + NUMBER_OF_OFFSPRING];
		
		//Add additional code here
		rouletteWheelSelection = new RouletteWheelSelection();
	    fussSelection = new FUSS();
		survivalSelectionOperator = new RouletteWheelSelection(); 

		
	}

	public Individual run() 
	{
		int generation;
		
		Individual offspring1,offspring2;


		//initialise randomly
		for(int i=0;i<POPULATION_SIZE;i++)
		{
			population[i] = new Individual(problemInstance);
			RandomInitialisation.initialiseRandom(population[i]);
		}
		
		TotalCostCalculator.calculateCostofPopulation(population,0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor) ;
		
		
		for( generation=0;generation<NUMBER_OF_GENERATION;generation++)
		{
			
			//For collecting min,max,avg
			
			Solver.gatherExcelData(population, POPULATION_SIZE, generation);
			TotalCostCalculator.calculateCostofPopulation(population,0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor) ;
			
			//fussSelection.initialise(population, false);
			rouletteWheelSelection.initialise(population, false);

			int i = 0; 
			while(i<NUMBER_OF_OFFSPRING)
			{
				parent1 = rouletteWheelSelection.getIndividual(population);
				parent2 = rouletteWheelSelection.getIndividual(population);
				
				offspring1 = new Individual(problemInstance);				
								
				Uniform_VariedEdgeRecombnation.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);
				
				mutation.applyMutation(offspring1);
				
				offspringPopulation[i] = offspring1;
				i++;
								
			}

			TotalCostCalculator.calculateCostofPopulation(offspringPopulation, 0,NUMBER_OF_OFFSPRING, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor) ;
			Utility.concatPopulation(parentOffspringTotalPopulation, population, offspringPopulation);
			
			
			if(Solver.checkForInvalidity)
			{
				for(int p=0;p<parentOffspringTotalPopulation.length;p++)
				{
					if(parentOffspringTotalPopulation[p].validationTest()==false)
					{
						System.err.println("ERROR\nERROR\nERROR\nIndividual is invalid!!!"+" gen : "+generation+" index : "+ p);
						out.println("\n\nINVALID INDIVIDUAL : \n");
						parentOffspringTotalPopulation[p].print();
						
						return population[0];
					}				
				}
			}

			TotalCostCalculator.calculateCostofPopulation(parentOffspringTotalPopulation, 0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
			Utility.sort(parentOffspringTotalPopulation);

			
						
			
			survivalSelectionOperator.initialise(parentOffspringTotalPopulation, true);
			for( i=0;i<POPULATION_SIZE;i++)
			{
				population[i]= survivalSelectionOperator.getIndividual(parentOffspringTotalPopulation);
			}
			

			Utility.sort(population);	

			int totalFeasible=0;
			if(Solver.printEveryGeneration)
			{
				if(Solver.showViz)
					Solver.visualiser.drawIndividual(population[0], "Best Gen: "+generation);
				
				double tmpSum=0;
				
				for(int tmpi=0;tmpi<POPULATION_SIZE;tmpi++){
					tmpSum += population[tmpi].costWithPenalty;
					if(population[tmpi].isFeasible) totalFeasible++;
				}
				//System.out.println(totalFeasible);	
				//System.out.println("Gen : "+generation+","+population[0].costWithPenalty+", "+totalFeasible);
				System.out.println("Gen : "+ generation + " Best : "+population[0].costWithPenalty+  " Feasibility : "+ population[0].isFeasible +" Avg : "+(tmpSum/POPULATION_SIZE)+"  total feasible percent : "+(totalFeasible*100.0/POPULATION_SIZE)+"%");
			}
			
			if(Solver.outputTrace)
			{
				//For collecting min,max,avg
				if((generation+1)%Solver.outputTracePrintStep==0)
				{
					Solver.outputTraceWriter.print(population[0].costWithPenalty+", ");
					Solver.outputTraceWriter.flush();
					System.out.println("Gen : "+ generation + " Best : "+population[0].costWithPenalty+  " Feasibility : "+ population[0].isFeasible);
				}
			}

		}


		TotalCostCalculator.calculateCostofPopulation(population,0,POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
		Utility.sort(population);
		Solver.gatherExcelData(population, POPULATION_SIZE, generation);
		
		System.out.println("REPAIR PROCESS, apply: "+GreedyVehicleReAssignment.apply+" success: "+GreedyVehicleReAssignment.success);
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
		
		for(int tmp=0;tmp<POPULATION_SIZE;tmp++)
		{
			if(population[tmp].isFeasible) 
				return population[tmp];
		}
		return population[0];
		

	}
	
	public int getNumberOfGeeration()
	{
		return NUMBER_OF_GENERATION;
	}
}
