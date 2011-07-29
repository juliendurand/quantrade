package blackbox.indicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.math.stat.StatUtils;

import blackbox.strategy.TickerPerformance;
import blackbox.timeserie.DailyCandle;
import blackbox.timeserie.TimeSerie;

public class ABCDERankIndicator implements IIndicator {

	Map<String, List<Double>> meanStats = new HashMap<String, List<Double>>();
	Map<String, List<Double>> pctWinStats = new HashMap<String, List<Double>>();

	@Override
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates) {
		for(Date date : dates){
			for(TimeSerie ts : data.values()){
				char c='@';
				try {
					double rank = ts.getCandle(date).intradayRank;
					//System.out.println(rank);
					if(rank>0.9)
						c = 'A';
					if(rank>0.8 && rank<0.9)
						c = 'B';
					if(rank>0.7 && rank<0.8)
						c = 'C';
					if(rank>0.6 && rank<0.7)
						c = 'D';
					if(rank>0.5 && rank<0.6)
						c = 'E';
					if(rank>0.4 && rank<0.5)
						c = 'F';
					if(rank>0.3 && rank<0.4)
						c = 'G';
					if(rank>0.2 && rank<0.3)
						c = 'H';
					if(rank>0.1 && rank<0.2)
						c = 'I';
					if(rank<=0.1)
						c = 'J';
					ts.getCandle(date)._quintile  = c;
					//System.out.println(ts.getTicker()+" "+date+" rank :"+ts.getCandle(date)._quintile);
				
					ts.setCursorOn(date);
					String key =String.valueOf(ts.getCandle(2)._quintile)+ 
								String.valueOf(ts.getCandle(1)._quintile)+
								String.valueOf(ts.getCandle(0)._quintile);
					if(!meanStats.containsKey(key)){
						meanStats.put(key, new ArrayList<Double>());
						pctWinStats.put(key, new ArrayList<Double>());
					}
					
					double gain = ts.getCandle(-1).getOpen()/ts.getCandle(0).getClose()-1;
					if(gain!=Double.NaN && Math.abs(gain)<0.2){
						meanStats.get(key).add(gain*100d);
						if(gain>0d)
							pctWinStats.get(key).add(1d);
						else
							pctWinStats.get(key).add(0d);
					}
						
				} catch (Exception e) {
				}
			}
		}
		List<TickerPerformance> performances = new ArrayList<TickerPerformance>();
		for(String key : meanStats.keySet()){
			//if(key.contains("@"))
			//		continue;
			List<Double> meanSet = meanStats.get(key);
			
			double[] values = new double[meanSet.size()];
			
			int i = 0;
			for(Double d : meanSet){
				values[i++]=d;
			}
			double mean = StatUtils.mean(values);
			performances.add(new TickerPerformance(key, mean));			
		}
		Collections.sort(performances);
		for(TickerPerformance tp : performances){
			List<Double> pctWinSet = pctWinStats.get(tp.ticker);
			double[] win = new double[pctWinSet.size()];
			int i = 0;
			for(Double d : pctWinSet){
				win[i++]=d;
			}
			//if(Math.abs(tp.performance)>0.2)
				System.out.println(tp.ticker+" "+tp.performance+" with "+StatUtils.mean(win)+"% success on card="+meanStats.get(tp.ticker).size());
		}
	}

}
