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

public class NearPerfectCloseOpenStrategy extends AInterdayStrategy {

	private InterdayExchange _exchange;
	
	private BigDecimal _nbSharesTraded = BigDecimal.ZERO;
	
	private int _maxShareTraded = 5;
	private List<TickerPerformance> _tickers = new ArrayList<TickerPerformance>();
	
	public NearPerfectCloseOpenStrategy(InterdayExchange exchange){
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
		for(TickerPerformance tp : _tickers){	
			String ticker = tp.ticker;
			try{
				_nbSharesTraded = BigDecimal.valueOf((int)(getExchange().getBank().getAccount(getName()).getBalance("EUR").doubleValue()/getExchange().getMarketOfficialPrice(ticker).doubleValue()));
				_nbSharesTraded = _nbSharesTraded.divide(new BigDecimal(_maxShareTraded));
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
		List<IOrder> orders = getExchange().getOrders(getName());
		for(IOrder order : orders){
			try {
				getExchange().cancelOrder(order.getOrderId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getDayTopGainer(){
		List<String> tickers = getExchange().getAllTickers();
		List<TickerPerformance> tickerList = new ArrayList<TickerPerformance>();
		for(String ticker : tickers){
			try{
				if(getExchange().getDailyCandle(ticker, 0).getClose() <2){
						continue;
				}
				double change = getExchange().getDailyCandle(ticker, 0).getOpen()/getExchange().getDailyCandle(ticker, 0).getClose();
				tickerList.add(new TickerPerformance(ticker, change));
			}catch (Exception e) {
				// TODO: handle exception
			}		
		}
		Collections.sort(tickerList);
		Collections.reverse(tickerList);
		_tickers = tickerList;
		if(tickerList.size()>_maxShareTraded){
			_tickers = tickerList.subList(0,_maxShareTraded);
		}
	}

}
