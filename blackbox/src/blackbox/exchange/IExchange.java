package blackbox.exchange;

import java.util.Date;

import blackbox.bank.IMarketPriceSource;

public interface IExchange extends IMarketPriceSource{
	
	public abstract long getExchangeTime();

	public abstract void replay();

	public abstract void replay(Date start, Date end);

	public abstract void registerOrder(IOrder order);

	public abstract void cancelOrder(String orderId) throws Exception;

}