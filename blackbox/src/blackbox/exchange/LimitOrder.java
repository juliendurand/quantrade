package blackbox.exchange;

import java.math.BigDecimal;
import java.util.Date;

public class LimitOrder extends AOrder{

	private double limit;
	
	public LimitOrder(String accoundId, String ticker, BigDecimal size,
			OrderDirection direction, Date expiry, double limitPrice) {
		super(accoundId, ticker, size, direction, expiry);
		this.limit = limitPrice;
	}

	@Override
	public boolean canExecute(double price) {
		if(getDirection()==OrderDirection.Buy) {		
			return price <= limit;
		}
		return price >= limit;
	}
	
	@Override
	public BigDecimal getExecutedPrice(double marketPrice) {
		return new BigDecimal(limit);
	}

}
