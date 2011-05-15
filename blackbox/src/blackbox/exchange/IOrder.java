package blackbox.exchange;

import java.math.BigDecimal;
import java.util.Date;

public interface IOrder {
	
	public enum OrderDirection {
		Buy,
		Sell
	}
	
	public String getOrderId();
	public String getAccountId();
	public String getTicker();
	public BigDecimal getSize();
	public OrderDirection getDirection();
	public Date getExpiry();
	public boolean canExecute();
	public void cancel();
}
