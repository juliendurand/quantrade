package blackbox.exchange;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Exchange {
	
	private Map<String, Order> _orders = new HashMap<String, Order>();
	
	public BigDecimal getInstumentLastPrice(String ticker){
		//TODO
		return BigDecimal.ZERO;
	}
	
	public void replayInterday(){
		
	}
	
	public void replayInterday(String start, String end){
		
	}
	
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
	
	public void cancelOrder(){
		
	}

}
