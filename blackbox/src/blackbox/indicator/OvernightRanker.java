package blackbox.indicator;

import blackbox.timeserie.DailyCandle;

public class OvernightRanker extends ARanker {

	public double getCandleValue(DailyCandle candle){
		return candle.overnightPctChange;
	}
	
	public void setCandleValue(DailyCandle candle, double rank){
		candle.overnightRank = rank;
	}

}
