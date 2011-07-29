package blackbox.strategy;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import blackbox.bank.TradingAccount;
import blackbox.data.Downloader;
import blackbox.exchange.AInterdayStrategy;
import blackbox.exchange.IOrder;
import blackbox.exchange.IOrder.OrderDirection;
import blackbox.exchange.InterdayExchange;
import blackbox.exchange.MarketOrder;
import blackbox.timeserie.DailyCandle;
import blackbox.timeserie.TimeSerie;

public class DataExtracterStrategy extends AInterdayStrategy {

	private InterdayExchange _exchange;
	private PrintWriter _printer;
	
	public DataExtracterStrategy(InterdayExchange exchange){
		_exchange = exchange;
		init();
	}

	@Override
	public InterdayExchange getExchange() {
		return _exchange;
	}

	@Override
	public BigDecimal getStartingCapital() {
		return BigDecimal.valueOf(10000.00d);
	}

	@Override
	public void onDayStart() {	

	}

	@Override
	public void onPreOpen() {
	}

	@Override
	public void onOpen() {
	}

	@Override
	public void onPreClose() {
	}

	@Override
	public void onClose() {
		DecimalFormat df = new DecimalFormat(); 
		df.setMaximumFractionDigits(4) ;
		try {
			for(String ticker : _exchange.getAllTickers()){
				DailyCandle candle = _exchange.getDailyCandle(ticker, 0);
				StringBuffer sb = new StringBuffer();
				sb.append(df.format(candle.intradayRank));
				sb.append(", ");
				sb.append(df.format(candle.overnightRank));
				sb.append(", ");
				sb.append(df.format(candle.getVol20()));
				sb.append(", ");
				candle = _exchange.getDailyCandle(ticker, 1);
				sb.append(df.format(candle.intradayRank));
				sb.append(", ");
				sb.append(df.format(candle.overnightRank));
				sb.append(", ");
				candle = _exchange.getDailyCandle(ticker, 2);
				sb.append(df.format(candle.intradayRank));
				sb.append(", ");
				sb.append(df.format(candle.overnightRank));
				_printer.println(sb.toString());
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public void init(){
		try{
			String filename = Downloader.PATH+"optimizerdata.csv";
			File file = new File(filename);
			if(!file.exists()){
				file.createNewFile();
			}
			_printer = new PrintWriter(file);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void finish(){
		_printer.flush();
		_printer.close();
	}

	@Override
	public void onDayEnd() {
	}

}
