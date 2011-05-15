package blackbox.exchange;

import java.math.BigDecimal;

public interface IExchange {

	public abstract BigDecimal getInstumentOfficialPrice(String ticker) throws Exception;
	
	public abstract long getExchangeTime();

	public abstract void replay();

	public abstract void replay(String start, String end);

	public abstract void registerOrder();

	public abstract void cancelOrder();

}