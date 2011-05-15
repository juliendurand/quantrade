package blackbox.timeserie;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blackbox.data.CSVFileReader;
import blackbox.data.Downloader;

public class TimeSerie {

	private DateFormat _dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private String _ticker;
	private Map<Date, DailyCandle> _candles = new HashMap<Date, DailyCandle>();
	private Date _cursor;
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
		    BigDecimal open = new BigDecimal(row[1]);
		    BigDecimal high = new BigDecimal(row[2]);
		    BigDecimal low = new BigDecimal(row[3]);
		    BigDecimal close = new BigDecimal(row[4]);
		    BigDecimal volume = new BigDecimal(row[5]);
		    BigDecimal adjClose = new BigDecimal(row[6]);
		    
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
	
	public DailyCandle getLastCandle(){
		return _candles.get(_dateIndex.get(_dateIndex.size()-1));
	}
	
	public DailyCandle getCandle(String dateString) throws Exception{
		Date date = _dateFormatter.parse(dateString);
		return _candles.get(date);
	}
	
	public DailyCandle getCandle(int period) throws Exception{
		int index = Collections.binarySearch(_dateIndex, _cursor);
		int position = index-period;
		if(position<0){
			throw new Exception("TimeSerie error - request data before the begining of the serie");
		}
		return _candles.get(_dateIndex.get(position));
	}
	
	public static void main(String[] args) throws Exception {
		TimeSerie ts = new TimeSerie("FTE.PA");
		System.out.println(ts.getCandle("2003-01-02").getClose());
	}
	
}
