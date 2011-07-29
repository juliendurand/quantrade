package blackbox.strategy;

public class TickerPerformance implements Comparable<TickerPerformance> {
	public String ticker;
	public double performance;
	
	public TickerPerformance(String ticker, double performance){
		this.ticker = ticker;
		this.performance = performance;
	}
	
	public int compareTo(TickerPerformance tp){
		double diff = (performance-tp.performance);
		if(diff>0)
			return 1;
		if(diff<0)
			return -1;
		return 0;
	}
}
