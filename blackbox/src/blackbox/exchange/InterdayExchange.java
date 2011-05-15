package blackbox.exchange;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blackbox.bank.Bank;
import blackbox.data.ABCDownloader;
import blackbox.timeserie.TimeSerie;

public class InterdayExchange implements IExchange {
	
	private Bank _bank;
	private Map<String, BigDecimal> _officialPrice = new HashMap<String, BigDecimal>();
	private Map<String, IOrder> _orders = new HashMap<String, IOrder>();
	private Set<IInterdayStrategy> _strategies = new HashSet<IInterdayStrategy>();
	private long _exchangeTime;
	private List<String> _tickers;
	private Map<String, TimeSerie> _historicalTimeSeries = new HashMap<String, TimeSerie>();
	
	public InterdayExchange(Bank bank){
		_bank = bank;
		_tickers = ABCDownloader.getAllTickers();
		int i=1;
		int size = _tickers.size();
		HashSet<String> badTicker = new HashSet<String>();
		for(String ticker : _tickers){
			System.out.print("Loading data for "+ticker+" ["+ i++ +"/"+size+"]");
			try{
				TimeSerie ts = new TimeSerie(ticker);
				_historicalTimeSeries.put( ticker, ts);
				_officialPrice.put(ticker, ts.getLastCandle().getClose());
			System.out.println(" -> OK");
			}catch(Exception e){
				System.out.println("Error loading Time-Series for "+ticker);
				e.printStackTrace();
				badTicker.add(ticker);
			}
		}
		for(String ticker : badTicker){
			System.out.println("Removing invalid ticker : "+ticker);
			int index = Collections.binarySearch(_tickers, ticker);
			_tickers.remove(index);
		}
	}

	public void registerInterdayStrategy(IInterdayStrategy strategy) {
		_strategies.add(strategy);
		_bank.createTradingAccount(strategy.getName());
		try{
			_bank.makeDeposit(strategy.getName(), strategy.getStartingCapital(), "EUR");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see blackbox.exchange.IExchange#getInstumentOfficialPrice(java.lang.String)
	 */
	@Override
	public BigDecimal getMarketOfficialPrice(String ticker) throws Exception{
		BigDecimal price = _officialPrice.get(ticker);
		if(price==null)
		{
			throw new Exception("Get Official Price - unknown instrument");
		}
		return price;
	}
	
	@Override
	public long getExchangeTime() {
		return _exchangeTime;
	}
	
	/* (non-Javadoc)
	 * @see blackbox.exchange.IExchange#replayInterday()
	 */
	@Override
	public void replay(){
		
	}
	
	/* (non-Javadoc)
	 * @see blackbox.exchange.IExchange#replayInterday(java.lang.String, java.lang.String)
	 */
	@Override
	public void replay(String start, String end){
		for(IInterdayStrategy strat : _strategies){
			strat.onDayStart();
		}
		for(IInterdayStrategy strat : _strategies){
			strat.onPreOpen();
		}
		open();
		for(IInterdayStrategy strat : _strategies){
			strat.onOpen();
		}
		simulateContinuousTrading();
		for(IInterdayStrategy strat : _strategies){
			strat.onPreClose();
		}
		close();
		for(IInterdayStrategy strat : _strategies){
			strat.onClose();
		}
		for(IInterdayStrategy strat : _strategies){
			strat.onDayEnd();
		}
	}
	
	private void open(){
		executeOrders();
	}
	
	private void simulateContinuousTrading(){
		// day high
		executeOrders();
		// day low
		executeOrders();
	}
	
	private void close(){
		executeOrders();
	}
	
	private void executeOrders(){
		for(IOrder order : _orders.values()){
			if(order.canExecute()){
				try{
					switch (order.getDirection()) {
					case Buy:
						_bank.BuyInstrument(order.getAccountId(), order.getTicker(), order.getSize(), getMarketOfficialPrice(order.getTicker()), "EUR");
						break;
					case Sell:
						_bank.SellInstrument(order.getAccountId(), order.getTicker(), order.getSize(), getMarketOfficialPrice(order.getTicker()), "EUR");
						break;
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				_orders.remove(order.getOrderId());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see blackbox.exchange.IExchange#registerOrder()
	 */
	@Override
	public void registerOrder(IOrder order){
		_orders.put(order.getOrderId(), order);	
	}
	
	/* (non-Javadoc)
	 * @see blackbox.exchange.IExchange#cancelOrder()
	 */
	@Override
	public void cancelOrder(String orderId) throws Exception{
		IOrder order = _orders.get(orderId);
		if(order==null){
			throw new Exception("Canceling order : No such order to cancel "+orderId);
		}
		order.cancel();
		_orders.remove(orderId);
	}

}
