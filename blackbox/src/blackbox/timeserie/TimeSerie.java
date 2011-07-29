package blackbox.timeserie;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blackbox.data.CSVFileReader;
import blackbox.data.Downloader;

public class TimeSerie {

	private DateFormat _dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private String _ticker;
	private Map<Date, DailyCandle> _candles = new HashMap<Date, DailyCandle>();
	private int _cursor = -1;
	private List<Date> _dateIndex;
	
	public TimeSerie(String ticker) throws Exception{
		load(ticker);
	}
	
	public void load(String ticker) throws Exception{
		_ticker = ticker;	
		List<String[]> content = CSVFileReader.readFile(Downloader.PATH+ticker+".ts");
		//remove header line
		content.remove(0);
		for(String[] row : content){			
		    Date date = (Date)_dateFormatter.parse(row[0]);
		    double open = Double.parseDouble(row[1]);
		    double high = Double.parseDouble(row[2]);
		    double low = Double.parseDouble(row[3]);
		    double close = Double.parseDouble(row[4]);
		    double volume = Double.parseDouble(row[5]);
		    double adjClose = Double.parseDouble(row[6]);
		    
		    DailyCandle candle = new DailyCandle();	    
		    candle.setDate(date);
		    candle.setOpen(open);
		    candle.setHigh(high);
		    candle.setLow(low);
		    candle.setClose(close);
		    candle.setVolume(volume);
		    candle.setAdjustedClose(adjClose);
		    
		    _candles.put(date, candle);
		}
		_dateIndex = new ArrayList<Date>(_candles.keySet());
		Collections.sort(_dateIndex);
	}
	
	public String getTicker(){
		return _ticker;
	}
	
	public void setCursorBefore(Date date){
		int index = Collections.binarySearch(_dateIndex, date);
		if(index>0){ // traded on that day
			_cursor = index-1;
		}
	}
	
	public void setCursorOn(Date date){
		int index = Collections.binarySearch(_dateIndex, date);
		if(index>0){ // traded on that day
			_cursor = index;
		}else{
			_cursor=-1;
		}
	}
	
	public Set<Date> getTradingDays(){
		return _candles.keySet();
	}
	
	public DailyCandle getLastCandle(){
		return _candles.get(_dateIndex.get(_dateIndex.size()-1));
	}
	
	public DailyCandle getCandle(String dateString) throws Exception{
		Date date = _dateFormatter.parse(dateString);
		return getCandle(date);
	}
	
	public DailyCandle getCandle(Date date) throws Exception{
		return _candles.get(date);
	}
	
	public DailyCandle getCandle(int period) throws Exception{
		int position = _cursor-period;
		if(position<0){
			throw new Exception("TimeSerie error - request data before the begining of the serie");
		}
		if(position>=_candles.size()){
			throw new Exception("TimeSerie error - request data after the end of the serie");
		}
		return _candles.get(_dateIndex.get(position));
	}
	
	public void setToFirst(){
		_cursor = 0;
	}
	
	public boolean next(){
		_cursor++;
		if(_cursor>=_dateIndex.size()){
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		TimeSerie ts = new TimeSerie("FTE.PA");
		System.out.println(ts.getCandle("2003-01-02").getClose());
	}
	
}
