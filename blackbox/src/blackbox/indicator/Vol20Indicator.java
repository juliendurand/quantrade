package blackbox.indicator;

import java.util.Date;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.math.stat.StatUtils;

import blackbox.timeserie.TimeSerie;

public class Vol20Indicator implements IIndicator{

	@Override
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates) {
		int nbPeriod = 20;
		double history[] = new double[nbPeriod];
		for(TimeSerie ts : data.values()){
			for(ts.setToFirst();ts.next();){
				try{
					for(int i=0;i<nbPeriod;i++){
						history[i] = ts.getCandle(i).getPctChange();
					}
					double vol = Math.sqrt(StatUtils.variance(history));
					ts.getCandle(0).setVol20(vol);
					//System.out.println(ts.getTicker()+" "+ts.getCandle(0).getDate()+" "+ts.getCandle(0).getVol20());
				}catch(Exception e){				
				}
			}
		}
		
	}
}
