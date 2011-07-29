package blackbox.indicator;

import java.util.Date;
import java.util.Map;
import java.util.TreeSet;

import blackbox.timeserie.TimeSerie;

public class IntradayChangeIndicator implements IIndicator{

	@Override
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates) {
		for(TimeSerie ts : data.values()){
			for(ts.setToFirst();ts.next();){
				try{
					double closeToday = ts.getCandle(0).getClose();
					double openToday = ts.getCandle(0).getOpen();
					double pctChange = closeToday/openToday - 1d;
					ts.getCandle(0).intradayPctChange = pctChange;
					//System.out.println(ts.getTicker()+" "+ts.getCandle(0).getDate()+" "+ts.getCandle(0).getIndicator(getName()));
				}catch(Exception e){				
				}
			}
		}
		
	}	
	
}
