package blackbox.timeserie;

import java.util.Date;

public class DailyCandle {
	
	private Date _date;
	private double _open;
	private double _high;
	private double _low;
	private double _close;
	private double _volume;
	private double _adjClose;
	private double _pctChange;
	public double intradayPctChange;
	public double overnightPctChange;
	public double intradayRank;
	public double overnightRank;
	private double _rank;
	private double _vol20;
	public char _quintile;

	public Date getDate() {
		return _date;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public double getOpen() {
		return _open;
	}
	
	public void setOpen(double open) {
		_open = open;
	}
	
	public double getHigh() {
		return _high;
	}
	
	public void setHigh(double high) {
		_high = high;
	}
	
	public double getLow() {
		return _low;
	}
	
	public void setLow(double low) {
		_low = low;
	}
	
	public double getClose() {
		return _close;
	}
	
	public void setClose(double close) {
		_close = close;
	}
	
	public double getVolume() {
		return _volume;
	}
	
	public void setVolume(double volume) {
		_volume = volume;
	}
	
	public void setAdjustedClose(double adjustedClose){
		_adjClose = adjustedClose;
	}
	
	public double getAdjustedClose(){
		return _adjClose;
	}

	public double getPctChange() {
		return _pctChange;
	}

	public void setPctChange(double pctChange) {
		this._pctChange = pctChange;
	}

	public double getRank() {
		return _rank;
	}

	public void setRank(double rank) {
		this._rank = rank;
	}

	public double getVol20() {
		return _vol20;
	}

	public void setVol20(double vol20) {
		this._vol20 = vol20;
	}
	
}
