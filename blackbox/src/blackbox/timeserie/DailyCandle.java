package blackbox.timeserie;

import java.math.BigDecimal;
import java.util.Date;

public class DailyCandle {
	
	private Date _date;
	private BigDecimal _open;
	private BigDecimal _high;
	private BigDecimal _low;
	private BigDecimal _close;
	private BigDecimal _volume;
	
	public Date getDate() {
		return _date;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public BigDecimal getOpen() {
		return _open;
	}
	
	public void setOpen(BigDecimal open) {
		_open = open;
	}
	
	public BigDecimal getHigh() {
		return _high;
	}
	
	public void setHigh(BigDecimal high) {
		_high = high;
	}
	
	public BigDecimal getLow() {
		return _low;
	}
	
	public void setLow(BigDecimal low) {
		_low = low;
	}
	
	public BigDecimal getClose() {
		return _close;
	}
	
	public void setClose(BigDecimal close) {
		_close = close;
	}
	
	public BigDecimal getVolume() {
		return _volume;
	}
	
	public void setVolume(BigDecimal volume) {
		_volume = volume;
	}
	
}
