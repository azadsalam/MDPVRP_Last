package Main.VRP;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Main.Visualiser;


public class ProblemInstance 
{
	Scanner in;
	public PrintWriter out;
	
	public int depotCount,customerCount,periodCount,nodeCount,vehicleCount;
	public int numberOfVehicleAllocatedToThisDepot[]; // kon depot er koyta kore vehicle
	public ArrayList<ArrayList<Integer>> vehiclesUnderThisDepot;
	public double costMatrix[][];
	public double travellingTimeMatrix[][];
	static public double[] dep_x,dep_y,cus_x,cus_y;
	/**
	 * kon vehicle kon depot er under a
	 */
	public int depotAllocation[]; 
	
	/**
	 *  kon vehicle max koto load nite parbe
	 */
	public double loadCapacity[]; //
	public double serviceTime[];  // kon client kototuk time lage service pete
	/**
	 * kon client koto demand
	 */
	public double demand[]; 	  // kon client koto demand
	public double timeConstraintsOfVehicles[][]; // periodCount * vehicleCount

	
	public int frequencyAllocation[];

	public ArrayList<ArrayList<Integer>> allPossibleVisitCombinations;
	public double co_ordinates[][];
	
	
	public ProblemInstance(Scanner input,PrintWriter output) throws FileNotFoundException
	{
		int i,j;
		// TODO Auto-generated constructor stub
		this.in = input;
		this.out = output;
		
		
		periodCount = in.nextInt();		
		escapeComment(in);
		
		depotCount = in.nextInt();
		escapeComment(in);

		vehicleCount = in.nextInt();
		escapeComment(in);
		
		
		
		//vehicle per depot
		
		numberOfVehicleAllocatedToThisDepot = new int[depotCount];
		depotAllocation = new int[vehicleCount];
		
		vehiclesUnderThisDepot = new ArrayList<ArrayList<Integer>>();
		for(i=0;i<depotCount;i++)
		{
			vehiclesUnderThisDepot.add(new ArrayList<Integer>());
		}
		
		int vehicleCursor = 0;

		for( j=0;j<depotCount;j++)
		{
			numberOfVehicleAllocatedToThisDepot[j] = in.nextInt();

			for( i=0;i<numberOfVehicleAllocatedToThisDepot[j];i++)
			{
				depotAllocation[vehicleCursor]=j;
				vehiclesUnderThisDepot.get(j).add(vehicleCursor);
				vehicleCursor++;
			}
		}
		
		/*for( j=0;j<depotCount;j++)
		{
			for(i=0;i<vehiclesUnderThisDepot.get(j).size();i++)
				System.out.print(vehiclesUnderThisDepot.get(j).get(i)+" ");
			
			System.out.println();
		}*/
		escapeComment(in);


		//String tmp = in.nextLine();
		//out.println("HERE ->"+tmp);
		
		//capacity of vehicle
		loadCapacity = new double[vehicleCount];
		for( i=0;i<vehicleCount;i++)
		{
			loadCapacity[i] = in.nextDouble();
		}
		escapeComment(in);

		

		//time constraints
        escapeComment(in); // escape the line "; t(total period) lines containg  v (total vehicle)
                         //values each referring maximum time limit for that day for that vehicle (NEW)"

        //read periodCount lines
        timeConstraintsOfVehicles = new double[periodCount][vehicleCount];
        for(i=0;i<periodCount;i++)
        {
            for(j=0;j<vehicleCount;j++)
            {
                timeConstraintsOfVehicles[i][j] = in.nextDouble();
            }
        }
        
        
		//CLIENT COUNT
		customerCount = in.nextInt();
		escapeComment(in);

		//frequency
		frequencyAllocation = new int[customerCount];
		for( i=0 ; i<customerCount; i++)
			frequencyAllocation[i]= in.nextInt();
		escapeComment(in);


		//service time
		serviceTime = new double[customerCount];
		for( i=0; i<customerCount; i++)
		{
			serviceTime[i]=in.nextDouble();
		}
		escapeComment(in);

		//demand
		demand = new double[customerCount];
		for( i=0 ; i<customerCount; i++)
		{
			demand[i] = in.nextDouble();
		}
		escapeComment(in);

		// cost matrix
		escapeComment(in); // escapes the line ";cost matrix"
		nodeCount = customerCount+depotCount;
		costMatrix = new double[nodeCount][nodeCount];

		int row,col;

		for( row=0;row<nodeCount;row++)
			for( col=0;col<nodeCount;col++)
				costMatrix[row][col] = in.nextDouble();

		//for now travel time == cost
		
		
		travellingTimeMatrix = costMatrix;
		//print();
	}
	
	public ProblemInstance(Scanner input,PrintWriter output, boolean fromOriginalBenchmark) throws FileNotFoundException
	{
		// TODO Auto-generated constructor stub
		this.in = input;
		this.out = output;
		
		
		int type = in.nextInt();
		
		int vehicleCountPerDepot = in.nextInt();
		customerCount = in.nextInt();
				
		if(type==8)//MDPVRP
		{	
			periodCount = in.nextInt();
			depotCount = in.nextInt();
		}
		else if(type ==2 ) // MDVRP
		{
			periodCount = 1;
			depotCount = in.nextInt();
		}
		else if(type == 1) //PVRP
		{
			periodCount = in.nextInt();
			depotCount = 1;
		}
		else
		{
			throw new FileNotFoundException("INVALID TYPE ");
		}
		
		vehicleCount = vehicleCountPerDepot * depotCount;
		
		
		
		numberOfVehicleAllocatedToThisDepot = new int[depotCount];
		depotAllocation = new int[vehicleCount];
		vehiclesUnderThisDepot = new ArrayList<ArrayList<Integer>>();
		
		
		for(int i=0;i<depotCount;i++)
		{
			vehiclesUnderThisDepot.add(new ArrayList<Integer>());
		}
		
		int vehicleCursor = 0;

		for(int  j=0;j<depotCount;j++)
		{
			numberOfVehicleAllocatedToThisDepot[j] = vehicleCountPerDepot;

			for( int i=0;i<numberOfVehicleAllocatedToThisDepot[j];i++)
			{
				depotAllocation[vehicleCursor]=j;
				vehiclesUnderThisDepot.get(j).add(vehicleCursor);
				vehicleCursor++;
			}
		}

		//read periodCount lines
        timeConstraintsOfVehicles = new double[periodCount][vehicleCount];
		loadCapacity = new double[vehicleCount];


        
      //read periodCount lines
        //timeConstraintsOfVehicles = new double[periodCount][vehicleCount];
        
		for(int period=0;period<periodCount;period++)
		{
			for(int depot=0;depot<depotCount;depot++)
			{
				double maxDuration = in.nextDouble();
				double maxLoad = in.nextDouble();

				for(int v = 0; v<vehicleCountPerDepot;v++)
				{
					timeConstraintsOfVehicles[period][v+depot*vehicleCountPerDepot] = maxDuration;
					loadCapacity[v + depot*vehicleCountPerDepot] = maxLoad;
				}	
			}
		}
		
		nodeCount = depotCount+customerCount;
		
		//System.out.println(nodeCount);
		
		costMatrix = new double[nodeCount][nodeCount];

		
		co_ordinates = new double[depotCount+customerCount][2];
		serviceTime = new double[customerCount];
		demand = new double[customerCount];
		frequencyAllocation = new int[customerCount];

		
		allPossibleVisitCombinations = new ArrayList<ArrayList<Integer>>();
		for(int client=0;client<customerCount;client++)
		{
			allPossibleVisitCombinations.add(new ArrayList<Integer>());
		}
		
		
		dep_x=new double[depotCount];
    	dep_y=new double[depotCount];
    	cus_x=new double[customerCount];
    	cus_y=new double[customerCount];
		
		if(type == 8 || type == 1) //PVRP & MDPVRP
		{
			readInfoForDepot();
			readInfoForClient(type);
		}
		else
		{
			readInfoForClient(type);
			readInfoForDepot();
		}
		
				
		
		
		for(int i=0;i<nodeCount;i++)
		{
			for(int j=0; j<nodeCount;j++)
			{
				double x1 = co_ordinates[i][0];
				double y1 = co_ordinates[i][1];

				double x2 = co_ordinates[j][0];
				double y2 = co_ordinates[j][1];
				
				double distance = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
				distance = Math.sqrt(distance);
				costMatrix[i][j] = distance;
				
			}
			
		}
		travellingTimeMatrix = costMatrix;
		
		//print();
	}

	public void readInfoForDepot()
	{
		for(int depot=0;depot<depotCount;depot++)
		{
			int serialNo = in.nextInt();
			
			double x = in.nextDouble();
			double y = in.nextDouble();
			
			dep_x[depot] = co_ordinates[depot][0] = x;
			dep_y[depot] = co_ordinates[depot][1] = y;
			
			escapeComment(in);
		}
	}
	public void readInfoForClient(int type)
	{
		for(int client=0;client<customerCount;client++)
		{
			int serialNo = in.nextInt();
			
			double x = in.nextDouble();
			double y = in.nextDouble();
			
			cus_x[client] = co_ordinates[depotCount+client][0] = x;
			cus_y[client] = co_ordinates[depotCount+client][1] = y;
			
			
			serviceTime[client] = in.nextDouble();
			demand[client] = in.nextDouble();
			frequencyAllocation[client] = in.nextInt();
			
			if(type == 8 || type == 1) //PVRP & MDPVRP
			{
				int possibleCOmbinations = in.nextInt();
				//System.out.println(possibleCOmbinations);
				for(int i=0;i<possibleCOmbinations;i++)
				{
					int comb = in.nextInt();
				//	System.out.print(comb+" ");
					allPossibleVisitCombinations.get(client).add(comb);
				}
			}
			else //MDVRP
			{
				int possibleCOmbinations = 1;
				//System.out.println(possibleCOmbinations);
				for(int i=0;i<possibleCOmbinations;i++)
				{
				//	System.out.print(comb+" ");
					allPossibleVisitCombinations.get(client).add(1);
				}				
			}
//			System.out.println();
			escapeComment(in);

		}
	}
	
	public void print() 
	{
		int i,j;
		out.println("Problem Instance : ");
		out.println("Period : "+ periodCount);
		out.println("Depot : " + depotCount);
		out.println("Vehicle Count : "+vehicleCount);
		
		out.print("Vehicle per depot :");
		for(i=0;i<depotCount;i++) out.print(" "+numberOfVehicleAllocatedToThisDepot[i]);
		out.print("\n");
		
		out.print("Depot allocation per depot :");
		for(i=0;i<vehicleCount;i++) out.print(" "+depotAllocation[i]);
		out.print("\n");

		out.print("Load capacity per vehicle :");
		for(i=0;i<vehicleCount;i++) out.print(" "+loadCapacity[i]);
		out.print("\n");
		
		out.print("Time constraints per vehicle :\n");
		for(i=0;i<periodCount;i++)
        {
            for(j=0;j<vehicleCount;j++)
            {
            	out.print(timeConstraintsOfVehicles[i][j]+" ");
            }
            out.print("\n");
        }
		
		
		/*out.print("Load capacity per vehicle :");
		for(i=0;i<vehicleCount;i++) out.print(" "+loadCapacity[i]);
		out.print("\n");
		*/
		

		
		out.println("Clients : "+customerCount);

		out.print("Frequency allocation : ");
		for( i =0;i<customerCount ;i++) out.print(frequencyAllocation[i] + " ");
		out.println();

		out.print("Service Time : ");
		for( i =0;i<customerCount ;i++) out.print( serviceTime[i] + " ");
		out.println();

		out.print ("Demand (load) : ");
		for( i =0;i<customerCount ;i++) out.print(demand[i] + " ");
		out.println();

		out.flush();
		out.print("Printing cost matrix : \n");

		int row,col;
		for( row=0;row<nodeCount;row++)
		{
			out.print("from node - "+row +" : ");
			for( col=0;col<nodeCount;col++)
			{
				out.print(costMatrix[row][col]+" ");
			}
			out.println();
		}
		out.println();
		
		
		if(allPossibleVisitCombinations!=null)
		{
			out.println("Visit Combinations : ");
			for(int client=0;client<customerCount;client++)
			{
				out.print("Client "+client+ " : ");
				int size = allPossibleVisitCombinations.get(client).size();
				//System.out.println(size);
				for(int indx=0;indx<size;indx++)
				{
					int comb  = allPossibleVisitCombinations.get(client).get(indx);					
					out.print(comb+" ( "+ Arrays.toString(toBinaryArray(comb))+" ) ");
				}
				out.println("");	
			}
		}

			
		out.flush();
				
	}
	
	public void escapeComment(Scanner input) 
	{
		input.nextLine();
		//String comment = input.nextLine();
		//System.out.println("COMMENT : "+comment);
	}
	
	public PrintWriter getPrintWriter() {
		return this.out;
		
	}
	
	
	public int[] toBinaryArray(int combination)
	{
		int[] bitArray = new int[periodCount];
		
		for(int i=periodCount-1;i>=0;i--)
		{
			bitArray[i] = combination%2;
			combination/=2;
		}
		return bitArray;
		//return  Arrays.toString(bitArray);
		
		/*StringBuilder bitString = new StringBuilder(Integer.toBinaryString(combination)); 
		
		while(bitString.length()<periodCount)
		{
			bitString = new StringBuilder("0").append(bitString);
		}
		
		return bitString.toString();*/
	}
	
	
	
}
