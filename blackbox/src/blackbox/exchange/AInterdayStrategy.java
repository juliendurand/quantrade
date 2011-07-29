package blackbox.exchange;

import java.math.BigDecimal;

public abstract class AInterdayStrategy {
	
	public String getName(){
		return this.getClass().getName();
	}
	abstract public InterdayExchange getExchange();
	abstract public BigDecimal getStartingCapital();
	abstract public void onDayStart();
	abstract public void onPreOpen();
	abstract public void onOpen();
	abstract public void onPreClose();
	abstract public void onClose();
	abstract public void onDayEnd();

}
