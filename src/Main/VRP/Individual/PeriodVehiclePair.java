package Main.VRP.Individual;

public class PeriodVehiclePair 
{
	int period;
	int vehicle;
	
	public PeriodVehiclePair(int period, int client) 
	{
		this.period = period;
		this.vehicle = client;
	}

	
	
	public int getPeriod() {
		return period;
	}


	public void setPeriod(int period) {
		this.period = period;
	}


	public int getVehicle() {
		return vehicle;
	}



	public void setVehicle(int vehicle) {
		this.vehicle = vehicle;
	}


	@Override
	public String toString() {
		return "<" + period + ", " + vehicle
				+ "> ";
	}
	
	
}
