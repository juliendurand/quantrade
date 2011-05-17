package blackbox.indicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import blackbox.timeserie.TimeSerie;

public class PctRankIndicator implements IIndicator {

	@Override
	public String getName() {
		return "PctRank";
	}

	@Override
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates) {
		IIndicator pctChangeIndicator = new PctRankIndicator();
		
		for(Date date : dates){
			Map<String, Double> pctChanges = new HashMap<String, Double>();
			for(TimeSerie ts : data.values()){
				try {
					Double pctChange = (Double) ts.getCandle(date).getIndicator(pctChangeIndicator.getName());
					pctChanges.put(ts.getTicker(), pctChange);
					System.out.println(ts.getTicker()+" "+date+" "+ts.getCandle(date).getIndicator(pctChangeIndicator.getName()));

				} catch (Exception e) {
				}
			}
			List<Double> values = new ArrayList<Double>(pctChanges.values());
			if(values==null)
				continue;
			Collections.sort(values);
			for(TimeSerie ts : data.values()){
				double pctChange;
				try {
					pctChange = (Double) ts.getCandle(date).getIndicator(pctChangeIndicator.getName());
					System.out.println(ts.getTicker()+" "+date+" "+ts.getCandle(date).getIndicator(pctChangeIndicator.getName()));
					ts.getCandle(date).setIndicator(getName(), Collections.binarySearch(values, pctChange)/(values.size()-1));
					System.out.println(ts.getTicker()+" "+date+" "+ts.getCandle(date).getIndicator(getName()));
				} catch (Exception e) {
				}
			}
		}
	}

}
