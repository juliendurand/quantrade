package blackbox.exchange;

import java.math.BigDecimal;

public interface IInterdayStrategy {
	
	public String getName();
	public InterdayExchange getExchange();
	public BigDecimal getStartingCapital();
	public void onDayStart();
	public void onPreOpen();
	public void onOpen();
	public void onPreClose();
	public void onClose();
	public void onDayEnd();

}
