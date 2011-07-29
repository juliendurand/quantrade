package blackbox.strategy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import blackbox.bank.TradingAccount;
import blackbox.exchange.AInterdayStrategy;
import blackbox.exchange.IOrder;
import blackbox.exchange.IOrder.OrderDirection;
import blackbox.exchange.InterdayExchange;
import blackbox.exchange.MarketOrder;

public class PerfectCloseOpenStrategy extends AInterdayStrategy {

	private InterdayExchange _exchange;
	
	private BigDecimal _nbSharesTraded = BigDecimal.ZERO;
	
	public PerfectCloseOpenStrategy(InterdayExchange exchange){
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
		
		try{
			String ticker = getDayTopGainer();
			_nbSharesTraded = BigDecimal.valueOf((int)(getExchange().getBank().getAccount(getName()).getBalance("EUR").doubleValue()/getExchange().getMarketOfficialPrice(ticker).doubleValue()));
			getExchange().registerOrder(new MarketOrder(getName(), ticker, _nbSharesTraded , OrderDirection.Buy, new Date(System.currentTimeMillis())));
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
	
	private String getDayTopGainer() throws Exception{
		List<String> tickers = getExchange().getAllTickers();
		double max = 0d;
		String bestticker = null;
		for(String ticker : tickers){
			try{
			double change = getExchange().getDailyCandle(ticker, -1).getOpen()/getExchange().getDailyCandle(ticker, 0).getClose();
			if(change > max){
				max = change;
				bestticker = ticker;
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
				
		}if(max>0)
			return bestticker;
		else
			return null;
	}

}
