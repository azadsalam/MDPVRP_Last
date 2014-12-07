package Main.VRP.Individual;

public class PeriodClientPair 
{
	int period;
	int client;
	
	public PeriodClientPair(int period, int client) 
	{
		this.period = period;
		this.client = client;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + client;
		result = prime * result + period;
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeriodClientPair other = (PeriodClientPair) obj;
		if (client != other.client)
			return false;
		if (period != other.period)
			return false;
		return true;
	}



	public int getPeriod() 
	{
		return period;
	}

	public int getClient() 
	{
		return client;
	}



	@Override
	public String toString() {
		return "<" + period + ", " + client
				+ "> ";
	}
	
	
}
