package Main.VRP.SelectionOperator;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;

public abstract class SelectionOperator 
{
	boolean survivalSelection;
	boolean mark[];
	public ProblemInstance problemInstance;
	
	 public void setSelectionOperator(ProblemInstance problemInstance) 
	 {
		this.problemInstance = problemInstance; 
	 }	
	
	public void initialise(Individual[] population,boolean survivalSelection)
	{
		
	}
	
	abstract public Individual getIndividual(Individual[] population);
	
	public void setProblemInsctance(ProblemInstance problemInstance) 
	{
		this.problemInstance = problemInstance; 
	}
}
