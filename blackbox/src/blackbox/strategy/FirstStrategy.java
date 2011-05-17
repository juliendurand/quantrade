package blackbox.strategy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
	
	private String _ticker;
	private BigDecimal _nbSharesTraded = BigDecimal.ZERO;
	
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
		return BigDecimal.valueOf(100000.00d);
	}

	@Override
	public void onDayStart() {
		IIndicator pctChangeIndicator = new PctChangeIndicator();
		IIndicator pctRankIndicator = new PctRankIndicator();
		List<String> tickers = getExchange().getAllTickers();
		double maxGain = 0d;
		String tickerMaxGain = null;
		for(String ticker : tickers){
			try {
//				double yesterday = getExchange().getDailyCandle(ticker, 0).getAdjustedClose().doubleValue();
//				double dayBeforeYesterday = getExchange().getDailyCandle(ticker, 1).getAdjustedClose().doubleValue();
//				double gain = yesterday/dayBeforeYesterday - 1d;				
//				if(gain>maxGain){
//					maxGain = gain;
//					tickerMaxGain = ticker;
//				}
				Double pctRank = (Double) getExchange().getDailyCandle(ticker, 0).getIndicator(pctRankIndicator.getName());
				if(pctRank == 1d){
					tickerMaxGain = ticker;
					maxGain = (Double) getExchange().getDailyCandle(ticker, 0).getIndicator(pctChangeIndicator.getName());
					break;
				}
			} catch (Exception e) {
			}
		}
		System.out.println("max gain is "+tickerMaxGain+" "+maxGain*100d+" %");
		_ticker = tickerMaxGain;
	}

	@Override
	public void onPreOpen() {
		try{
			if(_ticker!=null){
				_nbSharesTraded = BigDecimal.valueOf((int)(10000/getExchange().getMarketOfficialPrice(_ticker).doubleValue()));
				getExchange().registerOrder(new MarketOrder(getName(), _ticker, _nbSharesTraded , OrderDirection.Buy, new Date(System.currentTimeMillis())));
			}
		}catch (Exception e) {
		}
	}

	@Override
	public void onOpen() {
	}

	@Override
	public void onPreClose() {
		try{
			if(_ticker!=null){
				getExchange().registerOrder(new MarketOrder(getName(), _ticker, _nbSharesTraded , OrderDirection.Sell, new Date(System.currentTimeMillis())));
			}
		}
		catch (Exception e) {
		}
	}

	@Override
	public void onClose() {
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
