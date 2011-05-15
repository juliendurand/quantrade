package blackbox.timeserie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSerie {

	private String _ticker;
	private Map<Date, DailyCandle> _candles = new HashMap<Date, DailyCandle>();
	private Date _cursor;
	private List<Date> _dateIndex;
	
	public void load(String ticker){
		_ticker = ticker;
		_dateIndex = new ArrayList<Date>(_candles.keySet());
		Collections.sort(_dateIndex);
	}
	
	public DailyCandle get(int period) throws Exception{
		int index = Collections.binarySearch(_dateIndex, _cursor);
		int position = index-period;
		if(position<0){
			throw new Exception("TimeSerie error - request data before the begining of the serie");
		}
		return _candles.get(_dateIndex.get(position));
	}
	
}
