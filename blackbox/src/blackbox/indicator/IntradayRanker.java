package blackbox.indicator;

import blackbox.timeserie.DailyCandle;

public class IntradayRanker extends ARanker {

	public double getCandleValue(DailyCandle candle){
		return candle.intradayPctChange;
	}
	
	public void setCandleValue(DailyCandle candle, double rank){
		candle.intradayRank = rank;
	}

}
