package blackbox.exchange;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Exchange implements IExchange {
	
	private Map<String, BigDecimal> _officialPrice = new HashMap<String, BigDecimal>();
	private Map<String, Order> _orders = new HashMap<String, Order>();
	private long _exchangeTime;
	
	/* (non-Javadoc)
	 * @see blackbox.exchange.IExchange#getInstumentOfficialPrice(java.lang.String)
	 */
	@Override
	public BigDecimal getInstumentOfficialPrice(String ticker) throws Exception{
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
		
	}
	
	/* (non-Javadoc)
	 * @see blackbox.exchange.IExchange#registerOrder()
	 */
	@Override
	public void registerOrder(){
		
	}
	
	public void createLimitOrder(){
		
	}
	
	public void createBestLimitOrder(){
		
	}
	
	public void createMarketOrder(){
		
	}
	
	public void createStopOrder(){
		
	}
	
	public void createTrailingOrder(){
		
	}
	
	/* (non-Javadoc)
	 * @see blackbox.exchange.IExchange#cancelOrder()
	 */
	@Override
	public void cancelOrder(){
		
	}

}
