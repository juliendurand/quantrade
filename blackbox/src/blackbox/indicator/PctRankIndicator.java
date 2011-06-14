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

public class PctRankIndicator implements IIndicator {

	@Override
	public String getName() {
		return "PctRank";
	}

	@Override
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates) {
		IIndicator pctChangeIndicator = new PctChangeIndicator();
		String pctChangeName = pctChangeIndicator.getName();
		for(Date date : dates){
			Map<String, Double> pctChanges = new HashMap<String, Double>();
			for(TimeSerie ts : data.values()){
				try {
					DailyCandle candle = ts.getCandle(date);
					if(candle==null)
						continue;					
					Object o = candle.getIndicator(pctChangeName);
					Double pctChange = (Double) o;
					if(pctChange==null)
						continue;
					pctChanges.put(ts.getTicker(), pctChange);
					//System.out.println(ts.getTicker()+" "+date+" "+ts.getCandle(date).getIndicator(pctChangeIndicator.getName()));
				} catch (Exception e) {
				}
			}
			List<Double> values = new ArrayList<Double>(pctChanges.values());
			if(values==null)
				continue;
			try {
				Collections.sort(values);
			} catch (Exception e) {
				continue;
			}
			for(TimeSerie ts : data.values()){
				Double pctChange;
				try {
					pctChange = (Double) ts.getCandle(date).getIndicator(pctChangeName);
					if(pctChange==null)
						continue;
					double rank = Collections.binarySearch(values, pctChange);
					rank = rank /(values.size()-1);
					ts.getCandle(date).setIndicator(getName(), rank );
					//System.out.println(ts.getTicker()+" "+date+" rank :"+ts.getCandle(date).getIndicator(getName()));
				} catch (Exception e) {
				}
			}
		}
	}

}
