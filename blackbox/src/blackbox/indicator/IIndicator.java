package blackbox.indicator;

import java.util.Date;
import java.util.Map;
import java.util.TreeSet;

import blackbox.timeserie.TimeSerie;

public interface IIndicator {
	
	public String getName();
	public void calculate(Map<String, TimeSerie> data, TreeSet<Date> dates);

}
