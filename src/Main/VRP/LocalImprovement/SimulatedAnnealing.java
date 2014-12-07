package Main.VRP.LocalImprovement;


import java.util.Random;

import org.w3c.dom.ls.LSException;

import Main.Utility;
import Main.VRP.GeneticAlgorithm.Mutation;
import Main.VRP.GeneticAlgorithm.MutationWithWeightingScheme;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.MutationInterface;
import Main.VRP.Individual.MutationOperators.Three_Opt;


/**
 * Artificial Intelligence A Modern Approach (3rd Edition): Figure 4.5, page
 * 126.<br>
 * <br>
 *
 * <pre>
 * function SIMULATED-ANNEALING(problem, schedule) returns a solution state
 *                    
 *   current &lt;- MAKE-NODE(problem.INITIAL-STATE)
 *   for t = 1 to INFINITY do
 *     T &lt;- schedule(t)
 *     if T = 0 then return current
 *     next &lt;- a randomly selected successor of current
 *     /\E &lt;- next.VALUE - current.value
 *     if /\E &gt; 0 then current &lt;- next
 *     else current &lt;- next only with probability e&circ;(/\E/T)
 * </pre>
 *
 * Figure 4.5 The simulated annealing search algorithm, a version of stochastic
 * hill climbing where some downhill moves are allowed. Downhill moves are
 * accepted readily early in the annealing schedule and then less often as time
 * goes on. The schedule input determines the value of the temperature T as a
 * function of time.
 *
 * @author Ravi Mohan
 * @author Mike Stampone
 */


public class SimulatedAnnealing  extends LocalSearch
{

		private boolean print=false;
        private Scheduler scheduler;
        private Random rand;
        private MutationInterface mutation;

        /**
         * Constructs a simulated annealing search from the specified heuristic
         * function and a default scheduler.
         *
         * @param hf
         *            a heuristic function
         */
        public SimulatedAnnealing() 
        {         
        	scheduler = new Scheduler();
        	rand = new Random();
        	mutation = new MutationWithWeightingScheme();
        }

        
        public SimulatedAnnealing(MutationInterface mutat) 
        {         
        	scheduler = new Scheduler();
        	rand =  new Random();
        	mutation = mutat;
        }

        public SimulatedAnnealing(MutationInterface mutat,int k, double lam, int limit,boolean print) 
        {         
        	scheduler = new Scheduler(k, lam, limit);
        	rand =  new Random();
        	mutation = mutat;
        	this.print = print; 
        }

        

    	@Override
    	public void improve(Individual initialNode, double loadPenaltyFactor, double routeTimePenaltyFactor) 
    	{
    		// TODO Auto-generated method stub

    		//Mutation mutation = new Mutation();    		
    		Individual current,next,best;
    		current = new Individual(initialNode);
    		best= current;
			//calculate cost with current penalty factors
    		TotalCostCalculator.calculateCost(current, loadPenaltyFactor, routeTimePenaltyFactor);
    		double previousCostWithPenalty = current.costWithPenalty;
			next = null;
                    
    		// for t = 1 to INFINITY do
            int timeStep = 0;
            
            // temperature <- schedule(t)
            do
            {
	            double temperature = scheduler.getTemp(timeStep);
	         //   System.out.println("TimeStep - Temp : "+timeStep+" "+temperature );
	            if(print)
	            {
	            	System.out.print("Iteration: "+timeStep+" Cost: "+current.costWithPenalty);
	            	if(current.isFeasible)System.out.println(" feasible");
	            	else System.out.println(" infeasible");
	            }
	            
	            timeStep++;
	            // if temperature = 0 then return current
	            if (temperature == 0.0) 
	            {
	                    break;
	            }
	
	            // next <- a randomly selected successor of current
	            next = new Individual(current);
    			applyMutation(next);
    			TotalCostCalculator.calculateCost(next, loadPenaltyFactor, routeTimePenaltyFactor);
	            
    			
    			//System.out.print("Iter: "+timeStep+" Cur: "+current.costWithPenalty+" Next: "+next.costWithPenalty);
    			
    			// /\E <- current.VALUE - next.value
    			// if del E +ve -> next better 
	    		double deltaE = current.costWithPenalty - next.costWithPenalty;
	
	            if (shouldAccept(temperature, deltaE))
	            {
	                    current = next;
	            }
	            
	            if(current.costWithPenalty<best.costWithPenalty)
	            	best=current;
	            //System.out.println(" Best: "+best.costWithPenalty);
	            
            }while(true);

            //Three_Opt.onAllROute(current);
            //System.out.println("Before - After : "+initialNode.costWithPenalty + " "+current.costWithPenalty);
            if(previousCostWithPenalty>best.costWithPenalty)
            	initialNode.copyIndividual(best);
            
           // System.exit(1);

    	}
		
        // if /\E > 0 then current <- next
        // else current <- next only with probability e^(/\E/T)
        private boolean shouldAccept(double temperature, double deltaE) 
        {
        	if(deltaE >0) return true;
        	else    return (rand.nextDouble() <= probabilityOfAcceptance(
                                                temperature, deltaE));
        }

        /**
         * Returns <em>e</em><sup>&delta<em>E / T</em></sup>
         *
         * @param temperature
         *            <em>T</em>, a "temperature" controlling the probability of
         *            downward steps
         * @param deltaE
         *            VALUE[<em>next</em>] - VALUE[<em>current</em>]
         * @return <em>e</em><sup>&delta<em>E / T</em></sup>
         */
        public double probabilityOfAcceptance(double temperature, double deltaE) 
        {
        	
        		double divisionValue = deltaE / temperature;
        		double probability = Math.exp(divisionValue);
//        		System.out.println("DeltaE "+ deltaE+" Division Value: "+divisionValue+" Probability : "+ probability);
//        		System.out.println("DeltaE "+ deltaE+" Probability : "+ probability);

                return probability;
        }

		void applyMutation(Individual offspring)
		{
			
			mutation.applyMutation(offspring);
//			Three_Opt.onAllROute(offspring);
//			offspring.calculateCostAndPenalty();

		}

		
		public  void testScheduler() 
		{	
			Scheduler s = scheduler;
		
			for(int i=0;i<1000 ;i++)
			{
				double temp = s.getTemp(i);
				double prb = probabilityOfAcceptance(temp, -2);
				System.out.format("%d, %f\n",i,prb);
			}
		}
		
}

/**
 * @author Ravi Mohan
 *
 */
class Scheduler 
{

        private final int k, limit;

        private final double lam;

        public Scheduler(int k, double lam, int limit) {
                this.k = k;
                this.lam = lam;
                this.limit = limit;
        }

        public Scheduler() 
        {
/*                this.k = 20;
        		//this.k = 25;
                this.lam = 0.045;
                this.limit = 1000;*/
            this.k = 20;
    		//this.k = 25;
            this.lam = 0.01;
            this.limit = 1000;
        }

        public Scheduler(int limit) 
        {
                this.k = 20;
        		//this.k = 25;
                this.lam = 0.045;
                this.limit = limit;
        }
        public double getTemp(int t) {
                if (t < limit)
                {
                        double res = k * Math.exp((-1) * lam * t);
                        return res;
                } 
                else 
                {
                        return 0.0;
                }
        }
}



