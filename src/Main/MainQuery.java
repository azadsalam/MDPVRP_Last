package Main;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.ClientSpecificMutation;
import Main.VRP.GeneticAlgorithm.Neigbour_Steps_Grouped;
import Main.VRP.LocalImprovement.SimulatedAnnealing;

public class MainQuery 
{
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException 
	{
		
		SimulatedAnnealing sa = new SimulatedAnnealing(new Neigbour_Steps_Grouped(),20,0.01,1000,false);
		sa.testScheduler();

		if(true) return ;
		
		System.out.println("HERE");
		final Solver solver = new Solver();
		
		ProblemInstance problemInstance = solver.createProblemInstance(Solver.instanceFiles[0], "temp");
		
		Scanner scanner= new Scanner(System.in);
		while(true)
		{
			//System.out.println("Query: ");
			System.out.println("Query 1 : Distance between two clients: ");
			System.out.println("Query 2 : Distance between two depot and client: ");
			System.out.println("Query 3 : Route info calcutaion: ");

			int city1,city2,d,depot;
			double dis;
			ArrayList<Integer> list;
			 
			 
			int option  = scanner.nextInt();
			
			switch (option) {
			case 1:	
				System.out.print("insert the two clients: ");
				
				 city1  = scanner.nextInt();
				 city2  = scanner.nextInt();
				 d = problemInstance.depotCount;
				 dis = problemInstance.costMatrix[d+city1][d+city2];
				System.out.format("Distance between clients %d and %d is %f \n",city1,city2,dis);
				
				break;

			case 2:	
				System.out.print("insert the depot and client: ");
				
				 depot  = scanner.nextInt();
				 city1  = scanner.nextInt();
				 d = problemInstance.depotCount;
				 dis = problemInstance.costMatrix[depot][d+city1];
				System.out.format("Distance between depot %d and client %d is %f \n",depot,city1,dis);
				
				break;

				
			case 3:	
				 System.out.print("insert the depot : ");
				
				 depot  = scanner.nextInt();
				 System.out.print("insert the clients of the route (-1 to end): ");

				 
				 list = new ArrayList<Integer>();
				 
				 while(true)
				 {
					 city1  = scanner.nextInt();
					 if(city1==-1) break;
					 
					 list.add(city1);
				 }
				 
				 int i=0;
				 d = problemInstance.depotCount;
				 double totalCost= 0;
				 //dis=0;
				 
				 city1 = list.get(0);
				 dis = problemInstance.costMatrix[depot][d+city1];
				 totalCost+=dis;
				 
				 System.out.format("Distance: Depot %d -> %d is %f \n",depot,city1,dis);
				 for(i=1;i<list.size();i++)
				 {
					 city1 = list.get(i-1);
					 city2 = list.get(i);

					 dis = problemInstance.costMatrix[d+city1][d+city2];
					 System.out.format("Distance:   %d and %d is %f \n",city1,city2,dis);
					
					 totalCost+=dis;
				 }
				 
				 city1 = list.get(list.size()-1);
				 dis = problemInstance.costMatrix[d+city1][depot];
				 totalCost+=dis;
				 
				 System.out.format("Distance: %d -> depot %d is %f \n",city1,depot,dis);
				 
				 System.out.format("Total Distance: %f \n",totalCost);

				 double totalServiceTime = 0;
				 for(i=0;i<list.size();i++)
				 {
					 city1 = list.get(i);
					 
					 dis = problemInstance.serviceTime[city1];
					 totalServiceTime += dis;
					 System.out.format("Client: %d -> Service time %f \n",city1,dis);
				 }
				 
				 System.out.format("Total Service Time: %f \n",totalServiceTime);
				 System.out.format("Total Route Time: %f \n",totalServiceTime+totalCost);

	

				 double totalLoad = 0;
				 for(i=0;i<list.size();i++)
				 {
					 city1 = list.get(i);
					 
					 dis = problemInstance.demand[city1];
					 totalLoad += dis;
					 System.out.format("Client: %d -> Load %f \n",city1,dis);
				 }
				 
				 System.out.format("Total Load: %f \n",totalLoad);
				
				 
				
				break;
	
				
				
			default:
				System.out.print("Good Bye!!");
				return;
			}

				
		}
		

		
	}

}
