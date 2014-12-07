package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;

public class IntraRouteRandomSwap {

	public static void mutate(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		boolean success = false;
		do
		{
			int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
			int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			success = mutateRouteBySwapping(individual,period, vehicle);
		}while(success==false);
	}

	
	//returns true if permutation successful
	public static boolean mutateRouteBySwapping(Individual individual,int period,int vehicle)
	{
		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);

		if(route.size() < 2) return false;
		
		int first = Utility.randomIntInclusive(route.size()-1);

		int second;
		do
		{
			second = Utility.randomIntInclusive(route.size()-1);
		}
		while(second == first);

		//problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" SELCTED FOR SWAP "+route.get(first)+" "+route.get(second));
		
		int temp = route.get(first);
		route.set(first, route.get(second));
		route.set(second,temp);
				
		return true;
	}


}
