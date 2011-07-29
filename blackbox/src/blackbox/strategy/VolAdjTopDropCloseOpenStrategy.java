package blackbox.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import blackbox.bank.Position;
import blackbox.bank.TradingAccount;
import blackbox.exchange.AInterdayStrategy;
import blackbox.exchange.IOrder.OrderDirection;
import blackbox.exchange.InterdayExchange;
import blackbox.exchange.MarketOrder;
import blackbox.exchange.StopOrder;
import blackbox.timeserie.DailyCandle;

public class VolAdjTopDropCloseOpenStrategy extends AInterdayStrategy {

	private InterdayExchange _exchange;
	
	private BigDecimal _nbSharesTraded = BigDecimal.ZERO;
	
	private int _maxSelected = 20;
	private int _maxShareTraded =1;
	private List<TickerPerformance> _tickers = new ArrayList<TickerPerformance>();
	private List<TickerPerformance> tmpList;
	List<String> tickers;
	List<TickerPerformance> tickerList = new ArrayList<TickerPerformance>();
	
	public VolAdjTopDropCloseOpenStrategy(InterdayExchange exchange){
		_exchange = exchange;
		tickers = getExchange().getAllTickers();
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
		//((TradingAccount)_exchange.getBank().getAccount(getName())).liquidate(_exchange);
	}

	@Override
	public void onOpen() {
		for(Position pos : ((TradingAccount)_exchange.getBank().getAccount(getName())).getPositions().values()){
			try{
				String ticker = pos.getTicker();
				double close = _exchange.getDailyCandle(ticker, 1).getClose();
				double open = _exchange.getDailyCandle(ticker, 0).getOpen();
				if(open<close){
					//getExchange().registerOrder(new StopOrder(getName(), ticker, _nbSharesTraded , OrderDirection.Sell, new Date(System.currentTimeMillis()), open*0.999));
					//getExchange().registerOrder(new LimitOrder(getName(), ticker, _nbSharesTraded , OrderDirection.Sell, new Date(System.currentTimeMillis()), open * 1.001));				
					getExchange().registerOrder(new StopOrder(getName(), ticker, _nbSharesTraded , OrderDirection.Sell, new Date(System.currentTimeMillis()), open*0.998));
				}else{
					getExchange().registerOrder(new StopOrder(getName(), ticker, _nbSharesTraded , OrderDirection.Sell, new Date(System.currentTimeMillis()), open*0.998));

					//getExchange().registerOrder(new LimitOrder(getName(), ticker, _nbSharesTraded , OrderDirection.Sell, new Date(System.currentTimeMillis()), open));
					//((TradingAccount)_exchange.getBank().getAccount(getName())).liquidate(_exchange);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void onPreClose() {
		_exchange.clearOrders(getName());
		((TradingAccount)_exchange.getBank().getAccount(getName())).liquidate(_exchange);
		getDayTopGainer();
		for(TickerPerformance tp: _tickers){	
			String ticker = tp.ticker;
			try{
				double price = getExchange().getMarketOfficialPrice(ticker).doubleValue();
				_nbSharesTraded = BigDecimal.valueOf((int)(getExchange().getBank().getAccount(getName()).getBalance("EUR").doubleValue()/price/_maxShareTraded));
				getExchange().registerOrder(new MarketOrder(getName(), ticker, _nbSharesTraded , OrderDirection.Buy, new Date(System.currentTimeMillis())));
			}catch (Exception e) {
			}
		}
	}

	@Override
	public void onClose() {
		//System.out.println("balance: "+_exchange.getBank().getAccount(getName()).getBalance("EUR"));
	}

	@Override
	public void onDayEnd() {
	}
	
	private void getDayTopGainer(){
		tickerList.clear();
		for(String ticker : tickers){
			try{
				DailyCandle candle = getExchange().getDailyCandle(ticker, 0);
				if(getExchange().getDailyCandle(ticker, 1).getClose()>1.05*candle.getOpen())
				//if(candle.getVol20() >0.05){
					continue;
				//}			
				double change = candle.getOpen()/candle.getClose();
				tickerList.add(new TickerPerformance(ticker, change));
			}catch (Exception e) {
				//e.printStackTrace();
			}		
		}
		Collections.sort(tickerList);
		Collections.reverse(tickerList);
		tmpList = tickerList;
		if(tickerList.size()>_maxSelected){
			tmpList = tickerList.subList(0,_maxSelected);
		}
		_tickers.clear();
		for(TickerPerformance tp : tmpList){
			try{
				if(getExchange().getDailyCandle(tp.ticker, 0).getVol20() <0.02){
					_tickers.add(tp);
				}
				if(_tickers.size()>=_maxShareTraded)
					break;
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
