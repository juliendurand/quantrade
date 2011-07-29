package blackbox.indicator;

import blackbox.timeserie.DailyCandle;

public interface IRanker {
	
	public double getCandleValue(DailyCandle candle);
	
	public void setCandleValue(DailyCandle candle, double rank);

}
