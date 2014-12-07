package Main.VRP.Individual.MutationOperators;

import java.security.AllPermission;
import java.util.ArrayList;

import javax.sound.midi.SysexMessage;

import sun.misc.Perf;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
import com.sun.security.ntlm.Client;

import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class PatternMutation {

	
	public static int apply = 0;	
	public static double totalSec=0;

	public static boolean print= false;
	
	public static void mutate(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor, boolean improveResultantRoute)
	{
		long start = System.currentTimeMillis();
		
		ProblemInstance problemInstance = individual.problemInstance;

		//now the client is chosen
		int chosenClient;
		int noOfPossiblePatterns;
		do
		{
			chosenClient = Utility.randomIntInclusive(problemInstance.customerCount-1);
			noOfPossiblePatterns = problemInstance.allPossibleVisitCombinations.get(chosenClient).size();
		}while(noOfPossiblePatterns==1);

		ArrayList<Integer> allComb = problemInstance.allPossibleVisitCombinations.get(chosenClient);
		
		
		if(print)PatternImprovementNew.print=true;
		
		PatternImprovementNew.removeOccurancesOfThisClientFromAllPeriod(individual,chosenClient,loadPenaltyFactor,routeTimePenaltyFactor,false,improveResultantRoute);
		
		//now repeatedly add different patterns - chose the best one
		int previousPattern = individual.visitCombination[chosenClient],newPattern=previousPattern;
		
		while(newPattern==previousPattern)
		{
			int index = Utility.randomIntExclusive(noOfPossiblePatterns);
			newPattern = allComb.get(index);
		}
		
		PatternImprovementNew.updatePeriodAssignment(individual, chosenClient, newPattern);
		PatternImprovementNew.assignNewPatternToClient(individual, chosenClient, newPattern, loadPenaltyFactor, routeTimePenaltyFactor, false, true, improveResultantRoute);
		
		if(print)PatternImprovementNew.print=false;			
		if(print)
		{
			System.out.println("Prev Pattern: "+previousPattern+" New Pattern: "+newPattern);
		}
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		//System.exit(1);
	}
	
	
}
