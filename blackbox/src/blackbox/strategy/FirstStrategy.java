package blackbox.strategy;

import java.math.BigDecimal;

import blackbox.exchange.IInterdayStrategy;

public class FirstStrategy implements IInterdayStrategy {

	@Override
	public String getName() {
		return "FirstStrategy";
	}

	@Override
	public BigDecimal getStartingCapital() {
		return BigDecimal.valueOf(100000.00d);
	}

	@Override
	public void onDayStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreOpen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOpen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreClose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDayEnd() {
		// TODO Auto-generated method stub

	}

}
