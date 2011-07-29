package blackbox.indicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import blackbox.timeserie.DailyCandle;
import blackbox.timeserie.TimeSerie;

public abstract class ARanker implements IIndicator, IRanker {
	
	@Override
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates) {
		for(Date date : dates){
			Map<String, Double> pctChanges = new HashMap<String, Double>();
			for(TimeSerie ts : data.values()){
				try {
					DailyCandle candle = ts.getCandle(date);
					if(candle==null)
						continue;					
					Double pctChange = getCandleValue(candle);
					if(pctChange==null)
						continue;
					pctChanges.put(ts.getTicker(), pctChange);
				} catch (Exception e) {
				}
			}
			List<Double> values = new ArrayList<Double>(pctChanges.values());
			try {		
				Collections.sort(values);
			} catch (Exception e) {
				continue;
			}
			double[] valueArray = new double[values.size()];
			for(int i=0;i<values.size();i++){
				valueArray[i]=values.get(i);
			}
			for(TimeSerie ts : data.values()){
				Double pctChange;
				try {
					pctChange = getCandleValue(ts.getCandle(date));
					if(pctChange==0)
						continue;
					double rank = Collections.binarySearch(values, pctChange);
					if(rank<0)
						rank=(-rank)-1;
					rank = rank/values.size();	
					setCandleValue(ts.getCandle(date), rank);
					}catch (Exception e) {
						// TODO: handle exception
					}
			}
		}
	}

}
