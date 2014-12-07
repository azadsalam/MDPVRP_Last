package Main.VRP.LocalImprovement;

import Main.VRP.Individual.Individual;

public abstract class LocalSearch 
{

	public abstract void improve(Individual initialNode, double loadPenaltyFactor, double routeTimePenaltyFactor) ;

}
