package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;
import Main.Utility;
import Main.VRP.Individual.Individual;

public class IntraRouteRandomInsertion 
{

	
	public static void mutate(Individual individual)
	{
		int period,vehicle;
		boolean success;
		do
		{
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			vehicle = Utility.randomIntInclusive(individual.problemInstance.vehicleCount-1);
			success = mutateRouteWithInsertion(individual,period,vehicle);			
		}while(success==false);
		//System.out.println("Period - vehicle :" +period+" "+vehicle);
	}
	
	public static boolean mutateRouteWithInsertion(Individual individual,int period,int vehicle)
	{
		ArrayList<Integer> route = individual.routes.get(period).get(vehicle);
		int size=route.size(); 
		if(route.size()<2) return false;
		
		int selectedClientIndex = Utility.randomIntInclusive(route.size()-1);
		int selectedClient = route.get(selectedClientIndex);
		
		int newIndex;
		do
		{
			newIndex = Utility.randomIntInclusive(route.size()-1);
		}while(newIndex==selectedClientIndex);
				
		route.remove(selectedClientIndex);
		route.add(newIndex, selectedClient);
		
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		return true;
	}
	

}
