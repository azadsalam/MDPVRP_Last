package Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ExportToCSV 
{
	private String inputFileName;
	public double min[];
	public double avg[];
	public double max[];
	public int feasibleCount[];
	
	
	File outputFile,outputFile2;
	PrintWriter out,out2;
	int generation;
	public ExportToCSV(String inputFileName) 
	{
		// TODO Auto-generated constructor stub
		this.inputFileName =  inputFileName;
		
		int lastIndex = inputFileName.lastIndexOf(".");
		if(lastIndex==-1)lastIndex = inputFileName.length();
		int firstIndex = inputFileName.indexOf("/");
		
		String path = "reports/"+inputFileName.substring(firstIndex+1,lastIndex);
		String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime());
		
		outputFile = new File(path+"_"+timeStamp+"_Solution_over_generation.csv");
		//output = new PrintWriter(System.out);//for console output
		try 
		{
			out = new PrintWriter(outputFile);
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//for file output				
	}
	
	public void init(int generation)
	{
		this.generation=generation;
		min = new double[generation];
		avg = new double[generation];
		max = new double[generation];
		feasibleCount = new int[generation];
		
	}
	

	public void createCSV() 
	{
		out.print("Solutions over population");
		out.print("\n");
	
		
		out.print("");
		out.print(",");
		out.print("Best");
		out.print(",");
		out.print("Avg");
		out.print(",");
		out.print("Worst");
		out.println("");
		
		for(int i=0;i<generation;i++)
		{
			out.print(i);
			out.print(",");
			out.print(min[i]);
			out.print(",");
			out.print(avg[i]);
			out.print(",");
			out.print(max[i]);
			out.println("");
		}
		
		out.flush();
		out.close();
	}
}
