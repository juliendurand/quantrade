package blackbox.indicator;

import java.util.Date;
import java.util.Map;
import java.util.TreeSet;

import blackbox.timeserie.TimeSerie;

public class PctChangeIndicator implements IIndicator{

	
	
	@Override
	public String getName() {
		return "PctChange";
	}

	@Override
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates) {
		for(TimeSerie ts : data.values()){
			for(ts.setToFirst();ts.next();){
				try{
					double closeToday = ts.getCandle(0).getAdjustedClose().doubleValue();
					double closeYesterday = ts.getCandle(1).getAdjustedClose().doubleValue();
					double pctChange = closeToday/closeYesterday - 1d;
					ts.getCandle(0).setIndicator(getName(), pctChange);
					//System.out.println(ts.getTicker()+" "+ts.getCandle(0).getDate()+" "+ts.getCandle(0).getIndicator(getName()));
				}catch(Exception e){				
				}
			}
		}
		
	}
	
}
