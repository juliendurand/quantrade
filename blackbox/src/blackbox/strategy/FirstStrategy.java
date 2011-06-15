package blackbox.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import blackbox.bank.TradingAccount;
import blackbox.exchange.IInterdayStrategy;
import blackbox.exchange.IOrder;
import blackbox.exchange.InterdayExchange;
import blackbox.exchange.MarketOrder;
import blackbox.exchange.IOrder.OrderDirection;
import blackbox.indicator.IIndicator;
import blackbox.indicator.PctChangeIndicator;
import blackbox.indicator.PctRankIndicator;
import blackbox.timeserie.DailyCandle;

public class FirstStrategy implements IInterdayStrategy {

	private InterdayExchange _exchange;
	
	private List<TickerPerformance> _tickers = new ArrayList<TickerPerformance>();
	private BigDecimal _nbSharesTraded = BigDecimal.ZERO;
	private int max =1;
	
	public FirstStrategy(InterdayExchange exchange){
		_exchange = exchange;
	}
	
	@Override
	public String getName() {
		return "FirstStrategy";
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

	private void getDayTopGainer() {
		IIndicator pctChangeIndicator = new PctChangeIndicator();
		String pctChangeName = pctChangeIndicator.getName();
		IIndicator pctRankIndicator = new PctRankIndicator();
		String rankName = pctRankIndicator.getName();
		List<String> tickers = getExchange().getAllTickers();
		//double maxGain = 0d;
		//String tickerMaxGain = null;
		_tickers.clear();
		for(String ticker : tickers){
			try {
				Double pctChange = (Double) getExchange().getDailyCandle(ticker, 0).getIndicator(pctChangeName);
				Double pctRank = (Double) getExchange().getDailyCandle(ticker, 0).getIndicator(rankName);
				Double pctRank2 =	(Double) getExchange().getDailyCandle(ticker, 1).getIndicator(rankName)
					+(Double) getExchange().getDailyCandle(ticker, 2).getIndicator(rankName)
					+(Double) getExchange().getDailyCandle(ticker, 3).getIndicator(rankName)
					+(Double) getExchange().getDailyCandle(ticker, 4).getIndicator(rankName)
					+(Double) getExchange().getDailyCandle(ticker, 5).getIndicator(rankName);
					//+(Double) getExchange().getDailyCandle(ticker, 6).getIndicator(rankName)
					//+(Double) getExchange().getDailyCandle(ticker, 7).getIndicator(rankName)
					//+(Double) getExchange().getDailyCandle(ticker, 8).getIndicator(rankName);
					//+(Double) getExchange().getDailyCandle(ticker, 9).getIndicator(rankName);
				if(pctRank2<1.5 && pctRank > 0.63){
					_tickers.add(new TickerPerformance(ticker, pctRank));
				}
			} catch (Exception e) {
			}
		}
		Collections.sort(_tickers);
		Collections.reverse(_tickers);
		int size = _tickers.size();
		for(int i=max;i<size;i++)
			_tickers.remove(max);
	}

	@Override
	public void onPreOpen() {
		((TradingAccount)_exchange.getBank().getAccount(getName())).liquidate(_exchange);
	}

	@Override
	public void onOpen() {
	}

	@Override
	public void onPreClose() {

		getDayTopGainer();
		try{
			if(_tickers!=null){
				for(TickerPerformance tp: _tickers){
					_nbSharesTraded = BigDecimal.valueOf((int)(getExchange().getBank().getAccount(getName()).getBalance("EUR").doubleValue()/max/getExchange().getMarketOfficialPrice(tp.ticker).doubleValue()));
					getExchange().registerOrder(new MarketOrder(getName(), tp.ticker, _nbSharesTraded , OrderDirection.Buy, new Date(System.currentTimeMillis())));
				}
			}
		}catch (Exception e) {
		}
	}

	@Override
	public void onClose() {
		//System.out.println("balance: "+_exchange.getBank().getAccount(getName()).getBalance("EUR"));
		
	}

	@Override
	public void onDayEnd() {
		List<IOrder> orders = getExchange().getOrders(getName());
		for(IOrder order : orders){
			try {
				getExchange().cancelOrder(order.getOrderId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
