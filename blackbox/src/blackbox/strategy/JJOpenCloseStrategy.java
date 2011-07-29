package blackbox.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import blackbox.bank.TradingAccount;
import blackbox.exchange.AInterdayStrategy;
import blackbox.exchange.IOrder;
import blackbox.exchange.IOrder.OrderDirection;
import blackbox.exchange.InterdayExchange;
import blackbox.exchange.MarketOrder;

public class JJOpenCloseStrategy extends AInterdayStrategy {

	private InterdayExchange _exchange;
	
	private List<TickerPerformance> _tickers = new ArrayList<TickerPerformance>();
	private BigDecimal _nbSharesTraded = BigDecimal.ZERO;
	private int max =1;
	
	public JJOpenCloseStrategy(InterdayExchange exchange){
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
	
	private void getJJ() {
		_tickers.clear();
		for(String ticker : getExchange().getAllTickers()){
			try {
				String profil = //String.valueOf(getExchange().getDailyCandle(ticker, 2)._quintile)+
								String.valueOf(getExchange().getDailyCandle(ticker, 1)._quintile)+
								String.valueOf(getExchange().getDailyCandle(ticker, 0)._quintile);
				if("JJ".equals(profil)){
					if(getExchange().getDailyCandle(ticker, 0).getVol20() <0.02){
						_tickers.add(new TickerPerformance(ticker, 0));
					}
				}
			}catch (Exception e) {
			}
		}
	}

	@Override
	public void onPreOpen() {
		getJJ();
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
	public void onOpen() {
	}

	@Override
	public void onPreClose() {
		((TradingAccount)_exchange.getBank().getAccount(getName())).liquidate(_exchange);
		
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
