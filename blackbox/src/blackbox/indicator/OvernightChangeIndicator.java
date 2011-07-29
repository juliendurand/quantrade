package blackbox.indicator;

import java.util.Date;
import java.util.Map;
import java.util.TreeSet;

import blackbox.timeserie.TimeSerie;

public class OvernightChangeIndicator implements IIndicator{
	
	@Override
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates) {
		for(TimeSerie ts : data.values()){
			for(ts.setToFirst();ts.next();){
				try{
					double closeYersterday = ts.getCandle(1).getClose();
					double openToday = ts.getCandle(0).getOpen();
					double pctChange = openToday/closeYersterday - 1d;
					ts.getCandle(0).overnightPctChange = pctChange;
					//System.out.println(ts.getTicker()+" "+ts.getCandle(0).getDate()+" "+ts.getCandle(0).getIndicator(getName()));
				}catch(Exception e){				
				}
			}
		}
		
	}	
	
}
