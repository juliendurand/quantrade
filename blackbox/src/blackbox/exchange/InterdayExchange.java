package blackbox.exchange;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import blackbox.bank.Bank;
import blackbox.data.ABCDownloader;
import blackbox.indicator.ABCDERankIndicator;
import blackbox.indicator.IntradayChangeIndicator;
import blackbox.indicator.IntradayRanker;
import blackbox.indicator.OvernightChangeIndicator;
import blackbox.indicator.OvernightRanker;
import blackbox.indicator.PctChangeIndicator;
import blackbox.indicator.Vol20Indicator;
import blackbox.timeserie.DailyCandle;
import blackbox.timeserie.TimeSerie;

public class InterdayExchange implements IExchange {
	
	private Bank _bank;
	private Map<String, Double> _officialPrice = new HashMap<String, Double>();
	private Map<String, IOrder> _orders = new HashMap<String, IOrder>();
	private Set<AInterdayStrategy> _strategies = new HashSet<AInterdayStrategy>();
	private Date _exchangeDate;
	private long _exchangeTime;
	private List<String> _tickers;
	private Map<String, TimeSerie> _historicalTimeSeries = new HashMap<String, TimeSerie>();
	private TreeSet<Date> _tradingDays = new TreeSet<Date>();
	
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
				_tradingDays.addAll(ts.getTradingDays());
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
		
		System.out.println("Calculating PctChange");
		(new PctChangeIndicator()).calculate(_historicalTimeSeries, _tradingDays);
		
		System.out.println("Calculating IntradayPctChange");
		(new IntradayChangeIndicator()).calculate(_historicalTimeSeries, _tradingDays);
		
		System.out.println("Calculating OvernightPctChange");
		(new OvernightChangeIndicator()).calculate(_historicalTimeSeries, _tradingDays);
		
		System.out.println("Calculating Intraday Rank");
		(new IntradayRanker()).calculate(_historicalTimeSeries, _tradingDays);
		
		System.out.println("Calculating Overnight Rank");
		(new OvernightRanker()).calculate(_historicalTimeSeries, _tradingDays);
		
		System.out.println("Calculating Vol20");
		(new Vol20Indicator()).calculate(_historicalTimeSeries, _tradingDays);
		
		System.out.println("Calculating ABCDE");
		(new ABCDERankIndicator()).calculate(_historicalTimeSeries, _tradingDays);
	}
	
	public Bank getBank(){
		return _bank;
	}
	
	public Set<AInterdayStrategy> getInterdayStrategy(){
		return _strategies;
	}

	public void registerInterdayStrategy(AInterdayStrategy strategy) {
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
		Double price = _officialPrice.get(ticker);
		if(price==null)
		{
			throw new Exception("Get Official Price - unknown instrument");
		}
		return new BigDecimal(price);
	}
	
	@Override
	public long getExchangeTime() {
		return _exchangeTime;
	}
	
	public Date getExchangeDate(){
		return _exchangeDate;
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
	public void replay(Date start, Date end){
		SortedSet<Date> range = _tradingDays.subSet(start, end);
		for(Date day : range){
			_exchangeDate = day;
			System.out.println("replay "+day);
			for(TimeSerie ts : _historicalTimeSeries.values()){
				ts.setCursorOn(day);
			}
			for(AInterdayStrategy strat : _strategies){
				strat.onDayStart();
			}
			for(AInterdayStrategy strat : _strategies){
				strat.onPreOpen();
			}
			open();
			for(AInterdayStrategy strat : _strategies){
				strat.onOpen();
			}
			simulateContinuousTrading();
			for(AInterdayStrategy strat : _strategies){
				strat.onPreClose();
			}
			close();
			for(AInterdayStrategy strat : _strategies){
				strat.onClose();
			}
			for(AInterdayStrategy strat : _strategies){
				strat.onDayEnd();
			}
			_bank.closingRun(_exchangeDate);
		}
	}
	
	private void open(){
		for(String ticker : _tickers){
			try{
				TimeSerie ts = _historicalTimeSeries.get(ticker);
				if(ts==null){
					//System.out.println("No timeserie available for "+ticker);
					continue;
				}
				DailyCandle candle = ts.getCandle(_exchangeDate);
				if(candle==null){
					//System.out.println("No candle available for "+ticker);
					continue;
				}
				_officialPrice.put(ticker, candle.getOpen());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		executeOrders();
	}
	
	private void simulateContinuousTrading(){
		// day high
		for(String ticker : _tickers){
			try{
				TimeSerie ts = _historicalTimeSeries.get(ticker);
				if(ts==null){
					//System.out.println("No timeserie available for "+ticker);
					continue;
					}
				DailyCandle candle = ts.getCandle(_exchangeDate);
				if(candle==null){
					//System.out.println("No candle available for "+ticker);
					continue;
					}

				// day high
				_officialPrice.put(ticker, candle.getHigh());
				executeOrders();
				// day low
				_officialPrice.put(ticker, candle.getLow());
				executeOrders();
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	private void close(){
		for(String ticker : _tickers){
			try{
				TimeSerie ts = _historicalTimeSeries.get(ticker);
				if(ts==null){
					//System.out.println("No timeserie available for "+ticker);
					continue;
				}
				DailyCandle candle = ts.getCandle(_exchangeDate);
				if(candle==null){
					//System.out.println("No candle available for "+ticker);
					continue;
				}
				_officialPrice.put(ticker, candle.getClose());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		executeOrders();
	}
	
	private void executeOrders(){
		List<IOrder> ordersToRemove = new ArrayList<IOrder>();
		for(IOrder order : _orders.values()){
			try{
			String ticker = order.getTicker();
			double price = getMarketOfficialPrice(ticker).doubleValue();
			if(order.canExecute(price)){
					switch (order.getDirection()) {
					case Buy:
						_bank.BuyInstrument(_exchangeDate, order.getAccountId(), order.getTicker(), order.getSize(), order.getExecutedPrice(price), "EUR");
						break;
					case Sell:
						_bank.SellInstrument(_exchangeDate, order.getAccountId(), order.getTicker(), order.getSize(), order.getExecutedPrice(price), "EUR");
						break;
					}
					_bank.chargeFees(order.getAccountId(), new BigDecimal(order.getSize().doubleValue()*order.getExecutedPrice(price).doubleValue()*0.001),"EUR");
					ordersToRemove.add(order);
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		for(IOrder order : ordersToRemove){
			_orders.remove(order.getOrderId());
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
	
	public DailyCandle getDailyCandle(String ticker, int nbPeriod) throws Exception{
		return _historicalTimeSeries.get(ticker).getCandle(nbPeriod);		
	}
	
	public List<IOrder> getOrders(String accountId){
		List<IOrder> resultSet = new ArrayList<IOrder>();
		for(IOrder order : _orders.values()){
			if(order.getAccountId().equals(accountId)){
				resultSet.add(order);
			}
		}
		return resultSet;
	}
	
	public void clearOrders(String accountId){
		List<IOrder> orders = getOrders(accountId);
		for(IOrder order : orders){
			try {
				cancelOrder(order.getOrderId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getAllTickers() {
		return _tickers;
	}

}
