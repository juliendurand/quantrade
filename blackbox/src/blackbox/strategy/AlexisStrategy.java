package blackbox.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import blackbox.bank.TradingAccount;
import blackbox.exchange.AInterdayStrategy;
import blackbox.exchange.IOrder;
import blackbox.exchange.IOrder.OrderDirection;
import blackbox.exchange.InterdayExchange;
import blackbox.exchange.MarketOrder;

public class AlexisStrategy extends AInterdayStrategy {

	private InterdayExchange _exchange;
	
	private List<TickerPerformance> _tickers = new ArrayList<TickerPerformance>();
	private BigDecimal _nbSharesTraded = BigDecimal.ZERO;
	private int max =1;
	
	public AlexisStrategy(InterdayExchange exchange){
		_exchange = exchange;
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
		List<String> tickers = getExchange().getAllTickers();
		List<TickerPerformance> tickerList = new ArrayList<TickerPerformance>();
		//double maxGain = 0d;
		//String tickerMaxGain = null;
		_tickers.clear();
		for(String ticker : tickers){
			try {
				//Double pctChange = (Double) getExchange().getDailyCandle(ticker, 0).getPctChange();
				Double pctRank = (Double) getExchange().getDailyCandle(ticker, 0).getRank();
				//Double pctRank2 =	(Double) getExchange().getDailyCandle(ticker, 1).getRank()
				//	+(Double) getExchange().getDailyCandle(ticker, 2).getRank()
				//	+(Double) getExchange().getDailyCandle(ticker, 3).getRank()
				//	+(Double) getExchange().getDailyCandle(ticker, 4).getRank()
				//	+(Double) getExchange().getDailyCandle(ticker, 5).getRank()
				//pctRank2/=(Double) getExchange().getDailyCandle(ticker, 1).getIndicator("Vol20")*100/1.5;
				//	+(Double) getExchange().getDailyCandle(ticker, 6).getRank()
				//	+(Double) getExchange().getDailyCandle(ticker, 7).getRank()
				//	+(Double) getExchange().getDailyCandle(ticker, 8).getRank();
				//	//+(Double) getExchange().getDailyCandle(ticker, 9).getIndicator(rankName);
				//if(pctRank2<2.5 && pctRank > 0.6){
				tickerList.add(new TickerPerformance(ticker, pctRank));
				//}
			} catch (Exception e) {
			}
		}
		Collections.sort(tickerList);
		Collections.reverse(tickerList);
		_tickers = tickerList;
		if(tickerList.size()>max){
			_tickers = tickerList.subList(0,max);
		} 	
	}

	@Override
	public void onPreOpen() {
		
	}

	@Override
	public void onOpen() {
	}

	@Override
	public void onPreClose() {
		((TradingAccount)_exchange.getBank().getAccount(getName())).liquidate(_exchange);

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
