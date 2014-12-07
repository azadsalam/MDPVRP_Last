package Main.VRP.Individual.MutationOperators;

import Main.VRP.Individual.Individual;

public interface MutationInterface {

	public void updateWeights();
	public void applyMutation(Individual offspring);

	public void mutateSpecificRoute(Individual individual,int period, int vehicle);
}
