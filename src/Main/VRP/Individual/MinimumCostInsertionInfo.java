package Main.VRP.Individual;

public class MinimumCostInsertionInfo
{
	public int vehicle;
	public int insertPosition;

	public double increaseInCost;
	
	public double loadViolation;
	public double routeTimeViolation;
	/**
	 * If insertion creates a feasible route w.r.to load capacity, loadViolationContribution must be 0
	 */
	public double loadViolationContribution;
	public double routeTimeViolationContribution;

}
