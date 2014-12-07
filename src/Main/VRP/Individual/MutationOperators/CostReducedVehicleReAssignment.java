package Main.VRP.Individual.MutationOperators;
import java.util.ArrayList;

import javax.lang.model.element.NestingKind;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MinimumCostInsertionInfo;
import Main.VRP.Individual.RouteUtilities;

public class CostReducedVehicleReAssignment 
{
	static int fail=0;
	
	public static int success=0;
	public static int apply = 0;	
	public static double totalSec=0;
	/**
	 * Randomly selects a client,period 
	 * <br/>Inserts the client in the route, which cause minimum cost increase taking account of load+route time violation
	 * @param individual
	 */
	public static void mutate(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		long start = System.currentTimeMillis();
		
		int retry = 0;
		int period,client;
		boolean success=false;
		do
		{
			
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			client = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);
			
			if(individual.periodAssignment[period][client] == false) continue;
			
			//System.err.println(period+ " "+ client+" "+individual.periodAssignment[period][client]);
			
			success = mutateVehicleAssignmentGreedy(individual,period,client,loadPenaltyFactor,routeTimePenaltyFactor);			
			retry++;
			
		}while(success==false && retry<3);
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	public static void mutateFI(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		long start = System.currentTimeMillis();
		
		int retry = 0;
		int period,client;
		boolean success=false;
		do
		{
			
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			client = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);
			
			if(individual.periodAssignment[period][client] == false) continue;
			
			//System.err.println(period+ " "+ client+" "+individual.periodAssignment[period][client]);
			
			success = mutateVehicleAssignmentGreedyFI(individual,period,client,loadPenaltyFactor,routeTimePenaltyFactor);			
			retry++;
			
		}while(success==false && retry<3);
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	public static void mutateBT(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		long start = System.currentTimeMillis();
		
		int retry = 0;
		int period,client;
		boolean success=false;
		do
		{
			
			period = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
			client = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);
			
			if(individual.periodAssignment[period][client] == false) continue;
			
			//System.err.println(period+ " "+ client+" "+individual.periodAssignment[period][client]);
			
			success = mutateVehicleAssignmentGreedyFI(individual,period,client,loadPenaltyFactor,routeTimePenaltyFactor);			
			retry++;
			
		}while(success==false && retry<3);
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		//System.out.println("InsertionMutationGreedy FAILED");
	}

	public static void mutateWithPromisingClientDeterministic(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		long start = System.currentTimeMillis();
		
		int retry = 0;
		boolean success=false;
		do
		{
			if(print)
			{
				System.out.println("Before");
				for(int client=0;client<individual.problemInstance.customerCount;client++)
				{
					System.out.format("%4d",client);	
				}
				System.out.println();

				for(int period=0;period<individual.problemInstance.periodCount;period++)
				{
					
					for(int client=0;client<individual.problemInstance.customerCount;client++)
					{
						if(individual.periodAssignment[period][client] == false)
							System.out.format(" ---",individual.vehicleReassignmentApplied[period][client]);	
						else
							System.out.format("%4d",individual.vehicleReassignmentApplied[period][client]);	
					}
					System.out.println();
				}
			}
			
			int speriod=-1,sclient=-1,lowestApplication=Integer.MAX_VALUE;

			for(int period=0;period<individual.problemInstance.periodCount;period++)
			{
				for(int client=0;client<individual.problemInstance.customerCount;client++)
				{
					if(individual.periodAssignment[period][client] == false) continue;
					
					if(individual.vehicleReassignmentApplied[period][client] < lowestApplication)
					{
						lowestApplication = individual.vehicleReassignmentApplied[period][client];
						speriod = period;
						sclient = client;
					}
					else if (individual.vehicleReassignmentApplied[period][client] == lowestApplication)
					{
						//lowestApplication = individual.vehicleReassignmentApplied[period][client];
						int coin=Utility.randomIntInclusive(1);
						if(coin==1)
						{
							speriod = period;
							sclient = client;
						}
					}

				}
			}
			
			
			success = mutateVehicleAssignmentGreedy(individual,speriod,sclient,loadPenaltyFactor,routeTimePenaltyFactor);	
			
			if(!success) 
			{
				individual.vehicleReassignmentApplied[speriod][sclient]++;
			}
/*			else
			{
				individual.vehicleReassignmentApplied[speriod][sclient]--;
			}
*/			
			
			if(print)
			{
				System.out.format("Selected period %d , client %d success%b\n",speriod,sclient,success);
				System.out.println("After");
				for(int client=0;client<individual.problemInstance.customerCount;client++)
				{
					System.out.format("%4d",client);	
				}
				System.out.println();

				for(int period=0;period<individual.problemInstance.periodCount;period++)
				{
					
					for(int client=0;client<individual.problemInstance.customerCount;client++)
					{
						if(individual.periodAssignment[period][client] == false)
							System.out.format(" ---",individual.vehicleReassignmentApplied[period][client]);	
						else
							System.out.format("%4d",individual.vehicleReassignmentApplied[period][client]);						}
					System.out.println();
				}
				System.out.println();
				System.out.println();
				System.out.println();

			}

			//System.exit(1);
			retry++;
			
		}while(success==false && retry<7);
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	
	

	public static void mutateWithPromisingClientProbabilisticBinaryTournament(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		long start = System.currentTimeMillis();
		
		int retry = 0;
		boolean success=false;
		do
		{
			if(print)
			{
				System.out.println("Before");
				for(int client=0;client<individual.problemInstance.customerCount;client++)
				{
					System.out.format("%4d",client);	
				}
				System.out.println();

				for(int period=0;period<individual.problemInstance.periodCount;period++)
				{
					
					for(int client=0;client<individual.problemInstance.customerCount;client++)
					{
						if(individual.periodAssignment[period][client] == false)
							System.out.format(" ---",individual.vehicleReassignmentApplied[period][client]);	
						else
							System.out.format("%4d",individual.vehicleReassignmentApplied[period][client]);	
					}
					System.out.println();
				}
			}
			
			int speriod=-1,sclient=-1,lowestApplication=Integer.MAX_VALUE;


			int tournamentSize=2;
			int periods[] = new int[tournamentSize];
			int clients[] = new int[tournamentSize];
			
			int gen=0;
			

			while(gen<tournamentSize)
			{
				periods[gen] = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
				clients[gen] = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);
				
				if(individual.periodAssignment[periods[gen]][clients[gen]] == false) continue;
				
				//check if duplicate //works for binary tournament only
				if(gen>0 && periods[gen]==periods[gen-1] && clients[gen]==clients[gen-1]) continue;

				if(print)
					System.out.format("period %d client %d apply %d\n",periods[gen],clients[gen],individual.vehicleReassignmentApplied[periods[gen]][clients[gen]]);

				
				
				gen++;
								
			}
			
			//for now only binary tournament
			
			int apply0 = individual.vehicleReassignmentApplied[periods[0]][clients[0]];
			int apply1 = individual.vehicleReassignmentApplied[periods[1]][clients[1]];
			
			double sum = apply0+apply1+2;
			double prob0 = (apply1+1)/sum;
			
			double ran = Utility.randomGenerator.nextDouble();
			
			if(ran<=prob0) 
			{
				speriod = periods[0];
				sclient = clients[0];
			}
			else
			{
				speriod = periods[1];
				sclient = clients[1];				
			}
			
			
			success = mutateVehicleAssignmentGreedy(individual,speriod,sclient,loadPenaltyFactor,routeTimePenaltyFactor);	
			if(print) System.out.println("Success: "+success);
			if(!success) 
			{
				individual.vehicleReassignmentApplied[speriod][sclient]++;
			}
			
			
			
			if(print)
			{
				System.out.format("Selected period %d , client %d \nRandom: %f Prob0: %f",speriod,sclient,ran,prob0);
				System.out.println("After");
				for(int client=0;client<individual.problemInstance.customerCount;client++)
				{
					System.out.format("%4d",client);	
				}
				System.out.println();

				for(int period=0;period<individual.problemInstance.periodCount;period++)
				{
					
					for(int client=0;client<individual.problemInstance.customerCount;client++)
					{
						if(individual.periodAssignment[period][client] == false)
							System.out.format(" ---",individual.vehicleReassignmentApplied[period][client]);	
						else
							System.out.format("%4d",individual.vehicleReassignmentApplied[period][client]);						}
					System.out.println();
				}
				System.out.println();
				System.out.println();
				System.out.println();

			}

			//System.exit(1);
			retry++;
			
		}while(success==false && retry<7);
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	
	
	public static void mutateWithPromisingClientBinaryTournament(Individual individual, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		boolean print=false;
		long start = System.currentTimeMillis();
		
		int retry = 0;
		boolean success=false;
		do
		{
			if(print)
			{
				System.out.println("Before");
				for(int client=0;client<individual.problemInstance.customerCount;client++)
				{
					System.out.format("%4d",client);	
				}
				System.out.println();

				for(int period=0;period<individual.problemInstance.periodCount;period++)
				{
					
					for(int client=0;client<individual.problemInstance.customerCount;client++)
					{
						if(individual.periodAssignment[period][client] == false)
							System.out.format(" ---",individual.vehicleReassignmentApplied[period][client]);	
						else
							System.out.format("%4d",individual.vehicleReassignmentApplied[period][client]);	
					}
					System.out.println();
				}
			}
			
			int speriod=-1,sclient=-1,lowestApplication=Integer.MAX_VALUE;


			int tournamentSize=2;
			int periods[] = new int[tournamentSize];
			int clients[] = new int[tournamentSize];
			
			int gen=0;
			

			while(gen<tournamentSize)
			{
				periods[gen] = Utility.randomIntInclusive(individual.problemInstance.periodCount-1);
				clients[gen] = Utility.randomIntInclusive(individual.problemInstance.customerCount-1);
				
				if(individual.periodAssignment[periods[gen]][clients[gen]] == false) continue;
				
				if(print)
					System.out.format("period %d client %d apply %d\n",periods[gen],clients[gen],individual.vehicleReassignmentApplied[periods[gen]][clients[gen]]);
				//check if duplicate //works for binary tournament only
				if(gen>0 && periods[gen]==periods[gen-1] && clients[gen]==clients[gen-1]) continue;
				
				
				int apply = individual.vehicleReassignmentApplied[periods[gen]][clients[gen]];
				//select with tournament 
				if(apply<lowestApplication)
				{
					lowestApplication = apply;
					sclient =  clients[gen];
					speriod = periods[gen];
 				}
				else if(apply==lowestApplication)
				{
					if(Utility.randomIntInclusive(1)==1)
					{
						sclient =  clients[gen];
						speriod = periods[gen];
					}
 				}
				
					
				
				gen++;
								
			}

			
			success = mutateVehicleAssignmentGreedy(individual,speriod,sclient,loadPenaltyFactor,routeTimePenaltyFactor);	
			if(print) System.out.println("Success: "+success);
			if(!success) 
			{
				individual.vehicleReassignmentApplied[speriod][sclient]++;
			}
			else
			{
				individual.vehicleReassignmentApplied[speriod][sclient]--;
			}
			
			
			
			if(print)
			{
				System.out.format("Selected period %d , client %d \n",speriod,sclient);
				System.out.println("After");
				for(int client=0;client<individual.problemInstance.customerCount;client++)
				{
					System.out.format("%4d",client);	
				}
				System.out.println();

				for(int period=0;period<individual.problemInstance.periodCount;period++)
				{
					
					for(int client=0;client<individual.problemInstance.customerCount;client++)
					{
						if(individual.periodAssignment[period][client] == false)
							System.out.format(" ---",individual.vehicleReassignmentApplied[period][client]);	
						else
							System.out.format("%4d",individual.vehicleReassignmentApplied[period][client]);						}
					System.out.println();
				}
				System.out.println();
				System.out.println();
				System.out.println();

			}

			//System.exit(1);
			retry++;
			
		}while(success==false && retry<7);
		
		long end= System.currentTimeMillis();
		
		totalSec += (end-start);
		apply++;
		//System.out.println("InsertionMutationGreedy FAILED");
	}
	
	
	public static boolean mutateVehicleAssignmentGreedyFI(Individual individual,int period,int client, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		
		int assigendVehicle = RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);
		
		ArrayList<Integer> oldRoute = individual.routes.get(period).get(assigendVehicle);
		ArrayList<Integer> originalSavedRoute = new ArrayList<Integer>(oldRoute);
		
		double costb1 = individual.calculateCostWithPenalty(period, assigendVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
		
		
		int position = individual.routes.get(period).get(assigendVehicle).indexOf(client);	
		individual.routes.get(period).get(assigendVehicle).remove(position);
		
		Inter_Or_Opt.mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, assigendVehicle);
		//Neigbour_Steps_Grouped.improveRoute(individual, period, assigendVehicle);
		double costa1 = individual.calculateCostWithPenalty(period, assigendVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
		
		
		for(int vehicle = 0;vehicle<individual.problemInstance.vehicleCount;vehicle++)
		{
			if(vehicle == assigendVehicle)continue;
			
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);	
			ArrayList<Integer> savedCopy = new ArrayList<Integer>(route);
			
			double costb2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			MinimumCostInsertionInfo mcinfo = RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
			route.add(mcinfo.insertPosition, client);
					
			Inter_Or_Opt.mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, vehicle);
			
			double costa2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			double improvement = (costb1+costb2) - (costa1+costa2);
			
			if(improvement>0)
			{
				//accept this
				return true;
			}
			
			route.clear();
			route.addAll(savedCopy);
			
		}
		
		oldRoute.clear();
		oldRoute.addAll(originalSavedRoute);
		return false;

	}



	public static boolean mutateVehicleAssignmentGreedy(Individual individual,int period,int client, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		int selectedVehicle = -1;
		ArrayList<Integer> bestRoute=null;
		double maxImprovement = Double.NEGATIVE_INFINITY;
		
		int assigendVehicle = RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);
		
		ArrayList<Integer> oldRoute = individual.routes.get(period).get(assigendVehicle);
		ArrayList<Integer> originalSavedRoute = new ArrayList<Integer>(oldRoute);
		
		double costb1 = individual.calculateCostWithPenalty(period, assigendVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
		
		
		int position = individual.routes.get(period).get(assigendVehicle).indexOf(client);	
		individual.routes.get(period).get(assigendVehicle).remove(position);
		
		Inter_Or_Opt.mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, assigendVehicle);
		//Neigbour_Steps_Grouped.improveRoute(individual, period, assigendVehicle);
		double costa1 = individual.calculateCostWithPenalty(period, assigendVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
		
		
		for(int vehicle = 0;vehicle<individual.problemInstance.vehicleCount;vehicle++)
		{
			if(vehicle == assigendVehicle)continue;
			
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);	
			ArrayList<Integer> savedCopy = new ArrayList<Integer>(route);
			
			double costb2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			//adding in random position - for now
//			route.add(Utility.randomIntInclusive(route.size()), client);
			
			//
			MinimumCostInsertionInfo mcinfo = RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
			route.add(mcinfo.insertPosition, client);
		
			
			Inter_Or_Opt.mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, vehicle);
			//Neigbour_Steps_Grouped.improveRoute(individual, period, vehicle);
			
			double costa2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			double improvement = (costb1+costb2) - (costa1+costa2);
			
			if(improvement>maxImprovement)
			{
				maxImprovement = improvement;
				selectedVehicle = vehicle;
				bestRoute = new ArrayList<>(route);
			}
			
			route.clear();
			route.addAll(savedCopy);
			
		}
		
		if(maxImprovement>0)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(selectedVehicle);	
			route.clear();
			route.addAll(bestRoute);
		}
		else
		{
			oldRoute.clear();
			oldRoute.addAll(originalSavedRoute);
			return false;

		}
		
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		
		return true;
		
	}

	
	/**
	 * Returns a Object array, element 0 is the improvement, 1 is the selected vehicle <br>
	 * and 2 is the best route.. the original routes are not changed
	 * @param individual
	 * @param period
	 * @param client
	 * @param loadPenaltyFactor
	 * @param routeTimePenaltyFactor
	 * @return
	 */
	public static Object[] addClientToThisPeriod(Individual individual,int period,int client, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		int selectedVehicle = -1;
		ArrayList<Integer> bestRoute=null;
		double maxImprovement = Double.NEGATIVE_INFINITY;
		
		
		
		for(int vehicle = 0;vehicle<individual.problemInstance.vehicleCount;vehicle++)
		{						
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);			
			ArrayList<Integer> savedRoute = new ArrayList<Integer>(route);			
			double costb2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
									
			MinimumCostInsertionInfo mcinfo = RouteUtilities.minimumCostInsertionPosition(individual.problemInstance, vehicle, client, route);
			route.add(mcinfo.insertPosition, client);											
			Inter_Or_Opt.mutateRouteBy_Or_Opt_withFirstBetterMove_Optimized(individual, period, vehicle);
			double costa2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			
			double improvement = costb2-costa2;
			if(improvement>maxImprovement)
			{
				bestRoute = new ArrayList<Integer>(route);
				maxImprovement=improvement;
				selectedVehicle=vehicle;
			}
			
			//revert
			route.clear();
			route.addAll(savedRoute);
		}
		
		Object[] ret = new Object[3];
		ret[0] = new Double(maxImprovement);
		ret[1] = new Integer(selectedVehicle);	
		ret[2] = bestRoute;
		
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		
		return ret;
		
	}

	
	public static boolean mutateVehicleAssignmentGreedyTWO_OPT(Individual individual,int period,int client, double loadPenaltyFactor, double routeTimePenaltyFactor)
	{
		int selectedVehicle = -1;
		ArrayList<Integer> bestRoute=null;
		double maxImprovement = Double.NEGATIVE_INFINITY;
		
		int assigendVehicle = RouteUtilities.assignedVehicle(individual, client, period, individual.problemInstance);
		
		ArrayList<Integer> oldRoute = individual.routes.get(period).get(assigendVehicle);
		ArrayList<Integer> originalSavedRoute = new ArrayList<Integer>(oldRoute);
		
		double costb1 = individual.calculateCostWithPenalty(period, assigendVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
		
		
		int position = individual.routes.get(period).get(assigendVehicle).indexOf(client);	
		individual.routes.get(period).get(assigendVehicle).remove(position);
		
		Neigbour_Steps_Grouped.improveRoute(individual, period, assigendVehicle);
		double costa1 = individual.calculateCostWithPenalty(period, assigendVehicle, loadPenaltyFactor, routeTimePenaltyFactor);
		
		
		for(int vehicle = 0;vehicle<individual.problemInstance.vehicleCount;vehicle++)
		{
			if(vehicle == assigendVehicle)continue;
			
			ArrayList<Integer> route = individual.routes.get(period).get(vehicle);	
			ArrayList<Integer> savedCopy = new ArrayList<Integer>(route);
			
			double costb2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			route.add(Utility.randomIntInclusive(route.size()), client);
			
			Neigbour_Steps_Grouped.improveRoute(individual, period, vehicle);
			
			double costa2 = individual.calculateCostWithPenalty(period, vehicle, loadPenaltyFactor, routeTimePenaltyFactor);
			
			double improvement = (costb1+costb2) - (costa1+costa2);
			
			if(improvement>maxImprovement)
			{
				maxImprovement = improvement;
				selectedVehicle = vehicle;
				bestRoute = new ArrayList<>(route);
			}
			
			route.clear();
			route.addAll(savedCopy);
			
		}
		
		if(maxImprovement>0)
		{
			ArrayList<Integer> route = individual.routes.get(period).get(selectedVehicle);	
			route.clear();
			route.addAll(bestRoute);
		}
		else
		{
			oldRoute.clear();
			oldRoute.addAll(originalSavedRoute);
			return false;

		}
		
		//individual.problemInstance.out.println("Period : "+period+" vehicle : "+vehicle+" selected Client : "+selectedClient+" "+ " new Position : "+newIndex);
		
		return true;
		
	}


}

