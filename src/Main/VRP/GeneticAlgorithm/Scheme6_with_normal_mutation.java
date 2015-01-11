package Main.VRP.GeneticAlgorithm;

import java.io.PrintWriter;
import java.util.ArrayList;

import Main.Solver;
import Main.Utility;
import Main.Visualiser;
import Main.visualize;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.Initialise_ClosestDepot_GENI_GreedyCut;
import Main.VRP.Individual.Crossover.CrossoverStatistics;
import Main.VRP.Individual.Crossover.Crossover_Uniform_Uniform;
import Main.VRP.Individual.Crossover.IIC;
import Main.VRP.Individual.Crossover.IIC2;
import Main.VRP.Individual.Crossover.PIX;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation_GreedyCut;
import Main.VRP.Individual.Crossover.Uniform_VariedEdgeRecombnation_with_No_Load_Crossover;
import Main.VRP.Individual.MutationOperators.CostReducedVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.Inter_2_Opt;
import Main.VRP.Individual.MutationOperators.Intra_Or_Opt;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.Inter_Or_Opt;
import Main.VRP.Individual.MutationOperators.PatternImprovement;
import Main.VRP.Individual.MutationOperators.PatternImprovementNew;
import Main.VRP.Individual.MutationOperators.PatternMutation;
import Main.VRP.Individual.MutationOperators.RepairProcedure;
import Main.VRP.Individual.MutationOperators.Three_Opt;
import Main.VRP.Individual.MutationOperators.Intra_2_Opt;
import Main.VRP.LocalImprovement.FirstChoiceHillClimbing;
import Main.VRP.LocalImprovement.FirstChoiceHillClimbingIMPROVE_ALL_ROUTE;
import Main.VRP.LocalImprovement.LocalImprovement;
import Main.VRP.LocalImprovement.LocalImprovementAll;
import Main.VRP.LocalImprovement.LocalImprovementBasedOnBT;
import Main.VRP.LocalImprovement.LocalImprovementBasedOnFuss;
import Main.VRP.LocalImprovement.LocalImprovementBasedOnFussandElititst;
import Main.VRP.LocalImprovement.LocalImprovementBasedOnFussandElititstForKickBoost;
import Main.VRP.LocalImprovement.LocalSearch;
import Main.VRP.LocalImprovement.SimulatedAnnealing;
import Main.VRP.SelectionOperator.FUSS;
import Main.VRP.SelectionOperator.RouletteWheelSelection;
import Main.VRP.SelectionOperator.SelectionOperator;


public class Scheme6_with_normal_mutation implements GeneticAlgorithm
{
	//Algorithm parameters
	public static int POPULATION_SIZE = 10; 
	public static int NUMBER_OF_OFFSPRING = 10;   
	public static int NUMBER_OF_GENERATION = 5;

	public static int generation;
	
	static int INTERVAL_OF_DRAWING = 25;
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

	//Temporary Variables
	Individual parent1,parent2;
	
	ArrayList<Individual> drawnIndividuals;
	
	public Scheme6_with_normal_mutation(ProblemInstance problemInstance) 
	{
		System.err.println("in scheme 6");
//		System.err.println("\n\n TESTING IN INITALISSATION AND IN EDUCATION!!!!!!!");
		System.err.println("SEED FOR RANDOM GENERATOR: "+Utility.SEED);
		
		if(Solver.turnOffLocalLearning)System.err.println("\n\nLOCAL LEARNING TURNED OFF!!!!\nLOCAL LEARNING TURNED OFF!!!!\nLOCAL LEARNING TURNED OFF!!!!\nLOCAL LEARNING TURNED OFF!!!!\nLOCAL LEARNING TURNED OFF!!!!\n\n");
		if(Solver.turnOffHeuristicInit)System.err.println("\n\nHEURISTIC INITIALISATION IS TURNED OFF\nHEURISTIC INITIALISATION IS TURNED OFF\nHEURISTIC INITIALISATION IS TURNED OFF\nHEURISTIC INITIALISATION IS TURNED OFF\n\n");

		
/*		Solver.HallOfShamePCSize = problemInstance.periodCount * problemInstance.customerCount *1/3;
		
		System.err.println("Solver.HallOfShamePCSize = "+Solver.HallOfShamePCSize);*/

		drawnIndividuals = new ArrayList<>();
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
		localImprovement = new LocalImprovementBasedOnFuss(localSearch);	
		
	}

	public Individual run() 
	{
		LocalImprovementBasedOnBT.tmp=true;
		int i;
		
		Individual offspring1,offspring2;
		
		//Set penalty factors
		Solver.loadPenaltyFactor = problemInstance.customerCount;
		Solver.routeTimePenaltyFactor = problemInstance.customerCount;
		

		//Individual.calculateAssignmentProbalityForDiefferentDepot(problemInstance);
		//Individual.calculateProbalityForDiefferentVehicle(problemInstance);
		PopulationInitiator.initialisePopulation(population, POPULATION_SIZE, problemInstance);
		TotalCostCalculator.calculateCostofPopulation(population,0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor) ;
		
		
		System.out.println("Penalty Factor: "+Solver.loadPenaltyFactor);
		
		double previousBest=Double.MAX_VALUE;
		int unimprovedIteration=0;
		
		if(Solver.gatherCrossoverStat)
			CrossoverStatistics.init();
		
		for( generation=0;generation<NUMBER_OF_GENERATION;generation++)
		{
			
			/*if(generation%100==0)
			{
			
				for(int in=0;in<POPULATION_SIZE;in++)
				{
					population[in].initialiseVehiclePenaltyMatrix();
				}
			}*/
			
			//For collecting min,max,avg			
			Solver.gatherExcelData(population, POPULATION_SIZE, generation);
			TotalCostCalculator.calculateCostofPopulation(population,0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor) ;
						
			fussSelection.initialise(population, false);
			rouletteWheelSelection.initialise(population, false);
			
			i=0;
			 
/*			parent1 = population[0];
			parent2 = rouletteWheelSelection.getIndividual(population);
			offspring1 = new Individual(problemInstance);			
			
//			Uniform_VariedEdgeRecombnation_GreedyCut.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);
		//	Uniform_VariedEdgeRecombnation.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);			
			
			IIC.crossOver(problemInstance, parent1, parent2, offspring1);
			mutation.applyMutation(offspring1);
			offspringPopulation[i] = offspring1;
			i++;
			if(Solver.showViz && generation%INTERVAL_OF_DRAWING==0)
			{
				Solver.visualiser.drawIndividual(new Individual(parent1), "Parent1 ("+generation +")");
				Solver.visualiser.drawIndividual(new Individual(parent2), "Parent2 ("+generation +")");
				Solver.visualiser.drawIndividual(new Individual(offspring1), "Child ("+generation +")");
			}
*/			
			
			
			while(i<NUMBER_OF_OFFSPRING)
			{
				parent1 = rouletteWheelSelection.getIndividual(population);
				parent2 = fussSelection.getIndividual(population);				
				offspring1 = new Individual(problemInstance);	
				IIC.crossOver(problemInstance, parent1, parent2, offspring1);
				
				
				//Uniform_VariedEdgeRecombnation.crossOver_Uniform_VariedEdgeRecombination(problemInstance, parent1, parent2, offspring1);
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

			TotalCostCalculator.calculateCostofPopulation(parentOffspringTotalPopulation, 0, NUMBER_OF_OFFSPRING+POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);

			//local improvement block
/*			if(Solver.turnOffLocalLearning==false)
			{
				int coin = Utility.randomIntInclusive(1);
				LocalSearch hc=null,sa=null;
				if(coin==0)
				{
					hc = new FirstChoiceHillClimbing(new Neigbour_Steps_Grouped());
					LocalImprovement li = new LocalImprovementBasedOnBT(hc);
					li.initialise(parentOffspringTotalPopulation);
					li.run(parentOffspringTotalPopulation);
				}
				else
				{
					sa = new SimulatedAnnealing(new Neigbour_Steps_Grouped());
					LocalImprovement li = new LocalImprovementBasedOnBT(sa);
					li.initialise(parentOffspringTotalPopulation);
					li.run(parentOffspringTotalPopulation);
				}
			}
			*/
			if(Solver.turnOffLocalLearning==false)
			{
				LocalSearch	sa = new SimulatedAnnealing(new Neigbour_Steps_Grouped());
				LocalImprovement li = new LocalImprovementBasedOnBT(sa);
				li.initialise(parentOffspringTotalPopulation);
				li.run(parentOffspringTotalPopulation);
			
			}


/*			int totalloadViolationCount=0;
			int totalROuteTimeViolationCOunt=0;
			double totalLoadViolation=0;
			double totalRouteTimeViolation = 0;
			for(int p=0;p<parentOffspringTotalPopulation.length;p++)
			{
				if(!parentOffspringTotalPopulation[p].isFeasible && parentOffspringTotalPopulation[p].totalLoadViolation>0)
				{
					totalloadViolationCount++;
					//add route time violation
					totalLoadViolation += parentOffspringTotalPopulation[p].totalLoadViolation ;
				}
				if(!parentOffspringTotalPopulation[p].isFeasible && parentOffspringTotalPopulation[p].totalRouteTimeViolation>0)
				{
					totalRouteTimeViolation++;
					//add route time violation
					totalRouteTimeViolation += parentOffspringTotalPopulation[p].totalRouteTimeViolation ;
				}
			}
			double avgLoadInfeasibility,avgRouteInfeasibility;
			
			if(totalloadViolationCount>0)
				avgLoadInfeasibility = totalLoadViolation / totalloadViolationCount;
			else
				avgLoadInfeasibility=0;
			
			if(totalROuteTimeViolationCOunt>0)
				avgRouteInfeasibility = totalRouteTimeViolation / totalROuteTimeViolationCOunt;
			else avgRouteInfeasibility=0;
			
			
			for(int p=0;p<parentOffspringTotalPopulation.length;p++)
			{
				if( parentOffspringTotalPopulation[p].totalLoadViolation >= avgLoadInfeasibility || parentOffspringTotalPopulation[p].totalRouteTimeViolation >= avgRouteInfeasibility)
				{					
					int coin2 = Utility.randomIntInclusive(1);
					if(coin2==0)
					{
						LocalSearch sa2 = new SimulatedAnnealing(new Neigbour_Steps_Grouped());
						sa2.improve(parentOffspringTotalPopulation[p], Solver.loadPenaltyFactor*50, Solver.routeTimePenaltyFactor*50);
					}
					else
					{
						LocalSearch hc2 = new FirstChoiceHillClimbing(new Neigbour_Steps_Grouped());
						hc2.improve(parentOffspringTotalPopulation[p], Solver.loadPenaltyFactor*50, Solver.routeTimePenaltyFactor*50);
					}
				}
			}*/
			
			
			TotalCostCalculator.calculateCostofPopulation(parentOffspringTotalPopulation, 0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
			Utility.sort(parentOffspringTotalPopulation);

			
			int elitistRatio = (int)(POPULATION_SIZE * Solver.ServivorElitistRation);
			
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
			
	/*		if(population[0].costWithPenalty < previousBest)
			{
				unimprovedIteration=0;
				previousBest= population[0].costWithPenalty;				
			}
			else
			{
				unimprovedIteration++;
				//System.out.println("Unimproved: "+unimprovedIteration);
			}
			
			if(unimprovedIteration>=14)
			{
				unimprovedIteration=0;
				System.out.println("KICK BOOST!");
				kickBoost();
				TotalCostCalculator.calculateCostofPopulation(population, 0, POPULATION_SIZE, Solver.loadPenaltyFactor, Solver.routeTimePenaltyFactor);
				Utility.sort(population);
				
				if(population[0].costWithPenalty < previousBest)
				{
					unimprovedIteration=0;
					previousBest= population[0].costWithPenalty;				
				}

			}
*/			
			
			int totalFeasible=0;
			if(Solver.printEveryGeneration)
			{
//				drawnIndividuals.add(population[0]);
				
				Individual ind = new Individual(population[0]);
				if(Solver.showViz && generation% INTERVAL_OF_DRAWING==0)
					Solver.visualiser.drawIndividual(ind, "Best Gen: "+generation);
				
				double tmpSum=0;
				
				for(int tmpi=0;tmpi<POPULATION_SIZE;tmpi++)
				{
					tmpSum += population[tmpi].costWithPenalty;
					if(population[tmpi].isFeasible) totalFeasible++;
				}
				//System.out.println(totalFeasible);	
				//System.out.println("Gen : "+generation+","+population[0].costWithPenalty+", "+totalFeasible);
				//System.out.println("Gen : "+ generation + " Best : "+population[0].costWithPenalty+  " Feasibility : "+ population[0].isFeasible +" Avg : "+(tmpSum/POPULATION_SIZE)+"  total feasible percent : "+(totalFeasible*100.0/POPULATION_SIZE)+"%");
				
				double bestFitness = population[0].costWithPenalty;
				
				if(Solver.bksValue != -1)
				{
					double gapToBKS = (bestFitness-Solver.bksValue)*100/Solver.bksValue;
					System.out.println("Gen : "+ generation+" BKS: "+ Solver.bksValue +" Gap: "+gapToBKS+ "% Best : "+bestFitness+  " Feasibility : "+ population[0].isFeasible +" Avg : "+(tmpSum/POPULATION_SIZE)+"  total feasible percent : "+(totalFeasible*100.0/POPULATION_SIZE)+"%");
				}
				else
				{
					System.out.println("Gen : "+ generation + " Best : "+bestFitness+  " Feasibility : "+ population[0].isFeasible +" Avg : "+(tmpSum/POPULATION_SIZE)+"  total feasible percent : "+(totalFeasible*100.0/POPULATION_SIZE)+"%");
				}
			}
			
			if(Solver.outputTrace)
			{
				//For collecting min,max,avg
				if((generation+1)%Solver.outputTracePrintStep==0)
				{
					Solver.outputTraceWriter.print(population[0].costWithPenalty+", ");
					Solver.outputTraceWriter.flush();
					double bestFitness = population[0].costWithPenalty;
					
					if(Solver.bksValue != -1)
					{
						double gapToBKS = (bestFitness-Solver.bksValue)*100/Solver.bksValue;
						System.out.println("Gen : "+ generation +" Gap:"+gapToBKS+ " Best : "+bestFitness+  " Feasibility : "+ population[0].isFeasible);
					}
					else
					{
						System.out.println("Gen : "+ generation + " Best : "+bestFitness+  " Feasibility : "+ population[0].isFeasible);
					}
						
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
		
		
		System.out.format("Three Opt- Apply: %d AvgTimeTaken: %f\n",Three_Opt.apply,Three_Opt.totalSec/Three_Opt.apply);
		System.out.format("Two Opt- Apply: %d AvgTimeTaken: %f\n",Intra_2_Opt.apply,Intra_2_Opt.totalSec/Intra_2_Opt.apply);
		System.out.format("Inter Or Opt- Apply: %d AvgTimeTaken: %f\n",Inter_Or_Opt.apply,Inter_Or_Opt.totalSec/Inter_Or_Opt.apply);
		System.out.format("Intra Or Opt- Apply: %d AvgTimeTaken: %f\n",Intra_Or_Opt.apply,Intra_Or_Opt.totalSec/Intra_Or_Opt.apply);
//		System.out.format("CostReducedVehicleReassignment- Apply: %d AvgTimeTaken: %f\n",CostReducedVehicleReAssignment.apply,CostReducedVehicleReAssignment.totalSec/CostReducedVehicleReAssignment.apply);
		System.out.format("Pattern Improvement- Apply: %d AvgTimeTaken: %f\n",PatternImprovementNew.apply,PatternImprovementNew.totalSec/PatternImprovementNew.apply);
		System.out.format("Pattern Mutation- Apply: %d AvgTimeTaken: %f\n",PatternMutation.apply,PatternMutation.totalSec/PatternMutation.apply);
		
		System.out.format("Inter 2opt - Apply: %d AvgTimeTaken: %f\n",Inter_2_Opt.apply,Inter_2_Opt.totalSec/Inter_2_Opt.apply);
		
		
		if(Solver.turnOffLocalLearning)System.err.println("\n\nLOCAL LEARNING TURNED OFF!!!!\nLOCAL LEARNING TURNED OFF!!!!\nLOCAL LEARNING TURNED OFF!!!!\nLOCAL LEARNING TURNED OFF!!!!\nLOCAL LEARNING TURNED OFF!!!!\n\n");
		if(Solver.turnOffHeuristicInit)System.err.println("\n\nHEURISTIC INITIALISATION IS TURNED OFF\nHEURISTIC INITIALISATION IS TURNED OFF\nHEURISTIC INITIALISATION IS TURNED OFF\nHEURISTIC INITIALISATION IS TURNED OFF\n\n");
		
		for(int tmp=0;tmp<POPULATION_SIZE;tmp++)
		{
			if(population[tmp].isFeasible) 
				return population[tmp];
		}
		
		return population[0];
		

	}
	
	public void kickBoost()
	{		
		LocalImprovement li = new LocalImprovementBasedOnFussandElititstForKickBoost(new FirstChoiceHillClimbingIMPROVE_ALL_ROUTE(new Neigbour_Steps_Grouped()));
		li.initialise(population);
		li.run(population);
	}
	public int getNumberOfGeeration()
	{
		return NUMBER_OF_GENERATION;
	}
}
