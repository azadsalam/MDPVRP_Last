package Main.VRP.GeneticAlgorithm;

import java.io.PrintWriter;

import Main.Solver;
import Main.Utility;
import Main.Visualiser;
import Main.visualize;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.Initialise_ClosestDepot_GENI_GreedyCut;
import Main.VRP.Individual.Crossover.CrossoverStatistics;
import Main.VRP.Individual.Crossover.Crossover_Uniform_Uniform;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation_GreedyCut;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation_with_No_Load_Crossover;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.RepairProcedure;
import Main.VRP.LocalImprovement.FirstChoiceHillClimbing;
import Main.VRP.LocalImprovement.LocalImprovement;
import Main.VRP.LocalImprovement.LocalImprovementBasedOnFussandElititst;
import Main.VRP.LocalImprovement.LocalSearch;
import Main.VRP.LocalImprovement.SimulatedAnnealing;
import Main.VRP.SelectionOperator.FUSS;
import Main.VRP.SelectionOperator.RouletteWheelSelection;
import Main.VRP.SelectionOperator.SelectionOperator;


public class Scheme6_with_crossover_only implements GeneticAlgorithm
{
	//Algorithm parameters
	public static int POPULATION_SIZE = 100; 
	public static int NUMBER_OF_OFFSPRING = 100;   
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
    LocalImprovement localImprovement;
    LocalSearch localSearch;
	
	//Utility Functions	
	PrintWriter out; 
	ProblemInstance problemInstance;

	//Temprary Variables
	Individual parent1,parent2;
	

	
	public Scheme6_with_crossover_only(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
		out = problemInstance.out;

		mutation = new RandomMutation();
		
		//Change here if needed
		population = new Individual[POPULATION_SIZE];
		offspringPopulation = new Individual[NUMBER_OF_OFFSPRING];		
		parentOffspringTotalPopulation = new Individual[POPULATION_SIZE + NUMBER_OF_OFFSPRING];
		
		//Add additional code here
		rouletteWheelSelection = new RouletteWheelSelection();
	    fussSelection = new FUSS();
		survivalSelectionOperator = new RouletteWheelSelection(); 

		MutationInterface neighbourhoodStep = new Neigbour_Steps_Grouped();
		localSearch = new FirstChoiceHillClimbing(neighbourhoodStep);
		localImprovement = new LocalImprovementBasedOnFussandElititst(localSearch);	
		
	}

	public Individual run() 
	{
		int i,generation;
		
		Individual offspring1,offspring2;

		Individual.calculateAssignmentProbalityForDiefferentDepot(problemInstance);
		Individual.calculateProbalityForDiefferentVehicle(problemInstance);
		PopulationInitiator.initialisePopulation(population, POPULATION_SIZE, problemInstance);
		TotalCostCalculator.calculateCostofPopulation(population,0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor) ;
		
		
		int continuosInjection=0; 
		//int unImprovedGeneration=0;
		
		double previousBest=-1;
		double bestBeforeInjection=-1;
		int apply=0;
		
		
		if(Solver.gatherCrossoverStat)
			CrossoverStatistics.init();
		
		for( generation=0;generation<NUMBER_OF_GENERATION;generation++)
		{
			//For collecting min,max,avg
			
			Solver.gatherExcelData(population, POPULATION_SIZE, generation);
			TotalCostCalculator.calculateCostofPopulation(population,0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor) ;
			
			//  Best individual always reproduces K=1 times + roulette wheel			
			/*if(generation !=0 && generation%Solver.episodeSize==0)
			{
				//System.out.println("Generation : "+generation);				
				mutation.updateWeights();
				//System.out.println();
				//System.out.println();
			}
			*/
			
			fussSelection.initialise(population, false);
			rouletteWheelSelection.initialise(population, false);
			
			i=0;
			

			 
			parent1 = population[0];
			parent2 = rouletteWheelSelection.getIndividual(population);
			
/*			out.println("Parent1 -cost "+parent1.cost+" Load Violation "+parent1.totalLoadViolation+" Route Time VIolation "+parent1.totalRouteTimeViolation);
			parent1.print();
			out.println("Parent2 -cost "+parent2.cost+" Load Violation "+parent2.totalLoadViolation+" Route Time VIolation "+parent2.totalRouteTimeViolation);
			parent2.print();*/
			
			offspring1 = new Individual(problemInstance);
			offspring2 = new Individual(problemInstance);
			
//			Uniform_VariedEdgeRecombnation.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);			
			//Uniform_VariedEdgeRecombnation_GreedyCut.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);
			Uniform_VariedEdgeRecombnation_with_No_Load_Crossover.crossOver_Uniform_VariedEdgeRecombination_cost_greedy(problemInstance, parent1, parent2, offspring1);
			//Crossover_Uniform_Uniform.crossOver_Uniform_Uniform(problemInstance, parent1, parent2, offspring1, offspring2);
			
			
			/*TotalCostCalculator.calculateCost(offspring1, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
			out.println("Offspring -cost "+offspring1.cost+" Load Violation "+offspring1.totalLoadViolation+" Route Time VIolation "+offspring1.totalRouteTimeViolation);
			offspring1.print();
			*/
			offspringPopulation[i] = offspring1;
			i++;
			/*offspringPopulation[i] = offspring2;
			i++;
			*/
			while(i<NUMBER_OF_OFFSPRING)
			{
				parent1 = rouletteWheelSelection.getIndividual(population);
				parent2 = fussSelection.getIndividual(population);
				
				offspring1 = new Individual(problemInstance);				
				offspring2 = new Individual(problemInstance);				
				
				//Uniform_VariedEdgeRecombnation_GreedyCut.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);

				//Uniform_VariedEdgeRecombnation_GreedyCut.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);
				
				//Uniform_VariedEdgeRecombnation.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);
				Uniform_VariedEdgeRecombnation_with_No_Load_Crossover.crossOver_Uniform_VariedEdgeRecombination_cost_greedy(problemInstance, parent1, parent2, offspring1);
				
				//Crossover_Uniform_Uniform.crossOver_Uniform_Uniform(problemInstance, parent1, parent2, offspring1, offspring2);

				//mutation.applyMutation(offspring2);
				
				offspringPopulation[i] = offspring1;
				i++;
				
				/*offspringPopulation[i] = offspring2;
				i++;
				*/
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

			
			//////////////////////////////////////////////////////////////////////////////////////////
			int elitistRatio = (int)(POPULATION_SIZE * Solver.ServivorElitistRation);
			//int elitistRatio = (int)(POPULATION_SIZE);
			/////////////////////////////////////////////////////////////////////////////////////////
			
			
			population[0] = parentOffspringTotalPopulation[0];
			
			int index2=1;
			int index1=1;
			
			while(index1 < elitistRatio)
			{
				population[index1] = parentOffspringTotalPopulation[index2];
				index1++;
				index2++;
			}
			
			
			Individual total[] = new Individual[POPULATION_SIZE+NUMBER_OF_OFFSPRING-elitistRatio];
			System.arraycopy(parentOffspringTotalPopulation, elitistRatio, total, 0, total.length);
			
			survivalSelectionOperator.initialise(total, true);
			for( i=elitistRatio;i<POPULATION_SIZE;i++)
			{
				population[i]= survivalSelectionOperator.getIndividual(total);
			}
			
			
			
			Utility.sort(population);	
			int totalFeasible=0;
			if(Solver.printEveryGeneration)
			{
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
		
		//System.out.println("REPAIR PROCESS, apply: "+GreedyVehicleReAssignment.apply+" success: "+GreedyVehicleReAssignment.success);
		if(Solver.printFinalSolutionToFile)
		{
			out.print("\n\n\n\n\n--------------------------------------------------\n");
		//	calculateCostWithPenalty(0, POPULATION_SIZE, generation, true);
			out.print("\n\n\nFINAL POPULATION\n\n");
			for( i=0;i<POPULATION_SIZE;i++)
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
